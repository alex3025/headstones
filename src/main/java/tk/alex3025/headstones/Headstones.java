package tk.alex3025.headstones;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tk.alex3025.headstones.commands.HeadstonesCommand;
import tk.alex3025.headstones.commands.subcommands.ClearDatabaseCommand;
import tk.alex3025.headstones.commands.subcommands.ReloadConfigCommand;
import tk.alex3025.headstones.listeners.BlockBreakListener;
import tk.alex3025.headstones.listeners.PlayerDeathListener;
import tk.alex3025.headstones.listeners.RightClickListener;
import tk.alex3025.headstones.utils.ConfigFile;

public final class Headstones extends JavaPlugin {

    private static Headstones instance;

    private ConfigFile config;
    private ConfigFile messages;
    private ConfigFile database;

    @Override
    public void onEnable() {
        instance = this;

        this.loadConfigurationFiles();
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadConfigurationFiles() {
        this.config = new ConfigFile(this,"config.yml");
        this.messages = new ConfigFile(this,"messages.yml");
        this.database = new ConfigFile(this,"database.yml");
    }

    private void registerListeners() {
        new PlayerDeathListener();
        new BlockBreakListener();
        new RightClickListener();
    }

    private void registerCommands() {
        new HeadstonesCommand();

        // Subcommands
        new ClearDatabaseCommand();
        new ReloadConfigCommand();
    }

    public static Headstones getInstance() {
        return instance;
    }

    // Config getters
    @Override
    public @NotNull ConfigFile getConfig() {
        return config;
    }

    public ConfigFile getMessages() {
        return messages;
    }

    public ConfigFile getDatabase() {
        return database;
    }

}
