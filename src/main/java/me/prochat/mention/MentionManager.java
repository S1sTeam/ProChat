package me.prochat.mention;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import me.prochat.config.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionManager {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w{2,16})");
    private final ProChatPlugin plugin;

    public MentionManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public record MentionResult(Component message, Set<Player> mentioned) {}

    public MentionResult process(Player sender, Component message) {
        String plain = LegacyComponentSerializer.legacySection().serialize(message);
        Settings.MentionConfig cfg = plugin.getConfigManager().getSettings().mention;
        if (!cfg.enabled) return new MentionResult(message, Collections.emptySet());

        Set<Player> mentioned = new HashSet<>();
        Matcher matcher = MENTION_PATTERN.matcher(plain);

        if (!matcher.find()) return new MentionResult(message, Collections.emptySet());

        matcher.reset();
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String name = matcher.group(1);
            Player target = Bukkit.getPlayerExact(name);
            if (target != null && target.hasPermission(cfg.permission)) {
                mentioned.add(target);
                String mentionFormatted = cfg.format.replace("{player}", name);
                String mentionStr = FormatManager.parse(mentionFormatted).toString();
                matcher.appendReplacement(sb, Matcher.quoteReplacement(mentionStr));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
        }
        matcher.appendTail(sb);

        Component finalMsg = FormatManager.parse(sb.toString());

        for (Player target : mentioned) {
            String alert = plugin.getConfigManager().getRawMessage("mention_alert")
                    .replace("{player}", sender.getName());
            target.sendMessage(FormatManager.parse(alert));
            if (cfg.soundEnabled) {
                @SuppressWarnings("deprecation")
                Sound sound = Sound.valueOf(cfg.soundType);
                if (sound != null) {
                    target.playSound(target.getLocation(), sound, SoundCategory.MASTER, cfg.soundVolume, cfg.soundPitch);
                }
            }
        }

        return new MentionResult(cfg.highlightMessage ? finalMsg : message, mentioned);
    }
}
