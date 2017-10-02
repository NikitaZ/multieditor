package org.multieditor.data.multieditor.fixtures;

import org.multieditor.data.controllers.multieditor.DocumentControllerBean;
import org.multieditor.data.controllers.multieditor.DocumentControllerLocal;
import org.multieditor.data.controllers.multieditor.UserControllerBean;
import org.multieditor.data.controllers.multieditor.UserControllerLocal;
import org.multieditor.data.fixture.TestConnector;
import org.multieditor.data.fixture.TestFixtureBase;
import org.multieditor.data.services.multieditor.UserManagerBean;
import org.multieditor.data.services.multieditor.UserManagerLocal;

/**
 * This is the fixture for all EJB tests.
 * This fixture initializes persistence provider to in-memory database,
 * instantiates controllers and binds them together.
 */
public class MultieditorTestFixture extends TestFixtureBase {

    private DocumentControllerLocal documentController;

    private UserControllerLocal userController;

    private UserManagerLocal userManager;

    public MultieditorTestFixture() {
        this(new TestConnector());
    }

    public MultieditorTestFixture(TestConnector connector) {
        super(connector);

        userManager = new UserManagerBean();

        documentController = new DocumentControllerBean();
        injectEntityManager(documentController);
        inject(documentController, userManager, "userManager");

        userController = new UserControllerBean();
        injectEntityManager(userController);
        inject(userManager, userController, "userController");
    }

    public DocumentControllerLocal getDocumentController() {
        return documentController;
    }

    public UserControllerLocal getUserController() {
        return userController;
    }

    public UserManagerLocal getUserManager() {
        return userManager;
    }

}
