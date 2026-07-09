package me.prochat;

import me.prochat.antiabuse.AntiSpamManager;
import me.prochat.channel.ChannelManager;
import me.prochat.chat.ChatListener;
import me.prochat.chat.FormatManager;
import me.prochat.chat.ShortcodeManager;
import me.prochat.commands.ChatLogCommand;
import me.prochat.commands.ClearCommand;
import me.prochat.commands.ProChatCommand;
import me.prochat.config.ConfigManager;
import me.prochat.hook.BadgeManager;
import me.prochat.hook.PlaceholderAPIHook;
import me.prochat.hook.SoundManager;
import me.prochat.hook.ModerationHook;
import me.prochat.hook.VanishHook;
import me.prochat.log.ChatLogManager;
import me.prochat.mention.MentionManager;
import me.prochat.mute.MuteManager;
import me.prochat.pm.IgnoreManager;
import me.prochat.pm.MsgCommand;
import me.prochat.pm.PrivateMessageManager;
import me.prochat.pm.SocialSpyManager;
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
    private ShortcodeManager shortcodeManager;
    private PrivateMessageManager privateMessageManager;
    private IgnoreManager ignoreManager;
    private SocialSpyManager socialSpyManager;
    private ChatLogManager chatLogManager;
    private MuteManager muteManager;
    private VanishHook vanishHook;
    private ModerationHook moderationHook;
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

        shortcodeManager = new ShortcodeManager();
        shortcodeManager.reload(getConfigManager().getSettings().shortcodes);

        privateMessageManager = new PrivateMessageManager(this);
        ignoreManager = new IgnoreManager();
        socialSpyManager = new SocialSpyManager(this);
        chatLogManager = new ChatLogManager(this);
        muteManager = new MuteManager(this);
        vanishHook = new VanishHook();
        moderationHook = new ModerationHook();

        var logCfg = getConfigManager().getSettings().chatlog;
        if (logCfg != null) {
            chatLogManager.reload(logCfg.maxMessages, logCfg.notifyOnLogout);
        }

        papiHook = new PlaceholderAPIHook(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"));
        badgeManager = new BadgeManager(getConfigManager().getSettings().badges);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        getCommand("prochat").setExecutor(new ProChatCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new MsgCommand(this));
        getCommand("socialspy").setExecutor(new MsgCommand(this));
        getCommand("ignore").setExecutor(new MsgCommand(this));
        getCommand("chatlog").setExecutor(new ChatLogCommand(this));
        getCommand("clear").setExecutor(new ClearCommand(this));

        var muteAllCmd = getCommand("muteall");
        if (muteAllCmd != null) {
            muteAllCmd.setExecutor((sender, cmd, label, args) -> {
                if (!sender.hasPermission("prochat.muteall")) {
                    sender.sendMessage(me.prochat.chat.FormatManager.parse(
                            getConfigManager().getRawMessage("no_permission")
                    ));
                    return true;
                }
                int time = 0;
                String reason = null;
                if (args.length > 0) {
                    try { time = Integer.parseInt(args[0]); } catch (NumberFormatException e) { reason = args[0]; }
                    if (args.length > 1) {
                        var list = new java.util.ArrayList<>(java.util.List.of(args));
                        if (time > 0) list.remove(0);
                        reason = String.join(" ", list);
                    }
                }
                if (time > 0) {
                    muteManager.muteAll(time, reason);
                } else {
                    if (muteManager.isMuted()) {
                        muteManager.unmuteAll();
                    } else {
                        muteManager.muteAll(0, reason);
                    }
                }
                return true;
            });
        }

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
    public ShortcodeManager getShortcodeManager() { return shortcodeManager; }
    public PrivateMessageManager getPrivateMessageManager() { return privateMessageManager; }
    public IgnoreManager getIgnoreManager() { return ignoreManager; }
    public SocialSpyManager getSocialSpyManager() { return socialSpyManager; }
    public ChatLogManager getChatLogManager() { return chatLogManager; }
    public MuteManager getMuteManager() { return muteManager; }
    public VanishHook getVanishHook() { return vanishHook; }
    public ModerationHook getModerationHook() { return moderationHook; }
}
