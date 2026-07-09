package me.prochat.pm;

import me.prochat.chat.FormatManager;
import org.bukkit.entity.Player;

import java.util.*;

public class SocialSpyManager {

    private final Set<UUID> spies = new HashSet<>();

    public boolean toggle(Player player) {
        if (spies.contains(player.getUniqueId())) {
            spies.remove(player.getUniqueId());
            return false;
        }
        spies.add(player.getUniqueId());
        return true;
    }

    public boolean isSpying(Player player) {
        return spies.contains(player.getUniqueId());
    }

    public void notify(Player sender, Player target, String message) {
        String fmt = "&8[&cSS&8] &7{sender} &7-> &7{target}&7: &f{message}"
                .replace("{sender}", sender.getName())
                .replace("{target}", target.getName())
                .replace("{message}", message);

        for (Player spy : spyPlayers()) {
            if (!spy.equals(sender) && !spy.equals(target)) {
                spy.sendMessage(FormatManager.parse(fmt));
            }
        }
    }

    private List<Player> spyPlayers() {
        List<Player> result = new ArrayList<>();
        for (UUID uuid : spies) {
            Player p = org.bukkit.Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) result.add(p);
        }
        return result;
    }
}
