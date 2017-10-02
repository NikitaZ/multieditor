package org.multieditor.web;

import org.multieditor.data.info.multieditor.DocumentSummary;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * @author nikita.zinoviev@gmail.com
 * This is requestScoped bean so tricky 'queried' logic is not needed, compare to DocumentsEditPageBean.
 */
@Named("documentsPageBean")
@RequestScoped
public class DocumentsPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient List<DocumentSummary> documents;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    public List<DocumentSummary> getDocuments() {
        if (documents == null) {
            documents = serviceBean.getDocumentService().findAll();
        }
        return documents;
    }

    public String refresh() {
        return null;
    }

}