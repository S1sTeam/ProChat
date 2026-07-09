package me.prochat.channel;

import me.prochat.config.Settings;

public class ChatChannel {

    private final String name;
    private final Settings.ChannelConfig config;

    public ChatChannel(String name, Settings.ChannelConfig config) {
        this.name = name;
        this.config = config;
    }

    public String getName() { return name; }
    public String getDisplay() { return config.display; }
    public String getColor() { return config.color; }
    public int getRange() { return config.range; }
    public String getPermission() { return config.permission; }
    public boolean isEnabled() { return config.enabled; }
    public int getPriority() { return config.priority; }
    public boolean isGlobal() { return config.range < 0; }
    public String getPrefixString() { return config.color + "[" + config.display + "]&r "; }

    public boolean hasPermission(org.bukkit.entity.Player player) {
        return player.hasPermission(config.permission);
    }
}
