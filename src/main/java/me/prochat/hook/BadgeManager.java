package me.prochat.hook;

import me.prochat.config.Settings;
import org.bukkit.entity.Player;

import java.util.Map;

public class BadgeManager {

    private final Settings.BadgesConfig config;

    public BadgeManager(Settings.BadgesConfig config) {
        this.config = config;
    }

    public void reload(Settings.BadgesConfig config) {
        // config reference updated via plugin
    }

    public String getBadges(Player player) {
        if (!config.enabled) return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : config.badges.entrySet()) {
            if (!entry.getKey().equals("default") && player.hasPermission("prochat.badge." + entry.getKey())) {
                sb.append(entry.getValue());
            }
        }
        if (sb.isEmpty() && config.badges.containsKey("default")) {
            sb.append(config.badges.get("default"));
        }
        return sb.toString();
    }
}
