package org.multieditor.web;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author nikita.zinoviev@gmail.com
 */
public class UserAuthenticationPhaseListener implements PhaseListener {

    private static final Logger LOGGER = Logger.getLogger(UserAuthenticationPhaseListener.class.getName());

    @Inject
    @Named("userBean")
    private UserBean userBean;

    public void afterPhase(PhaseEvent phaseEvent) {
    }

    public void beforePhase(PhaseEvent phaseEvent) {
        if (!userBean.getLoggedIn()) {
            try {
                LOGGER.info("UserAuthenticationPhaseListener forwarding to login page");
//                final FacesContext fctx = FacesContext.getCurrentInstance();
//                ExternalContext ctx = fctx.getExternalContext();

                final FacesContext facesContext = phaseEvent.getFacesContext();
                final ExternalContext externalContext = facesContext.getExternalContext();
                final String requestContextPath = externalContext.getRequestPathInfo();
                if (!requestContextPath.contains("/Login.xhtml") && !requestContextPath.contains("/UserEdit.xhtml")) {
                    externalContext.invalidateSession();
                    externalContext.redirect(externalContext.getApplicationContextPath() + "/faces/Login.xhtml");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Not cool...", e);
            }
        }
    }

    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
