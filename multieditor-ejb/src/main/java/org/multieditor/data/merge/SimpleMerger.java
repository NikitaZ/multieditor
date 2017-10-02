package org.multieditor.data.merge;

/**
 * @author nikita.zinoviev@gmail.com
 */
// works well if no lines are inserted or deleted
// gets rid of trailing '\n's

// another strategy could be:
// if same line was changed twice changes by other user win as there's more chance that current user would be able to correct those if needed
// (especially changes near cursorPosition)
public class SimpleMerger implements Merger {
    // todo - here it may be more intelligent to use the whole chain of changes from previous to head
    // (somehow it seems to be one step up from previous (over mergeWith) then few steps from head via previousVersion
    // so 'head' should really be a chain from head to oldHead which is merged with previous)
    @Override
    public MergeResult merge(String previous, String head, String newContents, int cursorPosition) {
        StringBuilder result = new StringBuilder();
        final String[] prevLines = previous.split("\n");
        final String[] headLines = head.split("\n");
        final String[] newlines = newContents.split("\n");
        int i;
        int pos = 0;
        int oldPos = 0;
        int cursorCorrection = 0;
        for (i = 0; i < prevLines.length && i < headLines.length && i < newlines.length; i++) {
            String line;
            if (prevLines[i].equals(newlines[i])) { // line unchanged
                line = headLines[i];
                if (cursorPosition > oldPos) {
                    cursorCorrection += headLines[i].length() - newlines[i].length();
                }
            } else {
                line = newlines[i];
            }

            result.append(line);
            result.append('\n');
            pos += line.length() + 1;
            oldPos += newlines[i].length() + 1;
        }
        while (i < headLines.length) {
            final String line = headLines[i];
            result.append(line);
            result.append('\n');
            pos += line.length() + 1;
            i++;
        }
        while (i < newlines.length) {
            final String line = newlines[i];
            result.append(line);
            result.append('\n');
            pos += line.length() + 1;
            i++;
        }
        String mergedContents = result.substring(0, result.length() - 1);
        return new MergeResultImpl(cursorCorrection, mergedContents);
    }
}
