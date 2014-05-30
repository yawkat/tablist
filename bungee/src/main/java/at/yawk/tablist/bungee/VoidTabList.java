package at.yawk.tablist.bungee;

import net.md_5.bungee.api.tab.TabListAdapter;

/**
 * Empty TabList adapter to prevent vanilla tab list.
 *
 * @author Jonas Konrad (yawkat)
 */
class VoidTabList extends TabListAdapter {
    @Override
    public boolean onListUpdate(String s, boolean b, int i) {
        return false;
    }
}
