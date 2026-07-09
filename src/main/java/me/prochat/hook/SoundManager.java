package me.prochat.hook;

import me.prochat.ProChatPlugin;
import me.prochat.config.Settings;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoundManager {

    private final ProChatPlugin plugin;
    private final Map<UUID, String> playerSounds = new HashMap<>();
    private File dataFile;
    private YamlConfiguration data;

    public SoundManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        dataFile = new File(plugin.getDataFolder(), "sounds.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException ignored) {}
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        playerSounds.clear();
        for (String key : data.getKeys(false)) {
            try {
                playerSounds.put(UUID.fromString(key), data.getString(key));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save() {
        if (data == null) return;
        for (Map.Entry<UUID, String> entry : playerSounds.entrySet()) {
            data.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            data.save(dataFile);
        } catch (IOException ignored) {}
    }

    public String getSound(Player player) {
        Settings.PersonalSoundConfig cfg = plugin.getConfigManager().getSettings().personalSound;
        if (cfg == null || !cfg.enabled) return null;
        return playerSounds.getOrDefault(player.getUniqueId(), cfg.defaultSound);
    }

    public void setSound(Player player, String sound) {
        playerSounds.put(player.getUniqueId(), sound);
        save();
    }

    public void playSound(Player player, String soundName, float volume, float pitch) {
        if (soundName == null) return;
        try {
            @SuppressWarnings("deprecation")
            Sound sound = Sound.valueOf(soundName);
            if (sound != null) {
                player.playSound(player.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public void playPersonalMessageSound(Player recipient) {
        String sound = getSound(recipient);
        if (sound != null) {
            playSound(recipient, sound, 1.0f, 1.0f);
        }
    }
}
