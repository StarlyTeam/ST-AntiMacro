package net.starly.antimacro.listener;

import net.starly.antimacro.manager.StackManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class StackListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("starly.antimacro.bypass")) return;

        StackManager.getInstance().addCount(event.getPlayer());
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event) {
        if (event.getPlayer().hasPermission("starly.antimacro.bypass")) return;

        StackManager.getInstance().addCount(event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        if (player.hasPermission("starly.antimacro.bypass")) return;

        StackManager.getInstance().addCount(player);
    }
}
