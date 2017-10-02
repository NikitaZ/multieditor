package org.multieditor.data.entity.multieditor;

import org.multieditor.data.entity.NamedEntity;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@NamedQueries({@NamedQuery(name = UserAccount.FIND_BY_NAME,
        query = "select user from UserAccount user where user.name = :" + UserAccount.PARAM_NAME),
        @NamedQuery(name = UserAccount.FIND_ALL, query = "SELECT u from UserAccount u ORDER BY u.name")})
public class UserAccount implements Serializable, NamedEntity {
    private static final long serialVersionUID = 1L;
    private static final String QUERY_PREFIX = "multieditor.UserAccount.";
    public static final String PARAM_NAME = "name";
    public static final String FIND_BY_NAME = QUERY_PREFIX + "findByName";
    public static final String FIND_ALL = QUERY_PREFIX + "findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    /**
     * This is userName
     */
    @Column(nullable = false, unique = true)
    private String name;

    @Basic(optional = false)
    private String fullName;

    @Basic(optional = false)
    private String email;

    @Basic(optional = false)
    private String colour;

    public UserAccount() {
    }


    /**
     * @param name username, should be unique
     */
    public UserAccount(String name) {
        this.name = name;
    }

    public UserAccountSummary toUserAccountSummary() {
        return new UserAccountSummary(name, fullName, email, colour);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAccount that = (UserAccount) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
