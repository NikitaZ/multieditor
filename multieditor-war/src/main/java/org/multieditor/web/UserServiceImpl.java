package org.multieditor.web;

import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;
import org.multieditor.data.services.multieditor.UserManagerLocal;
import org.multieditor.data.services.multieditor.UserService;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.Path;
import java.util.List;

@Path("/userService")
@ManagedBean
/**
 * Exports UserManagerBean through a JAX-RS service.
 */
public class UserServiceImpl implements UserService {

    @EJB
    private UserManagerLocal userManagerLocal;

    @Override
    public UserAccountSummary findByName(String name) {
        return userManagerLocal.findByName(name);
    }

    @Override
    public UserAccountSummary createOrUpdate(String name, String fullName, String email, String colour) {
        return userManagerLocal.createOrUpdate(name, fullName, email, colour);
    }

    @Override
    public void delete(String userName) throws UserNotFoundException {
        userManagerLocal.delete(userName);
    }

    @Override
    public List<UserAccountSummary> findAll() {
        return userManagerLocal.findAll();
    }

}
