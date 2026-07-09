package me.prochat;

import me.prochat.antiabuse.AntiSpamManager;
import me.prochat.channel.ChannelManager;
import me.prochat.chat.ChatListener;
import me.prochat.commands.ProChatCommand;
import me.prochat.config.ConfigManager;
import me.prochat.hook.PlaceholderAPIHook;
import me.prochat.mention.MentionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProChatPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ChannelManager channelManager;
    private AntiSpamManager antiSpamManager;
    private MentionManager mentionManager;
    private PlaceholderAPIHook papiHook;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        channelManager = new ChannelManager(this);
        channelManager.reload();

        antiSpamManager = new AntiSpamManager(this);
        mentionManager = new MentionManager(this);

        papiHook = new PlaceholderAPIHook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        var cmd = getCommand("prochat");
        if (cmd != null) cmd.setExecutor(new ProChatCommand(this));

        getLogger().info("ProChat enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ProChat disabled!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public ChannelManager getChannelManager() { return channelManager; }
    public AntiSpamManager getAntiSpamManager() { return antiSpamManager; }
    public MentionManager getMentionManager() { return mentionManager; }
    public PlaceholderAPIHook getPapiHook() { return papiHook; }
}
