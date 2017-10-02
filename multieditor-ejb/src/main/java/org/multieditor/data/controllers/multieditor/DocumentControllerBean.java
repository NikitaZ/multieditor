package org.multieditor.data.controllers.multieditor;

import org.multieditor.data.entity.multieditor.Document;
import org.multieditor.data.entity.multieditor.UserAccount;
import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.services.multieditor.UserManagerLocal;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Stateless
public class DocumentControllerBean implements DocumentControllerLocal {

    @PersistenceContext(name = "multieditorDB")
    private EntityManager em;

    @EJB
    private UserManagerLocal userManager;


    @Override
    public Document findByName(String name) {
        List<Document> resultList;
        for (int i = 0; i < 10; i++) {
            resultList = em.createNamedQuery(Document.FIND_BY_NAME, Document.class).setParameter(Document.PARAM_NAME, name).getResultList();

            if (resultList.isEmpty()) {
                return null;
            }

            if (resultList.size() == 1) {
                return resultList.get(0);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        throw new EJBException(" DB integrity violation, many times looked up more than one Document for name " + name);
    }

    @Override
    public Document findByNameAndVersion(String name, String version) {
        List<Document> resultList = em.createNamedQuery(Document.FIND_BY_NAME_AND_VERSION, Document.class)
                .setParameter(Document.PARAM_NAME, name)
                .setParameter(Document.PARAM_VERSION, version)
                .getResultList();

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            throw new EJBException(" DB integrity violation, looked up more than one Document for name " + name + " and version " + version);
        }

        return resultList.get(0);
    }

    /**
     * Finds names of documents which were edited by a given user
     *
     * @param userName name of the user to look for
     * @return names of documents which were changed by the given user
     */
    @Override
    public List<String> findNamesChangedByUser(String userName) {

        return em.createNamedQuery(Document.FIND_NAMES_CHANGED_BY_USER, String.class)
                .setParameter(Document.PARAM_NAME, userName)
                .getResultList();
    }

    private List<Document> findAllVersionsByName(String documentName) {

        return em.createNamedQuery(Document.FIND_VERSIONS_BY_NAME, Document.class)
                .setParameter(Document.PARAM_NAME, documentName)
                .getResultList();
    }


    /**
     * Looks for document with a given name
     *
     * @param name of the document to search for
     * @return Does not return null but throws exception
     * @throws DocumentNotFoundException if document with a given name cannot be found
     */
    @Override
    public Document checkedFindByName(String name) throws DocumentNotFoundException {
        Document document = findByName(name);

        if (document == null) {
            throw new DocumentNotFoundException(name);
        }

        return document;
    }


    @Override
    public List<Document> findAll() {
        return em.createNamedQuery(Document.FIND_ALL, Document.class).getResultList();
    }

    @Override
    public void createOrUpdate(DocumentSummary documentSummary) throws UserNotFoundException {
        UserAccount ownerOfChange = userManager.checkedFindUserByName(documentSummary.getOwnerOfChange().getName());
        Document dbDocument = findByNameAndVersion(documentSummary.getName(), documentSummary.getVersion());
        if (dbDocument == null) {
            em.persist(new Document(documentSummary, ownerOfChange));
        } else {
            throw new EJBException(" DB integrity violation, found an existing document for name " + documentSummary.getName()
                    + " and version " + documentSummary.getVersion());
//            dbDocument.updateFrom(documentSummary, ownerOfChange);
        }
    }

    /**
     * Deletes document with given name if one exists
     *
     * @param documentName to delete
     * @throws DocumentNotFoundException
     */
    public void delete(String documentName) throws DocumentNotFoundException {
        List<Document> versions = findAllVersionsByName(documentName);
        if (versions.isEmpty()) {
            throw new DocumentNotFoundException(documentName);
        }
        versions.forEach(em::remove);
    }

    public boolean authorizeAccess(String userName, String documentName) throws UserNotFoundException {

        Document document = findByName(documentName);
        UserAccount user = userManager.checkedFindUserByName(userName);
        return true;

    }
}
