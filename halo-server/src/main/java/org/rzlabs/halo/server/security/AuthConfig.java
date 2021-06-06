package org.rzlabs.halo.server.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class AuthConfig {

    /**
     * HTTP attribute that holds an AuthenticationResult, with info about a successful authentication check.
     */
    public static final String HALO_AUTHENTICATION_RESULT = "Halo-Authentication-Result";

    /**
     * HTTP attribute set when a static method in AuthorizationUtils performs an authorization check on the request.
     */
    public static final String HALO_AUTHORIZATION_CHECKED = "Halo-Authorization-Checked";

    public static final String HALO_ALLOW_UNSECURED_PATH = "Halo-Allow-Unsecured-Path";

    public static final String ALLOW_ALL_NAME = "allowAll";

    public static final String ANONYMOUS_NAME = "anonymous";

    public static final String TRUSTED_DOMAIN_NAME = "trustedDomain";

    public AuthConfig() {
        this(null, null, null, false);
    }

    @JsonProperty
    private final List<String> authenticatorChain;

    @JsonProperty
    private List<String> authorizers;

    @JsonProperty
    private final List<String> unsecuredPaths;

    @JsonProperty
    private final boolean allowUnauthenticatedHttpOptions;

    @JsonCreator
    public AuthConfig(@JsonProperty("authenticatorChain") List<String> authenticatorChain,
                      @JsonProperty("authroizers") List<String> authorizers,
                      @JsonProperty("unsecuredPaths") List<String> unsecuredPaths,
                      @JsonProperty("allowUnauthenticatedHttpOptions") boolean allowUnauthenticatedHtpOptioons) {
        this.authenticatorChain = authenticatorChain;
        this.authorizers = authorizers;
        this.unsecuredPaths = unsecuredPaths;
        this.allowUnauthenticatedHttpOptions = allowUnauthenticatedHtpOptioons;
    }

    public List<String> getAuthenticatorChain() {
        return authenticatorChain;
    }

    public List<String> getAuthorizers() {
        return authorizers;
    }

    public List<String> getUnsecuredPaths() {
        return unsecuredPaths;
    }

    public boolean isAllowUnauthenticatedHttpOptions() {
        return allowUnauthenticatedHttpOptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthConfig that = (AuthConfig) o;
        return isAllowUnauthenticatedHttpOptions() == that.isAllowUnauthenticatedHttpOptions() &&
                Objects.equals(getAuthenticatorChain(), that.getAuthenticatorChain()) &&
                Objects.equals(getAuthorizers(), that.getAuthorizers()) &&
                Objects.equals(getUnsecuredPaths(), that.getUnsecuredPaths());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthenticatorChain(),
                getAuthorizers(),
                getUnsecuredPaths(),
                isAllowUnauthenticatedHttpOptions());
    }

    @Override
    public String toString() {
        return "AuthConfig{" +
                "authenticatorChain=" + authenticatorChain +
                ", authorizers=" + authorizers +
                ", unsecuredPaths=" + unsecuredPaths +
                ", allowUnauthenticatedHttpOptions=" + allowUnauthenticatedHttpOptions +
                "}";
    }
}
