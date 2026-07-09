package me.prochat.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class VoiceChatHook {

    private volatile boolean available;
    private Object api;
    private Method muteMethod;
    private Method unmuteMethod;
    private Method isMutedMethod;

    public VoiceChatHook(JavaPlugin plugin) {
        Plugin voicechat = Bukkit.getPluginManager().getPlugin("Voicechat");
        if (voicechat == null && Bukkit.getPluginManager().getPlugin("SimpleVoiceChat") == null) {
            return;
        }

        try {
            Class<?> serviceClass = Class.forName("de.maxhenkel.voicechat.api.BukkitVoicechatService");
            Object service = Bukkit.getServicesManager().load(serviceClass);
            if (service == null) return;

            Class<?> pluginInterface = Class.forName("de.maxhenkel.voicechat.api.VoicechatPlugin");
            Class<?> apiClass = Class.forName("de.maxhenkel.voicechat.api.BukkitVoicechatApi");

            Object proxy = Proxy.newProxyInstance(
                    pluginInterface.getClassLoader(),
                    new Class<?>[]{pluginInterface},
                    (obj, method, args) -> {
                        switch (method.getName()) {
                            case "getPluginId" -> {
                                return "ProChat";
                            }
                            case "onServerStart" -> {
                                if (args != null && args.length > 0) {
                                    Object event = args[0];
                                    Method getApi = event.getClass().getMethod("getVoicechat");
                                    Object voicechatApi = getApi.invoke(event);
                                    if (apiClass.isInstance(voicechatApi)) {
                                        api = voicechatApi;
                                        muteMethod = apiClass.getMethod("mutePlayer", UUID.class);
                                        unmuteMethod = apiClass.getMethod("unmutePlayer", UUID.class);
                                        isMutedMethod = apiClass.getMethod("isPlayerMuted", UUID.class);
                                        available = true;
                                        plugin.getLogger().info("[ProChat] VoiceChat hook enabled");
                                    }
                                }
                                return null;
                            }
                            default -> {
                                return null;
                            }
                        }
                    }
            );

            serviceClass.getMethod("registerPlugin", pluginInterface).invoke(service, proxy);
        } catch (Exception e) {
            plugin.getLogger().warning("[ProChat] Failed to hook VoiceChat: " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void mutePlayer(Player player) {
        if (!available || muteMethod == null || api == null) return;
        try {
            muteMethod.invoke(api, player.getUniqueId());
        } catch (Exception ignored) {}
    }

    public void unmutePlayer(Player player) {
        if (!available || unmuteMethod == null || api == null) return;
        try {
            unmuteMethod.invoke(api, player.getUniqueId());
        } catch (Exception ignored) {}
    }

    public boolean isMuted(Player player) {
        if (!available || isMutedMethod == null || api == null) return false;
        try {
            return (boolean) isMutedMethod.invoke(api, player.getUniqueId());
        } catch (Exception e) {
            return false;
        }
    }

    public void shutdown() {
        available = false;
        api = null;
        muteMethod = null;
        unmuteMethod = null;
        isMutedMethod = null;
    }
}
