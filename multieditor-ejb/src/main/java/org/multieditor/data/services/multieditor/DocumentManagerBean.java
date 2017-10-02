package org.multieditor.data.services.multieditor;

import org.multieditor.data.controllers.multieditor.DocumentControllerLocal;
import org.multieditor.data.entity.multieditor.Document;
import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.info.multieditor.UpdateSummary;
import org.multieditor.data.info.multieditor.UserAccountSummary;
import org.multieditor.data.merge.MergeResult;
import org.multieditor.data.merge.Merger;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class DocumentManagerBean implements DocumentManagerLocal {
    private static final Logger LOGGER = Logger.getLogger(DocumentManagerBean.class.getName());
    @EJB
    private DocumentControllerLocal documentController;
    @EJB
    private UserManagerLocal userManager;

    private long milisStartns = System.currentTimeMillis() * 1000_000; // convert to ns
    private long nanoStart = System.nanoTime();

    private long currentTimeNano() {
        return System.nanoTime() - nanoStart + milisStartns;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public UpdateSummary createOrUpdate(String name, String description, String contents, String userName,
                                        String previousVersion, int cursorPosition) throws UserNotFoundException {
        // 'savedVersion' allows to deal with the case when document was modified on the client during request
        // we pass 'savedVersion' only to instruct client that it's data has been saved and that client's version label should be updated
        // otherwise client shouldn't update the version label unless it shows new data to the user

        // due to name/previousVersion constraint inserts fail if the head changes

        LOGGER.log(Level.INFO, "createOrUpdate: {0}, description: {1}, userName: {2}, previousVersion: {3}, length: {4}",
                new Object[]{name, description, userName, previousVersion, contents.length()});

        final Document dbDocumentHead = documentController.findByName(name);
        final UserAccountSummary ownerOfChange = userManager.checkedFindUserSummaryByName(userName);

        // create first version of the document
        if (dbDocumentHead == null) {
            final String newVersion = generateNewVersion();
            final DocumentSummary documentSummary = new DocumentSummary(name, description, contents, newVersion,
                    "",
                    "",
                    ownerOfChange);
            documentController.createOrUpdate(documentSummary); // always creates
            return new UpdateSummary(documentSummary, newVersion, 0);
        }

        final Document dbDocumentPrevious =  // fast lookup, make later == comparison legal
                dbDocumentHead.getVersion().equals(previousVersion) ? dbDocumentHead :
                        documentController.findByNameAndVersion(name, previousVersion);

        if (dbDocumentPrevious == null) {
            throw new EJBException("Illegal state: unable to find document with name " + name + " and version " + previousVersion + " while head document exist.");
        }

        // a simplification: if the description has changed we skip contents update, otherwise code gets too unreadable
        // probably we should have a separate method for creation and for description update
        // since description is usually set when document is created and never changed again it doesn't matter much
        // also our main client does not allow to update description
        // if it equals previous description - skip the update
        if (!description.equals(dbDocumentHead.getDescription()) && !description.equals(dbDocumentPrevious.getDescription())) {
            final String newVersion = generateNewVersion();
            final DocumentSummary newDocumentSummary = new DocumentSummary(name, description, dbDocumentHead.getContents(), newVersion,
                    dbDocumentHead.getVersion(),
                    "",
                    ownerOfChange);
            documentController.createOrUpdate(newDocumentSummary);  // always creates
            return new UpdateSummary(newDocumentSummary, "", 0);
        }
        // proceed as if description is the same


        // this could be a coincidence -  we pass head.version as the version to save
        if (dbDocumentHead.getContents().equals(contents)) {
            return new UpdateSummary(dbDocumentHead.toDocumentSummary(), dbDocumentHead.getVersion(), 0);
        }

        // no conflicting changes => save the new contents, no merge needed
        if (dbDocumentHead == dbDocumentPrevious) {
            final String newVersion = generateNewVersion();
            final DocumentSummary newDocumentSummary = new DocumentSummary(name, description, contents, newVersion,
                    dbDocumentHead.getVersion(),
                    "",
                    ownerOfChange);
            documentController.createOrUpdate(newDocumentSummary);  // always creates
            return new UpdateSummary(newDocumentSummary, newVersion, 0);
        }

        // no changes w.r.t. saved data hence head should be returned  as it is probably an old update seen once again
        if (dbDocumentPrevious.getContents().equals(contents)) {
            final int cursorCorrection = Merger.getMerger().merge(
                    contents, dbDocumentHead.getContents(), contents, cursorPosition).getCursorCorrection();
            return new UpdateSummary(dbDocumentHead.toDocumentSummary(), "", cursorCorrection);
        }


        // Question: do we need to save both user's version and merged version or just the merged one??
        // the thing is that only merged version will be seen to the user and hence nobody will be able to tell that
        // user's version is a previous one. Although it can be found from 'previousVersion' or 'mergedWith'
        // (depending on where we store it) - the user would be able to request a merge with exactly this change
        // (incoming contents, not merged ones) - this allows to deal gracefully with the case when user continued editing
        // in parallel with the update request - but at the same time allows other user to see the change merged with head
        // hence reducing granularity of changes

        // this allows to avoid the need to sometimes merge on the client side too
        // as well as client merge won't distract the user actively editing the document
        // the user would see the changes one user pauses long enough for the request to go to the server and come back
        // with merged contents.

        final String newVersion = generateNewVersion();
        // this insert never fails and never is a head as it is a previousVersion of itself
        final DocumentSummary newNonMergedDocumentSummary = new DocumentSummary(name, description, contents, newVersion,
                newVersion,  // this should never fail
                dbDocumentPrevious.getVersion(),
                ownerOfChange);
        documentController.createOrUpdate(newNonMergedDocumentSummary);  // always creates


        final String mergedVersion = generateSecondNewVersion();

        LOGGER.log(Level.INFO, "Merging name: {0}, previousVersion: {1}, head: {2}, newNonMerged: {3}",
                new Object[]{dbDocumentHead.getName(), dbDocumentPrevious.getVersion(), dbDocumentHead.getVersion(), newNonMergedDocumentSummary.getVersion()});
        final MergeResult mergeResult = Merger.getMerger().merge(dbDocumentPrevious.getContents(), dbDocumentHead.getContents(), newNonMergedDocumentSummary.getContents(),
                cursorPosition);

        final DocumentSummary mergedDocumentSummary = new DocumentSummary(name, description, mergeResult.getMergedContents(), mergedVersion,
                dbDocumentHead.getVersion(),
                newNonMergedDocumentSummary.getVersion(),
                ownerOfChange);  // todo pass owner of head here too
        documentController.createOrUpdate(mergedDocumentSummary); // always creates
        return new UpdateSummary(mergedDocumentSummary, newNonMergedDocumentSummary.getVersion(), mergeResult.getCursorCorrection());
    }

    private String generateNewVersion() {
        // todo - use id generator instead of nanoTime
        // but really isn't it practically impossible
        // to get a clash here? and then, the transaction will rollback due to constraint violation

        return Long.toString(currentTimeNano());
    }

    // for sure differs from the one generated by previous method, probably, it is better to have a
    // method generating two new versions instead
    private String generateSecondNewVersion() {

        return Long.toString(currentTimeNano() + 1L);
    }

    @Override
    public void delete(String documentName) throws DocumentNotFoundException {
        documentController.delete(documentName);
    }

    @Override
    public DocumentSummary findByName(String documentName) {
        return wrap(documentController.findByName(documentName));
    }

    @Override
    public List<DocumentSummary> findAll() {
        List<Document> documents = documentController.findAll();
        return convertToDocumentSummary(documents);
    }

    @Override
    public List<String> findNamesChangedByUser(String userName) throws UserNotFoundException {
        return documentController.findNamesChangedByUser(userName);
    }

    private /*static*/ List<DocumentSummary> convertToDocumentSummary(List<Document> documents) {
        return documents.stream().map(Document::toDocumentSummary).collect(Collectors.toList());
    }

    private /*static*/ DocumentSummary wrap(Document document) {
        return document != null ? document.toDocumentSummary() : null;
    }
}
