package org.multieditor.data.merge;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author nikita.zinoviev@gmail.com
 */
public class MergerTest {
    private final String onePrefix = "012";
    private final String one = "3456789abcdef";
    private final String twoPrefix = "ABC";
    private final String two = "DEFGHIJKLMNOPQRSTUVWXYZ";
    private final String threePrefix = "zyx";
    private final String three = "wvutsrqponmlkjihgfedcba";
    private final String initialContents = onePrefix + "\n\n" +
            twoPrefix + "\n\n" +
            threePrefix;
    private final String expectedContents =
            onePrefix + one + "\n\n" +
                    twoPrefix + two + "\n\n" +
                    threePrefix + three;
    private final String newOne =
            onePrefix + one + "\n\n" +
                    twoPrefix + "\n\n" +
                    threePrefix + three;
    private final String head =
            onePrefix + "\n\n" +
                    twoPrefix + two + "\n\n" +
                    threePrefix;

    @Test
    public void simpleLineMerge() {
        final int cursorPosition = newOne.length() - 1;
        MergeResult results = Merger.getMerger().merge(initialContents, head, newOne, cursorPosition);
        assertEquals(expectedContents, results.getMergedContents());
        Assert.assertEquals(expectedContents.length() - 1,
                cursorPosition + results.getCursorCorrection());
        Assert.assertEquals(newOne.charAt(cursorPosition),
                results.getMergedContents().charAt(cursorPosition + results.getCursorCorrection()));
    }

    @Test
    public void simpleLineMergeOtherwayRound() {
        final int cursorPosition = head.length() - threePrefix.length() - 2 - 1;
        MergeResult results = Merger.getMerger().merge(initialContents, newOne, head, cursorPosition);
        assertEquals(expectedContents, results.getMergedContents());
        Assert.assertEquals(expectedContents.length() - three.length() - threePrefix.length() - 2 - 1,
                cursorPosition + results.getCursorCorrection());
        Assert.assertEquals(head.charAt(cursorPosition),
                results.getMergedContents().charAt(cursorPosition + results.getCursorCorrection()));

    }

    @Test
    public void simpleLineMergeDelete() {
        // now initial contents are expected
        final int cursorPosition = newOne.length() - 1;
        MergeResult results = Merger.getMerger().merge(expectedContents, head, newOne, cursorPosition);
        assertEquals(initialContents, results.getMergedContents());
        Assert.assertEquals(initialContents.length() - 1,
                cursorPosition + results.getCursorCorrection());
    }

    @Test
    public void simpleLineMergeDeleteOtherWayRound() {
        // now initial contents are expected
        final int cursorPosition = head.length() - 1;
        MergeResult results = Merger.getMerger().merge(expectedContents, newOne, head, cursorPosition);
        assertEquals(initialContents, results.getMergedContents());
        Assert.assertEquals(initialContents.length() - 1,
                cursorPosition + results.getCursorCorrection());
    }

    @Test
    public void newLinesAdded() {
        MergeResult results = Merger.getMerger().merge("a\n\nb", "aa\n\nb", "a\n\nb\nc", 0);
        assertEquals("aa\n\nb\nc", results.getMergedContents());

        results = Merger.getMerger().merge("a\nb", "a\nb", "a\nbb\nc", 0);
        assertEquals("a\nbb\nc", results.getMergedContents());
    }
}