package at.yawk.tablist.bungee;

import at.yawk.tablist.NativeTabList;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import net.md_5.bungee.protocol.packet.Team;

/**
 * NativeTabList implementation for bungee.
 *
 * @author Jonas Konrad (yawkat)
 */
@RequiredArgsConstructor
class NativeBungeeTabList implements NativeTabList {
    private final ProxiedPlayer player;

    private void send(DefinedPacket packet) {
        player.unsafe().sendPacket(packet);
    }

    @Override
    public void appendPlayer(String username) {
        send(new PlayerListItem(username, true, 0));
    }

    @Override
    public void removePlayer(String username) {
        send(new PlayerListItem(username, false, 0));
    }

    private void team(String id, int action, String prefix, Collection<String> members) {
        send(new Team(id,
                      (byte) action,
                      "",
                      prefix,
                      "",
                      "",
                      (byte) 0,
                      (byte) 0,
                      members.toArray(new String[members.size()])));
    }

    @Override
    public void createTeam(String teamId, String prefix, Collection<String> members) {
        team(teamId, 0, prefix, members);
    }

    @Override
    public void setTeamPrefix(String teamId, String newPrefix) {
        team(teamId, 2, newPrefix, Collections.emptySet());
    }

    @Override
    public void addTeamMembers(String teamId, Collection<String> members) {
        team(teamId, 3, "", members);
    }

    @Override
    public void removeTeamMembers(String teamId, Collection<String> members) {
        team(teamId, 4, "", members);
    }
}
