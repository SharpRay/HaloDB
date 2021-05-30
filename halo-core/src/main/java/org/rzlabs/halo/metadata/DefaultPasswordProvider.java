package org.rzlabs.halo.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultPasswordProvider implements PasswordProvider {

    private final String password;

    @JsonCreator
    public static DefaultPasswordProvider fromString(String str) {
        return new DefaultPasswordProvider(str);
    }

    @JsonCreator
    public DefaultPasswordProvider(@JsonProperty("password") String password) {
        this.password = password;
    }

    @Override
    @JsonProperty
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPasswordProvider)) {
            return false;
        }

        DefaultPasswordProvider that = (DefaultPasswordProvider) o;

        return getPassword() != null ? getPassword().equals(that.getPassword()) : that.getPassword() == null;
    }

    @Override
    public int hashCode() {
        return getPassword() != null ? getPassword().hashCode() : 0;
    }
}
