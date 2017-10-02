package org.multieditor.web;

import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

//import javax.annotation.ManagedBean;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Named("loginPageBean")
@RequestScoped
//@ManagedBean("loginPageBean") // does not work instead of @Named at least in case of JSF version="2.2" in faces-config.xml
public class LoginPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient String userName;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    @Inject
    @Named("userBean")
    private transient UserBean userBean;

    public LoginPageBean() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameViaParameter() {
        return null;
    }

    // this method is always called unless above getter returns null (then it is just not called at all)!
    public void setUserNameViaParameter(String name) {
        if (name == null) {
            return;
        }
        setUserName(name);
    }

    public boolean getReadOnly() {
        return false; // no access restrictions in this app
    }

    public String login() {
        UserAccountSummary userAccount = serviceBean.getUserService().findByName(getUserName());
        if (userAccount != null) {
            userBean.loginUser(userAccount);
            return "Documents";
        } else {
            return "Newcommer";
        }
    }


}