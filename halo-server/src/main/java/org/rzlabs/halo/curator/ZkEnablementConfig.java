package org.rzlabs.halo.curator;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.rzlabs.halo.util.common.StringUtils;

import java.util.Properties;

public class ZkEnablementConfig {

    private static final String PROP_KEY_ENABLED = StringUtils.format("%s.enabled",
            CuratorModule.CURATOR_CONFIG_PREFIX);

    public static final ZkEnablementConfig ENABLE = new ZkEnablementConfig(true);

    @JsonProperty
    private final boolean enabled;

    @JsonCreator
    public ZkEnablementConfig(@JsonProperty("enabled") Boolean enabled) {
        this.enabled = enabled == null ? true : enabled.booleanValue();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static boolean isEnabled(Properties properties) {
        String value = properties.getProperty(PROP_KEY_ENABLED);
        return value == null ? true : Boolean.parseBoolean(value);
    }
}
