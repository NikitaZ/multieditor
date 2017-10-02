package org.multieditor.data;

import junit.framework.Assert;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.multieditor.client.DocumentServiceClient;
import org.multieditor.client.UserServiceClient;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultieditorEmbeddedGlassfishTestSetup {
    public static final String TEST_USER = "test.user";
    public static final String USER_ONE = "userOne";
    public static final String USER_TWO = "userTwo";
    public static final String USER_THREE = "userThree";
    public static final String ROBOT = "robot";
    public static final String TEST_DOCUMENT = "Test";

    private static GlassFish glassfish;

    private static Deployer deployer;
    private static String multieditorAppName;
    private static AtomicBoolean setup = new AtomicBoolean(false);

    private static ThreadLocal<InitialContext> ctx = new ThreadLocal<InitialContext>() {
        @Override
        protected InitialContext initialValue() {
            try {
                return new InitialContext();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    private static ThreadLocal<UserTransaction> ut = new ThreadLocal<UserTransaction>() {
        @Override
        protected UserTransaction initialValue() {
            try {
                return (UserTransaction) ctx.get().lookup("java:comp/UserTransaction");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static class Committed implements AutoCloseable {

        public Committed() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            if (ut.get().getStatus() == Status.STATUS_ACTIVE) {
                ut.get().commit();
            }
            ut.get().begin();
        }

        @Override
        public void close() throws Exception {
            ut.get().commit();
        }
    }

    public static class RolledBack implements AutoCloseable {

        public RolledBack() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            if (ut.get().getStatus() == Status.STATUS_ACTIVE) {
                System.out.println("!!! WARNING: Commit before start rollback");
                new Throwable().printStackTrace();
                ut.get().commit();
            }
            ut.get().begin();
        }

        @Override
        public void close() throws Exception {
            ut.get().rollback();
        }
    }

    public static void setUpBeforeClass() throws Exception {
        if (!setup.get() && setup.compareAndSet(false, true)) {
            setupGlassFishServer();
        }
        // Wait for setup to complete (if multiple tests are started concurrently)
        while (!setup.get()) {
            Thread.sleep(10);
        }
    }

    private static void setupGlassFishServer() {
        try {

            System.getProperties().setProperty("org.jboss.weld.xml.disableValidating", "true");
            System.out.println("================ Setup GlassFish =================");
            Files.deleteIfExists(new File("target/multieditor.mv.db").toPath());
            Files.deleteIfExists(new File("target/multieditor.h2.db").toPath()); // in case other engine is running
            Files.deleteIfExists(new File("target/multieditor.trace.db").toPath());
            GlassFishProperties glassfishProperties = new GlassFishProperties();
            glassfishProperties.setConfigFileURI(new File("src/test/resources/domain.xml").toURI().toString()); // this is the only change to the above code.

            glassfish = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
            // Start Embedded GlassFish
            glassfish.start();

            // Install common resources
//            CommandRunner commandRunner = glassfish.getCommandRunner();
//            commandRunner.run("add-resources", "src/test/resources/app-server-resources.xml");


            // Deploy an application to the Embedded GlassFish
            deployer = glassfish.getDeployer();

            System.out.println("=============== Deploying Multieditor EAR ===============");
            multieditorAppName = deployer.deploy(new File("../multieditor-ear/target/multieditor-ear-1.0-SNAPSHOT.ear"));
            Assert.assertNotNull("Multieditor app not deployed", multieditorAppName);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    tearDownAfterClass();
                }
            });

            // todo: remove once h2 fixes it
//             to avoid h2/GF 4 bug with   RAR7115: Unable to set ClientInfo for connection
//            this needs to be done after the app deploy, otherwise it has no effect
            // may be the real problem is
//            java.sql.SQLClientInfoException: Property name 'numServers is used internally by H2.
//            we have a lot of that in the h2 db trace file. Waiting for h2 to be fixed, although it seems this is fixed in Payara
            Logger.getLogger("javax.enterprise.resource.resourceadapter.com.sun.gjc.spi").setLevel(Level.WARNING);

            try (Committed c = new Committed()) {
                UserServiceClient userService = new UserServiceClient();
                userService.createOrUpdate(ROBOT, "robot user", "robot@gmail.com", null);
                userService.createOrUpdate(TEST_USER, "test user", "test.user@gmail.com", null);
                userService.createOrUpdate(USER_ONE, "test user one", "test.user.1@gmail.com", "#FF0000");
                userService.createOrUpdate(USER_TWO, "test user two", "test.user.2@gmail.com", "#00FF00");
                userService.createOrUpdate(USER_THREE, "test user three", "test.user.3@gmail.com", "#0000FF");

                DocumentServiceClient documentService = new DocumentServiceClient();
                documentService.createOrUpdate(TEST_DOCUMENT, "Test doc", "Test Doc text", TEST_USER, "", 0);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Commits any currently running transaction and starts a new transaction.
     * Keeps most transaction logic out of the individual test to theoretically
     * be able to rollback at the end of a test cycle.
     *
     * @throws NotSupportedException
     * @throws SystemException
     * @throws RollbackException
     * @throws HeuristicMixedException
     * @throws HeuristicRollbackException
     */
    public static void commitAndStartNewTransaction() throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        if (ut.get().getStatus() == Status.STATUS_ACTIVE) {
            ut.get().commit();
        }
        ut.get().begin();
    }

    public static void tearDownAfterClass() {
        // Stop GlassFish
        try {
            // Undeploy the application
            System.out.println("========== Stopping Embedded GlassFish ===========");
            deployer.undeploy(multieditorAppName);

            // Stop Embedded GlassFish
            glassfish.stop();
            glassfish.dispose();
            System.out.println("======== Removed Embedded GlassFish Files ========");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lookup an object based on the
     *
     * @param <T>
     * @param type
     * @return
     */
    public static <T> T lookup(Class<T> type, String name) {
        try {
            return (T) ctx.get().lookup(name);
        } catch (NamingException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
