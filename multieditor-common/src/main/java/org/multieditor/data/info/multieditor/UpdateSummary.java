package org.multieditor.data.info.multieditor;

import org.multieditor.data.entity.NamedEntity;

import java.io.Serializable;

public class UpdateSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private DocumentSummary document;

    // extra fields for the user UI

    private String savedVersion;

    private int cursorCorrection = 0;

    public UpdateSummary() {
    }

    public UpdateSummary(DocumentSummary document, String savedVersion, int cursorCorrection) {
        this.document = document;
        this.savedVersion = savedVersion;
        this.cursorCorrection = cursorCorrection;
    }

    public DocumentSummary getDocument() {
        return document;
    }

    public String getSavedVersion() {
        return savedVersion;
    }

    public int getCursorCorrection() {
        return cursorCorrection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateSummary that = (UpdateSummary) o;

        if (cursorCorrection != that.cursorCorrection) return false;
        if (document != null ? !document.equals(that.document) : that.document != null) return false;
        return savedVersion != null ? savedVersion.equals(that.savedVersion) : that.savedVersion == null;
    }

    @Override
    public int hashCode() {
        int result = document != null ? document.hashCode() : 0;
        result = 31 * result + (savedVersion != null ? savedVersion.hashCode() : 0);
        result = 31 * result + cursorCorrection;
        return result;
    }
}
