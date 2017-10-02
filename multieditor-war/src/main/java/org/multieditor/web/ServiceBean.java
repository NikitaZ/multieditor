package org.multieditor.web;

import org.multieditor.client.DocumentServiceClient;
import org.multieditor.data.services.multieditor.DocumentService;
import org.multieditor.data.services.multieditor.UserManagerLocal;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Application scope data bean for the application.
 * An instance of this class is created automatically, the first
 * time application evaluates a value binding expression or method binding
 * expression that references a managed bean using this class.
 */
@Named("ServiceBean")
@ApplicationScoped
public class ServiceBean {
    private DocumentService documentManager = new DocumentServiceClient();

//     this is a bit more performant than
//    private UserService userManager = new UserServiceClient();
    @EJB
    private UserManagerLocal userManager;

    public DocumentService getDocumentService() {
        return documentManager;
    }

    public UserManagerLocal getUserService() {
        return userManager;
    }

    public boolean getDevelopment() {
        return FacesContext.getCurrentInstance().getApplication().getProjectStage() == ProjectStage.Development;
    }
}