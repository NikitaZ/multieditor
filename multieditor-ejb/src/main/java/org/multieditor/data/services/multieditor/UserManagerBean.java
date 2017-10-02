package org.multieditor.data.services.multieditor;

import org.multieditor.data.controllers.multieditor.UserControllerLocal;
import org.multieditor.data.entity.multieditor.UserAccount;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author nikita.zinoviev@gmail.com
 */
@Stateless
public class UserManagerBean implements UserManagerLocal {

    @EJB
    private UserControllerLocal userController;

    private static final Logger LOGGER = Logger.getLogger(UserManagerBean.class.getName());

    private /*static*/ List<UserAccountSummary> convertToSummaryList(Collection<UserAccount> accounts) {
        return accounts.stream().map(UserAccount::toUserAccountSummary).collect(Collectors.toList());
    }

    @Override
    public List<UserAccountSummary> findAll() {
        return convertToSummaryList(userController.findAll());
    }

    @Override
    public UserAccountSummary findByName(String name) {
        return wrap(userController.findByName(name));
    }

    @Override
    public UserAccount checkedFindUserByName(String name) throws UserNotFoundException {
        return userController.checkedFindByName(name);
    }

    @Override
    public UserAccountSummary checkedFindUserSummaryByName(String name) throws UserNotFoundException {
        return checkedFindUserByName(name).toUserAccountSummary();
    }

    @Override
    public UserAccountSummary createOrUpdate(String name, String fullName, String email, String colour) {
        LOGGER.log(Level.INFO, "createOrUpdateUser: {0} fullName: {1}, e-mail: {2}, colour: {3}",
                new Object[]{name, fullName, email, colour});

        UserAccountSummary user = findByName(name);
        UserAccount account;
        if (user == null) {
            account = userController.create(name);
        } else {
            account = userController.findByName(name);
        }
        account.setFullName(fullName);
        account.setEmail(email);
        account.setColour(colour);
        return account.toUserAccountSummary();
    }

    @Override
    public void delete(String userName) throws UserNotFoundException {
        userController.delete(userName);
    }

//    not used
//    @Override
//    public void deleteUser(String userName) {
//        userController.delete(userName);
//    }

    private /*static*/ UserAccountSummary wrap(UserAccount userAccount) {
        return userAccount != null ? userAccount.toUserAccountSummary() : null;
    }
}
