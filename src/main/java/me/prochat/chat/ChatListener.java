package me.prochat.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.prochat.ProChatPlugin;
import me.prochat.antiabuse.AntiSpamManager;
import me.prochat.channel.ChannelManager;
import me.prochat.channel.ChatChannel;
import me.prochat.config.ConfigManager;
import me.prochat.config.Settings;
import me.prochat.hook.BadgeManager;
import me.prochat.hook.PlaceholderAPIHook;
import me.prochat.hook.SoundManager;
import me.prochat.mention.MentionManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private final ProChatPlugin plugin;

    public ChatListener(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String plainMessage = LegacyComponentSerializer.legacySection().serialize(event.message());

        ConfigManager configManager = plugin.getConfigManager();
        AntiSpamManager antiSpam = plugin.getAntiSpamManager();
        PlaceholderAPIHook papi = plugin.getPapiHook();
        BadgeManager badges = plugin.getBadgeManager();
        SoundManager soundManager = plugin.getSoundManager();
        Settings settings = configManager.getSettings();

        String check = antiSpam.check(player, plainMessage);
        if (check != null) {
            event.setCancelled(true);
            player.sendMessage(FormatManager.parse(check));
            return;
        }

        ChatChannel channel = plugin.getChannelManager().getPlayerChannel(player);

        String prefix = papi != null ? papi.getPrefix(player) : "";
        String suffix = papi != null ? papi.getSuffix(player) : "";
        String badgeStr = badges != null ? badges.getBadges(player) : "";
        String displayName = player.getName();

        if (settings.animatedGradient != null && settings.animatedGradient.enabled && !settings.animatedGradient.cycleColors.isEmpty()) {
            displayName = "<anim_gradient>" + player.getName() + "</anim_gradient>";
        }

        String formatted = settings.chatFormat
                .replace("{badges}", badgeStr)
                .replace("{channel_prefix}", channel.getPrefixString())
                .replace("{prefix}", prefix)
                .replace("{suffix}", suffix)
                .replace("{player}", player.getName())
                .replace("{display_name}", displayName)
                .replace("{display_name_anim}", displayName)
                .replace("{world}", player.getWorld().getName())
                .replace("{message}", plainMessage);

        Component baseComponent = FormatManager.parse(formatted, settings.animatedGradient != null ? settings.animatedGradient.cycleColors : null);

        MentionManager.MentionResult mentionResult = plugin.getMentionManager().process(player, baseComponent);
        Map<UUID, Component> personalized = mentionResult.personalizedMessages();

        event.setCancelled(true);

        for (Player recipient : plugin.getChannelManager().getRecipients(player, channel)) {
            Component msg = personalized.getOrDefault(recipient.getUniqueId(), mentionResult.baseMessage());
            recipient.sendMessage(msg);
        }

        plugin.getSLF4JLogger().info(
                LegacyComponentSerializer.legacySection().serialize(mentionResult.baseMessage())
        );

        if (settings.chatParticles != null && settings.chatParticles.enabled) {
            if (player.hasPermission(settings.chatParticles.permission)) {
                try {
                    Particle particle = Particle.valueOf(settings.chatParticles.type);
                    Location loc = player.getLocation().add(0, 1.0, 0);
                    player.getWorld().spawnParticle(
                            particle, loc, settings.chatParticles.count,
                            settings.chatParticles.spread, settings.chatParticles.spread, settings.chatParticles.spread,
                            settings.chatParticles.speed
                    );
                } catch (IllegalArgumentException ignored) {}
            }
        }

        if (soundManager != null) {
            for (Player recipient : plugin.getChannelManager().getRecipients(player, channel)) {
                soundManager.playPersonalMessageSound(recipient);
            }
        }
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
