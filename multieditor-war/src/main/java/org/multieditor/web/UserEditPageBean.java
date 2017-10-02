package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Named("userEditPageBean")
@RequestScoped
public class UserEditPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient String userName;

    private transient String fullName;

    private transient String email;

    private transient String colour;

    private transient boolean logIntoOnSave;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    @Inject
    @Named("userBean")
    private transient UserBean userBean;

    public UserEditPageBean() {
        setColour("#FFFFFF");
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getUserNameViaParameter() {
        return null;
    }

    // this method is always called unless above getter returns null (then it is just not called at all)!
    public void setUserNameViaParameter(String name) {
        if (name == null) {
            return;
        }

        UserAccountSummary user = serviceBean.getUserService().findByName(name);
        if (user == null) {
            setUserName(name);
            return;
        }

        setUserName(user.getName());
        setFullName(user.getFullName());
        setEmail(user.getEmail());
        setColour(user.getColour());
    }

    public boolean getLogIntoOnSave() {
        return logIntoOnSave;
    }

    public void setLogIntoOnSave(boolean logIntoOnSave) {
        this.logIntoOnSave = logIntoOnSave;
    }

    public boolean getReadOnly() {
        return false; // no access restrictions in this app
    }

    /*
     * JSF action methods, return null to refresh the page
     */
    public String delete() {
        try {
            serviceBean.getUserService().delete(getUserName());
        } catch (UserNotFoundException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "Error during delete"));
        }
        return "Users";
    }

    public String save() {
        UserAccountSummary user = serviceBean.getUserService().createOrUpdate(getUserName(), getFullName(), getEmail(), getColour());
        if (logIntoOnSave) {
            userBean.loginUser(user);
            return "Documents";
        }
        return "Users";
    }


}