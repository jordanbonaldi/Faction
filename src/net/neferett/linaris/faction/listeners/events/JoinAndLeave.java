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

public class JoinAndLeave extends PlayerManagers implements Listener {

	static String				footer	= "§6Notre site§f: §bhttp://linaris.fr/ §f- §6Notre Twitter§f: §c@LinarisMC \n §6Boutique sur§f: §b§nhttp://linaris.fr/shop/";
	static String				header	= "§eVous êtes connecté sur §aplay.linaris.fr \n §6Rejoignez nous sur TeamSpeak§f: §bts.linaris.fr";
	protected static boolean	spawned	= false;

	Hologram createMagicBoxHolo(final String name, final Location lc) {
		final Hologram holo = new Hologram(name, lc);
		Main.getInstanceMain().getHologramManager().addActiveHologram(holo);
		holo.addLine(new TextLine(holo, "§dBoite Mystere"));
		holo.addLine(new TextLine(holo, "§eCrate §f- §aClique droit pour ouvrir"));
		holo.addLine(new TextLine(holo, "§eClique gauche §7pour regarder les §cGains"));
		return holo;
	}

	@EventHandler
	public void JoinEvent(final PlayerJoinEvent e) throws IOException {
		e.setJoinMessage("");
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());

		p.sendMessage("");
		p.sendMessage("§7Bienvenue sur note §d§n§oPvPFaction Magie");
		p.sendMessage("");
		p.sendMessage("§7Le but du jeu est de §crankup §7jusqu'aux meilleurs grades");
		p.sendMessage("§7pour ainsi améliorer votre §acapacité §7et débloquer tous les §dsorts §7!");
		p.sendMessage("");
		p.sendMessage("§7Pour rankup utilisez la commande§f: §c/classe ou /cl");
		p.sendMessage("§7Vous verrez ainsi les differents avantages du prochain grade");
		p.sendMessage("§7Pour le débloquer il vous faudra de la §emonnaie §7ou des §ctokens");
		p.sendMessage("");
		p.sendMessage("§7Pour gagner de la §emonnaie§7 vous devez soit§f:");
		p.sendMessage("    §f- §cFaire des kills (10$ par kill) ");
		p.sendMessage("    §f- §cSoit en faisant une farm a nourriture !");
		p.sendMessage("");
		p.sendMessage("§c§oPour davantages de questions adresses-toi à un modo !");

		if (!p.getPlayerData().contains("connected-" + ConfigReader.getInstance().getGameName())
				|| !new DataReader(p.getName().toLowerCase()).isExists()) {
			p.getPlayerData().setBoolean("connected-" + ConfigReader.getInstance().getGameName(), true);
			p.setScore(5);
			p.getDataReader().setKills(0);
			p.getDataReader().setDeaths(0);
			p.getDataReader().setLevel(0);
			p.getDataReader().setMoney(0);
			p.getDataReader().setRank("Recrue");
			p.getDataReader().setAchivement(0);
			p.tp(ConfigReader.getInstance().getSpawn());
		}

		if (p.getPlayerData().getRank().getModerationLevel() < 1
				&& !p.getPlayerData().getRank().getName().equals(p.getDataReader().getRank().getName()))
			p.setRank(p.getDataReader().getRank().getName());

		if (!spawned) {
			TaskManager.runTaskLater(() -> {
				this.createMagicBoxHolo("loc1", ConfigReader.getInstance().getLocation("config.holo1"));
				this.createMagicBoxHolo("loc2", ConfigReader.getInstance().getLocation("config.holo2"));
				this.createMagicBoxHolo("loc3", ConfigReader.getInstance().getLocation("config.holo3"));
				this.createMagicBoxHolo("loc4", ConfigReader.getInstance().getLocation("config.holo4"));
				this.createMagicBoxHolo("loc5", ConfigReader.getInstance().getLocation("config.holo5"));

			}, 20);
			spawned = true;
		}
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
