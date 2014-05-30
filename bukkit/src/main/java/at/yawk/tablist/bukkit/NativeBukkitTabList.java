package at.yawk.tablist.bukkit;

import at.yawk.tablist.NativeTabList;
import com.comphenix.packetwrapper.AbstractPacket;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * NativeTabList implementation for bukkit.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor
class NativeBukkitTabList implements NativeTabList {
    private final Player player;

    private void send(AbstractPacket packet) {
        if (BukkitTabList.getInstance().filterRegistered) {
            BukkitTabList.getInstance().allowedPackets.add(packet.getHandle().getHandle());
        }
        packet.sendPacket(player);
    }

    private void tabList(String username, boolean online) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        packet.setPlayerName(username);
        packet.setOnline(online);
        send(packet);
    }

    @Override
    public void appendPlayer(String username) {
        tabList(username, true);
    }

    @Override
    public void removePlayer(String username) {
        tabList(username, false);
    }

    private void team(String id, int action, String prefix, Collection<String> members) {
        WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();
        packet.setTeamName(id);
        packet.setPacketMode((byte) action);
        packet.setTeamPrefix(prefix);
        packet.setPlayers(members);
        send(packet);
    }

    @Override
    public void createTeam(String teamId, String prefix, Collection<String> members) {
        team(teamId, WrapperPlayServerScoreboardTeam.Modes.TEAM_CREATED, prefix, members);
    }

    @Override
    public void setTeamPrefix(String teamId, String newPrefix) {
        team(teamId, WrapperPlayServerScoreboardTeam.Modes.TEAM_UPDATED, newPrefix, Collections.emptySet());
    }

    @Override
    public void addTeamMembers(String teamId, Collection<String> members) {
        team(teamId, WrapperPlayServerScoreboardTeam.Modes.PLAYERS_ADDED, "", members);
    }

    @Override
    public void removeTeamMembers(String teamId, Collection<String> members) {
        team(teamId, WrapperPlayServerScoreboardTeam.Modes.PLAYERS_REMOVED, "", members);
    }
}
