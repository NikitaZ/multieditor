package org.multieditor.client;

import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.info.multieditor.UpdateSummary;
import org.multieditor.data.services.multieditor.DocumentService;
import org.multieditor.rest.client.AbstractRestClient;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.multieditor.ObjectUtils.emptyIfNull;
import static org.multieditor.client.ClientProperties.SERVICE_URL_PROP;

/**
 * JAX-RS client to DocumentManagerService
 */
public class DocumentServiceClient extends AbstractRestClient implements DocumentService {
    private static final String SERVICE_URI = "/multieditor/rest/documentService/";

    private static final String URL = ClientProperties.getProperty(SERVICE_URL_PROP, "http://localhost:13000");

    private static final GenericType<List<DocumentSummary>> DOCUMENT_LIST =
            new GenericType<List<DocumentSummary>>() {
            };

    public DocumentServiceClient() {
        super(URL, SERVICE_URI);
    }

    public DocumentServiceClient(String url) {
        super(url, SERVICE_URI);
    }

    @Override
    public UpdateSummary createOrUpdate(String name, String description, String contents, String userName, String previousVersion, int cursorPosition) throws UserNotFoundException {
        Form formData = new Form();
        formData.param("name", name);
        formData.param("description", description);
        formData.param("contents", contents);
        formData.param("user", userName);
        formData.param("previousVersion", previousVersion);
        formData.param("cursorPosition", String.valueOf(cursorPosition));
        try {
            return buildWebTarget("createOrUpdate")
                    .request().accept(MediaType.APPLICATION_JSON_TYPE)
                    .post(Entity.form(formData), UpdateSummary.class);
        } catch (ProcessingException exception) {
            throw checkedException(exception, UserNotFoundException.class);
        }
    }

    @Override
    public void delete(String documentName) throws DocumentNotFoundException {
        try {
            Response resp = buildWebTarget("delete")
                    .queryParam("name", documentName)
                    .request()
                    .delete();
            checkSuccessful(resp);
        } catch (ProcessingException exception) {
            throw checkedException(exception, DocumentNotFoundException.class);
        }

    }

    @Override
    public DocumentSummary findByName(String documentName) {
        return buildWebTarget("name/" + escapePathParam(documentName))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(DocumentSummary.class);
    }

    @Override
    public List<DocumentSummary> findAll() {
        return emptyIfNull(buildWebTarget("listAll")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(DOCUMENT_LIST));
    }

    @Override
    public List<String> findNamesChangedByUser(String userName) throws UserNotFoundException {
        try {
            return buildWebTarget("namesByUser").queryParam("user", userName)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get(STRING_LIST);
        } catch (ProcessingException exception) {
            throw checkedException(exception, UserNotFoundException.class);
        }
    }

}
