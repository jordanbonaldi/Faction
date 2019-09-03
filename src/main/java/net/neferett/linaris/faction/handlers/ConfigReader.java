package net.neferett.linaris.faction.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.faction.classes.DefinedRank;
import net.neferett.linaris.faction.classes.DefinedRankManager;
import net.neferett.linaris.faction.events.players.M_Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.TextLine;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.utils.CuboidRegion;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ConfigReader {

	private static ConfigReader instance;

	public static ConfigReader getInstance() {
		return instance == null ? new ConfigReader() : instance;
	}

	private FileConfiguration configfile;

	private File file;

	public ConfigReader() {
		instance = this;
		this.file = new File("plugins/Faction/config.yml");
		this.configfile = YamlConfiguration.loadConfiguration(this.file);
		try {
			this.configfile.save(this.file);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean checkInside() {
		return this.configfile.getBoolean("config.check");
	}

	public boolean getChectClickable() {
		return this.configfile.getBoolean("config.chestclick");
	}

	public String getGameName() {
		return this.configfile.getString("config.gamename");
	}

	public Location getLocation(final String configpath) {
		return new Location(Bukkit.getWorld(this.configfile.getString(configpath + ".world")),
				this.configfile.getDouble(configpath + ".x"), this.configfile.getDouble(configpath + ".y"),
				this.configfile.getDouble(configpath + ".z"), this.configfile.getInt(configpath + ".yaw"),
				this.configfile.getInt(configpath + ".pitch"));
	}

	public List<Location> getLocationChest() {
		final List<Location> locs = new ArrayList<>();
		this.configfile.getList("chests").forEach(loc -> {
			final String[] locarray = loc.toString().split(":");
			locs.add(new Location(Bukkit.getWorld(locarray[0]), Integer.parseInt(locarray[1]),
					Integer.parseInt(locarray[2]), Integer.parseInt(locarray[3])));
		});
		return locs;
	}

	public double getMaxHeight() {
		return this.configfile.getDouble("config.maxheight");
	}

	public Location getPos1() {
		return this.getLocation("config.inside.p1");
	}

	public Location getPos2() {
		return this.getLocation("config.inside.p2");
	}

	public Location getPos3() {
		return this.getLocation("config.inside.p3");
	}

	public Location getPos4() {
		return this.getLocation("config.inside.p4");
	}

	public Location getSpawn() {
		return this.getLocation("config.spawn");
	}

	public CuboidRegion getWarzone() {
		return new CuboidRegion(this.getLocation("config.inside.p5"), this.getLocation("config.inside.p6"));
	}

	public CuboidRegion getWarzoneSafe() {
		return new CuboidRegion(this.getLocation("config.inside.p7"), this.getLocation("config.inside.p8"));
	}

	public boolean isMoney() {
		return this.configfile.getBoolean("config.money");
	}

	public boolean isClass() {
		return this.configfile.getBoolean("config.class");
	}

	public boolean isWarzoneInSpawn() {
		return this.configfile.getBoolean("config.warzoneinspawn");
	}

	public boolean warzoneSpawnCheck(M_Player p) {
		return !isWarzoneInSpawn() || !Main.getInstanceMain().getCb2().isInside(p.getLocation());
	}

	public void loadHolos() {
		final int holonb = this.configfile.getInt("config.holo.nb");
		for (int i = 1; i <= holonb; i++) {
			final Location loc = this.getLocation("config.holo.holo" + i);
			if (!loc.getChunk().isLoaded())
				loc.getChunk().load();
			final Hologram h = new Hologram("holo" + i, loc);
			Main.getInstanceMain().getHologramManager().addActiveHologram(h);
			this.configfile.getList("config.holo.holo" + i + ".lines").forEach(lines ->
				h.addLine(new TextLine(h, (String) lines))
			);
		}
	}

	public CuboidRegion SignClassementCuboid() {
		return new CuboidRegion(this.getLocation("config.inside.signp1"), this.getLocation("config.inside.signp2"));
	}

	private void save() {
		try {
			this.configfile.save(this.file);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void pushRanks() {
		DefinedRankManager.getInstance().getRanks().forEach(e -> {
			this.configfile.set("ranks." + e.getLinkedRank() + ".cooldown", e.getCooldown());
			this.configfile.set("ranks." + e.getLinkedRank() + ".items", e.getItems());
			this.configfile.set("ranks." + e.getLinkedRank() + ".commands", e.getCommands());
			this.configfile.set("ranks." + e.getLinkedRank() + ".spells", e.getSpells());
		});

		this.save();
	}

	public void loadDefinedRanks() {
		this.configfile.getConfigurationSection("ranks").getKeys(false).forEach(e -> {
			DefinedRank definedRank = new DefinedRank(Integer.parseInt(e));
			definedRank.setCommands(this.configfile.getStringList("ranks." + e + ".commands"));
			definedRank.setSpells(this.configfile.getStringList("ranks." + e + ".spells"));
			definedRank.setItems((List<ItemStack>) this.configfile.getList("ranks." + e + ".items"));
			definedRank.setCooldown(this.configfile.getLong("ranks." + e + ".cooldown"));

			DefinedRankManager.getInstance().getRanks().add(definedRank);
		});
	}
}
