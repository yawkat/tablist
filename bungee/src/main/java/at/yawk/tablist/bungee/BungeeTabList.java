package at.yawk.tablist.bungee;

import at.yawk.tablist.NativeTabList;
import at.yawk.tablist.TabListDriver;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author Jonas Konrad (yawkat)
 */
public class BungeeTabList extends TabListDriver<ProxiedPlayer> {
    private static final BungeeTabList instance = new BungeeTabList();

    private BungeeTabList() {}

    public static BungeeTabList getInstance() {
        return instance;
    }

    @Override
    public NativeTabList getNativeTabList(ProxiedPlayer player) {
        return new NativeBungeeTabList(player);
    }

    public void preventVanillaTabList(Plugin plugin) {
        plugin.getProxy().getPluginManager().registerListener(plugin, new TabListListener());
    }

}
