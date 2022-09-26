package tk.alex3025.headstones.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.alex3025.headstones.Headstones;
import tk.alex3025.headstones.commands.subcommands.SubcommandBase;
import tk.alex3025.headstones.utils.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HeadstonesCommand implements CommandExecutor, TabCompleter {

    public HeadstonesCommand() {
        PluginCommand command = Headstones.getInstance().getCommand("headstones");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            String prefix = Message.getTranslation("prefix");
            Message.sendMessage(sender, "&8&m+----------+&r " + prefix + " &8&m+----------+");
            sender.sendMessage("");
            Message.sendMessage(sender, "   &7Author: &balex3025");
            sender.sendMessage("");
            Message.sendMessage(sender, "   &7Version: &b" + Headstones.getInstance().getDescription().getVersion());
            sender.sendMessage("");
            Message.sendMessage(sender, "&8&m+----------+&r " + prefix + " &8&m+----------+");
        } else {
            SubcommandBase subcommand = SubcommandBase.getSubcommand(args[0]);
            if (subcommand != null) {
                // Remove the subcommand name from the args
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);

                if (subcommand.isPlayersOnly() && !(sender instanceof Player)) {
                    new Message(sender).translation("player-only").send();
                    return true;
                }

                if (!subcommand.hasPermission(sender)) {
                    new Message(sender).translation("no-permissions").send();
                    return true;
                }

                return subcommand.onCommand(sender, newArgs);
            }
            new Message(sender).translation("unknown-subcommand").send();
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {
        List<String> matches = new ArrayList<>();
		if (args.length == 1) {
			for (SubcommandBase subcommand : SubcommandBase.getRegisteredSubcommands())
                if (subcommand.hasPermission(sender))
                    matches.add(subcommand.getName());

			return StringUtil.copyPartialMatches(args[0], matches, new ArrayList<>());
		}
		return Collections.emptyList();
    }

}
