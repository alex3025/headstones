package tk.alex3025.headstones.commands.subcommands;

import org.bukkit.command.CommandSender;
import tk.alex3025.headstones.utils.ConfigFile;
import tk.alex3025.headstones.utils.Message;

public class ReloadConfigCommand {

    public ReloadConfigCommand() {
        new SubcommandBase("reload", "headstones.reload") {
            @Override
            public boolean onCommand(CommandSender sender, String[] args) {
                ConfigFile.reloadAll();
                Message.sendPrefixedMessage(sender, "&aSuccessfully reloaded all configuration files!");
                return true;
            }
        };
    }

}
