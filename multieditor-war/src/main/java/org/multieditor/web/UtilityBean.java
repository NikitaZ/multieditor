package org.multieditor.web;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;

/**
 * @author Nikita.Zinoviev@gmail.com
 */
@Named("UtilityBean")
@ApplicationScoped
public class UtilityBean {
    private static final String USER_PARAM = "userName";
    private static final String DOCUMENT_PARAM = "documentName";
    private static final String LOG_INTO_ON_SAVE_PARAM = "logIntoOnSave";

    public UtilityBean() {
    }

    public String getUserParamName() {
        return USER_PARAM;
    }

    public String getDocumentParamName() {
        return DOCUMENT_PARAM;
    }

    public String getLogIntoOnSaveParamName() {
        return LOG_INTO_ON_SAVE_PARAM;
    }

    /**
     * whether we are rendering the page
     */
    public static boolean isRendering() {
        return FacesContext.getCurrentInstance().getCurrentPhaseId().equals(PhaseId.RENDER_RESPONSE);
    }

    public static boolean isAjaxRequest() {
        return FacesContext.getCurrentInstance().getPartialViewContext().isAjaxRequest();
    }

}
