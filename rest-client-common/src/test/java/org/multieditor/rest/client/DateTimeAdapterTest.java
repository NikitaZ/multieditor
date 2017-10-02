package org.multieditor.rest.client;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Date;

public class DateTimeAdapterTest {

    private final DateTimeAdapter adapter = new DateTimeAdapter();

    public DateTimeAdapterTest() {
    }

    @Test
    public void testFormat() {
        try {
            final Date date = new Date(1);
            Assert.assertEquals("1970-01-01T00:00:00.001Z", adapter.marshal(date));
            Assert.assertEquals(date, new Date(1));

            Date unmarshalledDate = adapter.unmarshal("1970-01-01T00:00:00.001Z");
            Assert.assertEquals(date.getTime(), unmarshalledDate.getTime());
        } catch (Exception ex) {
            Assert.fail("Shouldn't throw exception");
        }
    }
}