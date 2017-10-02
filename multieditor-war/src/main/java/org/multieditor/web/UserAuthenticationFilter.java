package org.multieditor.web;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// disabled in the MultieditorRestApplication as we do not need it at the moment
// this works but only for 'rest' part of the application
// for JSF we use a phase listener instead
// at least it allows to access the logged in username
@ManagedBean
public class UserAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(UserAuthenticationFilter.class.getName());

    @Inject
    @Named("userBean")
    private UserBean userBean;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        LOGGER.finest("Entering UserAuthenticationFilter");
        try {
            if (!userBean.getLoggedIn()) {
                LOGGER.info("UserAuthenticationFilter forwarding to login page");
//                facesContext is null in the 'rest' part of the application
//                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
//                ctx.invalidateSession();
//                ctx.redirect(ctx.getApplicationContextPath() + "Login.xhtml");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Not cool...", e);
        }
        LOGGER.finest("Exiting UserAuthenticationFilter");
    }
}
