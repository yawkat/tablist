package at.yawk.tablist;

import lombok.*;

/**
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Value
public class TabListEntry {
    private static final TabListEntry EMPTY = new TabListEntry("", "");

    private final String prefix;
    private final String name;

    /**
     * Create a new entry from the given string value.
     */
    public static TabListEntry create(String value) {
        // shortcut
        if (value.isEmpty()) { return empty(); }

        int thres = 16; // end of prefix

        String prefix = substring(value, 0, thres);

        // do not split color codes
        if (value.length() > 16 && prefix.charAt(15) == '\247') {
            thres = 15;
            prefix = substring(value, 0, thres);
        }

        String main = substring(value, thres, 32);
        return new TabListEntry(prefix, main);
    }

    public static TabListEntry empty() {
        return EMPTY;
    }

    /**
     * Returns a substring of a string, starting from <code>start</code> (inclusive) and ending with <code>end</code>
     * (exclusive). If either is not within the bounds of the string this method will not fail but return all
     * characters within that range.
     */
    private static String substring(String haystack, int start, int end) {
        int len = haystack.length();
        if (start < 0) {
            start = 0;
        } else if (start > len) {
            return "";
        }
        if (end < start) {
            end = start;
        } else if (end > len) {
            end = len;
        }

        return haystack.substring(start, end);
    }
}
