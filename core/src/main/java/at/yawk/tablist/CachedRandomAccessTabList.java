package at.yawk.tablist;

/**
 * @author Jonas Konrad (yawkat)
 */
public interface CachedRandomAccessTabList extends RandomAccessTabList {
    void flush();

    // override so we can return CachedRandomAccessTabList

    @Override
    CachedRandomAccessTabList set(int index, TabListEntry entry);

    @Override
    default CachedRandomAccessTabList set(int x, int y, TabListEntry entry) {
        return set(x + y * getWidth(), entry);
    }

    @Override
    default CachedRandomAccessTabList cached() {
        CachedRandomAccessTabList handle = this;
        return new CachedRandomAccessTabList() {
            @Override
            public void flush() {}

            @Override
            public CachedRandomAccessTabList set(int index, TabListEntry entry) {
                handle.set(index, entry);
                return this;
            }

            @Override
            public int getLength() {
                return handle.getLength();
            }

            @Override
            public TabListEntry get(int index) {
                return handle.get(index);
            }
        };
    }
}
