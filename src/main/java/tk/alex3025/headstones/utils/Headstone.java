package tk.alex3025.headstones.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.alex3025.headstones.Headstones;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class Headstone {

    private final String uuid;
    private final OfflinePlayer owner;
    private final Location location;
    private final long timestamp;
    private final int experience;
    private final ItemStack[] inventory;

    public Headstone(@NotNull Player player) {
        this.uuid = UUID.randomUUID().toString();

        this.owner = player;
        this.location = player.getLocation();
        this.timestamp = Instant.now().toEpochMilli();

        this.experience = ExperienceManager.getExperience(player);
        this.inventory = player.getInventory().getContents();
    }

    private Headstone(String uuid, OfflinePlayer player, Location location, long timestamp, int experience, ItemStack[] inventory) {
        this.uuid = uuid;

        this.owner = player;
        this.location = location;
        this.timestamp = timestamp;

        this.experience = experience;
        this.inventory = inventory;
    }

    public static @Nullable Headstone fromUUID(String uuid) {
        ConfigurationSection headstones = Headstone.getHeadstonesData().getConfigurationSection("headstones");
        if (headstones != null) {
            ConfigurationSection hs = headstones.getConfigurationSection(uuid);
            if (hs != null) {
                Location location = new Location(Bukkit.getWorld(hs.getString("world")), hs.getDouble("x"), hs.getDouble("y"), hs.getDouble("z"));

                ItemStack[] inventory = null;
                if (hs.getString("inventory") != null)
                    try {
                        inventory = InventorySerializer.deserialize(hs.getString("inventory"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(hs.getString("owner")));
                return new Headstone(uuid, owner, location, hs.getLong("timestamp"), hs.getInt("experience", 0), inventory);
            }
        }
        return null;
    }

    public static @Nullable Headstone fromLocation(Location location) {
        ConfigurationSection headstones = Headstone.getHeadstonesData().getConfigurationSection("headstones");

        if (headstones != null)
            for (String uuid : headstones.getKeys(false)) {
                Headstone hs = Headstone.fromUUID(uuid);
                if (hs != null)
                    if (location.equals(hs.getLocation()))
                        return hs;
            }
        return null;
    }

    public static @Nullable Headstone fromBlock(@NotNull Block block) {
        return block.getType().equals(Material.PLAYER_HEAD) ? Headstone.fromLocation(block.getLocation()) : null;
    }

    public void onPlayerDeath(PlayerDeathEvent event, boolean keepExperience, boolean keepInventory) {
        Location skullLocation = this.createPlayerSkull();

        if (skullLocation != null) {
            // Disable drops
            event.getDrops().clear();
            event.setShouldDropExperience(false);

            this.savePlayerData(skullLocation, keepExperience, keepInventory);
        }
    }

    public void onBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        this.restorePlayerInventory(player);
        this.deletePlayerData();

        event.setDropItems(Headstones.getInstance().getConfig().getBoolean("drop-player-head"));

        player.spawnParticle(Particle.REDSTONE, this.location.add(0.5, 0.2, 0.5), 10, 0.2, 0.1, 0.2, new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1.5F));
        player.playSound(this.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);

        new Message(player).translation("headstone-broken").prefixed(false).send();
    }

    private void savePlayerData(@NotNull Location skullLocation, boolean keepExperience, boolean keepInventory) {
        ConfigFile headstonesFile = Headstone.getHeadstonesData();
        ConfigurationSection hs = headstonesFile.createSection("headstones." + this.uuid);

        hs.set("owner", this.owner.getUniqueId().toString());

        hs.set("x", (int) Math.floor(skullLocation.getX()));
        hs.set("y", (int) Math.floor(skullLocation.getY()));
        hs.set("z", (int) Math.floor(skullLocation.getZ()));
        hs.set("world", skullLocation.getWorld().getName());

        hs.set("timestamp", Instant.now().toEpochMilli());

        if (keepExperience)
            hs.set("experience", this.experience);

        if (keepInventory)
            hs.set("inventory", InventorySerializer.serialize(this.inventory));

        headstonesFile.save();
    }

    private void deletePlayerData() {
        ConfigFile headstonesFile = Headstone.getHeadstonesData();
        ConfigurationSection headstones = headstonesFile.getConfigurationSection("headstones");

        if (headstones != null)
            headstones.set(this.uuid, null);

        headstonesFile.save();
    }

    private void restorePlayerInventory(Player player) {
        ExperienceManager.setExperience(player, this.experience);

        if (this.inventory != null)
            for (int i = 0, size = this.inventory.length; i < size; i++)
                if (this.inventory[i] != null) {
                    PlayerInventory playerInventory = player.getInventory();
                    if (playerInventory.getItem(i) == null)
                        playerInventory.setItem(i, this.inventory[i]);
                    else {
                        HashMap<Integer, ItemStack> drops = playerInventory.addItem(this.inventory[i]);
                        for (ItemStack item : drops.values())
                            player.getWorld().dropItem(this.location, item);
                    }
                }
    }

    private @Nullable Block checkForSafeBlock() {
        int playerX = this.location.getBlockX();
        int playerY = this.location.getBlockY();
        int playerZ = this.location.getBlockZ();

        int radius = 0;

        for (int x = playerX - radius; x <= playerX + radius; x++) {
            for (int y = playerY - radius; y <= playerY + radius; y++)
                for (int z = playerZ - radius; z <= playerZ + radius; z++) {
                    Block block = this.location.getWorld().getBlockAt(x,y,z);
                    if (block.getType().isEmpty())
                        return block;
                }

            if (radius <= 5)
                radius++;
        }

        return null;
    }

    private @Nullable Location createPlayerSkull() {
        Block block = this.checkForSafeBlock();

        if (block != null) {
            block.setType(Material.PLAYER_HEAD);

            if (block.getState() instanceof Skull skull) {
                skull.setOwningPlayer(this.owner);

                BlockData data = skull.getBlockData();

                List<BlockFace> faces = new ArrayList<>(List.of(BlockFace.values()));
                // Remove invalid faces
                faces.remove(BlockFace.UP);
                faces.remove(BlockFace.DOWN);
                faces.remove(BlockFace.SELF);

                ((Rotatable) data).setRotation(faces.get(new Random().nextInt(faces.size())));

                skull.setBlockData(data);
                skull.update();

                return block.getLocation();
            }
        }
        return null;
    }

    public boolean isOwner(@NotNull Player player) {
        return player.getUniqueId().equals(this.owner.getUniqueId());
    }

    public OfflinePlayer getOwner() {
        return this.owner;
    }

    public Location getLocation() {
        return this.location;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    private static ConfigFile getHeadstonesData() {
        return Headstones.getInstance().getDatabase();
    }

}
