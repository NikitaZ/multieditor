package org.multieditor.rest.client;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.multieditor.rest.exception.MultieditorRemoteException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract base JAX-RS client
 */
public abstract class AbstractRestClient {

    private static final Logger LOGGER = Logger.getLogger(AbstractRestClient.class.getName());

    private static final Client HTTP_CLIENT =
            ClientBuilder.newBuilder()
                    .register(MultieditorJSONMapper.class)
                    .register(JacksonFeature.class)
                    .register(ResponseErrorFilter.class)
                    .build();

    private static final Client DEBUGGING_HTTP_CLIENT =
            ClientBuilder.newBuilder()
                    .register(MultieditorJSONMapper.class)
                    .register(JacksonFeature.class)
                    .register(ResponseErrorFilter.class)
                    .register(new LoggingFilter(LOGGER, true))
                    .build();

    protected static final GenericType<Set<String>> STRING_SET = new GenericType<Set<String>>() {
    };

    protected static final GenericType<List<String>> STRING_LIST = new GenericType<List<String>>() {
    };

    protected static final GenericType<Map<String, String>> KEY_VALUE_MAP = new GenericType<Map<String, String>>() {
    };

    protected static final GenericType<LinkedHashSet<String>> STRING_ORDERED_SET
            = new GenericType<LinkedHashSet<String>>() {
    };

    private final String target;

    private final String basePath;

    public AbstractRestClient(String target, String basePath) {
        // ensure //'s are removed from the URI while preserving http(s)://
        this.target = target;
        this.basePath = basePath;
    }

    private Client client = HTTP_CLIENT;

    public void setDebug(boolean debug) {
        if (debug) {
            Logger.getLogger("com.sun.jersey").setLevel(Level.FINEST);
            client = DEBUGGING_HTTP_CLIENT;
        } else {
            client = HTTP_CLIENT;
        }
    }

    public WebTarget buildWebTarget(String path) {
        LOGGER.log(Level.FINE, "Setting up REST call to {0}{1}{2}", new Object[]{target, basePath, path});
        return client.target(target).path(basePath + path);
    }

    protected UnsupportedOperationException unsupported() {
        try {
            throw new UnsupportedOperationException("Operation not supported yet.");
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(AbstractRestClient.class.getName()).log(Level.SEVERE, "Unsupported", ex);
            throw ex;
        }
    }

    public static Response redirect(String uri) {
        try {
            return Response.seeOther(new URI(uri)).build();
        } catch (URISyntaxException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public static String escapePathParam(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0) {
            return true;
        }
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }

    public static String encodeQueryParam(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(AbstractRestClient.class.getName())
                    .severe("UTF-8 input can never be unsupported.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Convenience utility to try and convert an UniformInterfaceException back to the checked
     * exception that caused it.
     * <p/>
     * Use the one most appropriate for the use case, for example:
     * <code>
     * try {
     * buildWebTarget(hostName + "/reserve/" + host + "/user/" + user).post();
     * } catch (UniformInterfaceException ex) {
     * throw checkedException(ex, HostNotFoundException.class, UserNotFoundException.class);
     * }
     * </code>
     *
     * @param exception the UniformInterfaceException thrown
     * @return the exception, in case it couldn't be mapped to an exception
     * @throws T
     */
    public <T extends Exception>
    ProcessingException checkedException(ProcessingException exception,
                                         Class<T> clazz) throws T {
        internalCheckAndThrow(exception, clazz);
        return exception;
    }

    public <T extends Exception, U extends Exception>
    ProcessingException checkedException(ProcessingException exception,
                                         Class<T> clazz, Class<U> clazz2) throws T, U {
        internalCheckAndThrow(exception, clazz);
        internalCheckAndThrow(exception, clazz2);
        return exception;
    }

    public <T extends Exception, U extends Exception, V extends Exception>
    ProcessingException checkedException(ProcessingException exception,
                                         Class<T> clazz, Class<U> clazz2, Class<V> clazz3) throws T, U, V {
        internalCheckAndThrow(exception, clazz);
        internalCheckAndThrow(exception, clazz2);
        internalCheckAndThrow(exception, clazz3);
        return exception;
    }

    public <T extends Exception, U extends Exception, V extends Exception, A extends Exception>
    ProcessingException checkedException(ProcessingException exception,
                                         Class<T> clazz, Class<U> clazz2, Class<V> clazz3, Class<A> clazz4) throws T, U, V, A {
        internalCheckAndThrow(exception, clazz);
        internalCheckAndThrow(exception, clazz2);
        internalCheckAndThrow(exception, clazz3);
        internalCheckAndThrow(exception, clazz4);
        return exception;
    }

    public <T extends Exception, U extends Exception, V extends Exception, A extends Exception, B extends Exception>
    ProcessingException checkedException(ProcessingException exception,
                                         Class<T> clazz, Class<U> clazz2, Class<V> clazz3, Class<A> clazz4, Class<B> clazz5) throws T, U, V, A, B {
        internalCheckAndThrow(exception, clazz);
        internalCheckAndThrow(exception, clazz2);
        internalCheckAndThrow(exception, clazz3);
        internalCheckAndThrow(exception, clazz4);
        internalCheckAndThrow(exception, clazz5);
        return exception;
    }

    private <T extends Exception>
    void internalCheckAndThrow(ProcessingException exception, Class<T> clazz) throws T {
        if (exception.getCause() instanceof MultieditorRemoteException) {
            MultieditorRemoteException remoteException = (MultieditorRemoteException) exception.getCause();
            if (clazz.getName().equals(remoteException.getClassName())) {
                try {
                    T e = clazz.newInstance();
                    Field detailMessage = Throwable.class.getDeclaredField("detailMessage");
                    detailMessage.setAccessible(true);
                    detailMessage.set(e, remoteException.getOriginalMessage());
                    e.initCause(remoteException);
                    throw e;
                } catch (InstantiationException | IllegalAccessException | NoSuchFieldException failedConversion) {
                    // throw the original exception
                    LOGGER.log(Level.SEVERE, "Failed conversion: " + failedConversion.getMessage() + " message: " + remoteException, failedConversion);
                    throw remoteException;
                }
            }
        } else {
            LOGGER.log(Level.SEVERE, "Infrastructural processing exception caught", exception);
            throw exception;
        }
    }

    /**
     * Used by POST methods that returns nothing to check the server response
     */
    public static void checkSuccessful(Response resp) {
        int status = resp.getStatus();

        if ((status != Response.Status.OK.getStatusCode()) &&
                (status != Response.Status.CREATED.getStatusCode()) &&
                (status != Response.Status.ACCEPTED.getStatusCode()) &&
                (status != Response.Status.NO_CONTENT.getStatusCode()) &&
                (status != Response.Status.RESET_CONTENT.getStatusCode()) &&
                (status != Response.Status.PARTIAL_CONTENT.getStatusCode())) {
            throw new ProcessingException(resp.toString());
        }
    }
}
