package me.prochat.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private YamlConfiguration messagesConfig;
    private final Settings settings = new Settings();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        settings.load(plugin.getConfig());

        File msgFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!msgFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(msgFile);
    }

    public Settings getSettings() { return settings; }

    public String getMessage(String path) {
        String msg = messagesConfig.getString(path);
        if (msg == null) return "&cMessage not found: " + path;
        return getPrefix() + msg;
    }

    public String getRawMessage(String path) {
        String msg = messagesConfig.getString(path);
        return msg != null ? msg : "&cMessage not found: " + path;
    }

    public String getPrefix() {
        return messagesConfig.getString("prefix", "&8[&bProChat&8] &7");
    }
}
