package me.prochat;

import me.prochat.antiabuse.AntiSpamManager;
import me.prochat.channel.ChannelManager;
import me.prochat.chat.ChatListener;
import me.prochat.chat.FormatManager;
import me.prochat.commands.ProChatCommand;
import me.prochat.config.ConfigManager;
import me.prochat.hook.BadgeManager;
import me.prochat.hook.PlaceholderAPIHook;
import me.prochat.hook.SoundManager;
import me.prochat.mention.MentionManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class ProChatPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ChannelManager channelManager;
    private AntiSpamManager antiSpamManager;
    private MentionManager mentionManager;
    private PlaceholderAPIHook papiHook;
    private BadgeManager badgeManager;
    private SoundManager soundManager;
    private BukkitTask animTask;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.load();

        channelManager = new ChannelManager(this);
        channelManager.reload();

        antiSpamManager = new AntiSpamManager(this);
        mentionManager = new MentionManager(this);
        soundManager = new SoundManager(this);
        soundManager.load();

        papiHook = new PlaceholderAPIHook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
        badgeManager = new BadgeManager(getConfigManager().getSettings().badges);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        var cmd = getCommand("prochat");
        if (cmd != null) cmd.setExecutor(new ProChatCommand(this));

        animTask = getServer().getScheduler().runTaskTimer(this, FormatManager::tickAnimation, 0L, 2L);

        getLogger().info("ProChat enabled!");
    }

    @Override
    public void onDisable() {
        if (animTask != null) animTask.cancel();
        if (soundManager != null) soundManager.save();
        getLogger().info("ProChat disabled!");
    }

    public ConfigManager getConfigManager() { return configManager; }
    public ChannelManager getChannelManager() { return channelManager; }
    public AntiSpamManager getAntiSpamManager() { return antiSpamManager; }
    public MentionManager getMentionManager() { return mentionManager; }
    public PlaceholderAPIHook getPapiHook() { return papiHook; }
    public BadgeManager getBadgeManager() { return badgeManager; }
    public SoundManager getSoundManager() { return soundManager; }
}
