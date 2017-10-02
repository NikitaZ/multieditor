package org.multieditor.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
public class ClientProperties {

    public static final String SERVICE_URL_PROP = "service.url";

    public static final String[] propsSystemInherit
            = {SERVICE_URL_PROP};

    private static final Logger LOGGER = Logger.getLogger(ClientProperties.class.getName());

    private static Properties properties = null;

    static synchronized void init() {
        Properties props = new Properties();
        // we do not use try-with-resources because of language level (see pom.xml)
        InputStream is = null;
        try {
            // todo fixme
            is = ClientProperties.class.getResourceAsStream("/client.properties");
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading client.properties file", e);
        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Error closing client.properties file", ex);
                }
            }

            for (String name : propsSystemInherit) {
                String val = System.getProperty(name);

                if (val != null) {
                    String oldVal = props.getProperty(name);
                    if (oldVal != null) {
                        LOGGER.log(Level.INFO,
                                String.format("Override %s=[%s] from system properties. Value from 'client.properties': [%s]", name, val, oldVal));
                        System.out.println();
                    } else {
                        LOGGER.log(Level.INFO, String.format("Got %s=[%s] from system properties", name, val));
                    }
                    props.setProperty(name, val);
                }
            }

            properties = props;
        }
    }

    public static String getProperty(String key, String defaultValue) {
        if (properties == null) {
            init();
        }
        return properties.getProperty(key, defaultValue);
    }
}
