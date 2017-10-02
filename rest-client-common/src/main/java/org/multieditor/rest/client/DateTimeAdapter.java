package org.multieditor.rest.client;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Adapts Date on the ISO-format yyyy-MM-dd'T'HH:mm:ss.SSSZZ
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    /**
     * @param isoDateString a string strictly adhering to the full ISO timestamp format, yyyy-MM-dd'T'HH:mm:ss.SSSZZ
     * @return the date representation
     * @throws Exception if the string is not a valid ISO timestamp
     */
    @Override
    public Date unmarshal(String isoDateString) throws Exception {
        return toDate(isoDateString);
    }

    public static Date toDate(String isoDateString) {
        return Date.from(Instant.parse(isoDateString));
    }

    /**
     * @param date
     * @return a string in UTC locale representing the specified date
     * @throws Exception
     */
    @Override
    public String marshal(Date date) throws Exception {
        return toString(date);
    }

    public static String toString(Date date) {
        return FORMATTER.format(date.toInstant());
    }

}
