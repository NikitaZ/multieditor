package org.multieditor.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.multieditor.testutil.SystemPropertiesSaver;

import static org.junit.Assert.assertEquals;

public class ClientPropertiesTest {

    public ClientPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetProperty() {
        String defaultValue = "http://localhost:14000";
        String expResult = "http://localhost:13333"; // defined in src/test/resources/client.properties

        // to be not dependent on tests execution order
        ClientProperties.init();

        String result = ClientProperties.getProperty(ClientProperties.SERVICE_URL_PROP, defaultValue);
        assertEquals(expResult, result);

        defaultValue = "nothing";
        expResult = "nothing";
        result = ClientProperties.getProperty("dummy.value", defaultValue);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFromSysProperty() {
        try (SystemPropertiesSaver s = new SystemPropertiesSaver()) {
            String expResult = "http://localhost:99999";
            System.setProperty(ClientProperties.SERVICE_URL_PROP, expResult);

            // test requires reinitialization of the class
            ClientProperties.init();
            assertEquals(expResult, ClientProperties.getProperty(ClientProperties.SERVICE_URL_PROP, "http://localhost:14000"));
        }
    }

}