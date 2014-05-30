package at.yawk.tablist;

import static com.google.common.base.Preconditions.checkArgument;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Class used to quickly build short, unique strings from a set of characters.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WhitespaceBuilder {
    private static final WhitespaceBuilder MINECRAFT_WHITESPACE_BUILDER = create(' ', '\u0a00');

    private final char[] characters;

    /**
     * Create a new WhitespaceBuilder instance that returns strings containing the given characters.
     * <p/>
     * <i>Currently only arrays of exactly two characters are supported.</i>
     */
    public static WhitespaceBuilder create(char... characters) {
        checkArgument(characters.length == 2, "WhitespaceBuilder needs exactly two characters to function.");

        return new WhitespaceBuilder(characters);
    }

    /**
     * Create a new WhitespaceBuilder that contains only characters that are invisible in minecraft font.
     */
    public static WhitespaceBuilder createMinecraftBuilder() {
        return MINECRAFT_WHITESPACE_BUILDER;
    }

    /**
     * Build a string consisting of the allowed characters that is unique to the given index.
     * <p/>
     * <i>Return value of this method can change (while staying unique) across versions,
     * so it should not be used for persistance.</i>
     *
     * @param index String index. Must be greater or equal zero, otherwise a (non-unique) empty string is returned.
     * @return The unique string.
     */
    public String build(long index) {
        /*
         * Example:
         * index = (10)100 = (2)1100100
         * characters = ['a', 'b']
         *
         * returns "aabaab", the little-endian bit representation with the MSB missing.
         * BE would be nicer here but LE is more efficient with StringBuilder.
         * The MSB is always the same (1) except for index=0 so we can safely ignore it as long as index > 0.
         */

        index++; // index is incremented by one so we can follow the contract while still keeping it above 0.

        StringBuilder builder = new StringBuilder();
        while ((index & ~1) > 0) { // as long as we got more bits except the MSB
            // least significant bit
            builder.append(characters[(int) (index & 1)]);
            // bitshift right so we can get the next bit
            index >>>= 1;
        }
        return builder.toString();
    }
}
