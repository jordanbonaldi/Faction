package net.neferett.linaris.faction.events.players;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.api.PlayerLocalManager;
import net.neferett.linaris.api.RankAPI;
import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.classes.Classes;
import net.neferett.linaris.faction.classes.ClassesManager;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.handlers.DataReader;
import net.neferett.linaris.faction.listeners.events.DamageEvent;
import net.neferett.linaris.faction.spell.Spell;
import net.neferett.linaris.faction.utils.Maths;
import net.neferett.linaris.utils.PlayerUtils;
import net.neferett.linaris.utils.TimeUtils;
import net.neferett.linaris.utils.TitleUtils;

public class M_Player extends Players {

	private Classes						c;
	private long						cltime;

	private Spell						current;
	private Location					old;
	private final Player				p;

	private RankAPI						rank;
	private DataReader					rd;
	private boolean						spellon;
	private Spell						spellshooted;
	private M_Player					spellshooter;
	private final HashMap<String, Long>	spelltime	= new HashMap<>();

	private long						spelltimer;

	public M_Player(final Player p) {
		super(p);
		this.p = p;
		try {
			this.rd = new DataReader(p);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.rank = this.getPlayerData().getRank();
		if (this.rank.getModerationLevel() < 1)
			this.c = ClassesManager.get().getClassByName(this.rank.getName());
		else
			this.c = ClassesManager.get().getClassByName("Maréchal");
		this.current = this.c.getSpells().size() == 0 ? null : this.c.getSpells().get(0);
	}

	public void addDeath() {
		this.rd.setDeaths(this.getDeaths() + 1);
	}

	public void addKill() {
		this.rd.setKills(this.getKills() + 1);
	}

	@SuppressWarnings("deprecation")
	public void addMoney(final int value) {
		if (Main.getInstanceMain().getEcon().hasAccount(this.p.getName().toLowerCase()))
			Main.getInstanceMain().getEcon().depositPlayer(this.p.getName().toLowerCase(), value);
		else {
			Main.getInstanceMain().getEcon().createPlayerAccount(this.p.getName().toLowerCase());
			Main.getInstanceMain().getEcon().depositPlayer(this.p.getName().toLowerCase(), value);
		}
	}

	public boolean CanLevelUp() {
		return this.rd.getScore() <= 0;
	}

	public void death(final Player cause) {
		this.addDeath();
		PlayerUtils.sendForceRespawn(cause, 1);
		if (!(cause.getKiller() instanceof Player))
			return;
		this.setBackPos(this.p.getPlayer().getLocation());
		if (ConfigReader.getInstance().isMoney())
			PlayerUtils.sendActionMessage(this.p.getPlayer(), "§c§l- 5$");
	}

	@SuppressWarnings("deprecation")
	public void delMoney(final int value) {
		if (Main.getInstanceMain().getEcon().hasAccount(this.p.getName().toLowerCase()))
			Main.getInstanceMain().getEcon().withdrawPlayer(this.p.getName().toLowerCase(), value);
		else
			Main.getInstanceMain().getEcon().createPlayerAccount(this.p.getName().toLowerCase());
	}

	public void delScore() {
		if (this.getScore() - 1 < 0)
			return;
		this.rd.setScore(this.getScore() - 1);
	}

	public int getAchievements() {
		return this.rd.getAchievements();
	}

	public Location getBackPos() {
		if (this.old == null)
			this.old = ConfigReader.getInstance().getSpawn();
		return this.old;
	}

	public Classes getClasses() {
		return this.c;
	}

	public long getCltime() {
		return this.cltime;
	}

	public Spell getCurrent() {
		return this.current;
	}

	public DataReader getDataReader() {
		return this.rd;
	}

	public int getDeaths() {
		return this.rd.getDeaths();
	}

	public int getHome() {
		return this.c.getHomes();
	}

	public int getKills() {
		return this.rd.getKills();
	}

	@Override
	public int getLevel() {
		return this.rd.getLevel();
	}

	@SuppressWarnings("deprecation")
	public int getMoney() {
		if (Main.getInstanceMain().getEcon().hasAccount(this.p.getName().toLowerCase()))
			return (int) Main.getInstanceMain().getEcon().getBalance(this.p.getName().toLowerCase());
		else {
			Main.getInstanceMain().getEcon().createPlayerAccount(this.p.getName().toLowerCase());
			return 0;
		}
	}

	@Override
	public Player getPlayer() {
		return this.p;
	}

	public RankAPI getRank() {
		return this.rank;
	}

	public int getScore() {
		return this.rd.getScore();
	}

	public Spell getSpellshooted() {
		return this.spellshooted;
	}

	public M_Player getSpellshooter() {
		return this.spellshooter;
	}

	public HashMap<String, Long> getSpelltime() {
		return this.spelltime;
	}

	public long getSpelltimer() {
		return this.spelltimer;
	}

	public long getTime() {
		return this.spelltime.containsKey(this.current.getName()) ? this.spelltime.get(this.current.getName()) : -1;
	}

	public boolean isSpellExecutable() {
		return !TimeUtils.CreateTestCoolDown(this.current.getTime()).test(this.getTime());
	}

	public boolean isSpellon() {
		return this.spellon;
	}

	public void kill() {
		this.addKill();
		this.addMoney(10);
		this.delScore();
		if (this.CanLevelUp())
			this.LevelUP();
		if (ConfigReader.getInstance().isMoney())
			PlayerUtils.sendActionMessage(this.getPlayer(), "§6§l+ 10$");
		DamageEvent.time.remove(this.getPlayer());
	}

	public void LevelUP() {
		this.rd.setLevel(this.getLevel() + 1);
		TitleUtils.sendTitle(this.getPlayer(), "§7Level §e" + this.getLevel(), "§6+ 1 Level");
		this.setScore(this.getLevel() * 5 * this.getLevel() * Maths.Rand(0, this.getLevel() * 2)
				+ Maths.Rand(0, this.getLevel() * 5));
	}

	public Spell nextSpell() {
		int i = 0;

		for (final Spell s : this.c.getSpells().stream().filter(e -> e != null).collect(Collectors.toList())) {
			if (s.equals(this.current))
				return s.equals(this.c.getSpells().get(this.c.getSpells().size() - 1)) ? this.c.getSpells().get(0)
						: this.c.getSpells().get(i + 1);
			i++;
		}
		return null;
	}

	public void setAchievements(final int i) {
		this.rd.setAchivement(i);
	}

	public void setBackPos(final Location pos) {
		this.old = pos.clone();
	}

	public void setCltime() {
		this.cltime = System.currentTimeMillis();
	}

	public void setCurrent(final Spell s) {
		this.current = s;
	}

	public void setRank(final String name) {
		System.out.println("GRADE -> " + name);
		this.getDataReader().setRank(name);
		this.rank = this.getDataReader().getRank();
		System.out.println(this.rank);
		this.getPlayerData().setRank(this.rank.getId());
		BukkitAPI.get().getPlayerLocalManager().unload(this.p.getPlayer().getName());
		PlayerLocalManager.get().getPlayerLocal(this.p.getName()).inits();
		BukkitAPI.get().getPlayerLocalManager().getPlayerLocal(this.p.getName())
				.setPrefix(this.getRank().getTablist(this.getPlayerData()));
		this.c = ClassesManager.get().getClassByName(this.getRank().getName());
		this.current = this.c.getSpells().size() == 0 ? null : this.c.getSpells().get(0);
	}

	public void setScore(final int a) {
		this.rd.setScore(a);
	}

	public void setSpellon(final boolean spellon) {
		this.spellon = spellon;
	}

	public void setSpellTimer(final Player p, final String spellname) {
		this.spellshooted = Spell.getByName(spellname);
		this.setSpellon(true);
		this.spellshooter = PlayerManagers.get().getPlayer(p);
		this.spelltimer = System.currentTimeMillis();
	}

	public void setTime() {
		this.spelltime.put(this.current.getName(), System.currentTimeMillis());
	}

	public void tp(final Location loc) {
		this.p.teleport(loc);
	}

}
