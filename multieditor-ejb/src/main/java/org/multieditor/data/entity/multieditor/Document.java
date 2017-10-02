package org.multieditor.data.entity.multieditor;

import org.multieditor.data.entity.NamedEntity;
import org.multieditor.data.info.multieditor.DocumentSummary;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * User: nikita.zinoviev@gmail.com
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "version"}),
        @UniqueConstraint(columnNames = {"name", "previousVersion"})})
@NamedQueries({
        @NamedQuery(name = Document.FIND_BY_NAME_AND_VERSION,
                query = "   SELECT document FROM Document document "
                        + " WHERE document.name = :" + Document.PARAM_NAME
                        + " AND document.version = :" + Document.PARAM_VERSION),
        @NamedQuery(name = Document.FIND_VERSIONS_BY_NAME,
                query = "   SELECT document from Document document "
                        + " WHERE document.name = :" + Document.PARAM_NAME),
        @NamedQuery(name = Document.FIND_BY_NAME,
                query = "   SELECT document from Document document "
                        + " WHERE document.name = :" + Document.PARAM_NAME
                        + " AND NOT EXISTS "
                        + " ( SELECT next FROM Document next "
                        + "   WHERE next.name=document.name "
                        + "   AND (next.previousVersion = document.version)"
                        + " )"),
        @NamedQuery(name = Document.FIND_ALL,
                query = "   SELECT document FROM Document document "
                        + " WHERE NOT EXISTS "
                        + " ( SELECT next FROM Document next "
                        + "   WHERE next.name=document.name "
                        + "   AND (next.previousVersion = document.version) "
                        + " ) ORDER BY document.name"),
        @NamedQuery(name = Document.FIND_NAMES_CHANGED_BY_USER,
                query = "   SELECT DISTINCT document.name FROM Document document "
                        + " WHERE document.ownerOfChange.name = :" + Document.PARAM_NAME
                        + " ORDER BY document.name")
})
public class Document implements Serializable, NamedEntity {
    public static final String PARAM_NAME = "name";
    public static final String PARAM_VERSION = "version";
    private static final long serialVersionUID = 1L;
    private static final String QUERY_PREFIX = "multieditor.document.";
    public static final String FIND_BY_NAME = QUERY_PREFIX + "findByName";
    public static final String FIND_BY_NAME_AND_VERSION = QUERY_PREFIX + "findByNameAndVersion";
    public static final String FIND_NAMES_CHANGED_BY_USER = QUERY_PREFIX + "findNamesChangedByUser";
    public static final String FIND_ALL = QUERY_PREFIX + "findAll";
    public static final String FIND_VERSIONS_BY_NAME = QUERY_PREFIX + "findVersionsByName";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    //    @Column(nullable = false, unique = true)
    private String name;

    @Basic(optional = false)
    private String description;

    @Lob
    @Basic(optional = false)
    private String contents;

    @Basic(optional = false)
    private String version;

    // these two are really symmetric
    @Basic(optional = false)
    private String previousVersion;

    // this one is empty more often
    @Basic(optional = false)
    private String mergedWith;

    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = false, fetch = FetchType.EAGER)
    private UserAccount ownerOfChange;


    public Document() {
    }

    public Document(DocumentSummary documentSummary, UserAccount ownerOfChange) {
        updateFrom(documentSummary, ownerOfChange);
    }

    public DocumentSummary toDocumentSummary() {
        return new DocumentSummary(name, description, contents, version, previousVersion, mergedWith, ownerOfChange.toUserAccountSummary());
    }


    /**
     * to be called only from DocumentControllerBean
     */
    private void updateFrom(DocumentSummary summary, UserAccount ownerOfChange) {
        this.name = summary.getName();
        this.description = summary.getDescription();
        this.contents = summary.getContents();
        this.version = summary.getVersion();
        this.ownerOfChange = ownerOfChange;
        this.previousVersion = summary.getPreviousVersion();
        this.mergedWith = summary.getMergedWith();
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

    public UserAccount getOwnerOfChange() {
        return ownerOfChange;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public String getMergedWith() {
        return mergedWith;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Document document = (Document) o;

        if (name != null ? !name.equals(document.name) : document.name != null) {
            return false;
        }
        if (version != null ? !version.equals(document.version) : document.version != null) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
