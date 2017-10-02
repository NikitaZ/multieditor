package org.multieditor.data.info.multieditor;

import org.multieditor.data.entity.NamedEntity;

import java.io.Serializable;

public class UserAccountSummary implements Serializable, NamedEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    private String fullName;

    private String email;

    private String colour;

    private static final String DEFAULT_USER_NAME = "Anonymous";

    public static final UserAccountSummary DEFAULT_USER = new UserAccountSummary(DEFAULT_USER_NAME, DEFAULT_USER_NAME, "", "");

    public UserAccountSummary() {
    }

    public UserAccountSummary(String name, String fullName, String email, String colour) {
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.colour = colour;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getColour() {
        return colour;
    }

    @Override
    public String toString() {
        return name;
    }
}
