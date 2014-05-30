package at.yawk.tablist.examples.playercounter;

import at.yawk.tablist.CachedRandomAccessTabList;
import at.yawk.tablist.RandomAccessTabList;
import at.yawk.tablist.TabListEntry;
import at.yawk.tablist.bungee.BungeeTabList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Jonas Konrad (yawkat)
 */
public class PlayerCounterBungee extends Plugin implements Listener {
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onLogin(ServerSwitchEvent event) {
        updateTabList(null);
    }

    @EventHandler
    public void onLogout(PlayerDisconnectEvent event) {
        updateTabList(event.getPlayer());
    }

    private void updateTabList(ProxiedPlayer except) {
        // player counter
        TabListEntry counter = TabListEntry.create("Players: " + getProxy().getPlayers().size());
        // streamable of players except the excluded player
        Supplier<Stream<ProxiedPlayer>> players = () -> getProxy().getPlayers().stream().filter(p -> !p.equals(except));
        // list of tab list entries for each player (color + name)
        List<TabListEntry> playerEntries = players.get()
                                                  .map(p -> TabListEntry.create(
                                                          ChatColor.getByChar(Integer.toHexString(p.getUniqueId()
                                                                                                   .hashCode())
                                                                                     .charAt(0)) + p.getName()
                                                  ))
                                                  .collect(Collectors.toList());
        // apply tab list
        players.get().forEach(player -> {
            RandomAccessTabList tabList = BungeeTabList.getInstance().getTabList(player);
            CachedRandomAccessTabList cached = tabList.cached();
            // names
            for (int i = 0; i < playerEntries.size() && i < cached.getLength(); i++) {
                cached.set(i, playerEntries.get(i));
            }
            // overwrite non-empty slots
            for (int i = playerEntries.size(); i < cached.getLength(); i++) {
                if (cached.get(i).equals(TabListEntry.empty())) { break; } // done
                cached.set(i, TabListEntry.empty());
            }
            // add counter
            cached.set(1, 19, counter);
            // finish
            cached.flush();
        });
    }
}
