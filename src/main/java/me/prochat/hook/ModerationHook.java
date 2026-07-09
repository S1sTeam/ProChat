package me.prochat.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class ModerationHook {

    private static final String[] MUTE_PERMS = {
            "litebans.muted",
            "advancedban.muted",
            "exile.muted",
            "libertybans.muted",
            "banmanager.muted",
            "essentials.muted",
            "cmi.muted",
            "bml.muted",
            "muted"
    };

    private Object essentialsInstance;
    private Method essentialsGetUser;
    private Method userIsMuted;

    private Object cmiInstance;
    private Method cmiGetPlayerManager;
    private Object cmiPlayerManager;
    private Method cmiGetUser;
    private Method cmiUserIsMuted;

    public ModerationHook() {
        hookEssentials();
        hookCMI();
    }

    private void hookEssentials() {
        Plugin essentials = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essentials == null) return;
        try {
            Class<?> essClass = Class.forName("com.earth2me.essentials.Essentials");
            Method getUsers = essClass.getMethod("getUser", java.util.UUID.class);
            Class<?> userClass = Class.forName("com.earth2me.essentials.User");
            Method isMuted = userClass.getMethod("isMuted");

            essentialsInstance = essentials;
            essentialsGetUser = getUsers;
            userIsMuted = isMuted;
            Bukkit.getLogger().info("[ProChat] EssentialsX mute hook enabled");
        } catch (Exception ignored) {}
    }

    private void hookCMI() {
        Plugin cmi = Bukkit.getPluginManager().getPlugin("CMI");
        if (cmi == null) return;
        try {
            Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
            Method getInstance = cmiClass.getMethod("getInstance");
            cmiInstance = getInstance.invoke(null);
            if (cmiInstance == null) return;

            Method getPlayerManager = cmiClass.getMethod("getPlayerManager");
            cmiPlayerManager = getPlayerManager.invoke(cmiInstance);
            if (cmiPlayerManager == null) return;

            Class<?> playerManagerClass = Class.forName("com.Zrips.CMI.Modules.Players.CMIPlayerManager");
            Method getCmiUser = playerManagerClass.getMethod("getUser", java.util.UUID.class);
            Class<?> cmiUserClass = Class.forName("com.Zrips.CMI.CMIPlayer");
            Method isMuted = cmiUserClass.getMethod("isMuted");

            cmiGetPlayerManager = getPlayerManager;
            cmiGetUser = getCmiUser;
            cmiUserIsMuted = isMuted;
            Bukkit.getLogger().info("[ProChat] CMI mute hook enabled");
        } catch (Exception ignored) {}
    }

    public boolean isMuted(Player player) {
        if (essentialsInstance != null) {
            try {
                Object user = essentialsGetUser.invoke(essentialsInstance, player.getUniqueId());
                if (user != null && (boolean) userIsMuted.invoke(user)) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        if (cmiPlayerManager != null) {
            try {
                Object cmiUser = cmiGetUser.invoke(cmiPlayerManager, player.getUniqueId());
                if (cmiUser != null && (boolean) cmiUserIsMuted.invoke(cmiUser)) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        for (String perm : MUTE_PERMS) {
            if (player.hasPermission(perm)) return true;
        }

        if (player.hasMetadata("muted")) {
            for (MetadataValue val : player.getMetadata("muted")) {
                if (val.asBoolean()) return true;
            }
        }

        try {
            if (player.hasMetadata("banned")) {
                for (MetadataValue val : player.getMetadata("banned")) {
                    if (val.asBoolean()) return true;
                }
            }
        } catch (Exception ignored) {}

        return false;
    }
}
