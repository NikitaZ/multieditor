package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.UserNotFoundException;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * todo
 * Problems: it is possible to edit desciption by creating a document over an existing one, but this will also overwrite its contents.
 * One may skip null or null and empty content updates or separate creation and update logic.
 *
 * @author nikita.zinoviev@gmail.com
 */
@Named("documentCreatePageBean")
@RequestScoped
public class DocumentCreatePageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient String documentName;

    private transient String description;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    @Inject
    @Named("userBean")
    private transient UserBean userBean;


    public DocumentCreatePageBean() {

    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getReadOnly() {
        return false; // no access restrictions in this app
    }

    public String cancel() {
        return "Documents";
    }

    public String save() {
        try {
            serviceBean.getDocumentService().createOrUpdate(getDocumentName(), getDescription(), "",
                    userBean.getUserName(), "0", 0);
        } catch (UserNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during save"));
        }

        return "Document";
    }


//    public String getDocumentNameViaUrlParameter() {
//        return getDocumentName();
//    }
//
//    // this method is always called unless above getter returns null (then it is just not called at all)!
//    public void setDocumentNameViaUrlParameter(String name) {
//        if (name != null) {
//            setDocumentName(name);
//        }
//
//    }


}