package at.yawk.tablist.bungee;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * Bungee plugin for custom tab lists.
 *
 * @author Jonas Konrad (yawkat)
 */
public class BungeeTabListPlugin extends Plugin {
    @Override
    public void onEnable() {
        BungeeTabList.getInstance().preventVanillaTabList(this);
    }
}
