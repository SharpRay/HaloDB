package org.rzlabs.halo.metadata;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * TODO: User defined provider as the extention point
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = DefaultPasswordProvider.class)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "default", value = DefaultPasswordProvider.class),
        @JsonSubTypes.Type(name = "environment", value = EnvironmentVariablePasswordProvider.class)
})
public interface PasswordProvider {

    String getPassword();
}
