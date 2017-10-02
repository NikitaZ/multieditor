package org.multieditor.rest.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.multieditor.rest.exception.MultieditorExceptionMessage;
import org.multieditor.rest.exception.MultieditorRemoteException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

import static javax.ws.rs.core.Response.Status.MOVED_PERMANENTLY;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

@Priority(Priorities.USER)
public class ResponseErrorFilter implements ClientResponseFilter {

    private ObjectReader appMessageReader;

    public ResponseErrorFilter() throws Exception {
        appMessageReader = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .reader(MultieditorExceptionMessage.class);
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) throws IOException {
        int status = clientResponseContext.getStatus();

        if (status != NO_CONTENT.getStatusCode()
                && status != OK.getStatusCode()
                && status != MOVED_PERMANENTLY.getStatusCode()) {
            MultieditorExceptionMessage message = null;
            try {
                message = appMessageReader.readValue(clientResponseContext.getEntityStream());
            } catch (Exception e) {
                // ignore
            }
            if (message != null) {
                throw MultieditorRemoteException.createMultieditorRemoteException(message);
            }
        }
    }
}
