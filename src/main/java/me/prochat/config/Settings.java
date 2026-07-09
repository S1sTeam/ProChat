package me.prochat.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class Settings {

    public String chatFormat;
    public String joinFormat;
    public String quitFormat;

    public final Map<String, ChannelConfig> channels = new LinkedHashMap<>();

    public MentionConfig mention;

    public AntiSpamConfig antiSpam;

    public void load(ConfigurationSection config) {
        ConfigurationSection fmt = config.getConfigurationSection("format");
        if (fmt != null) {
            chatFormat = fmt.getString("chat", "{channel_prefix}{prefix}{display_name}{suffix}&7: {message}");
            joinFormat = fmt.getString("join", "&8[&a+&8] &7{player}");
            quitFormat = fmt.getString("quit", "&8[&c-&8] &7{player}");
        }

        channels.clear();
        ConfigurationSection chSection = config.getConfigurationSection("channels");
        if (chSection != null) {
            for (String key : chSection.getKeys(false)) {
                ConfigurationSection sec = chSection.getConfigurationSection(key);
                if (sec == null) continue;
                ChannelConfig cc = new ChannelConfig();
                cc.name = key;
                cc.enabled = sec.getBoolean("enabled", true);
                cc.display = sec.getString("display", key.substring(0, 1).toUpperCase());
                cc.color = sec.getString("color", "&7");
                cc.range = sec.getInt("range", -1);
                cc.permission = sec.getString("permission", "prochat.channel." + key);
                cc.priority = sec.getInt("priority", 1);
                channels.put(key, cc);
            }
        }

        ConfigurationSection ment = config.getConfigurationSection("mention");
        if (ment != null) {
            mention = new MentionConfig();
            mention.enabled = ment.getBoolean("enabled", true);
            mention.format = ment.getString("format", "&b&l@{player}&r");
            ConfigurationSection snd = ment.getConfigurationSection("sound");
            if (snd != null) {
                mention.soundEnabled = snd.getBoolean("enabled", true);
                mention.soundType = snd.getString("type", "entity.experience_orb.pickup");
                mention.soundVolume = (float) snd.getDouble("volume", 1.0);
                mention.soundPitch = (float) snd.getDouble("pitch", 1.0);
            }
            mention.permission = ment.getString("permission", "prochat.mention");
            mention.clickable = ment.getBoolean("clickable", true);
            mention.highlightMessage = ment.getBoolean("highlight_message", true);
        }

        ConfigurationSection aspam = config.getConfigurationSection("anti_spam");
        if (aspam != null) {
            antiSpam = new AntiSpamConfig();
            antiSpam.enabled = aspam.getBoolean("enabled", true);
            ConfigurationSection cd = aspam.getConfigurationSection("cooldown");
            if (cd != null) {
                antiSpam.cooldownEnabled = cd.getBoolean("enabled", true);
                antiSpam.cooldownSeconds = cd.getInt("seconds", 2);
            }
            ConfigurationSection caps = aspam.getConfigurationSection("caps");
            if (caps != null) {
                antiSpam.capsEnabled = caps.getBoolean("enabled", true);
                antiSpam.capsMinLength = caps.getInt("min_length", 5);
                antiSpam.capsThreshold = caps.getInt("threshold", 75);
            }
            ConfigurationSection swear = aspam.getConfigurationSection("swear");
            if (swear != null) {
                antiSpam.swearEnabled = swear.getBoolean("enabled", true);
                antiSpam.swearWords = new HashSet<>(swear.getStringList("words"));
                antiSpam.swearReplace = swear.getString("replace_char", "*");
            }
            ConfigurationSection repeat = aspam.getConfigurationSection("repeat");
            if (repeat != null) {
                antiSpam.repeatEnabled = repeat.getBoolean("enabled", true);
                antiSpam.repeatMaxSimilarity = repeat.getInt("max_similarity", 85);
            }
        }
    }

    public static class ChannelConfig {
        public String name;
        public boolean enabled;
        public String display;
        public String color;
        public int range;
        public String permission;
        public int priority;
    }

    public static class MentionConfig {
        public boolean enabled;
        public String format;
        public boolean soundEnabled;
        public String soundType;
        public float soundVolume;
        public float soundPitch;
        public String permission;
        public boolean clickable;
        public boolean highlightMessage;
    }

    public static class AntiSpamConfig {
        public boolean enabled;
        public boolean cooldownEnabled;
        public int cooldownSeconds;
        public boolean capsEnabled;
        public int capsMinLength;
        public int capsThreshold;
        public boolean swearEnabled;
        public Set<String> swearWords = new HashSet<>();
        public String swearReplace;
        public boolean repeatEnabled;
        public int repeatMaxSimilarity;
    }
}
