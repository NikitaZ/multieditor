package org.multieditor.data.exceptions.multieditor;

public class DocumentNotFoundException extends Exception {

    public DocumentNotFoundException() {
        super();
    }

    public DocumentNotFoundException(String name) {
        super("Cannot find Document with name '" + name + "'");
    }
}