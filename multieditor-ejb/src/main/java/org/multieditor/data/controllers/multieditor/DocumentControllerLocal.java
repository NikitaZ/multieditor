package org.multieditor.data.controllers.multieditor;

import org.multieditor.data.entity.multieditor.Document;
import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;

import javax.ejb.Local;
import java.util.List;

@Local
public interface DocumentControllerLocal {
    void createOrUpdate(DocumentSummary documentSummary) throws UserNotFoundException;

    Document findByName(String name);

    Document findByNameAndVersion(String name, String version);

    void delete(String documentName) throws DocumentNotFoundException;

    Document checkedFindByName(String name) throws DocumentNotFoundException;

    List<Document> findAll();

    List<String> findNamesChangedByUser(String userName);

    boolean authorizeAccess(String userName, String documentName) throws UserNotFoundException;
}
