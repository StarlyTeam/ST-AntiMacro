package net.starly.antimacro.manager;

import net.starly.antimacro.AntiMacroMain;
import net.starly.antimacro.context.MessageContent;
import net.starly.antimacro.context.MessageType;
import net.starly.antimacro.util.MacroUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class StackManager {

    private static StackManager instance;
    public static StackManager getInstance() {
        if (instance == null) instance = new StackManager();
        return instance;
    }

    private final HashMap<UUID, Integer> countMap = new HashMap<>();

    public Integer getCount(Player p) { return countMap.getOrDefault(p.getUniqueId(), 0); }

    public void resetCount(Player p) { countMap.remove(p.getUniqueId()); }

    public void addCount(Player p) {
        int value = getCount(p) + 1;
        if (value >= MessageContent.getInstance().getInt(MessageType.CONFIG, "checkMacroStack")) {
            MacroUtil.checkMacro(p);
            resetCount(p);
            return;
        }

        countMap.put(p.getUniqueId(), value);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline()) return;
                if (!countMap.containsKey(p.getUniqueId())) return;
                if (getCount(p) != value) return;
                resetCount(p);
            }
        }.runTaskLater(AntiMacroMain.getInstance(), MessageContent.getInstance().getInt(MessageType.CONFIG, "resetTimer") * 20L);

    }
}
