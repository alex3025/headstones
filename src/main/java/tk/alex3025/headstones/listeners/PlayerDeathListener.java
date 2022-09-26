package tk.alex3025.headstones.listeners;

import com.bgsoftware.wildloaders.api.npc.ChunkLoaderNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import tk.alex3025.headstones.utils.ExperienceManager;
import tk.alex3025.headstones.utils.Headstone;

public class PlayerDeathListener extends ListenerBase {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();

        // Check if the player is a chunk loader from the WildLoaders plugin
        if (player instanceof ChunkLoaderNPC) return;

        boolean keepExperience = !event.getKeepLevel() && player.hasPermission("headstones.keep-experience");
        boolean keepInventory = !event.getKeepInventory() && player.hasPermission("headstones.keep-inventory");

        if (!(keepExperience && keepInventory) || !player.getInventory().isEmpty() || ExperienceManager.getExperience(player) != 0)
            new Headstone(player).onPlayerDeath(event, keepExperience, keepInventory);
    }

}
