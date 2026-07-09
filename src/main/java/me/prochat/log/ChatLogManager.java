package me.prochat.log;

import me.prochat.ProChatPlugin;
import org.bukkit.entity.Player;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ChatLogManager {

    private final ProChatPlugin plugin;
    private final Map<UUID, Deque<LogEntry>> messageCache = new HashMap<>();
    private int maxMessages = 50;
    private boolean logToFile;

    public ChatLogManager(ProChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload(int maxMessages, boolean logToFile) {
        this.maxMessages = maxMessages;
        this.logToFile = logToFile;
    }

    public void log(Player sender, String message) {
        log0(sender.getUniqueId(), sender.getName(), message, "CHAT");
    }

    public void log(Player sender, Player target, String message) {
        log0(sender.getUniqueId(), sender.getName(), "-> " + target.getName() + ": " + message, "PM");
    }

    private void log0(UUID uuid, String name, String message, String type) {
        LogEntry entry = new LogEntry(LocalDateTime.now(), name, message, type);
        Deque<LogEntry> deque = messageCache.computeIfAbsent(uuid, k -> new ConcurrentLinkedDeque<>());
        deque.addLast(entry);
        while (deque.size() > maxMessages) {
            deque.pollFirst();
        }
        if (logToFile) {
            writeToFile(name, message, type);
        }
    }

    public List<LogEntry> getHistory(Player player) {
        Deque<LogEntry> deque = messageCache.get(player.getUniqueId());
        if (deque == null) return List.of();
        return new ArrayList<>(deque);
    }

    public void clearHistory(Player player) {
        messageCache.remove(player.getUniqueId());
    }

    public void clearAll() {
        messageCache.clear();
    }

    private void writeToFile(String name, String message, String type) {
        File dataFolder = new File(plugin.getDataFolder(), "logs");
        if (!dataFolder.exists()) dataFolder.mkdirs();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File logFile = new File(dataFolder, "chat-" + date + ".log");
        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            out.println("[" + ts + "][" + type + "] " + name + ": " + message);
        } catch (IOException ignored) {}
    }

    public record LogEntry(LocalDateTime time, String playerName, String message, String type) {
        public String format() {
            return "&7[" + time.format(DateTimeFormatter.ofPattern("HH:mm")) + "] &f"
                    + playerName + "&7: &f" + message;
        }
    }
}
