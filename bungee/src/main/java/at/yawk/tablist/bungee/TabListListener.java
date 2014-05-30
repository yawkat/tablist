package at.yawk.tablist.bungee;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Jonas Konrad (yawkat)
 */
public class TabListListener implements Listener {
    TabListListener() {}

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        event.getPlayer().setTabList(new VoidTabList());
    }
}
