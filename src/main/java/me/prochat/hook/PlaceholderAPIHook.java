package me.prochat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook {

    private final boolean enabled;

    public PlaceholderAPIHook(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPrefix(Player player) {
        if (!enabled) return "";
        return PlaceholderAPI.setPlaceholders(player, "%player_prefix%");
    }

    public String getSuffix(Player player) {
        if (!enabled) return "";
        return PlaceholderAPI.setPlaceholders(player, "%player_suffix%");
    }

    public String setPlaceholders(Player player, String text) {
        if (!enabled) return text;
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
