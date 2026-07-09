package me.prochat.commands;

import me.prochat.ProChatPlugin;
import me.prochat.channel.ChatChannel;
import me.prochat.chat.FormatManager;
import me.prochat.config.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        if (args.length == 0) {
            sender.sendMessage(FormatManager.parse(
                    "&8[&bProChat&8] &7v" + plugin.getPluginMeta().getVersion()
            ));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                if (!sender.hasPermission("prochat.reload")) {
                    sender.sendMessage(noPerm());
                    return true;
                }
                plugin.getConfigManager().load();
                plugin.getChannelManager().reload();
                sender.sendMessage(msg("reload_success"));
            }
            case "channel" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(msgRaw("player_only"));
                    return true;
                }
                if (!player.hasPermission("prochat.reload")) {
                    sender.sendMessage(noPerm());
                    return true;
                }
                handleChannel(player, args);
            }
            case "sound" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(msgRaw("player_only"));
                    return true;
                }
                if (!player.hasPermission("prochat.sound")) {
                    sender.sendMessage(noPerm());
                    return true;
                }
                handleSound(player, args);
            }
            default -> {
                sender.sendMessage(FormatManager.parse(
                        "&cUsage: /prochat [reload|channel|sound]"
                ));
            }
        }
        return true;
    }

    private void handleChannel(Player player, String[] args) {
        if (args.length < 2) {
            ChatChannel current = plugin.getChannelManager().getPlayerChannel(player);
            player.sendMessage(FormatManager.parse(
                    msgRaw("channel_current")
                            .replace("{channel}", current.getColor() + current.getDisplay())
            ));
            return;
        }
        String channelName = args[1].toLowerCase();
        ChatChannel target = plugin.getChannelManager().getChannel(channelName);
        if (target == null) {
            player.sendMessage(msgRaw("channel_not_found"));
            return;
        }
        if (!target.hasPermission(player)) {
            player.sendMessage(msgRaw("channel_no_permission"));
            return;
        }
        plugin.getChannelManager().setPlayerChannel(player, target);
        player.sendMessage(FormatManager.parse(
                msgRaw("channel_switched")
                        .replace("{channel}", target.getColor() + target.getDisplay())
        ));
    }

    private void handleSound(Player player, String[] args) {
        if (args.length < 2) {
            String current = plugin.getSoundManager().getSound(player);
            if (current == null) current = "default";
            player.sendMessage(FormatManager.parse(
                    msgRaw("sound_current").replace("{sound}", current)
            ));
            player.sendMessage(FormatManager.parse(msgRaw("sound_usage")));
            return;
        }
        String soundName = args[1].toUpperCase();
        try {
            @SuppressWarnings("deprecation")
            Sound sound = Sound.valueOf(soundName);
            if (sound != null) {
                plugin.getSoundManager().setSound(player, soundName);
                player.sendMessage(FormatManager.parse(
                        msgRaw("sound_set").replace("{sound}", soundName)
                ));
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(FormatManager.parse(msgRaw("sound_not_found")));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> base = new java.util.ArrayList<>(List.of("reload", "channel", "sound"));
            return base;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("channel")) {
            return plugin.getChannelManager().getChannels().stream()
                    .map(ChatChannel::getName)
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("sound")) {
            String partial = args[1].toLowerCase();
            List<String> sounds = new java.util.ArrayList<>();
            String[] common = {
                "ENTITY_EXPERIENCE_ORB_PICKUP", "BLOCK_NOTE_BLOCK_PLING", "ENTITY_PLAYER_LEVELUP",
                "ENTITY_PLAYER_HURT", "UI_BUTTON_CLICK", "ENTITY_VILLAGER_YES",
                "ENTITY_VILLAGER_NO", "ENTITY_BLAZE_HURT", "BLOCK_ANVIL_USE",
                "ENTITY_FIREWORK_ROCKET_BLAST", "ITEM_TRIDENT_THUNDER", "ENTITY_LIGHTNING_BOLT_THUNDER",
                "BLOCK_NOTE_BLOCK_HAT", "BLOCK_NOTE_BLOCK_SNARE", "ENTITY_WITHER_DEATH",
                "ENTITY_ENDER_DRAGON_GROWL", "MUSIC_DISC_13", "ENTITY_CREEPER_PRIMED",
                "BLOCK_LAVA_POP", "WEATHER_RAIN"
            };
            for (String sound : common) {
                if (sound.toLowerCase().contains(partial)) {
                    sounds.add(sound);
                }
            }
            return sounds;
        }
        return List.of();
    }

    private Component msg(String path) {
        return FormatManager.parse(plugin.getConfigManager().getMessage(path));
    }

    private String msgRaw(String path) {
        return plugin.getConfigManager().getRawMessage(path);
    }

    private Component noPerm() {
        return FormatManager.parse(plugin.getConfigManager().getRawMessage("no_permission"));
    }
}
