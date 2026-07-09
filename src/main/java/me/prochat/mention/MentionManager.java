package me.prochat.mention;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import me.prochat.config.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionManager {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w{2,16})");
    private final ProChatPlugin plugin;

    public MentionManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public record MentionResult(Component baseMessage, Map<UUID, Component> personalizedMessages, Set<Player> mentioned) {}

    public MentionResult process(Player sender, Component message) {
        String plain = LegacyComponentSerializer.legacySection().serialize(message);
        Settings.MentionConfig cfg = plugin.getConfigManager().getSettings().mention;
        if (cfg == null || !cfg.enabled) {
            return new MentionResult(message, Collections.emptyMap(), Collections.emptySet());
        }

        Set<Player> mentioned = new HashSet<>();
        Map<UUID, Component> personalized = new HashMap<>();

        Matcher matcher = MENTION_PATTERN.matcher(plain);
        if (!matcher.find()) {
            return new MentionResult(message, Collections.emptyMap(), Collections.emptySet());
        }

        matcher.reset();
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String name = matcher.group(1);
            Player target = Bukkit.getPlayerExact(name);
            if (target != null && target.hasPermission(cfg.permission)) {
                mentioned.add(target);
                String formatted = cfg.format.replace("{player}", name);
                if (cfg.clickable) {
                    formatted = "&b&l@" + name + "&r";
                    Component mentionComp = FormatManager.parse(formatted)
                            .clickEvent(ClickEvent.suggestCommand("/msg " + name + " "));
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(
                            LegacyComponentSerializer.legacySection().serialize(mentionComp)
                    ));
                } else {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(formatted));
                }
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);

        Component baseMsg = FormatManager.parse(sb.toString());

        for (Player target : mentioned) {
            Component personalMsg = baseMsg;
            if (cfg.personalHighlight) {
                String highlighted = cfg.highlightColor + plain;
                personalMsg = FormatManager.parse(highlighted).clickEvent(null);
                personalized.put(target.getUniqueId(), personalMsg);
            }

            String alert = plugin.getConfigManager().getRawMessage("mention_alert")
                    .replace("{player}", sender.getName());
            target.sendMessage(FormatManager.parse(alert));

            if (cfg.actionbarEnabled) {
                String abMsg = cfg.actionbarMessage.replace("{player}", sender.getName());
                target.sendActionBar(FormatManager.parse(abMsg));
            }

            if (cfg.titleEnabled) {
                target.showTitle(Title.title(
                        FormatManager.parse(cfg.titleText),
                        FormatManager.parse(cfg.titleSubtitle.replace("{player}", sender.getName())),
                        Title.Times.times(
                                Duration.ofMillis(cfg.titleFadeIn * 50L),
                                Duration.ofMillis(cfg.titleStay * 50L),
                                Duration.ofMillis(cfg.titleFadeOut * 50L)
                        )
                ));
            }

            if (cfg.soundEnabled) {
                try {
                    @SuppressWarnings("deprecation")
                    Sound sound = Sound.valueOf(cfg.soundType);
                    if (sound != null) {
                        target.playSound(target.getLocation(), sound, SoundCategory.MASTER, cfg.soundVolume, cfg.soundPitch);
                    }
                } catch (IllegalArgumentException ignored) {}
            }

            if (cfg.particlesEnabled) {
                try {
                    Particle particle = Particle.valueOf(cfg.particlesType);
                    Location loc = target.getLocation().add(0, 1.5, 0);
                    target.getWorld().spawnParticle(particle, loc, cfg.particlesCount, 0.5, 0.5, 0.5, cfg.particlesSpeed);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return new MentionResult(baseMsg, personalized, mentioned);
    }
}
