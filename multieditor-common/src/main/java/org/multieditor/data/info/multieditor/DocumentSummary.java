package org.multieditor.data.info.multieditor;

import org.multieditor.data.entity.NamedEntity;

import java.io.Serializable;

public class DocumentSummary implements Serializable, NamedEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String contents;

    private String version;

    private String previousVersion;

    private String mergedWith;

    private UserAccountSummary ownerOfChange;


    public DocumentSummary() {
    }

    public DocumentSummary(String name, String description, String contents, String version, String previousVersion, String mergedWith, UserAccountSummary ownerOfChange) {
        this.name = name;
        this.description = description;
        this.contents = contents;
        this.version = version;
        this.previousVersion = previousVersion;
        this.mergedWith = mergedWith;
        this.ownerOfChange = ownerOfChange;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContents() {
        return contents;
    }

    public String getVersion() {
        return version;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public String getMergedWith() {
        return mergedWith;
    }

    public UserAccountSummary getOwnerOfChange() {
        return ownerOfChange;
    }

    // todo may need to update equals and hashcode to include all the fields

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DocumentSummary that = (DocumentSummary) o;

        if (contents != null ? !contents.equals(that.contents) : that.contents != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
//        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (contents != null ? contents.hashCode() : 0);
//        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
