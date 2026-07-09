package me.prochat.hook;

import me.prochat.config.Settings;
import org.bukkit.entity.Player;

import java.util.Map;

public class BadgeManager {

    private Settings.BadgesConfig config;
    private Settings.VoiceChatConfig vcConfig;
    private VoiceChatHook voiceChatHook;

    public BadgeManager(Settings.BadgesConfig config, Settings.VoiceChatConfig vcConfig, VoiceChatHook voiceChatHook) {
        reload(config, vcConfig, voiceChatHook);
    }

    public void reload(Settings.BadgesConfig config, Settings.VoiceChatConfig vcConfig, VoiceChatHook voiceChatHook) {
        this.config = config;
        this.vcConfig = vcConfig;
        this.voiceChatHook = voiceChatHook;
    }

    public String getBadges(Player player) {
        StringBuilder sb = new StringBuilder();
        if (config != null && config.enabled) {
            for (Map.Entry<String, String> entry : config.badges.entrySet()) {
                if (!entry.getKey().equals("default") && player.hasPermission("prochat.badge." + entry.getKey())) {
                    sb.append(entry.getValue());
                }
            }
            if (sb.isEmpty() && config.badges.containsKey("default")) {
                sb.append(config.badges.get("default"));
            }
        }
        if (vcConfig != null && vcConfig.enabled && vcConfig.badge != null && !vcConfig.badge.isEmpty()
                && voiceChatHook != null && voiceChatHook.isAvailable()
                && player.hasPermission("prochat.badge.voicechat")) {
            sb.insert(0, vcConfig.badge);
        }
        return sb.toString();
    }
}
