package tk.alex3025.headstones.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExperienceManager {

    public static int getExperience(int level) {
        int xp = 0;

        if (level >= 0 && level <= 15)
            xp = (int) Math.round(Math.pow(level, 2) + 6 * level);
        else if (level > 15 && level <= 30)
            xp = (int) Math.round((2.5 * Math.pow(level, 2) - 40.5 * level + 360));
        else if (level > 30)
            xp = (int) Math.round(((4.5 * Math.pow(level, 2) - 162.5 * level + 2220)));

        return xp;
    }

    public static int getExperience(@NotNull Player player) {
        return Math.round(player.getExp() * player.getExpToLevel()) + getExperience(player.getLevel());
    }

    public static void setExperience(Player player, int amount) {
        float a = 0, b = 0, c = -amount;

        if (amount > getExperience(0) && amount <= getExperience(15)) {
            a = 1;
            b = 6;
        } else if (amount > getExperience(15) && amount <= getExperience(30)) {
            a = 2.5f;
            b = -40.5f;
            c += 360;
        } else if (amount > getExperience(30)) {
            a = 4.5f;
            b = -162.5f;
            c += 2220;
        }

        int level = (int) Math.floor((-b + Math.sqrt(Math.pow(b, 2) - (4 * a * c))) / (2 * a));
        int xp = amount - getExperience(level);

        player.setLevel(level);
        player.setExp(0);
        player.giveExp(xp);
    }
}
