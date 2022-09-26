package tk.alex3025.headstones.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import tk.alex3025.headstones.Headstones;
import tk.alex3025.headstones.utils.Headstone;
import tk.alex3025.headstones.utils.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RightClickListener extends ListenerBase {

    @EventHandler
    public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction().isRightClick() && event.getHand().name().equals("HAND")) {
            Headstone headstone = Headstone.fromBlock(event.getClickedBlock());

            if (headstone != null)
                new Message(event.getPlayer(), new HashMap<>() {{
                    put("username", headstone.getOwner().getName());
                    put("datetime", new SimpleDateFormat(Headstones.getInstance().getConfig().getString("date-format")).format(new Date(headstone.getTimestamp())));
                }}).translation("headstone-info").send();
        }
    }

}
