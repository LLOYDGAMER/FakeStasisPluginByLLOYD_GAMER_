
package com.lg.smp.stasis;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FakeStasisPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Location> stasisLocations = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("FakeStasisPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FakeStasisPlugin disabled!");
    }

    @EventHandler
    public void onPearlThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl pearl)) return;
        if (!(pearl.getShooter() instanceof Player player)) return;

        stasisLocations.put(player.getUniqueId(), pearl.getLocation());
        player.sendMessage("§aStasis location saved!");
    }

    @EventHandler
    public void onPearlTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (stasisLocations.containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRodUse(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.FISHING_ROD) return;

        Player player = event.getPlayer();
        Location loc = stasisLocations.get(player.getUniqueId());

        if (loc != null) {
            player.teleport(loc);
            player.sendMessage("§aTeleported to stasis location!");
        } else {
            player.sendMessage("§cNo stasis location saved!");
        }
    }
}
