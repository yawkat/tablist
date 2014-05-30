package at.yawk.tablist;

import java.util.Collection;

/**
 * @author Jonas Konrad (yawkat)
 */
public interface NativeTabList {
    void appendPlayer(String username);

    void removePlayer(String username);

    void createTeam(String teamId, String prefix, Collection<String> members);

    void setTeamPrefix(String teamId, String newPrefix);

    void addTeamMembers(String teamId, Collection<String> members);

    void removeTeamMembers(String teamId, Collection<String> members);
}
