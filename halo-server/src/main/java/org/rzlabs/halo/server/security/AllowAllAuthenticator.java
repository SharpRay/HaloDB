package org.rzlabs.halo.server.security;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

/**
 * Should only be used in conjunction with AllowAllAuthorizer.
 */
public class AllowAllAuthenticator implements Authenticator {

    public static final AuthenticationResult ALLOW_ALL_RESULT = new AuthenticationResult(
            AuthConfig.ALLOW_ALL_NAME,
            AuthConfig.ALLOW_ALL_NAME,
            AuthConfig.ALLOW_ALL_NAME,
            null);

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(ServletRequest servletRequest,
                                 ServletResponse servletResponse,
                                 FilterChain filterChain) throws IOException, ServletException {
                servletRequest.setAttribute(AuthConfig.HALO_AUTHENTICATION_RESULT, ALLOW_ALL_RESULT);
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            public void destroy() {

            }
        };
    }

    @Override
    public Class<? extends Filter> getFilterClass() {
        return null;
    }

    @Override
    public Map<String, String> getInitParameters() {
        return null;
    }

    @Override
    public String[] getPaths() {
        return new String[]{"/*"};
    }

    @Override
    public EnumSet<DispatcherType> getDispatcherType() {
        return null;
    }

    @Override
    public String getAuthChallengeHeader() {
        return null;
    }

    @Override
    public AuthenticationResult authenticateJDBCContext(Map<String, Object> context) {
        return ALLOW_ALL_RESULT;
    }
}
