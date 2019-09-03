package net.neferett.linaris.faction.listeners.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.api.PlayerData;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.utils.CuboidRegion;

public class MoveListener implements Listener {

	private final CuboidRegion cb = new CuboidRegion(ConfigReader.getInstance().getPos1(),
			ConfigReader.getInstance().getPos2());

	@EventHandler
	public void onFood(final FoodLevelChangeEvent e) {
		final Player p = (Player) e.getEntity();
		if (this.cb.isInside(p.getLocation()) && e.getFoodLevel() < 10)
			e.setCancelled(true);
	}

	@EventHandler
	public void onMove(final PlayerMoveEvent e) {

		if (this.cb.isInside(e.getPlayer().getLocation()) && e.getPlayer().getLocation().getBlockY() < 10
				|| e.getPlayer().getWorld().getName().contains("nether") && e.getPlayer().getLocation().getY() > 126) {
			e.getPlayer().teleport(ConfigReader.getInstance().getSpawn());
			return;
		}

		if (e.getPlayer().getWorld().getName().toLowerCase().contains("nether")
				&& e.getPlayer().getLocation().getBlockY() >= 127)
			e.getPlayer().teleport(ConfigReader.getInstance().getSpawn());

	}

	@EventHandler
	public void onTP(final PlayerTeleportEvent e) {
		final PlayerData pd = BukkitAPI.get().getPlayerDataManager().getPlayerData(e.getPlayer().getName());

		if (pd.contains("invisible") && pd.getBoolean("invisible"))
			return;
		if (pd.getRank().getModerationLevel() > 1)
			return;
		e.getPlayer().setFlying(false);
		e.getPlayer().setAllowFlight(false);
	}

}
