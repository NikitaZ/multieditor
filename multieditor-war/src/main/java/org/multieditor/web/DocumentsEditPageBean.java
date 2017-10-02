package org.multieditor.web;

import org.multieditor.data.entity.NamedEntity;
import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.services.multieditor.DocumentService;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;
import java.io.Serializable;
import java.util.List;

/**
 * @author nikita.zinoviev@gmail.com
 */
//@ManagedBean("documentsEditPageBean") // does not work instead of @Named at least in case of JSF version="2.2" in faces-config.xml
@Named("documentsEditPageBean")
@ViewScoped   //todo change to conversational scope ? need an example of @ConversationScope and JSF
public class DocumentsEditPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> documentNames;

    private transient String documentName;

    private transient String description;

    private transient String contents;

    private transient String selectedDocumentName;

    private transient boolean queriedOnRender = false;

    private String previousVersion = "0";


    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    @Inject
    @Named("userBean")
    private transient UserBean userBean;

    public DocumentsEditPageBean() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getSelectedDocumentName() {
        return selectedDocumentName;
    }

    public void setSelectedDocumentName(String selectedDocumentName) {
        this.selectedDocumentName = selectedDocumentName;
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

    /**
     * Action method, loads data from the selected query into the form fields
     *
     * @return
     */
    public String editDocument() {
        if (selectedDocumentName != null) {
            loadDocument(selectedDocumentName);
        }
        return null;
    }

    private void loadDocument(String documentName) {
        setSelectedDocumentName(documentName);
        setDocumentName(documentName);
        DocumentSummary document = serviceBean.getDocumentService().findByName(documentName);
        if (document != null) {
            setDescription(document.getDescription());
            setContents(document.getContents());
            previousVersion = document.getVersion();
        }
    }

    /**
     * Action method, deletes document corresponding to the selected documentName
     *
     * @return
     */
    public String deleteDocument() {
        try {
            serviceBean.getDocumentService().delete(selectedDocumentName);
        } catch (DocumentNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during delete"));
        }
        return null;
    }

    public String saveDocument() throws JAXBException {
        final DocumentService documentService = serviceBean.getDocumentService();
        try {
            final DocumentSummary document = documentService.createOrUpdate(getDocumentName(), getDescription(), getContents(),
                    userBean.getUserName(), previousVersion, 0).getDocument();
            setContents(document.getContents());
            previousVersion = document.getVersion();
        } catch (UserNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during update"));
        }
        return null;
    }

    public List<String> getDocumentNames() {
        // we need to always requery documentNames on rendering as list of documents might be changed concurrently
        // even though queried is transient it is not reset between page requests after JSF 2.1.x
        // so we reset it here in non-rendering phases.
        // but if this is not a first request we need to keep old document list till rendering phase
        // so that view will be restored correctly
        if (!UtilityBean.isRendering()) {
            queriedOnRender = false;
        }
        if (documentNames == null || (UtilityBean.isRendering() && !queriedOnRender)) {
            documentNames = queryDocumentNames();
            // this same bean may change list of documents before rendering phase, so make sure we requery once on render
            if (UtilityBean.isRendering()) {
                queriedOnRender = true;
            }
        }
        return documentNames;
    }

    private List<String> queryDocumentNames() {
        return NamedEntity.convertNamedEntitiesToNamesList(serviceBean.getDocumentService().findAll());
    }

    public boolean getReadOnly() {
        return false; // no access restrictions in this app
    }

}