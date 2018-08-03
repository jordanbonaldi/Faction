package net.neferett.linaris.faction.listeners.events;

import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import net.neferett.linaris.faction.handlers.ConfigReader;

public class EntityExplode implements Listener {

	@EventHandler
	public void onExplode(final EntityExplodeEvent e) {
		if (ConfigReader.getInstance().getWarzone().isInside(e.getEntity().getLocation()))
			e.setCancelled(true);

		final List<Block> destroyed = e.blockList();
		final Iterator<Block> it = destroyed.iterator();
		while (it.hasNext()) {
			final Block block = it.next();
			if (ConfigReader.getInstance().getWarzone().isInside(block.getLocation()))
				it.remove();
		}
	}

	@EventHandler
	public void onExplodeChangeBlock(final EntityChangeBlockEvent e) {
		if (ConfigReader.getInstance().getWarzone().isInside(e.getBlock().getLocation()))
			e.setCancelled(true);
	}

}
