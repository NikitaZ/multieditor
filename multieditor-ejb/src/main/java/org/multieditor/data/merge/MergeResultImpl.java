package org.multieditor.data.merge;

/**
 * @author nikita.zinoviev@gmail.com
 * Date: 9/30/17
 * Time: 1:29 AM
 */
public class MergeResultImpl implements MergeResult {
    private final int cursorCorrection;
    private final String mergedContents;

    MergeResultImpl(int cursorCorrection, String mergedContents) {
        this.cursorCorrection = cursorCorrection;
        this.mergedContents = mergedContents;
    }

    @Override
    public int getCursorCorrection() {
        return cursorCorrection;
    }

    @Override
    public String getMergedContents() {
        return mergedContents;
    }
}
