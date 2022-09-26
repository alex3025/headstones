package tk.alex3025.headstones.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import tk.alex3025.headstones.Headstones;

public abstract class ListenerBase implements Listener {

    public ListenerBase() {
        Bukkit.getPluginManager().registerEvents(this, Headstones.getInstance());
    }

}
