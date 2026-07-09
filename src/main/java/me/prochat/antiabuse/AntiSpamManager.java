package me.prochat.antiabuse;

import me.prochat.ProChatPlugin;
import me.prochat.config.Settings;
import org.bukkit.entity.Player;

import java.util.*;

public class AntiSpamManager {

    private final ProChatPlugin plugin;
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final Map<UUID, String> lastMessageContent = new HashMap<>();

    public AntiSpamManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public String check(Player player, String message) {
        Settings.AntiSpamConfig cfg = plugin.getConfigManager().getSettings().antiSpam;
        if (cfg == null || !cfg.enabled) return null;
        if (player.hasPermission("prochat.bypass.antispam")) return null;

        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis() / 1000;

        String cooldownMsg = checkCooldown(player, uuid, now, cfg);
        if (cooldownMsg != null) return cooldownMsg;

        if (cfg.capsEnabled && message.length() >= cfg.capsMinLength) {
            int upper = 0;
            int letters = 0;
            for (char c : message.toCharArray()) {
                if (Character.isLetter(c)) {
                    letters++;
                    if (Character.isUpperCase(c)) upper++;
                }
            }
            if (letters > 0 && (upper * 100 / letters) >= cfg.capsThreshold) {
                return plugin.getConfigManager().getRawMessage("antispam_caps");
            }
        }

        if (cfg.swearEnabled && !cfg.swearWords.isEmpty()) {
            String check = message.toLowerCase().replaceAll("[^a-zA-Zа-яА-Я]", "");
            for (String word : cfg.swearWords) {
                if (check.contains(word.toLowerCase())) {
                    return plugin.getConfigManager().getRawMessage("antispam_swear");
                }
            }
        }

        if (cfg.repeatEnabled) {
            String prev = lastMessageContent.get(uuid);
            if (prev != null) {
                double similarity = similarity(prev, message);
                if (similarity >= cfg.repeatMaxSimilarity) {
                    return plugin.getConfigManager().getRawMessage("antispam_repeat");
                }
            }
        }

        lastMessageTime.put(uuid, now);
        lastMessageContent.put(uuid, message);
        return null;
    }

    private String checkCooldown(Player player, UUID uuid, long now, Settings.AntiSpamConfig cfg) {
        if (!cfg.cooldownEnabled) return null;
        if (player.hasPermission("prochat.cooldown.bypass")) return null;

        int cooldown = cfg.cooldownSeconds;

        for (Map.Entry<String, Integer> entry : cfg.cooldownGroups.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                cooldown = Math.min(cooldown, entry.getValue());
            }
        }

        Long last = lastMessageTime.get(uuid);
        if (last != null && (now - last) < cooldown) {
            long remaining = cooldown - (now - last);
            return plugin.getConfigManager().getRawMessage("antispam_cooldown")
                    .replace("{time}", String.valueOf(remaining));
        }
        return null;
    }

    private double similarity(String a, String b) {
        if (a == null || b == null) return 0;
        String s1 = a.toLowerCase().trim();
        String s2 = b.toLowerCase().trim();
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 100;
        int dist = levenshtein(s1, s2);
        return (1.0 - (double) dist / maxLen) * 100;
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
