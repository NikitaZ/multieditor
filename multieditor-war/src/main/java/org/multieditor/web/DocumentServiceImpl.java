package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.info.multieditor.UpdateSummary;
import org.multieditor.data.services.multieditor.DocumentManagerLocal;
import org.multieditor.data.services.multieditor.DocumentService;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.List;
import java.util.logging.Logger;

/**
 * Exports DocumentManager as a JAX-RS service.
 */
@Path("/documentService")
//@ManagedBean
@ApplicationScoped
public class DocumentServiceImpl implements DocumentService {
    private static final int RETRIES = 20;
    private static final Logger LOGGER = Logger.getLogger(DocumentServiceImpl.class.getName());

    /*  * Implementation note:
        * we use com.sun.faces.push.WebsocketPushContext to send DocumentUpdateMessage to all the connected clients.
        * It encodes the given message object as JSON and sends it to all open web socket sessions associated with given web
        * socket channel identifier.
        *
        * We could do it manually via JavaEE 7 Websockets instead and send updates only to clients editing corresponding document
        * (by iterating over all the existing connections)
     */
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    @Push(channel = "multieditorJSFWebsocketChannel")
    private PushContext jsfWebsocketPushContext;

    @EJB
    private DocumentManagerLocal documentManager;

    public static class DocumentUpdateMessage {
        private String documentName;
        private String version;

        public DocumentUpdateMessage() {
        }

        public DocumentUpdateMessage(String documentName, String version) {
            this.documentName = documentName;
            this.version = version;
        }

        public String getDocumentName() {
            return documentName;
        }

        public String getVersion() {
            return version;
        }
    }

    public UpdateSummary createOrUpdate(String name, String description, String contents, String userName,
                                        String previousVersion, int cursorPosition) throws UserNotFoundException {

        Throwable last = null;
        for (int t = 0; t < RETRIES; t++) {
            try {
                final UpdateSummary updateSummary = documentManager.createOrUpdate(name, description, contents, userName, previousVersion, cursorPosition);
                final DocumentSummary document = updateSummary.getDocument();
                // it does not distiguish between update and refresh from an old version
                if (!previousVersion.equals(document.getVersion())) {
                    // notify about update
                    jsfWebsocketPushContext.send(new DocumentUpdateMessage(document.getName(), document.getVersion()));
                }
                return updateSummary;
            } catch (UserNotFoundException u) {
                throw u;
            } catch (Throwable e) {
                LOGGER.warning("createOrUpdate: Exception occurred on step " + t + " " + e.getMessage());
                last = e;
//                doesn't make it any better, even worse - kept this here to show that we tried it
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ignored) {
//
//                }
            }
        }
        throw new EJBException("createOrUpdate failed for " + RETRIES + " time", new Exception(last));
    }

    @Override
    public void delete(String documentName) throws DocumentNotFoundException {
        documentManager.delete(documentName);
    }

    @Override
    public DocumentSummary findByName(String documentName) {
        return documentManager.findByName(documentName);
    }

    @Override
    public List<DocumentSummary> findAll() {
        return documentManager.findAll();
    }

    @Override
    public List<String> findNamesChangedByUser(String userName) throws UserNotFoundException {
        return documentManager.findNamesChangedByUser(userName);
    }

}
