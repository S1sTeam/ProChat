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

    private static final String[] BAN_PERMS = {
            "litebans.banned",
            "advancedban.banned",
            "exile.banned",
            "libertybans.banned",
            "banmanager.banned",
            "banned"
    };

    private static final String[] FREEZE_PERMS = {
            "essentials.frozen",
            "cmi.frozen",
            "frozen"
    };

    private static final String[] JAIL_PERMS = {
            "essentials.jailed",
            "cmi.jailed",
            "jailed"
    };

    private Object essentialsInstance;
    private Method essentialsGetUser;
    private Method userIsMuted;
    private Method userIsBanned;
    private Method userIsFrozen;
    private Method userIsJailed;

    private Object cmiPlayerManager;
    private Method cmiGetUser;
    private Method cmiUserIsMuted;
    private Method cmiUserIsBanned;
    private Method cmiUserIsFrozen;
    private Method cmiUserIsJailed;

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

            essentialsInstance = essentials;
            essentialsGetUser = getUsers;
            userIsMuted = safeMethod(userClass, "isMuted");
            userIsBanned = safeMethod(userClass, "isBanned");
            userIsFrozen = safeMethod(userClass, "isFrozen");
            userIsJailed = safeMethod(userClass, "isJailed");

            if (userIsMuted != null || userIsBanned != null || userIsFrozen != null || userIsJailed != null) {
                Bukkit.getLogger().info("[ProChat] EssentialsX hook enabled");
            }
        } catch (Exception ignored) {}
    }

    private void hookCMI() {
        Plugin cmi = Bukkit.getPluginManager().getPlugin("CMI");
        if (cmi == null) return;
        try {
            Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
            Method getInstance = cmiClass.getMethod("getInstance");
            Object instance = getInstance.invoke(null);
            if (instance == null) return;

            Method getPlayerManager = cmiClass.getMethod("getPlayerManager");
            Object pm = getPlayerManager.invoke(instance);
            if (pm == null) return;

            Class<?> pmClass = Class.forName("com.Zrips.CMI.Modules.Players.CMIPlayerManager");
            Method getUser = pmClass.getMethod("getUser", java.util.UUID.class);
            Class<?> userClass = Class.forName("com.Zrips.CMI.CMIPlayer");

            cmiPlayerManager = pm;
            cmiGetUser = getUser;
            cmiUserIsMuted = safeMethod(userClass, "isMuted");
            cmiUserIsBanned = safeMethod(userClass, "isBanned");
            cmiUserIsFrozen = safeMethod(userClass, "isFrozen");
            cmiUserIsJailed = safeMethod(userClass, "isJailed");

            if (cmiUserIsMuted != null || cmiUserIsBanned != null || cmiUserIsFrozen != null || cmiUserIsJailed != null) {
                Bukkit.getLogger().info("[ProChat] CMI hook enabled");
            }
        } catch (Exception ignored) {}
    }

    private Method safeMethod(Class<?> clazz, String name) {
        try {
            return clazz.getMethod(name);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private boolean checkApi(Object instance, Method method) {
        if (instance == null || method == null) return false;
        try {
            return (boolean) method.invoke(instance);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkReflection(Player player, Object manager, Method getUserMethod,
                                     Object userInstance, Method checkMethod) {
        if (manager == null || getUserMethod == null || checkMethod == null) return false;
        try {
            Object user = getUserMethod.invoke(manager, player.getUniqueId());
            return user != null && (boolean) checkMethod.invoke(user);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkPerms(Player player, String[] perms) {
        for (String perm : perms) {
            if (player.hasPermission(perm)) return true;
        }
        return false;
    }

    private boolean checkMetadata(Player player, String key) {
        if (player.hasMetadata(key)) {
            for (MetadataValue val : player.getMetadata(key)) {
                if (val.asBoolean()) return true;
            }
        }
        return false;
    }

    public boolean isMuted(Player player) {
        if (checkReflection(player, essentialsInstance, essentialsGetUser, null, userIsMuted)) return true;
        if (checkReflection(player, cmiPlayerManager, cmiGetUser, null, cmiUserIsMuted)) return true;
        if (checkPerms(player, MUTE_PERMS)) return true;
        return checkMetadata(player, "muted");
    }

    public boolean isBanned(Player player) {
        if (checkReflection(player, essentialsInstance, essentialsGetUser, null, userIsBanned)) return true;
        if (checkReflection(player, cmiPlayerManager, cmiGetUser, null, cmiUserIsBanned)) return true;
        if (checkPerms(player, BAN_PERMS)) return true;
        return checkMetadata(player, "banned");
    }

    public boolean isFrozen(Player player) {
        if (checkReflection(player, essentialsInstance, essentialsGetUser, null, userIsFrozen)) return true;
        if (checkReflection(player, cmiPlayerManager, cmiGetUser, null, cmiUserIsFrozen)) return true;
        if (checkPerms(player, FREEZE_PERMS)) return true;
        return checkMetadata(player, "frozen");
    }

    public boolean isJailed(Player player) {
        if (checkReflection(player, essentialsInstance, essentialsGetUser, null, userIsJailed)) return true;
        if (checkReflection(player, cmiPlayerManager, cmiGetUser, null, cmiUserIsJailed)) return true;
        if (checkPerms(player, JAIL_PERMS)) return true;
        return checkMetadata(player, "jailed");
    }

    public boolean isPunished(Player player) {
        return isMuted(player) || isBanned(player) || isFrozen(player) || isJailed(player);
    }
}
