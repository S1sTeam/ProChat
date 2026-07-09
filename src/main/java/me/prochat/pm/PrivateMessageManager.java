package me.prochat.pm;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.*;

public class PrivateMessageManager {

    private final ProChatPlugin plugin;
    private final Map<UUID, UUID> lastSender = new HashMap<>();

    public PrivateMessageManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean sendMessage(Player sender, Player target, String message) {
        var cfg = plugin.getConfigManager().getSettings().privateMessages;
        var l = plugin.getConfigManager();
        if (cfg == null || !cfg.enabled) {
            sender.sendMessage(FormatManager.parse(l.getRawMessage("msg_disabled")));
            return false;
        }

        if (!sender.hasPermission("prochat.msg")) {
            sender.sendMessage(FormatManager.parse(l.getRawMessage("no_permission")));
            return false;
        }

        if (!target.hasPermission("prochat.msg.receive")) {
            sender.sendMessage(FormatManager.parse(l.getRawMessage("msg_no_permission_target")));
            return false;
        }

        if (plugin.getIgnoreManager() != null && plugin.getIgnoreManager().isIgnored(target, sender)) {
            sender.sendMessage(FormatManager.parse(l.getRawMessage("msg_ignored")));
            return false;
        }

        String sendFmt = cfg.formatSend
                .replace("{target}", target.getName())
                .replace("{message}", message);
        String recvFmt = cfg.formatReceive
                .replace("{sender}", sender.getName())
                .replace("{message}", message);

        sender.sendMessage(FormatManager.parse(sendFmt));
        target.sendMessage(FormatManager.parse(recvFmt));

        lastSender.put(target.getUniqueId(), sender.getUniqueId());

        if (cfg.soundEnabled) {
            try {
                @SuppressWarnings("deprecation")
                Sound sound = Sound.valueOf(cfg.soundType);
                if (sound != null) {
                    target.playSound(target.getLocation(), sound, SoundCategory.MASTER, cfg.soundVolume, cfg.soundPitch);
                }
            } catch (IllegalArgumentException ignored) {}
        }

        if (plugin.getSocialSpyManager() != null) {
            plugin.getSocialSpyManager().notify(sender, target, message);
        }

        plugin.getChatLogManager().log(sender, target, message);
        return true;
    }

    public Player getLastSender(Player player) {
        UUID uuid = lastSender.get(player.getUniqueId());
        if (uuid == null) return null;
        Player p = Bukkit.getPlayer(uuid);
        if (p == null || !p.isOnline()) {
            lastSender.remove(player.getUniqueId());
            return null;
        }
        return p;
    }

    public void reply(Player player, String message) {
        Player target = getLastSender(player);
        if (target == null) {
            player.sendMessage(FormatManager.parse(plugin.getConfigManager().getRawMessage("msg_no_one")));
            return;
        }
        sendMessage(player, target, message);
    }
}
