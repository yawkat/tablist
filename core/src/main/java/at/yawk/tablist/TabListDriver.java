package at.yawk.tablist;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * @author Jonas Konrad (yawkat)
 */
public abstract class TabListDriver<Pl> {
    private final Map<Pl, RandomAccessTabList> players =
            Collections.synchronizedMap(new WeakHashMap<Pl, RandomAccessTabList>());

    public RandomAccessTabList getTabList(Pl player, Function<Pl, RandomAccessTabList> tabListFactory) {
        return players.computeIfAbsent(player, tabListFactory);
    }

    public RandomAccessTabList getTabList(Pl player, int length) {
        return getTabList(player, pl -> BasicRandomAccessTabList.create(getNativeTabList(player), length));
    }

    public RandomAccessTabList getTabList(Pl player) {
        return getTabList(player, 60);
    }

    public abstract NativeTabList getNativeTabList(Pl player);
}
