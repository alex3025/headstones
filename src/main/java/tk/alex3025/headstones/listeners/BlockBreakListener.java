package tk.alex3025.headstones.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import tk.alex3025.headstones.utils.Headstone;
import tk.alex3025.headstones.utils.Message;

public class BlockBreakListener extends ListenerBase {

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Headstone headstone = Headstone.fromBlock(event.getBlock());

        if (headstone != null)
            if (headstone.isOwner(event.getPlayer()))
                headstone.onBreak(event);
            else {
                event.setCancelled(true);
                new Message(event.getPlayer()).translation("cannot-break-others").prefixed(false).send();
            }
    }

}
