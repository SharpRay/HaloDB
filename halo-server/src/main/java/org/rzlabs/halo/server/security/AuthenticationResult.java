package org.rzlabs.halo.server.security;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * An AuthenticationResult contains information about a successfully authenticated request.
 */
public class AuthenticationResult {

    /**
     * The identity of the requester.
     */
    private final String identity;

    /**
     * The name of the Authorizer that should handle the authenticated request.
     */
    private final String authorizerName;

    /**
     * Name of authenticator whom created the results.
     */
    @Nullable
    private final String authenticatedBy;

    /**
     * Parameter containing additional context information from an Authenticator.
     */
    @Nullable
    private final Map<String, Object> context;

    public AuthenticationResult(final String identity,
                                final String authorizerName,
                                final String authenticatedBy,
                                final Map<String, Object> context) {
        this.identity = identity;
        this.authorizerName = authorizerName;
        this.authenticatedBy = authenticatedBy;
        this.context = context;
    }

    public String getIdentity() {
        return identity;
    }

    public String getAuthorizerName() {
        return authorizerName;
    }

    @Nullable
    public Map<String, Object> getContext() {
        return context;
    }

    @Nullable
    public String getAuthenticatedBy() {
        return authenticatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthenticationResult that = (AuthenticationResult) o;
        return Objects.equals(getIdentity(), that.getIdentity()) &&
                Objects.equals(getAuthorizerName(), that.getAuthorizerName()) &&
                Objects.equals(getAuthenticatedBy(), that.getAuthenticatedBy()) &&
                Objects.equals(getContext(), that.getContext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentity(), getAuthorizerName(), getAuthenticatedBy(), getContext());
    }
}
