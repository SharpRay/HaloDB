package org.rzlabs.halo.curator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import org.rzlabs.halo.metadata.DefaultPasswordProvider;
import org.rzlabs.halo.metadata.PasswordProvider;

import javax.validation.constraints.Min;

public class CuratorConfig {

    @JsonProperty("host")
    private String zkHosts = "localhost";

    @JsonProperty("sessionTimeoutMs")
    @Min(0)
    private int zkSessionTimeoutMs = 30_000;

    @JsonProperty("connectionTimeoutMs")
    @Min(0)
    private int zkConnectionTimeoutMs = 15_000;

    @JsonProperty("compress")
    private boolean enableCompression = true;

    @JsonProperty("acl")
    private boolean enableAcl = false;

    @JsonProperty("user")
    private String zkUser;

    @JsonProperty("pwd")
    private PasswordProvider zkPwd = new DefaultPasswordProvider("");

    @JsonProperty("authScheme")
    private String authScheme = "digest";

    public String getZkHosts() {
        return zkHosts;
    }

    public void setZkHosts(String zkHosts) {
        this.zkHosts = zkHosts;
    }

    public int getZkSessionTimeoutMs() {
        return zkSessionTimeoutMs;
    }

    public void setZkSessionTimeoutMs(int zkSessionTimeoutMs) {
        this.zkSessionTimeoutMs = zkSessionTimeoutMs;
    }

    public int getZkConnectionTimeoutMs() {
        return zkConnectionTimeoutMs;
    }

    public void setZkConnectionTimeoutMs(int zkConnectionTimeoutMs) {
        this.zkConnectionTimeoutMs = zkConnectionTimeoutMs;
    }

    public boolean getEnableCompression() {
        return enableCompression;
    }

    public void setEnableCompression(Boolean enableCompression) {
        Preconditions.checkNotNull(enableCompression, "enableCompression");
        this.enableCompression = enableCompression;
    }

    public boolean getEnableAcl() {
        return enableAcl;
    }

    public void setEnableAcl(Boolean enableAcl) {
        Preconditions.checkNotNull(enableAcl, "enableAcl");
        this.enableAcl = enableAcl;
    }

    public String getZkUser() {
        return zkUser;
    }

    public String getZkPwd() {
        return zkPwd.getPassword();
    }

    public String getAuthScheme() {
        return authScheme;
    }
}
