package me.prochat.pm;

import me.prochat.ProChatPlugin;
import me.prochat.chat.FormatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class MsgCommand implements TabExecutor {

    private final ProChatPlugin plugin;

    public MsgCommand(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(FormatManager.parse("&cOnly players can use this command."));
            return true;
        }

        return switch (cmd.getName().toLowerCase()) {
            case "msg", "tell", "whisper", "w", "m" -> handleMsg(player, args);
            case "reply", "r" -> handleReply(player, args);
            case "socialspy", "sspy" -> handleSocialSpy(player);
            case "ignore" -> handleIgnore(player, args);
            default -> false;
        };
    }

    private String lang(String key) {
        return plugin.getConfigManager().getRawMessage(key);
    }

    private boolean handleMsg(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(FormatManager.parse(lang("msg_usage")));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(FormatManager.parse(lang("msg_not_found")));
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage(FormatManager.parse(lang("msg_self")));
            return true;
        }
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        plugin.getPrivateMessageManager().sendMessage(player, target, message);
        return true;
    }

    private boolean handleReply(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(FormatManager.parse(lang("reply_usage")));
            return true;
        }
        String message = String.join(" ", args);
        plugin.getPrivateMessageManager().reply(player, message);
        return true;
    }

    private boolean handleSocialSpy(Player player) {
        if (!player.hasPermission("prochat.socialspy")) {
            player.sendMessage(FormatManager.parse(lang("no_permission")));
            return true;
        }
        boolean enabled = plugin.getSocialSpyManager().toggle(player);
        player.sendMessage(FormatManager.parse(
                lang("socialspy_toggle")
                        .replace("{status}", enabled ? lang("socialspy_enabled") : lang("socialspy_disabled"))
        ));
        return true;
    }

    private boolean handleIgnore(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage(FormatManager.parse(lang("ignore_usage")));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            player.sendMessage(FormatManager.parse(lang("msg_not_found")));
            return true;
        }
        if (target.equals(player)) {
            player.sendMessage(FormatManager.parse(lang("ignore_self")));
            return true;
        }
        if (target.hasPermission("prochat.ignore.bypass")) {
            player.sendMessage(FormatManager.parse(lang("ignore_bypass")));
            return true;
        }
        boolean ignored = plugin.getIgnoreManager().toggleIgnore(player, target);
        String status = ignored ? lang("ignore_now") : lang("ignore_no");
        player.sendMessage(FormatManager.parse(
                lang("ignore_toggle")
                        .replace("{player}", target.getName())
                        .replace("{status}", status)
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd,
                                       @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && cmd.getName().equalsIgnoreCase("ignore")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        if (args.length == 1 && (cmd.getName().equalsIgnoreCase("msg") ||
                cmd.getName().equalsIgnoreCase("tell") ||
                cmd.getName().equalsIgnoreCase("whisper"))) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> plugin.getVanishHook() == null || plugin.getVanishHook().canSee((Player) sender, p))
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
