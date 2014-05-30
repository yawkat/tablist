package at.yawk.tablist;

/**
 * @author Jonas Konrad (yawkat)
 */
public interface RandomAccessTabList extends TabList {
    default RandomAccessTabList set(int x, int y, TabListEntry entry) {
        return set(x + y * getWidth(), entry);
    }

    RandomAccessTabList set(int index, TabListEntry entry);

    default TabListEntry get(int x, int y) {
        return get(x + y * getWidth());
    }

    CachedRandomAccessTabList cached();
}
