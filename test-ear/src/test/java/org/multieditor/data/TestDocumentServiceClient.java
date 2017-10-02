package org.multieditor.data;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.multieditor.client.DocumentServiceClient;
import org.multieditor.client.UserServiceClient;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.services.multieditor.DocumentService;
import org.multieditor.data.services.multieditor.UserService;
import org.multieditor.testutil.LongTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import static org.multieditor.data.MultieditorEmbeddedGlassfishTestSetup.*;

@Category(LongTest.class)
public class TestDocumentServiceClient {

    private DocumentService documentServiceClient = new DocumentServiceClient();
    private UserService userServiceClient = new UserServiceClient();

    @BeforeClass
    public static void setUpClass() throws Exception {
        MultieditorEmbeddedGlassfishTestSetup.setUpBeforeClass();
    }

    @Test
    public void testCreateOrUpdate() throws UserNotFoundException {
        documentServiceClient.createOrUpdate("testCreateOrUpdate", "testde", "contents", TEST_USER, "", 0);
    }

    @Test
    public void testFindAll1() throws UserNotFoundException {
        List<DocumentSummary> documentsBefore = documentServiceClient.findAll();
        documentServiceClient.createOrUpdate("testFindAll", "testFindAll", "contents", TEST_USER, "", 0);
        List<DocumentSummary> documentsAfter = documentServiceClient.findAll();
        Assert.assertEquals(documentsBefore.size() + 1, documentsAfter.size());
    }

    @Test
    public void testFindAll2() throws UserNotFoundException {
        documentServiceClient.createOrUpdate("test2", "test", "contents", TEST_USER, "", 0);
        List<DocumentSummary> documentNames = documentServiceClient.findAll();
        boolean hasDocument = false;
        for (DocumentSummary document : documentNames) {
            if (document.getName().equals("test2")) {
                hasDocument = true;
            }
        }
        Assert.assertTrue("Must have 'test2'", hasDocument);
    }

    @Test
    public void testFindDocument() throws UserNotFoundException {
        final String contents = "contents";
        final String testDocumentName = "test";
        String result = documentServiceClient.createOrUpdate(testDocumentName, "test description", contents, TEST_USER, "", 0).getDocument().getContents();
        Assert.assertEquals(contents, result);
        DocumentSummary testDocument = documentServiceClient.findByName(testDocumentName);
        Assert.assertEquals(testDocumentName, testDocument.getName());
        Assert.assertEquals(contents, testDocument.getContents());
        DocumentSummary noDocument = documentServiceClient.findByName("nosuchdocument");
        Assert.assertEquals(null, noDocument);
    }

    // this method is not used at the moment
    @Test
    public void testFindNamesByUser() throws UserNotFoundException {
        final String testUserName = "testuserFNBU";
        userServiceClient.createOrUpdate(testUserName, "testuser", "testemail", "");
        documentServiceClient.createOrUpdate("testU", "test", "contents", testUserName, "", 0);
        try {
            List<String> documentNames = documentServiceClient.findNamesChangedByUser(testUserName);
            boolean hasDocument = false;
            for (String document : documentNames) {
                if (document.equals("testU")) {
                    hasDocument = true;
                }
            }
            Assert.assertTrue("Should find testU document", hasDocument);
        } catch (UserNotFoundException ex) {
            Assert.fail("testuser not found");
        }
    }


    @Test
    public void testBigUpdateSeq() {
        final String testDocumentName = "testA0zSeq";
        executeA0zScenarioOnce(testDocumentName, true);
    }

    // this probably should be in a separate test class
    @Test
    public void testBigUpdate() {
        final String testDocumentName = "testA0z";
        executeA0zScenarioOnce(testDocumentName, false);
    }

    //    disable test until the above succeeds
    @Test
    public void testBigUpdateConcurrentDocs() {
        final String testDocumentName = "testA0z";
        final AtomicBoolean failedFlag = new AtomicBoolean(false);
        IntStream.range(0, 10).mapToObj(i -> {
            Thread thread = new Thread(() -> {
                try {
                    executeA0zScenarioOnce(testDocumentName + i, false);
                } catch (Throwable error) {
                    error.printStackTrace();
                    failedFlag.set(true);
                }
            });
            thread.start();
            return thread;
        }).forEach(this::join);
        Assert.assertFalse(failedFlag.get());
    }

    private void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void executeA0zScenarioOnce(String testDocumentName, boolean runSequentially) {
        final String onePrefix = "012";
        final String one = "3456789abcdef";
        final String twoPrefix = "ABC";
        final String two = "DEFGHIJKLMNOPQRSTUVWXYZ";
        final String threePrefix = "zyx";
        final String three = "wvutsrqponmlkjihgfedcba";
        final String initialContents = onePrefix + "\n\n" +
                twoPrefix + "\n\n" +
                threePrefix;
        final String expectedContents =
                onePrefix + one + "\n\n" +
                        twoPrefix + two + "\n\n" +
                        threePrefix + three;

        final DocumentSummary initialDocument;
        try (Committed c = new Committed()) {
            try {

                initialDocument = documentServiceClient.createOrUpdate(
                        testDocumentName, "simple 3 line test", initialContents,
                        TEST_USER, "", 0).getDocument();
            } catch (UserNotFoundException logged) {  // should never happen
                Assert.fail("Got UserNotFoundException " + logged);
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        final Thread threadOne = new Thread(() -> typeLine(onePrefix, one, initialDocument, 200, USER_ONE));
        final Thread threadTwo = new Thread(() -> typeLine(twoPrefix, two, initialDocument, 50, USER_TWO));
        final Thread threadThree = new Thread(() -> typeLine(threePrefix, three, initialDocument, 100, USER_THREE));

        if (!runSequentially) {
            threadOne.start();
            threadTwo.start();
            threadThree.start();
        } else {
//          run serially - should always pass
            threadOne.run();
            threadTwo.run();
            threadThree.run();
        }
        join(threadOne);
        join(threadTwo);
        join(threadThree);
        DocumentSummary resultingDocument = documentServiceClient.findByName(initialDocument.getName());
        Assert.assertEquals("Resulting document contents differ from expected result. ", expectedContents, resultingDocument.getContents());
        System.out.println(initialDocument.getName() + " is okay.");
    }

    private void typeLine(String prefix, String line, DocumentSummary initialDocument, int maxDelay, String userName) {
        try {
            DocumentSummary document = documentServiceClient.findByName(initialDocument.getName());
            for (char c : line.toCharArray()) {
                try {
                    Thread.sleep((long) (Math.random() * maxDelay));
                } catch (InterruptedException ignored) {
                }
                final String oldContents = document.getContents();
                int start = oldContents.indexOf(prefix);
                int cur = oldContents.indexOf("\n\n", start);
                String newContents = cur > 0 ? oldContents.substring(0, cur) + c + oldContents.substring(cur) : oldContents + c;

                document = documentServiceClient.createOrUpdate(document.getName(), document.getDescription(),
                        newContents, userName, document.getVersion(), 0).getDocument();

            }
        } catch (UserNotFoundException logged) {
            logged.printStackTrace(); // should never happen, but then update must have failed and hence the test will fail
        }
    }

    public static void main(String[] args) {
        new TestDocumentServiceClient().testBigUpdate();
    }

}
