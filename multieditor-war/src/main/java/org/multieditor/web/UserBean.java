package org.multieditor.web;

import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

/**
 * User: nikita.zinoviev@gmail.com
 */
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String fullName;
    private String email;

    public UserBean() {
        userName = null;
        fullName = null;
        email = null;
    }

//    public UserBean(String userName, String fullName, String email) {
//        this.userName = userName;
//        this.fullName = fullName;
//        this.email = email;
//    }

    @PostConstruct
    public void init() {

    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public boolean getLoggedIn() {
        return userName != null;
    }

    public String logout() {
        userName = null;
        return "logout";
    }

    public void loginUser(UserAccountSummary userAccount) {
        this.userName = userAccount.getName();
        this.fullName = userAccount.getFullName();
        this.email = userAccount.getEmail();
    }
}