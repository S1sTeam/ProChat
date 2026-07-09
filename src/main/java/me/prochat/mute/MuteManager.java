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
                Bukkit.broadcast(FormatManager.parse("&8[&bProChat&8] &aChat has been automatically unmuted."));
            }, seconds * 20L).getTaskId();
        }
        String msg = "&8[&bProChat&8] &cChat has been muted" +
                (seconds > 0 ? " for " + seconds + "s" : "") +
                (reason != null && !reason.isEmpty() ? ": &f" + reason : ".");
        Bukkit.broadcast(FormatManager.parse(msg));
    }

    public void unmuteAll() {
        muted = false;
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        Bukkit.broadcast(FormatManager.parse("&8[&bProChat&8] &aChat has been unmuted."));
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
