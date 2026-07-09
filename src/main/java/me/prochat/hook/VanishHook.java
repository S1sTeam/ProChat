package me.prochat.hook;

import org.bukkit.entity.Player;

public class VanishHook {

    public boolean isVanished(Player player) {
        try {
            if (player.hasMetadata("vanished")) {
                return player.getMetadata("vanished").stream()
                        .anyMatch(v -> v.asBoolean());
            }
            return player.isInvisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canSee(Player viewer, Player target) {
        if (!isVanished(target)) return true;
        if (viewer.equals(target)) return true;
        return viewer.hasPermission("vanish.see") || viewer.hasPermission("premiumvanish.see") || viewer.hasPermission("vanish.view");
    }
}
