package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.rest.exception.MultieditorExceptionMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class FrontendExceptionMappers {


    @Provider
    public static class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

        @Override
        public Response toResponse(UserNotFoundException e) {
            return buildResponse(Response.Status.NOT_FOUND, e);
        }
    }

    @Provider
    public static class GenericExceptionMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception e) {
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Provider
    public static class DocumentNotFoundExceptionMapper implements ExceptionMapper<DocumentNotFoundException> {
        @Override
        public Response toResponse(DocumentNotFoundException e) {
            return buildResponse(Response.Status.NOT_FOUND, e);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(FrontendExceptionMappers.class.getName());

    private static Response buildResponse(Response.Status code, Throwable e) {
        // Consider letting each Mapper define if the exception should be logged or not.

        // Limit verbosity of the common NOT_FOUND exceptions initially.
        LOGGER.log(Level.INFO, e.getMessage() + " - service returning HTTP " + code.getStatusCode());
        return Response.status(code)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(createExceptionMessage(e))
                .build();
    }

    private static MultieditorExceptionMessage createExceptionMessage(Throwable e) {
        Throwable cause = e.getCause();
        MultieditorExceptionMessage causeMessage = (cause != null) ? createExceptionMessage(cause) : null;
        return new MultieditorExceptionMessage(e.getMessage(), e.getClass().getName(), e.getStackTrace(), causeMessage);
    }

}
