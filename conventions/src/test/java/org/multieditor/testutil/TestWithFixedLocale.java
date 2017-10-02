package org.multieditor.testutil;

import org.junit.BeforeClass;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Base class for all tests with fixed locale/timezone
 *
 * @author ilia
 */
public abstract class TestWithFixedLocale {

    @BeforeClass
    public static void setupBeforeTests() {
        // fix locale and timezone - to get the same results on computers with different settings
        Locale.setDefault(Locale.US);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }
}
