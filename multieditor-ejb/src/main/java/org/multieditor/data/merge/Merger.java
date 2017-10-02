package org.multieditor.data.merge;

/**
 * @author nikita.zinoviev@gmail.com
 */
public interface Merger {
    MergeResult merge(String previous, String head, String newContents, int cursorPosition);

    // encapsulates the choice of currently preferred merger
    static Merger getMerger() {
        return new SimpleMerger();
    }
}
