package org.multieditor.testutil;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndAttributeQualifier;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author ilia
 */
public final class XMLComparator {
    private XMLComparator() {
    }

    /**
     * Compares XML with ignoring order of elements and attributes
     * For example, the following XML is equal (similar):
     * <a> <b>c</b> <d>e<d/> <a/> and
     * <a> <d>e<d/> <b>c</b> <a/>
     * If XML is not equal the fails with assertion error
     */
    public static void compareIgnoringOrder(String expected, String actual) throws IOException {
        XMLUnit.setCompareUnmatched(false);
        XMLUnit.setIgnoreAttributeOrder(true);
        Diff xmlDiff = null;
        try {
            xmlDiff = new Diff(expected, actual);
            xmlDiff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
        } catch (SAXException e) {
            e.printStackTrace();
            Assert.fail("SAXException");
        }
        Assert.assertTrue("XML is not similar: " + xmlDiff.toString() + "\n\n" + actual,
                xmlDiff.similar());
    }
}
