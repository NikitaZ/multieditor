package org.multieditor.data.services.multieditor;

import org.multieditor.data.entity.multieditor.UserAccount;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.ejb.Local;

@Local
public interface UserManagerLocal extends UserService {
    // these methods are for EJB layer only

    // we need this for instance to build relationships, etc.
    UserAccount checkedFindUserByName(String name) throws UserNotFoundException;

    UserAccountSummary checkedFindUserSummaryByName(String name) throws UserNotFoundException;

//    void deleteUser(String userName);
}
