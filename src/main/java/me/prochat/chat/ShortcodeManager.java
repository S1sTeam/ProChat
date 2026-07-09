package me.prochat.chat;

import me.prochat.config.Settings;

import java.util.HashMap;
import java.util.Map;

public class ShortcodeManager {

    private final Map<String, String> shortcodes = new HashMap<>();
    private boolean enabled;

    public void reload(Settings.ShortcodesConfig config) {
        shortcodes.clear();
        this.enabled = config.enabled;
        if (config.shortcodes != null) {
            shortcodes.putAll(config.shortcodes);
        }
    }

    public String process(String message) {
        if (!enabled || shortcodes.isEmpty()) return message;
        String result = message;
        for (Map.Entry<String, String> entry : shortcodes.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
