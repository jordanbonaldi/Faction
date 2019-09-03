package net.neferett.linaris.faction.listeners.events;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.TextLine;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.handlers.DataReader;
import net.neferett.linaris.faction.utils.TimeUtils;
import net.neferett.linaris.utils.ScoreboardSign;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public class JoinAndLeave implements Listener {
	private static boolean	spawned	= false;

	@EventHandler
	public void JoinEvent(final PlayerJoinEvent e) throws IOException {
		e.setJoinMessage("");
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());

		p.sendMessage("");
		p.sendMessage("§7Bienvenue sur note §d§n§oPvPFaction");
		p.sendMessage("");
		if (!p.getPlayerData().contains("connected-" + ConfigReader.getInstance().getGameName())
				|| !new DataReader(p.getName().toLowerCase()).isExists()) {
			p.getPlayerData().setBoolean("connected-" + ConfigReader.getInstance().getGameName(), true);
			p.setScore(5);
			p.getDataReader().setKills(0);
			p.getDataReader().setDeaths(0);
			p.getDataReader().setLevel(0);
			p.getDataReader().setMoney(0);
			p.tp(ConfigReader.getInstance().getSpawn());
		}

		if (ConfigReader.getInstance().isClass() && p.getPlayerData().getRank().getModerationLevel() < 1
				&& !p.getPlayerData().getRank().getName().equals(p.getDataReader().getRank().getName()))
			p.setRank(p.getDataReader().getRank().getName());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPlayerLeft(final PlayerQuitEvent event) {
		if (DamageEvent.time.containsKey(event.getPlayer())
				&& TimeUtils.CreateTestCoolDown(15).test(DamageEvent.time.get(event.getPlayer()))) {
			event.getPlayer().getInventory().forEach(item -> {
				if (item != null && item.getType() != null && !item.getType().equals(org.bukkit.Material.AIR)
						&& item.getTypeId() != 397)
					event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), item);
			});
			Arrays.asList(event.getPlayer().getInventory().getArmorContents()).forEach(item -> {
				if (item != null && item.getType() != null && !item.getType().equals(org.bukkit.Material.AIR))
					event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), item);
			});
			event.getPlayer().getInventory().setBoots(null);
			event.getPlayer().getInventory().setHelmet(null);
			event.getPlayer().getInventory().setChestplate(null);
			event.getPlayer().getInventory().setLeggings(null);
			event.getPlayer().getInventory().clear();
		}
		final ScoreboardSign bar = ScoreboardSign.get(event.getPlayer());
		if (bar != null)
			bar.destroy();
		PlayerManagers.get().removePlayer(event.getPlayer());
		event.setQuitMessage("");
	}

}
