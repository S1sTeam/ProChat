package me.prochat.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.prochat.ProChatPlugin;
import me.prochat.antiabuse.AntiSpamManager;
import me.prochat.channel.ChannelManager;
import me.prochat.channel.ChatChannel;
import me.prochat.config.ConfigManager;
import me.prochat.config.Settings;
import me.prochat.hook.PlaceholderAPIHook;
import me.prochat.mention.MentionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    private final ProChatPlugin plugin;

    public ChatListener(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String plainMessage = LegacyComponentSerializer.legacySection().serialize(event.message());

        ChannelManager channelManager = plugin.getChannelManager();
        ConfigManager configManager = plugin.getConfigManager();
        AntiSpamManager antiSpam = plugin.getAntiSpamManager();
        MentionManager mentionManager = plugin.getMentionManager();
        PlaceholderAPIHook papi = plugin.getPapiHook();
        Settings settings = configManager.getSettings();

        ChatChannel channel = channelManager.getPlayerChannel(player);

        String check = antiSpam.check(player, plainMessage);
        if (check != null) {
            event.setCancelled(true);
            player.sendMessage(FormatManager.parse(check));
            return;
        }

        String formatted = settings.chatFormat
                .replace("{channel_prefix}", channel.getPrefixString())
                .replace("{prefix}", papi != null ? papi.getPrefix(player) : "")
                .replace("{suffix}", papi != null ? papi.getSuffix(player) : "")
                .replace("{player}", player.getName())
                .replace("{display_name}", LegacyComponentSerializer.legacySection().serialize(player.displayName()))
                .replace("{world}", player.getWorld().getName())
                .replace("{message}", plainMessage);

        Component chatComponent = FormatManager.parse(formatted);

        MentionManager.MentionResult mentionResult = mentionManager.process(player, chatComponent);
        Component finalMessage = mentionResult.message();

        event.setCancelled(true);

        for (Player recipient : channelManager.getRecipients(player, channel)) {
            recipient.sendMessage(finalMessage);
        }
        plugin.getSLF4JLogger().info(
                LegacyComponentSerializer.legacySection().serialize(finalMessage)
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String msg = plugin.getConfigManager().getSettings().joinFormat
                .replace("{player}", event.getPlayer().getName())
                .replace("{display_name}", LegacyComponentSerializer.legacySection().serialize(event.getPlayer().displayName()));
        event.joinMessage(FormatManager.parse(msg));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String msg = plugin.getConfigManager().getSettings().quitFormat
                .replace("{player}", event.getPlayer().getName())
                .replace("{display_name}", LegacyComponentSerializer.legacySection().serialize(event.getPlayer().displayName()));
        event.quitMessage(FormatManager.parse(msg));
    }
}
