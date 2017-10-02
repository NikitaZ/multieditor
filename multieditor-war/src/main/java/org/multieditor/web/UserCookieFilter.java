package org.multieditor.web;

import javax.annotation.ManagedBean;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// disabled in the MultieditorRestApplication as we do not need it at the moment
@ManagedBean
public class UserCookieFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(UserCookieFilter.class.getName());

    public static final String AUTHENTICATION_COOKIE = "CCC_INFO";

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOGGER.finest("Entering UserCookieFilter");
        try {
            Cookie cookie = request.getCookies().get(AUTHENTICATION_COOKIE);
            if (cookie != null) {
//                getUserAccount(cookie.getValue());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Not cool...", e);
        }
        LOGGER.finest("Exiting UserCookieFilter");
    }
}
