package org.rzlabs.halo.guice;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import org.rzlabs.halo.guice.annotations.LazySingleton;
import org.rzlabs.halo.util.common.lifecycle.Lifecycle;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * A Module to add lifecycle management to the injector.
 * {@link SingletonBindingModule} must also be included.
 */
public class LifecycleModule implements Module {

    /**
     * This scope includes final logging shutdown, so all other handlers in this lifecycle scope should avoid
     * logging in their stop() method, either failing silently or failing violently and throwing an exception
     * causing an ungraceful exit.
     */
    private final LifecycleScope initScope = new LifecycleScope(Lifecycle.Stage.INIT);
    private final LifecycleScope normalScope = new LifecycleScope(Lifecycle.Stage.NORMAL);
    private final LifecycleScope serverScope = new LifecycleScope(Lifecycle.Stage.SERVER);
    private final LifecycleScope announcementsScope = new LifecycleScope(Lifecycle.Stage.ANNOUNCEMENTS);

    /**
     * Registers a class to instantiate eagerly. Classes mentioned here will be pulled out of
     * the injector with an injector.getInstance() call when the lifecycle is created.
     *
     * Eagerly loaded classes will *not* be automatically added to the Lifecycle unless they are
     * bound to the proper scope. That is, they are genarally eagerly loaded because the loading
     * operation will produce some beneficial *side-effect* even if nothing actually directly
     * depends on the instance.
     *
     * This mechanism exists to allow the {@link Lifecycle} to be the primary entry point from the injector,
     * not to auto-register things with the {@link Lifecycle}. It is also possible to just bind things
     * eagerly with Guice, it is not clear which is actually the best approach. This is more explicit, but
     * eager bindings inside of modules is less error-prone.
     *
     * @param clazz the class to instantiate
     */
    public static void register(Binder binder,Class<?> clazz) {
        registerKey(binder, Key.get(clazz));
    }

    public static void register(Binder binder, Class<?> clazz, Class<? extends Annotation> annotation) {
        registerKey(binder, Key.get(clazz, annotation));
    }

    public static void registerKey(Binder binder, Key<?> key) {
        getEagerBinder(binder).addBinding().toInstance(new KeyHolder<Object>(key));
    }

    private static Multibinder<KeyHolder> getEagerBinder(Binder binder) {
        return Multibinder.newSetBinder(binder, KeyHolder.class, Names.named("lifecycle"));
    }

    @Override
    public void configure(Binder binder) {
        // Load up the eager binder so that it will inject the empty set at a minimum.
        getEagerBinder(binder);

        // Bind annotation to specific scope.
        binder.bindScope(ManageLifecycleInit.class, initScope);
        binder.bindScope(ManageLifecycle.class, normalScope);
        binder.bindScope(ManageLifecycleServer.class, serverScope);
        binder.bindScope(ManageLifecycleAnnouncements.class, announcementsScope);
    }

    @Provides @LazySingleton
    public Lifecycle getLifecycle(final Injector injector) {
        final Key<Set<KeyHolder>> keyHolderKey = Key.get(new TypeLiteral<Set<KeyHolder>>(){},
                Names.named("lifecycle"));
        final Set<KeyHolder> eagerClasses = injector.getInstance(keyHolderKey);

        Lifecycle lifecycle = new Lifecycle("module") {
            @Override
            public void start() throws Exception {
                // Pull the key so as to "eagerly" load up the class.
                // If the registered class has been bind to one of the ManageLifecycleInit,
                // ManageLifecycle, ManageLifecycleServer or ManageLifecycleAnnouncements Scopes,
                // the instance will be add to the corresponding Scope in the progress of the call
                // of Injector's getInstance() method.
                eagerClasses.forEach(keyHolder -> injector.getInstance(keyHolder.getKey()));
                super.start();
            }
        };

        initScope.setLifecycle(lifecycle);
        normalScope.setLifecycle(lifecycle);
        serverScope.setLifecycle(lifecycle);
        announcementsScope.setLifecycle(lifecycle);

        return lifecycle;
    }
}
