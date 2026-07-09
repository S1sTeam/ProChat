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

    private String lang(String key) {
        return plugin.getConfigManager().getRawMessage(key);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("prochat.chatlog")) {
            sender.sendMessage(FormatManager.parse(lang("no_permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FormatManager.parse(lang("chatlog_usage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("prochat.chatlog.clear")) {
                sender.sendMessage(FormatManager.parse(lang("no_permission")));
                return true;
            }
            plugin.getChatLogManager().clearAll();
            String name = sender instanceof Player ? sender.getName() : "Console";
            Bukkit.broadcast(FormatManager.parse(
                    lang("chatlog_cleared_broadcast").replace("{sender}", name)
            ), "prochat.chatlog");
            sender.sendMessage(FormatManager.parse(lang("chatlog_cleared")));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(FormatManager.parse(lang("chatlog_not_found")));
            return true;
        }

        List<ChatLogManager.LogEntry> history = plugin.getChatLogManager().getHistory(target);
        if (history.isEmpty()) {
            sender.sendMessage(FormatManager.parse(
                    lang("chatlog_empty").replace("{player}", target.getName())
            ));
            return true;
        }

        sender.sendMessage(FormatManager.parse(
                lang("chatlog_header").replace("{player}", target.getName())
        ));
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
