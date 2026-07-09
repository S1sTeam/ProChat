package me.prochat.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final Settings settings = new Settings();
    private YamlConfiguration messages;
    private String locale;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        settings.load(plugin.getConfig());

        locale = plugin.getConfig().getString("locale", "en");

        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        saveLang("en");
        if (!locale.equals("en")) {
            saveLang(locale);
        }

        File langFile = new File(langDir, locale + ".yml");
        if (langFile.exists()) {
            messages = YamlConfiguration.loadConfiguration(langFile);
        } else {
            try (InputStreamReader def = new InputStreamReader(
                    plugin.getResource("lang/en.yml"))) {
                messages = YamlConfiguration.loadConfiguration(def);
            } catch (IOException e) {
                messages = new YamlConfiguration();
            }
        }
    }

    private void saveLang(String loc) {
        File target = new File(plugin.getDataFolder(), "lang/" + loc + ".yml");
        if (!target.exists()) {
            try {
                plugin.saveResource("lang/" + loc + ".yml", false);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public Settings getSettings() { return settings; }
    public String getLocale() { return locale; }

    public String getMessage(String path) {
        String msg = messages.getString(path);
        if (msg == null) {
            msg = fallback(path);
        }
        return getPrefix() + (msg != null ? msg : "&cMissing message: " + path);
    }

    public String getRawMessage(String path) {
        String msg = messages.getString(path);
        if (msg == null) {
            msg = fallback(path);
        }
        return msg != null ? msg : "&cMissing message: " + path;
    }

    private String fallback(String path) {
        try (InputStreamReader reader = new InputStreamReader(
                plugin.getResource("lang/en.yml"))) {
            return YamlConfiguration.loadConfiguration(reader).getString(path);
        } catch (Exception e) {
            return null;
        }
    }

    public String getPrefix() {
        String p = messages.getString("prefix");
        return p != null ? p : "&8[&bProChat&8] &7";
    }
}
