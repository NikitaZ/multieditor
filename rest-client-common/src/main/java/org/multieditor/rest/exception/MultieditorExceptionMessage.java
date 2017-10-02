package org.multieditor.rest.exception;

import java.util.Arrays;
import java.util.Objects;

/**
 * Simple DTO to handle passing of internal application exception
 * over REST in a uniform way.
 */
public class MultieditorExceptionMessage {

    private String message;

    private String exceptionType;

    private StackTraceElement[] stacktrace;

    private MultieditorExceptionMessage cause;

    public MultieditorExceptionMessage() {
    }

    public MultieditorExceptionMessage(String message, String exceptionType, StackTraceElement[] stacktrace, MultieditorExceptionMessage cause) {
        this.message = message;
        this.exceptionType = exceptionType;
        this.stacktrace = stacktrace;
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public StackTraceElement[] getStacktrace() {
        return stacktrace;
    }

    public MultieditorExceptionMessage getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "MultieditorExceptionMessage{" +
                "message='" + message + '\'' +
                ", exceptionType='" + exceptionType + '\'' +
                ", stacktrace='" + Arrays.toString(stacktrace) + '\'' +
                "', cause=" + Objects.toString(cause) + "}'";
    }
}
