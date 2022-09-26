package tk.alex3025.headstones.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SubcommandBase {

    private static final List<SubcommandBase> registeredSubcommands = new ArrayList<>();

    private final String name;
    private String permission = null;
    private boolean playersOnly = false;

    public SubcommandBase(@NotNull String name) {
        this.name = name;
        this.registerSubcommand();
    }

    public SubcommandBase(@NotNull String name, String permission) {
        this(name);
        this.permission = permission;
    }

    public SubcommandBase(@NotNull String name, String permission, boolean playersOnly) {
        this(name, permission);
        this.playersOnly = playersOnly;
    }

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public boolean hasPermission(CommandSender sender) {
        return this.getPermission() == null || (this.getPermission() != null && sender.hasPermission(this.getPermission()));
    }

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isPlayersOnly() {
        return this.playersOnly;
    }

    public void registerSubcommand() {
        registeredSubcommands.add(this);
    }

    public static List<SubcommandBase> getRegisteredSubcommands() {
        return registeredSubcommands;
    }

    public static @Nullable SubcommandBase getSubcommand(String subcommand) {
        for (SubcommandBase registered : registeredSubcommands)
            if (registered.getName().equals(subcommand))
                return registered;
        return null;
    }

}
