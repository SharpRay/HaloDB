package org.rzlabs.halo.curator;

import com.google.common.base.Strings;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.DefaultExhibitorRestClient;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.exhibitor.Exhibitors;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.rzlabs.halo.guice.JsonConfigProvider;
import org.rzlabs.halo.guice.annotations.LazySingleton;
import org.rzlabs.halo.util.common.StringUtils;
import org.rzlabs.halo.util.common.lifecycle.Lifecycle;
import org.rzlabs.halo.util.common.logger.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class CuratorModule implements Module {

    static final String CURATOR_CONFIG_PREFIX = "halo.zk.service";

    static final String EXHIBITOR_CONFIG_PREFIX = "halo.exhibitor.service";

    private static final int BASE_SLEEP_TIME_MS = 1000;

    private static final int MAX_SLEEP_TIME_MS = 45000;

    private static final int MAX_RETRIES = 29;

    private static final Logger log = new Logger(CuratorModule.class);

    @Override
    public void configure(Binder binder) {
        JsonConfigProvider.bind(binder, CURATOR_CONFIG_PREFIX, ZkEnablementConfig.class);
        JsonConfigProvider.bind(binder, CURATOR_CONFIG_PREFIX, CuratorConfig.class);
        JsonConfigProvider.bind(binder, EXHIBITOR_CONFIG_PREFIX, ExhibitorConfig.class);
    }

    @Provides
    @LazySingleton
    public CuratorFramework makeCurator(ZkEnablementConfig zkEnablementConfig, CuratorConfig config,
                                        EnsembleProvider ensembleProvider, Lifecycle lifecycle) {
        if (!zkEnablementConfig.isEnabled()) {
            throw new RuntimeException("Zookeeper is disabled, Can't create CuratorFramework");
        }

        final CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        if (!Strings.isNullOrEmpty(config.getZkUser()) && !Strings.isNullOrEmpty(config.getZkPwd())) {
            builder.authorization(
                    config.getAuthScheme(),
                    StringUtils.format("%s:%s", config.getZkUser(), config.getZkPwd())
                            .getBytes(StandardCharsets.UTF_8)
            );
        }

        RetryPolicy retryPolicy =
                new BoundedExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_SLEEP_TIME_MS, MAX_RETRIES);

        final CuratorFramework framework = builder
                .ensembleProvider(ensembleProvider)
                .sessionTimeoutMs(config.getZkSessionTimeoutMs())
                .connectionTimeoutMs(config.getZkConnectionTimeoutMs())
                .retryPolicy(retryPolicy)
                .compressionProvider(new PotentiallyGzippedCompressionProvider(config.getEnableCompression()))
                .aclProvider(config.getEnableAcl() ? new SecuredACLProvider() : new DefaultACLProvider())
                .build();

        framework.getUnhandledErrorListenable().addListener((message, e) -> {
            log.error(e, "Unhandled error in Curator, stopping server.");
            shutdown(lifecycle);
        });

        lifecycle.addHandler(
                new Lifecycle.Handler() {
                    @Override
                    public void start() throws Exception {
                        log.debug("Starting Curator");
                        framework.start();
                    }

                    @Override
                    public void stop() {
                        log.debug("Stopping Curator");
                        framework.close();
                    }
                }
        );

        return framework;
    }

    @Provides
    @LazySingleton
    public EnsembleProvider makeEnsembleProvider(CuratorConfig config, ExhibitorConfig exhibitorConfig) {
        if (exhibitorConfig.getHosts().isEmpty()) {
            return new FixedEnsembleProvider(config.getZkHosts());
        }

        RetryPolicy retryPolicy = new BoundedExponentialBackoffRetry(
                BASE_SLEEP_TIME_MS, MAX_SLEEP_TIME_MS, MAX_RETRIES);

        return new ExhibitorEnsembleProvider(
                new Exhibitors(
                        exhibitorConfig.getHosts(),
                        exhibitorConfig.getRestPort(),
                        newBackupProvider(config.getZkHosts())
                ),
                new DefaultExhibitorRestClient(exhibitorConfig.getUseSsl()),
                exhibitorConfig.getRestUriPath(),
                exhibitorConfig.getPollingMs(),
                retryPolicy
        ) {
            @Override
            public void start() throws Exception {
                log.debug("Polling the list of Zookeeper servers for the initial ensemble");
                this.pollForInitialEnsemble();;
                super.start();
            }
        }
    }

    private void shutdown(Lifecycle lifecycle) {
        try {
            lifecycle.stop();
        } catch (Throwable t) {
            log.error(t, "Exception when stopping server after unhandled Curator error.");
        } finally {
            System.exit(1);
        }
    }

    private Exhibitors.BackupConnectionStringProvider newBackupProvider(final String zkHosts) {
        return () -> zkHosts;
    }

    static class SecuredACLProvider implements ACLProvider {

        @Override
        public List<ACL> getDefaultAcl() {
            return ZooDefs.Ids.CREATOR_ALL_ACL;
        }

        @Override
        public List<ACL> getAclForPath(String path) {
            return ZooDefs.Ids.CREATOR_ALL_ACL;
        }
    }
}
