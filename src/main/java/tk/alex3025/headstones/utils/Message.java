package tk.alex3025.headstones.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import tk.alex3025.headstones.Headstones;

import java.util.Map;

public class Message {

    private String rawMessage;
    private final CommandSender sender;
    private Map<String, String> placeholders;

    private boolean prefixed = true;

    public Message(CommandSender sender) {
        this.sender = sender;
    }

    public Message(CommandSender sender, Map<String, String> placeholders) {
        this.sender = sender;
        this.placeholders = placeholders;
    }

    public Message text(String message) {
        this.rawMessage = message;
        return this;
    }

    public Message translation(String key) {
        return this.text(Headstones.getInstance().getMessages().getString(key));
    }

    public Message prefixed(boolean prefixed) {
        this.prefixed = prefixed;
        return this;
    }

    public void send() {
        if (this.rawMessage != null && !this.rawMessage.isEmpty()) {
            // Format placeholders
            if (this.placeholders != null)
                for (Map.Entry<String, String> entry : this.placeholders.entrySet())
                    this.rawMessage = this.rawMessage.replace("%" + entry.getKey() + "%", entry.getValue());

            if (this.prefixed)
                Message.sendPrefixedMessage(this.sender, this.rawMessage);
            else
                Message.sendMessage(this.sender, this.rawMessage);
        }
    }

    public static void sendMessage(@NotNull CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void sendPrefixedMessage(CommandSender sender, String message) {
        String prefix = Headstones.getInstance().getMessages().getString("prefix");
        Message.sendMessage(sender, prefix + " " + message);
    }

    public static String getTranslation(String key) {
        return Headstones.getInstance().getMessages().getString(key);
    }

}
