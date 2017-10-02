package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.services.multieditor.DocumentService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.Serializable;


/**
 * @author nikita.zinoviev@gmail.com
 */
@Named("documentPageBean")
@ViewScoped //todo change to conversational scope
public class DocumentPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String documentName;

    private String previousVersion = "0";

    private String description;

    private transient String contents;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    @Inject
    @Named("userBean")
    private transient UserBean userBean;


    public DocumentPageBean() {
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public String getContents() {
        return contents;
    }

    public void contentsValueChange(ValueChangeEvent event) {
        this.contents = event.getNewValue().toString();

        final DocumentService documentService = serviceBean.getDocumentService();
        final String mergedContents;
        try {
            mergedContents = documentService.createOrUpdate(getDocumentName(), getDescription(), getContents(),
                    userBean.getUserName(), previousVersion, 0).getDocument().getContents();
            this.contents = mergedContents;
        } catch (UserNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during update"));
        }
    }

    public void setContents(String contents) {
//        this.contents = contents;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocumentNameViaUrlParameter() {
        return null;
    }

    // this method is always called unless above getter returns null (then it is just not called at all)!
    public void setDocumentNameViaUrlParameter(String name) {
        if (name != null) {
            loadDocument(name);
        }

    }

    private void loadDocument(String documentName) {
        setDocumentName(documentName);
        DocumentSummary document = serviceBean.getDocumentService().findByName(documentName);
        if (document != null) {
            setDescription(document.getDescription());
            this.contents = document.getContents();
            this.previousVersion = document.getVersion();
        }
    }

    /**
     * Action method, deletes document corresponding to the selected documentName
     *
     * @return
     */
    public String deleteDocument() {
        try {
            serviceBean.getDocumentService().delete(documentName);
        } catch (DocumentNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during delete"));
        }
        return "Documents";
    }

    public String saveDocument() throws JAXBException {
//        saved during contentsValueChange
//        final DocumentService documentService = serviceBean.getDocumentService();
//        final String mergedContents = documentService.createOrUpdate(getDocumentName(), getDescription(), getContents(),
//                userBean.getUserName(), previousVersion);
//        this.contents = mergedContents;
        return null;
    }

    public boolean getReadOnly() {
        return false; // no access restrictions in this app
    }

    private void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getSavedAt() {
        return "" + System.currentTimeMillis();
    }

    public String refresh() {
        loadDocument(getDocumentName());
        return null;
    }
}

//import javax.faces.bean.ViewScoped;
