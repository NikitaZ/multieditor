package org.multieditor.rest.exception;

/**
 * Facade exception to represent the internal exception thrown
 * from a JAX-RS endpoint.
 */
public class MultieditorRemoteException extends RuntimeException {

    private String className;

    private StackTraceElement[] stackTrace;

    public MultieditorRemoteException(String message, String className, StackTraceElement[] stackTrace, Throwable cause) {
        super(message, cause);
        this.className = className;
        this.stackTrace = stackTrace;
    }

    @Override
    public String getMessage() {
        return className + ": " + getOriginalMessage();
    }

    public String getClassName() {
        return className;
    }

    public String getOriginalMessage() {
        return super.getMessage();
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public static MultieditorRemoteException createMultieditorRemoteException(MultieditorExceptionMessage message) {
        MultieditorExceptionMessage causeMessage = message.getCause();
        MultieditorRemoteException cause = (causeMessage != null) ? createMultieditorRemoteException(causeMessage) : null;
        return new MultieditorRemoteException(message.getMessage(), message.getExceptionType(), message.getStacktrace(), cause);
    }
}
