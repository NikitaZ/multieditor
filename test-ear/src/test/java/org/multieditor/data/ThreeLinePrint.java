package org.multieditor.data;

import org.multieditor.client.DocumentServiceClient;
import org.multieditor.client.UserServiceClient;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.services.multieditor.DocumentService;

/**
 * @author nikita.zinoviev@gmail.com
 */
public class ThreeLinePrint {

    public static final String USER_ONE = "userOne";
    public static final String USER_TWO = "userTwo";
    public static final String USER_THREE = "userThree";


    private DocumentService documentServiceClient = new DocumentServiceClient();

    public static void main(String[] args) {
        UserServiceClient userService = new UserServiceClient();
        userService.createOrUpdate(USER_ONE, "test user one", "test.user.1@gmail.com", "#FF0000");
        userService.createOrUpdate(USER_TWO, "test user two", "test.user.2@gmail.com", "#00FF00");
        userService.createOrUpdate(USER_THREE, "test user three", "test.user.3@gmail.com", "#0000FF");

        final String testDocumentName = "testA0z";
        new ThreeLinePrint().executeA0zScenarioOnce(testDocumentName);
    }

    private void executeA0zScenarioOnce(String testDocumentName) {
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
        try {
            DocumentSummary previous = documentServiceClient.findByName(testDocumentName);
            final String previousVersion = previous != null ? previous.getVersion() : "";
            initialDocument = documentServiceClient.createOrUpdate(
                    testDocumentName, "simple 3 line test", initialContents,
                    USER_ONE, previousVersion, 0).getDocument();
        } catch (UserNotFoundException logged) {  // should never happen
            System.out.println("Got UserNotFoundException " + logged);
            return;
        }
        final Thread threadOne = new Thread(() -> typeLine(onePrefix, one, initialDocument, 200, USER_ONE));
        final Thread threadTwo = new Thread(() -> typeLine(twoPrefix, two, initialDocument, 50, USER_TWO));
        final Thread threadThree = new Thread(() -> typeLine(threePrefix, three, initialDocument, 100, USER_THREE));

        threadOne.start();
        threadTwo.start();
        threadThree.start();

        join(threadOne);
        join(threadTwo);
        join(threadThree);
        DocumentSummary resultingDocument = documentServiceClient.findByName(initialDocument.getName());
        if (expectedContents.equals(resultingDocument.getContents())) {
            System.out.println("The test succeeded!");
        } else {
            System.out.println("The test FAILED.");
        }
    }

    private void typeLine(String prefix, String line, DocumentSummary initialDocument, int maxDelay, String userName) {
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
            try {

                document = documentServiceClient.createOrUpdate(
                        document.getName(), document.getDescription(),
                        newContents,
                        userName, document.getVersion(), 0).getDocument();

            } catch (UserNotFoundException logged) {
                logged.printStackTrace(); // should never happen, but then update must have failed and hence the test will fail
            }
        }
    }

    private void join(Thread t) {
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
