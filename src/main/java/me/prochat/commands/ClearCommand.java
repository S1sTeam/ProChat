package me.prochat.commands;

import me.prochat.chat.FormatManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ClearCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("prochat.clear")) {
            sender.sendMessage(FormatManager.parse("&cNo permission."));
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage(Component.text(""));
            }
        }
        String name = sender instanceof Player ? sender.getName() : "Console";
        Bukkit.broadcast(FormatManager.parse("&8[&bProChat&8] &7Chat cleared by &f" + name));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                       @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
