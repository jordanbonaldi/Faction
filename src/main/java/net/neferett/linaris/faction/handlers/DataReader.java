package net.neferett.linaris.faction.handlers;

import net.neferett.linaris.api.API;
import net.neferett.linaris.api.ranks.RankAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {

	protected FileConfiguration	configfile;

	protected File				data;
	private final String		p;

	public DataReader(final Player p) throws IOException {
		this.p = p.getName().toLowerCase();
		this.data = new File("plugins/Faction/data/" + this.p + ".dat");
		this.configfile = YamlConfiguration.loadConfiguration(this.data);
		this.configfile.save(this.data);
	}

	public DataReader(final String p) {
		this.p = p.toLowerCase();
		this.data = new File("plugins/Faction/data/" + this.p + ".dat");
		this.configfile = YamlConfiguration.loadConfiguration(this.data);
		try {
			this.configfile.save(this.data);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addHome(final Location loc, final String homename) {
		List<String> homes = this.getHomes();

		if (homes == null)
			homes = new ArrayList<>();
		homes.add(homename);
		this.configfile.set(this.p + ".homeslist", homes);
		this.configfile.set(this.p + ".homes." + homename + ".world", loc.getWorld().getName());
		this.configfile.set(this.p + ".homes." + homename + ".x", loc.getX());
		this.configfile.set(this.p + ".homes." + homename + ".y", loc.getY());
		this.configfile.set(this.p + ".homes." + homename + ".z", loc.getZ());
		this.configfile.set(this.p + ".homes." + homename + ".yaw", loc.getYaw());
		this.configfile.set(this.p + ".homes." + homename + ".pitch", loc.getPitch());
		this.save();
	}

	public List<String> getAchievements() {
		return this.configfile.getStringList("achivements");
	}

	public int getDeaths() {
		return this.configfile.getInt(this.p + ".deaths");
	}

	public Location getHome(final String homename) {
		return new Location(Bukkit.getWorld(this.configfile.getString(this.p + ".homes." + homename + ".world")),
				this.configfile.getDouble(this.p + ".homes." + homename + ".x"),
				this.configfile.getDouble(this.p + ".homes." + homename + ".y"),
				this.configfile.getDouble(this.p + ".homes." + homename + ".z"),
				(float) this.configfile.getDouble(this.p + ".homes." + homename + ".yaw"),
				(float) this.configfile.getDouble(this.p + ".homes." + homename + ".pitch"));
	}

	@SuppressWarnings("unchecked")
	public List<String> getHomes() {
		return (List<String>) this.configfile.getList(this.p + ".homeslist");
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getItems() {
		List<ItemStack> i = (List<ItemStack>) this.configfile.get(this.p + ".virtualcchest");
		if (i == null)
			i = new ArrayList<>();
		return i;
	}

	public int getKills() {
		return this.configfile.getInt(this.p + ".kills");
	}

	public int getLevel() {
		return this.configfile.getInt(this.p + ".level");
	}

	public int getMoney() {
		return this.configfile.getInt(this.p + ".money");
	}

	public int getScore() {
		return this.configfile.getInt(this.p + ".score");
	}

	public boolean isExists() {
		return new File("plugins/Faction/data/" + this.p + ".dat").length() > 0;
	}

	public void removeHome(final String homename) {
		final List<String> homes = this.getHomes();

		if (homes.contains(homename))
			homes.remove(homename);
		this.configfile.set(this.p + ".homeslist", homes);
		this.configfile.set(this.p + ".homes." + homename, null);
		this.save();
	}

	public void save() {
		try {
			this.configfile.save(this.data);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAchivement(String achivement) {
		List<String> achievements = this.getAchievements();
		achievements.add(achivement);
		this.configfile.set("achivements", achievements);
	}

	public void setDeaths(final int i) {
		this.configfile.set(this.p + ".deaths", i);
		this.save();
	}

	public void setItems(final List<ItemStack> i) {
		this.configfile.set(this.p + ".virtualcchest", i);
		this.save();
	}

	public void setKills(final int i) {
		this.configfile.set(this.p + ".kills", i);
		this.save();
	}

	public void setLevel(final int i) {
		this.configfile.set(this.p + ".level", i);
		this.save();
	}

	public void setMoney(final int i) {
		this.configfile.set(this.p + ".money", i);
		this.save();
	}

	public RankAPI getRank() {
		return API.getInstance().getGeneralInstance().getRankManager().getRank(this.configfile.getString(this.p + ".rank"));
	}

	public void setRank(final String id) {
		this.configfile.set(this.p + ".rank", id);
		this.save();
	}

	public void setScore(final int i) {
		this.configfile.set(this.p + ".score", i);
		this.save();
	}

}
