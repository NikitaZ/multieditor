package org.multieditor.testutil;

import java.util.Properties;

/**
 * @author ilia
 */
public class SystemPropertiesSaver implements AutoCloseable {

    private final Properties origProperties;

    public SystemPropertiesSaver() {
        this.origProperties = (Properties) System.getProperties().clone();
    }

    public void close() {
        System.setProperties(origProperties);
    }
}
