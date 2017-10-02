package org.multieditor.data;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.multieditor.client.UserServiceClient;
import org.multieditor.data.info.multieditor.UserAccountSummary;
import org.multieditor.testutil.LongTest;

import java.util.List;

@Category(LongTest.class)
public class TestUserServiceClient {

    private static final String TEST_USER_NAME = "UserServiceTestUser";
    private UserServiceClient client = new UserServiceClient();

    @BeforeClass
    public static void setUpClass() throws Exception {
        MultieditorEmbeddedGlassfishTestSetup.setUpBeforeClass();
    }


    private UserAccountSummary createUser(String user) {
        return client.createOrUpdate(user, "", "", "");
    }

    @Test
    public void testCreateUser() {
        UserAccountSummary user = createUser(TEST_USER_NAME);
        Assert.assertEquals(TEST_USER_NAME, user.getName());
    }

    @Test
    public void testFindUserByName() {
        Assert.assertNull(client.findByName("NoSuchUser"));
        UserAccountSummary user = createUser(TEST_USER_NAME);
        UserAccountSummary foundUser = client.findByName(TEST_USER_NAME);
        Assert.assertEquals(user.getName(), foundUser.getName());
    }


    @Test
    public void testFindAllUsers() {
        List<UserAccountSummary> allUsers = client.findAll();
        createUser(TEST_USER_NAME + ".findAllUsers");
        List<UserAccountSummary> allUsersAfter = client.findAll();
        Assert.assertEquals(allUsers.size() + 1, allUsersAfter.size());
    }

}
