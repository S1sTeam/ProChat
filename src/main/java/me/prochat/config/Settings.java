package me.prochat.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class Settings {

    public String chatFormat;
    public String joinFormat;
    public String quitFormat;

    public final Map<String, ChannelConfig> channels = new LinkedHashMap<>();

    public AnimatedGradientConfig animatedGradient;
    public BadgesConfig badges;
    public ChatParticlesConfig chatParticles;
    public PersonalSoundConfig personalSound;
    public MentionConfig mention;
    public AntiSpamConfig antiSpam;

    public void load(ConfigurationSection config) {
        ConfigurationSection fmt = config.getConfigurationSection("format");
        if (fmt != null) {
            chatFormat = fmt.getString("chat", "{badges}{channel_prefix}{prefix}{display_name_anim}{suffix}&7: {message}");
            joinFormat = fmt.getString("join", "&8[&a+&8] &7{player}");
            quitFormat = fmt.getString("quit", "&8[&c-&8] &7{player}");
        }

        channels.clear();
        ConfigurationSection ch = config.getConfigurationSection("channels");
        if (ch != null) {
            for (String key : ch.getKeys(false)) {
                ConfigurationSection sec = ch.getConfigurationSection(key);
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

        ConfigurationSection anim = config.getConfigurationSection("animated_gradient");
        if (anim != null) {
            animatedGradient = new AnimatedGradientConfig();
            animatedGradient.enabled = anim.getBoolean("enabled", true);
            animatedGradient.speed = anim.getInt("speed", 3);
            animatedGradient.cycleColors = anim.getStringList("cycle_colors");
        }

        ConfigurationSection badge = config.getConfigurationSection("badges");
        if (badge != null) {
            this.badges = new BadgesConfig();
            this.badges.enabled = badge.getBoolean("enabled", true);
            ConfigurationSection list = badge.getConfigurationSection("list");
            if (list != null) {
                for (String key : list.getKeys(false)) {
                    this.badges.badges.put(key, list.getString(key, ""));
                }
            }
        }

        ConfigurationSection cp = config.getConfigurationSection("chat_particles");
        if (cp != null) {
            chatParticles = new ChatParticlesConfig();
            chatParticles.enabled = cp.getBoolean("enabled", true);
            chatParticles.type = cp.getString("type", "HEART");
            chatParticles.count = cp.getInt("count", 8);
            chatParticles.speed = cp.getDouble("speed", 0.05);
            chatParticles.spread = cp.getDouble("spread", 0.6);
            chatParticles.permission = cp.getString("permission", "prochat.particles");
        }

        ConfigurationSection ps = config.getConfigurationSection("personal_sound");
        if (ps != null) {
            personalSound = new PersonalSoundConfig();
            personalSound.enabled = ps.getBoolean("enabled", true);
            personalSound.defaultSound = ps.getString("default", "block.note_block.pling");
            personalSound.permission = ps.getString("permission", "prochat.sound");
        }

        ConfigurationSection ment = config.getConfigurationSection("mention");
        if (ment != null) {
            mention = new MentionConfig();
            mention.enabled = ment.getBoolean("enabled", true);
            mention.format = ment.getString("format", "&b&l@{player}&r");
            mention.permission = ment.getString("permission", "prochat.mention");
            mention.clickable = ment.getBoolean("clickable", true);

            ConfigurationSection snd = ment.getConfigurationSection("sound");
            if (snd != null) {
                mention.soundEnabled = snd.getBoolean("enabled", true);
                mention.soundType = snd.getString("type", "entity.experience_orb.pickup");
                mention.soundVolume = (float) snd.getDouble("volume", 0.5);
                mention.soundPitch = (float) snd.getDouble("pitch", 1.5);
            }

            ConfigurationSection ab = ment.getConfigurationSection("actionbar");
            if (ab != null) {
                mention.actionbarEnabled = ab.getBoolean("enabled", true);
                mention.actionbarMessage = ab.getString("message", "&b&l⚡ {player} &bmentioned you!");
            }

            ConfigurationSection tit = ment.getConfigurationSection("title");
            if (tit != null) {
                mention.titleEnabled = tit.getBoolean("enabled", false);
                mention.titleText = tit.getString("title", "&b&lMENTION!");
                mention.titleSubtitle = tit.getString("subtitle", "&7by {player}");
                mention.titleFadeIn = tit.getInt("fade_in", 10);
                mention.titleStay = tit.getInt("stay", 40);
                mention.titleFadeOut = tit.getInt("fade_out", 10);
            }

            ConfigurationSection psp = ment.getConfigurationSection("particles");
            if (psp != null) {
                mention.particlesEnabled = psp.getBoolean("enabled", true);
                mention.particlesType = psp.getString("type", "HAPPY_VILLAGER");
                mention.particlesCount = psp.getInt("count", 15);
                mention.particlesSpeed = psp.getDouble("speed", 0.2);
            }

            ConfigurationSection ph = ment.getConfigurationSection("personal_highlight");
            if (ph != null) {
                mention.personalHighlight = ph.getBoolean("enabled", true);
                mention.highlightColor = ph.getString("color", "&e&l");
            }
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

    public static class AnimatedGradientConfig {
        public boolean enabled;
        public int speed;
        public List<String> cycleColors = new ArrayList<>();
    }

    public static class BadgesConfig {
        public boolean enabled;
        public Map<String, String> badges = new LinkedHashMap<>();
    }

    public static class ChatParticlesConfig {
        public boolean enabled;
        public String type;
        public int count;
        public double speed;
        public double spread;
        public String permission;
    }

    public static class PersonalSoundConfig {
        public boolean enabled;
        public String defaultSound;
        public String permission;
    }

    public static class MentionConfig {
        public boolean enabled;
        public String format;
        public String permission;
        public boolean clickable;
        public boolean soundEnabled;
        public String soundType;
        public float soundVolume;
        public float soundPitch;
        public boolean actionbarEnabled;
        public String actionbarMessage;
        public boolean titleEnabled;
        public String titleText;
        public String titleSubtitle;
        public int titleFadeIn;
        public int titleStay;
        public int titleFadeOut;
        public boolean particlesEnabled;
        public String particlesType;
        public int particlesCount;
        public double particlesSpeed;
        public boolean personalHighlight;
        public String highlightColor;
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
