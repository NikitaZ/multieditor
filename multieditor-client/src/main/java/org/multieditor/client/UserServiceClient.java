package org.multieditor.client;

import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;
import org.multieditor.data.services.multieditor.UserService;
import org.multieditor.rest.client.AbstractRestClient;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.multieditor.ObjectUtils.emptyIfNull;

/**
 * JAX-RS client to UserManagerService
 */
public class UserServiceClient extends AbstractRestClient implements UserService {
    private static final String SERVICE_URI = "/multieditor/rest/userService/";

    private static final String URL = ClientProperties.getProperty(ClientProperties.SERVICE_URL_PROP, "http://localhost:13000");

    private static final GenericType<List<UserAccountSummary>> ACCOUNT_LIST = new GenericType<List<UserAccountSummary>>() {
    };

    public UserServiceClient() {
        super(URL, SERVICE_URI);
    }

    public UserServiceClient(String url) {
        super(url, SERVICE_URI);
    }


    @Override
    public UserAccountSummary createOrUpdate(String name, String fullName, String email, String colour) {
        Form formData = new Form();
        formData.param("name", name);
        formData.param("fullName", fullName);
        formData.param("email", email);
        formData.param("colour", colour);
        return buildWebTarget("createOrUpdate")
                .request(MediaType.APPLICATION_JSON_TYPE)
//                .post(Entity.entity(useraccount, MediaType.APPLICATION_JSON_TYPE), UserAccountSummary.class);
                .post(Entity.form(formData), UserAccountSummary.class);
    }

    @Override
    public void delete(String userName) throws UserNotFoundException {
        try {
            Response resp = buildWebTarget("delete")
                    .queryParam("name", userName)
                    .request()
                    .delete();
            checkSuccessful(resp);
        } catch (ProcessingException exception) {
            throw checkedException(exception, UserNotFoundException.class);
        }
    }

    @Override
    public UserAccountSummary findByName(String name) {
        return buildWebTarget("name/" + escapePathParam(name)).request(MediaType.APPLICATION_JSON_TYPE)
                .get(UserAccountSummary.class);
    }

    @Override
    public List<UserAccountSummary> findAll() {
        return emptyIfNull(buildWebTarget("listAll").request(MediaType.APPLICATION_JSON_TYPE)
                .get(ACCOUNT_LIST));
    }
}
