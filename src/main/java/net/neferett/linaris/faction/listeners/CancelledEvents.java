package net.neferett.linaris.faction.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.minecraft.server.v1_8_R3.Material;
import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.handlers.ConfigReader;

public class CancelledEvents implements Listener {

	@EventHandler
	public void flawingWater(final BlockFromToEvent e) {
		final Block b = e.getToBlock();
		if (b == null)
			return;
		if (b.getType().equals(Material.WATER) && ConfigReader.getInstance().getWarzone().isInside(b.getLocation()))
			e.setCancelled(true);
		else if (b.getType().equals(Material.LAVA) && ConfigReader.getInstance().getWarzone().isInside(b.getLocation()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e) {
		if (ConfigReader.getInstance().getWarzone().isInside(e.getPlayer().getLocation()) && !e.getPlayer().isOp())
			e.setCancelled(true);
		else if (ConfigReader.getInstance().getWarzone().isInside(e.getBlock().getLocation()) && !e.getPlayer().isOp())
			e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent e) {

		if (ConfigReader.getInstance().getWarzone().isInside(e.getBlock().getLocation()) && !e.getPlayer().isOp())
			e.setCancelled(true);

		if (ConfigReader.getInstance().getWarzone().isInside(e.getPlayer().getLocation()) && !e.getPlayer().isOp())
			e.setCancelled(true);
	}

	@EventHandler
	public void onMonsterSpawn(final EntitySpawnEvent e) {
		if (e.getEntity() instanceof Monster && Main.getInstanceMain().getCb().isInside(e.getEntity().getLocation())
				&& (!ConfigReader.getInstance().isWarzoneInSpawn() || Main.getInstanceMain().getCb2().isInside(e.getEntity().getLocation())))
			e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(final WeatherChangeEvent e) {
		if (!e.getWorld().getName().equals("world"))
			return;
		e.setCancelled(true);
	}

}
