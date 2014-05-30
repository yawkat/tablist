package at.yawk.tablist;

/**
 * @author Jonas Konrad (yawkat)
 */
public interface TabList {
    int getLength();

    default int getWidth() {
        return (int) Math.ceil((double) getLength() / 20);
    }

    default int getHeight() {
        return (int) Math.ceil((double) getLength() / getWidth());
    }

    TabListEntry get(int index);
}
