package net.neferett.linaris.faction.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.TextLine;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.utils.CuboidRegion;

public class ConfigReader {

	protected static ConfigReader instance;

	public static ConfigReader getInstance() {
		return instance == null ? new ConfigReader() : instance;
	}

	protected FileConfiguration configfile;

	public ConfigReader() {
		instance = this;
		final File configFile = new File("plugins/Faction/config.yml");
		this.configfile = YamlConfiguration.loadConfiguration(configFile);
		try {
			this.configfile.save(configFile);
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

	public void loadHolos() {
		final int holonb = this.configfile.getInt("config.holo.nb");
		for (int i = 1; i <= holonb; i++) {
			final Location loc = this.getLocation("config.holo.holo" + i);
			if (!loc.getChunk().isLoaded())
				loc.getChunk().load();
			final Hologram h = new Hologram("holo" + i, loc);
			Main.getInstanceMain().getHologramManager().addActiveHologram(h);
			this.configfile.getList("config.holo.holo" + i + ".lines").forEach(lines -> {
				h.addLine(new TextLine(h, (String) lines));
			});
		}
	}

	public CuboidRegion SignClassementCuboid() {
		return new CuboidRegion(this.getLocation("config.inside.signp1"), this.getLocation("config.inside.signp2"));
	}
}
