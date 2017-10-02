package org.multieditor.data.multieditor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.multieditor.data.entity.NamedEntity;
import org.multieditor.data.entity.multieditor.Document;
import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.info.multieditor.UserAccountSummary;
import org.multieditor.data.multieditor.fixtures.MultieditorTestFixture;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestMultieditor {

    private static final String FIRST_DOCUMENT = "document1";
    private static final String SECOND_DOCUMENT = "doc2";
    private static final String TEST_USER_A = "usera";

    private MultieditorTestFixture fixture;

    @Before
    public void setUp() {
        fixture = new MultieditorTestFixture();

        fixture.beginTransaction();
    }

    @After
    public void tearDown() {
        fixture.rollbackTransaction();
        fixture.finish();
    }

    @Test
    public void testEmpty() {

    }

    @Test
    public void testCreateUser() {
        UserAccountSummary user =
                fixture.getUserManager().createOrUpdate("robot", "robot user", "robot@gmail.com", null);
        Assert.assertEquals("robot", user.getName());
        Assert.assertEquals("robot@gmail.com", user.getEmail());
        Assert.assertEquals("robot user", user.getFullName());
        Assert.assertEquals(null, user.getColour());

        user =
                fixture.getUserManager().createOrUpdate("robot", "modified robot user", "mrobot@gmail.com",
                        "red");
        Assert.assertEquals("robot", user.getName());
        Assert.assertEquals("mrobot@gmail.com", user.getEmail());
        Assert.assertEquals("modified robot user", user.getFullName());
        Assert.assertEquals("red", user.getColour());

    }

    private void mockUserSetting() throws UserNotFoundException {
        fixture.getUserManager().createOrUpdate(TEST_USER_A, "User A", "usera@gmail.com", "FFFFDD");
        fixture.getUserManager().createOrUpdate("userb", "User B", "userb@gmail.com", "FFDDFF");
        fixture.getUserManager().createOrUpdate("userc", "User C", "userc@gmail.com", "DDFFFF");

        List<UserAccountSummary> users = fixture.getUserManager().findAll();
        Set<String> names = NamedEntity.convertNamedEntitiesToNamesSet(users);
        Assert.assertEquals(3, users.size());
        Assert.assertTrue(names.contains(TEST_USER_A));
        Assert.assertTrue(names.contains("userb"));
        Assert.assertTrue(names.contains("userc"));


    }

    @Test
    public void testMockSetting() throws UserNotFoundException {
        mockUserSetting();
    }


    @Test
    public void testDeleteUser() throws UserNotFoundException {
        mockUserSetting();
        Assert.assertNotNull(fixture.getUserManager().findByName(TEST_USER_A));
        fixture.getUserManager().delete(TEST_USER_A);
        Assert.assertNull(fixture.getUserManager().findByName(TEST_USER_A));

    }

    private void mockDocumentSetting() throws UserNotFoundException, DocumentNotFoundException {
        mockUserSetting();
        UserAccountSummary userA = fixture.getUserManager().findByName(TEST_USER_A);

        DocumentSummary document = new DocumentSummary(FIRST_DOCUMENT, "sample document", "Contents of sample document",
                "0", "", "", userA);
        fixture.getDocumentController().createOrUpdate(document);

        List<Document> all = fixture.getDocumentController().findAll();

        Document dbDocument = fixture.getDocumentController().checkedFindByName(FIRST_DOCUMENT);

        Assert.assertEquals(document.getName(), dbDocument.getName());
        Assert.assertEquals(document.getContents(), dbDocument.getContents());
        Assert.assertEquals(document.getDescription(), dbDocument.getDescription());
        Assert.assertEquals(document.getOwnerOfChange().getName(), dbDocument.getOwnerOfChange().getName());

        Assert.assertEquals(document, dbDocument.toDocumentSummary());

        DocumentSummary doc2 = new DocumentSummary(SECOND_DOCUMENT, "description 2", "Document 2",
                "0", "", "", userA);
        fixture.getDocumentController().createOrUpdate(doc2);
        Assert.assertEquals(2, fixture.getDocumentController().findAll().size());

        Document dbDoc2 = fixture.getDocumentController().checkedFindByName(SECOND_DOCUMENT);
        Assert.assertEquals(doc2, dbDoc2.toDocumentSummary());
    }

    @Test
    public void testDocumentMock() throws UserNotFoundException, DocumentNotFoundException {
        mockDocumentSetting();
    }

    private Set<String> set(String... strings) {
        return new HashSet<>(Arrays.asList(strings));
    }

    @Test
    public void testDocumentDelete() throws UserNotFoundException, DocumentNotFoundException {
        mockDocumentSetting();
        fixture.getDocumentController().delete(FIRST_DOCUMENT);
        Assert.assertEquals(1, fixture.getDocumentController().findAll().size());
    }

    @Test
    public void testDocumentDelete1() throws UserNotFoundException, DocumentNotFoundException {
        mockDocumentSetting();

        fixture.getDocumentController().delete(SECOND_DOCUMENT);

        List<String> docNames = fixture.getDocumentController().findNamesChangedByUser(TEST_USER_A);
        Assert.assertEquals(1, docNames.size());

        docNames = fixture.getDocumentController().findNamesChangedByUser(TEST_USER_A);
        Assert.assertEquals(FIRST_DOCUMENT, docNames.get(0));

        final List<Document> allDocs = fixture.getDocumentController().findAll();
        Assert.assertEquals(1, allDocs.size());
        Assert.assertEquals(FIRST_DOCUMENT, allDocs.get(0).getName());
    }

    @Test
    public void testDocumentUpdate() throws UserNotFoundException, DocumentNotFoundException {
        mockDocumentSetting();
        UserAccountSummary userA = fixture.getUserManager().findByName(TEST_USER_A);
        DocumentSummary modifiedDocument = new DocumentSummary(FIRST_DOCUMENT,
                "new doc description", "la-la DB Document", "1", "0", "", userA);
        fixture.getDocumentController().createOrUpdate(modifiedDocument);

        Document dbDoc = fixture.getDocumentController().checkedFindByName(FIRST_DOCUMENT);

        Assert.assertEquals(modifiedDocument, dbDoc.toDocumentSummary());
    }

}
