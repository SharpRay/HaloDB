package org.rzlabs.halo.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class EnvironmentVariablePasswordProvider implements PasswordProvider {

    private final String variable;

    @JsonCreator
    public EnvironmentVariablePasswordProvider(@JsonProperty("variable") String variable) {
        this.variable = Preconditions.checkNotNull(variable);
    }

    @JsonProperty("variable")
    public String getVariable() {
        return variable;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return System.getenv(variable);
    }

    @Override
    public String toString() {
        return String.format("EnvironmentVariablePasswordProvider{variable='%s'}", variable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnvironmentVariablePasswordProvider that = (EnvironmentVariablePasswordProvider) o;

        return variable != null ? variable.equals(that.variable) : that.variable == null;
    }

    @Override
    public int hashCode() {
        return variable != null ? variable.hashCode() : 0;
    }
}
