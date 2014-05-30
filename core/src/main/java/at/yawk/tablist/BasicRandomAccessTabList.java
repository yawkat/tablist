package at.yawk.tablist;

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Standard RandomAccessTabList implementation.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicRandomAccessTabList implements RandomAccessTabList {
    /**
     * Native handle.
     */
    private final NativeTabList nativeTabList;

    /**
     * The <i>currently cached</i> entries of this tab list.
     */
    private final TabListEntry[] entries;
    /**
     * The <i>last sent</i> entries of this tab list.
     */
    private final TabListEntry[] sent;
    /**
     * The usernames sent for each slot. Should contain exactly all elements of #usedPlayerNames.
     */
    private final String[] usedPlayerNamesBySlot;
    /**
     * All usernames sent, for quick uniqueness check. Should contain exactly all elements of #usedPlayerNamesBySlot.
     */
    private final Set<String> usedPlayerNames = new HashSet<>();
    /**
     * Array of unique whitespace for each slot. Must not be modified.
     */
    private final String[] uniqueNames;

    private boolean initialized = false;

    /**
     * Create a new empty RandomAccessTabList with the given length. The default minecraft whitespace builder is used.
     */
    public static RandomAccessTabList create(NativeTabList nativeTabList, int length) {
        return create(nativeTabList, WhitespaceBuilder.createMinecraftBuilder(), length);
    }

    /**
     * Create a new empty RandomAccessTabList with the given length.
     */
    public static RandomAccessTabList create(NativeTabList nativeTabList,
                                             WhitespaceBuilder whitespaceBuilder,
                                             int length) {
        checkNotNull(nativeTabList, "nativePingList");
        checkNotNull(whitespaceBuilder, "whitespaceBuilder");
        checkArgument(length >= 0, "length must be > 0");

        TabListEntry[] entries = new TabListEntry[length];
        Arrays.fill(entries, TabListEntry.empty());

        String[] uniqueNames = new String[length];
        IntStream.range(0, length).forEach(i -> uniqueNames[i] = whitespaceBuilder.build(i));

        return new BasicRandomAccessTabList(nativeTabList, entries, entries.clone(), new String[length], uniqueNames);
    }

    @Override
    public RandomAccessTabList set(int index, TabListEntry entry) {
        if (set0(index, entry)) { flush(); }
        return this;
    }

    /**
     * #set but without flushing.
     *
     * @return true if the list was modified, false otherwise.
     */
    private boolean set0(int index, TabListEntry entry) {
        checkNotNull(entry, "entry");
        checkElementIndex(index, getLength(), "index");

        if (!entries[index].equals(entry)) {
            entries[index] = entry;
            return true;
        }
        return false;
    }

    @Override
    public CachedRandomAccessTabList cached() {
        return new CachedRandomAccessTabList() {
            @Override
            public void flush() {
                BasicRandomAccessTabList.this.flush();
            }

            @Override
            public CachedRandomAccessTabList set(int index, TabListEntry entry) {
                set0(index, entry);
                return this;
            }

            @Override
            public int getLength() { return BasicRandomAccessTabList.this.getLength(); }

            @Override
            public TabListEntry get(int index) { return BasicRandomAccessTabList.this.get(index); }
        };
    }

    @Override
    public int getLength() {
        return entries.length;
    }

    @Override
    public TabListEntry get(int index) {
        checkElementIndex(index, getLength(), "index");

        return entries[index];
    }

    private void init() {
        IntStream.range(0, getLength())
                 .mapToObj(i -> "t" + i)
                 .forEach(s -> nativeTabList.createTeam(s, "", Collections.emptySet()));
        initialized = true;
    }

    private void flush() {
        // first entry index where the actual name had to be changed (we need to flash all entries after that)
        int firstFlashed = Integer.MAX_VALUE;

        if (!initialized) {
            init();
            firstFlashed = 0;
        }

        for (int i = 0; i < getLength(); i++) {
            // we need to flash this entry
            // null means that it hasn't been sent yet.
            if (firstFlashed < i && usedPlayerNamesBySlot[i] != null) {
                nativeTabList.removePlayer(usedPlayerNamesBySlot[i]);
                usedPlayerNames.remove(usedPlayerNamesBySlot[i]);
            }

            if (entries[i].equals(sent[i])) {
                // nothing changed
                continue;
            }

            TabListEntry newEntry = entries[i];
            TabListEntry oldEntry = sent[i];

            if (!newEntry.getPrefix().equals(oldEntry.getPrefix())) {
                // update team prefix
                nativeTabList.setTeamPrefix("t" + i, newEntry.getPrefix());
            }

            // check if we need to flash starting from here
            if (firstFlashed > i && !newEntry.getName().equals(oldEntry.getName())) {
                firstFlashed = i;
                nativeTabList.removePlayer(usedPlayerNamesBySlot[i]);
            }

            sent[i] = entries[i];
        }

        // readd flashed entries. firstFlashed is MAX_INT if no flashing is performed so we don't need to check
        // manually.
        for (int i = firstFlashed; i < getLength(); i++) {
            // full name that is supposed to be added at this position
            String fullName = entries[i].getName();
            // how many characters of the following newNameBuilder are used up by the name
            int used = fullName.length();
            // the new name that should be sent. Consists of the fullName and the uniqueName at this position.
            StringBuilder newNameBuilder = new StringBuilder(fullName).append(uniqueNames[i]);
            // don't use the same name multiple times in the same tab list. clip to avoid this.
            while (usedPlayerNames.contains(newNameBuilder.substring(0, Math.min(16, newNameBuilder.length()))) &&
                   // let's just hope this doesn't happen, it's very unlikely
                   used > 0) {
                // used already, we need to clip at least one character
                used--;
                newNameBuilder.deleteCharAt(used);
            }
            // the name that should actually be sent
            String newName = newNameBuilder.substring(0, Math.min(16, newNameBuilder.length()));
            usedPlayerNames.add(newName);
            usedPlayerNamesBySlot[i] = newName;

            nativeTabList.addTeamMembers("t" + i, Collections.singleton(newName));
            // finally append
            nativeTabList.appendPlayer(newName);
        }
    }
}
