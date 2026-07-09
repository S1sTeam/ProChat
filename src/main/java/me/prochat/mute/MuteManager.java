package me.prochat.mute;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MuteManager {

    private final ProChatPlugin plugin;
    private boolean muted;
    private int taskId = -1;

    public MuteManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isMuted() {
        return muted;
    }

    public void muteAll(int seconds, String reason) {
        muted = true;
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        if (seconds > 0) {
            taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                muted = false;
                Bukkit.broadcast(FormatManager.parse(
                        plugin.getConfigManager().getRawMessage("muteall_auto")
                ));
            }, seconds * 20L).getTaskId();
        }
        String reasonPart = "";
        if (reason != null && !reason.isEmpty()) {
            reasonPart = ": &f" + reason;
        }
        String msg = plugin.getConfigManager().getRawMessage("muteall_muted")
                .replace("{reason}", reasonPart);
        Bukkit.broadcast(FormatManager.parse(msg));
    }

    public void unmuteAll() {
        muted = false;
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        Bukkit.broadcast(FormatManager.parse(
                plugin.getConfigManager().getRawMessage("muteall_unmuted")
        ));
    }

    public boolean canTalk(Player player) {
        if (!muted) return true;
        return player.hasPermission("prochat.bypass.muteall");
    }

    public boolean canPrivateMessage(Player player) {
        if (!muted) return true;
        return player.hasPermission("prochat.bypass.muteall");
    }
}
