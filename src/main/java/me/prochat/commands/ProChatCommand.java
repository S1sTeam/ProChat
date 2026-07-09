package me.prochat.commands;

import me.prochat.ProChatPlugin;
import me.prochat.channel.ChannelManager;
import me.prochat.channel.ChatChannel;
import me.prochat.chat.FormatManager;
import me.prochat.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProChatCommand implements TabExecutor {

    private final ProChatPlugin plugin;

    public ProChatCommand(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("prochat.reload")) {
            sender.sendMessage(FormatManager.parse(
                    plugin.getConfigManager().getRawMessage("no_permission")
            ));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FormatManager.parse(
                    "&8[&bProChat&8] &7v" + plugin.getPluginMeta().getVersion()
            ));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigManager().load();
                plugin.getChannelManager().reload();
                sender.sendMessage(FormatManager.parse(
                        plugin.getConfigManager().getMessage("reload_success")
                ));
            }
            case "channel" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(FormatManager.parse(
                            plugin.getConfigManager().getRawMessage("player_only")
                    ));
                    return true;
                }
                handleChannel(player, args);
            }
            default -> {
                sender.sendMessage(FormatManager.parse(
                        "&cUsage: /prochat [reload|channel]"
                ));
            }
        }
        return true;
    }

    private void handleChannel(Player player, String[] args) {
        ChannelManager cm = plugin.getChannelManager();
        ConfigManager cfg = plugin.getConfigManager();

        if (args.length < 2) {
            ChatChannel current = cm.getPlayerChannel(player);
            player.sendMessage(FormatManager.parse(
                    cfg.getRawMessage("channel_current")
                            .replace("{channel}", current.getColor() + current.getDisplay())
            ));
            return;
        }

        String channelName = args[1].toLowerCase();
        ChatChannel target = cm.getChannel(channelName);

        if (target == null) {
            player.sendMessage(FormatManager.parse(
                    cfg.getRawMessage("channel_not_found")
            ));
            return;
        }

        if (!target.hasPermission(player)) {
            player.sendMessage(FormatManager.parse(
                    cfg.getRawMessage("channel_no_permission")
            ));
            return;
        }

        cm.setPlayerChannel(player, target);
        player.sendMessage(FormatManager.parse(
                cfg.getRawMessage("channel_switched")
                        .replace("{channel}", target.getColor() + target.getDisplay())
        ));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "channel");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("channel")) {
            return plugin.getChannelManager().getChannels().stream()
                    .map(ChatChannel::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
