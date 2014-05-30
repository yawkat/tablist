package at.yawk.tablist.bukkit;

import at.yawk.tablist.NativeTabList;
import at.yawk.tablist.TabListDriver;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Tab list driver for bukkit (using ProtocolLib).
 *
 * @author Jonas Konrad (yawkat)
 */
public class BukkitTabList extends TabListDriver<Player> {
    private static final BukkitTabList instance = new BukkitTabList();

    /**
     * Set of packets that will be sent to a client in a future but should be let through.
     */
    final Set<Object> allowedPackets = Collections.synchronizedSet(new HashSet<>());
    /**
     * Flag whether a packet filter is registered and allowedPackets should be used.
     */
    boolean filterRegistered = false;

    private BukkitTabList() {}

    public static BukkitTabList getInstance() {
        return instance;
    }

    @Override
    public NativeTabList getNativeTabList(Player player) {
        return new NativeBukkitTabList(player);
    }

    /**
     * Filter tab list packets so only the custom one will be shown.
     */
    public synchronized void preventVanillaTabList(Plugin plugin) {
        if (filterRegistered) { return; }
        PacketListener adapter = new PacketAdapter(plugin, WrapperPlayServerPlayerInfo.TYPE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (!allowedPackets.remove(event.getPacket().getHandle())) {
                    event.setCancelled(true);
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(adapter);
        filterRegistered = true;
    }
}
