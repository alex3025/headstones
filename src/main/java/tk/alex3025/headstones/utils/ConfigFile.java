package tk.alex3025.headstones.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import tk.alex3025.headstones.Headstones;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile extends YamlConfiguration {

    private final static List<ConfigFile> CONFIGS = new ArrayList<>();

    private final Headstones instance;
    private File file;

    public ConfigFile(Headstones instance, String filename) {
        this.instance = instance;

        try {
            this.createOrLoadConfig(filename);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        CONFIGS.add(this);
    }

    public void createOrLoadConfig(String filename) throws IOException, InvalidConfigurationException {
        this.file = new File(this.instance.getDataFolder(), filename);

        if (!this.file.exists()) {
            this.file.getParentFile().mkdirs();
            instance.saveResource(filename, false);
        }

        this.load(this.file);
    }

    public void save() {
        try {
            this.save(this.file);
            this.reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            this.load(this.file);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException ignored) {
            try {
                this.createOrLoadConfig(this.file.getName());
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reloadAll() {
        for (ConfigFile config : CONFIGS)
            config.reload();
    }

}
