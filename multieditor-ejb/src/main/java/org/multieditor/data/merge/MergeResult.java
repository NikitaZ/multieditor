package org.multieditor.data.merge;

/**
 * @author nikita.zinoviev@gmail.com
 * Date: 9/30/17
 * Time: 1:28 AM
 */
public interface MergeResult {
    int getCursorCorrection();

    String getMergedContents();
}
