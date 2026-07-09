package me.prochat.channel;

import me.prochat.ProChatPlugin;
import me.prochat.config.Settings;
import org.bukkit.entity.Player;

import java.util.*;

public class ChannelManager {

    private final ProChatPlugin plugin;
    private final Map<String, ChatChannel> channels = new LinkedHashMap<>();
    private final Map<UUID, ChatChannel> playerChannels = new HashMap<>();

    public ChannelManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        channels.clear();
        Settings settings = plugin.getConfigManager().getSettings();
        for (Map.Entry<String, Settings.ChannelConfig> entry : settings.channels.entrySet()) {
            if (entry.getValue().enabled) {
                channels.put(entry.getKey(), new ChatChannel(entry.getKey(), entry.getValue()));
            }
        }
    }

    public ChatChannel getChannel(String name) {
        return channels.get(name.toLowerCase());
    }

    public Collection<ChatChannel> getChannels() {
        return channels.values();
    }

    public ChatChannel getPlayerChannel(Player player) {
        return playerChannels.getOrDefault(player.getUniqueId(), getDefaultChannel());
    }

    public void setPlayerChannel(Player player, ChatChannel channel) {
        if (channel != null) {
            playerChannels.put(player.getUniqueId(), channel);
        }
    }

    public ChatChannel getDefaultChannel() {
        return channels.values().stream()
                .min(Comparator.comparingInt(ChatChannel::getPriority))
                .orElse(null);
    }

    public List<Player> getRecipients(Player sender, ChatChannel channel) {
        List<Player> result = new ArrayList<>();
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (channel.isGlobal()) {
                if (channel.hasPermission(online)) {
                    result.add(online);
                }
            } else {
                if (!sender.getWorld().equals(online.getWorld())) continue;
                if (!channel.hasPermission(online)) continue;
                double dist = sender.getLocation().distanceSquared(online.getLocation());
                double rangeSq = (double) channel.getRange() * channel.getRange();
                if (dist <= rangeSq) {
                    result.add(online);
                }
            }
        }
        return result;
    }
}
