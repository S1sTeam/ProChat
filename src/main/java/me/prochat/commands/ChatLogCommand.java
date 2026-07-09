package me.prochat.commands;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import me.prochat.log.ChatLogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChatLogCommand implements TabExecutor {

    private final ProChatPlugin plugin;

    public ChatLogCommand(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("prochat.chatlog")) {
            sender.sendMessage(FormatManager.parse("&cNo permission."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FormatManager.parse("&cUsage: /chatlog <player> | /chatlog clear"));
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("prochat.chatlog.clear")) {
                sender.sendMessage(FormatManager.parse("&cNo permission."));
                return true;
            }
            plugin.getChatLogManager().clearAll();
            Bukkit.broadcast(FormatManager.parse("&8[&bProChat&8] &7Chat log has been cleared by " + sender.getName()), "prochat.chatlog");
            sender.sendMessage(FormatManager.parse("&aChat log cleared."));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(FormatManager.parse("&cPlayer not found or offline."));
            return true;
        }

        List<ChatLogManager.LogEntry> history = plugin.getChatLogManager().getHistory(target);
        if (history.isEmpty()) {
            sender.sendMessage(FormatManager.parse("&7No messages logged for &f" + target.getName() + "&7."));
            return true;
        }

        sender.sendMessage(FormatManager.parse("&8&m---&8[ &bLast messages: " + target.getName() + " &8]&m---"));
        for (ChatLogManager.LogEntry entry : history) {
            sender.sendMessage(FormatManager.parse(entry.format()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                       @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return java.util.Arrays.asList("clear", Bukkit.getOnlinePlayers().stream()
                    .findFirst().map(Player::getName).orElse("player"));
        }
        return List.of();
    }
}
