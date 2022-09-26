package tk.alex3025.headstones.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.alex3025.headstones.Headstones;
import tk.alex3025.headstones.utils.ConfigFile;
import tk.alex3025.headstones.utils.Message;

import java.util.HashMap;
import java.util.Map;

public class ClearDatabaseCommand extends SubcommandBase {

    private final Map<String, Long> waitingConfirmPlayers = new HashMap<>();;

    public ClearDatabaseCommand() {
        super("cleardb", "headstones.cleardb");

        // Clear waiting players after 10 seconds
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Headstones.getInstance(), () -> {
            for (Map.Entry<String, Long> entry : this.waitingConfirmPlayers.entrySet())
                if (System.currentTimeMillis() - entry.getValue() > 10000)
                    this.waitingConfirmPlayers.remove(entry.getKey());
        }, 0, 40);
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (this.waitingConfirmPlayers.containsKey(player.getUniqueId().toString())) {
            this.waitingConfirmPlayers.remove(player.getUniqueId().toString());

            ConfigFile headstonesFile = Headstones.getInstance().getDatabase();
            headstonesFile.set("headstones", new HashMap<>());
            headstonesFile.save();

            Message.sendPrefixedMessage(player, "&aDatabase cleared!");
        } else {
            this.waitingConfirmPlayers.put(player.getUniqueId().toString(), System.currentTimeMillis());
            Message.sendPrefixedMessage(player, "&eAre you sure you want to clear the database? &c&lTHIS WILL MAKE ALL EXISTING HEADSTONES USELESS. &7Type &f/headstones cleardb &7again to confirm.");
        }
        return true;
    }

}
