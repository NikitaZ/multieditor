package org.multieditor.web;

import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Named("usersPageBean")
@RequestScoped
public class UsersPageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient List<UserAccountSummary> users;

    @Inject
    @Named("ServiceBean")
    private transient ServiceBean serviceBean;

    public List<UserAccountSummary> getUsers() {
        if (users == null) {
            users = serviceBean.getUserService().findAll();
        }
        return users;
    }

    public String refresh() {
        return null;
    }

}