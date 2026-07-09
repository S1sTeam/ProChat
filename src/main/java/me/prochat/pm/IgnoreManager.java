package me.prochat.pm;

import org.bukkit.entity.Player;

import java.util.*;

public class IgnoreManager {

    private final Map<UUID, Set<UUID>> ignored = new HashMap<>();

    public boolean isIgnored(Player player, Player target) {
        Set<UUID> set = ignored.get(player.getUniqueId());
        return set != null && set.contains(target.getUniqueId());
    }

    public boolean toggleIgnore(Player player, Player target) {
        Set<UUID> set = ignored.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (set.contains(target.getUniqueId())) {
            set.remove(target.getUniqueId());
            return false;
        } else {
            set.add(target.getUniqueId());
            return true;
        }
    }

    public Set<UUID> getIgnored(Player player) {
        return ignored.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }
}
