package net.neferett.linaris.faction.utils;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RandomTp {

	private static void onWaterNormal(final Player p) {
		final Random random = new Random();
		Location teleportLocation = null;
		boolean onLand = false;
		while (!onLand) {
			final int x = random.nextInt(2 * 1000) - 1000 + p.getWorld().getSpawnLocation().getBlockX();
			int y = 150;
			final int z = random.nextInt(2 * 1000) - 1000 + p.getWorld().getSpawnLocation().getBlockZ();

			teleportLocation = new Location(p.getWorld(), x, y, z);

			while (teleportLocation.getBlock().getType() == Material.AIR) {
				teleportLocation = new Location(p.getWorld(), x, y, z);
				y--;
			}

			if (!teleportLocation.getBlock().isLiquid())
				onLand = true;
			else {
				onWaterNormal(p);
				break;
			}
		}
		p.teleport(new Location(p.getWorld(), teleportLocation.getX(), teleportLocation.getY() + 1.0D,
				teleportLocation.getZ()));

		p.sendMessage("§7Vous avez été téléporté !");

	}

	@SuppressWarnings("deprecation")
	public static void RandomTeleport(final Player p) {
		final Random random = new Random();
		Location teleportLocation = null;
		boolean onLand = false;
		while (!onLand) {
			final int x = random.nextInt(2 * 1000) - 1000 + p.getWorld().getSpawnLocation().getBlockX();
			int y = 150;
			final int z = random.nextInt(2 * 1000) - 1000 + p.getWorld().getSpawnLocation().getBlockZ();

			teleportLocation = new Location(p.getWorld(), x, y, z);

			while (teleportLocation.getBlock().getType() == Material.AIR) {
				teleportLocation = new Location(p.getWorld(), x, y, z);
				y--;
			}

			if (!teleportLocation.getBlock().isLiquid())
				onLand = true;
			else {
				onWaterNormal(p);
				break;
			}
		}
		if (!onLand) {
			p.teleport(new Location(p.getWorld(), teleportLocation.getX(), teleportLocation.getY() + 1.0D,
					teleportLocation.getZ()));

			p.sendMessage("§7Vous avez été téléporté !");
		}
		p.playEffect(p.getLocation(), Effect.POTION_BREAK, 10);

	}

}
