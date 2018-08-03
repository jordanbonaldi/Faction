package net.neferett.linaris.faction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;

import net.milkbowl.vault.economy.Economy;
import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.api.API;
import net.neferett.linaris.api.RankAPI;
import net.neferett.linaris.faction.classes.ClassesManager;
import net.neferett.linaris.faction.commands.AddItem;
import net.neferett.linaris.faction.commands.AddLoc;
import net.neferett.linaris.faction.commands.Back;
import net.neferett.linaris.faction.commands.ClassesCommand;
import net.neferett.linaris.faction.commands.CraftCommand;
import net.neferett.linaris.faction.commands.EnderChest;
import net.neferett.linaris.faction.commands.Feed;
import net.neferett.linaris.faction.commands.GiveKits;
import net.neferett.linaris.faction.commands.KitsCommand;
import net.neferett.linaris.faction.commands.LevelCommand;
import net.neferett.linaris.faction.commands.MoneyManagement;
import net.neferett.linaris.faction.commands.Near;
import net.neferett.linaris.faction.commands.PayCommand;
import net.neferett.linaris.faction.commands.SpawnCommand;
import net.neferett.linaris.faction.commands.SpawnerCommand;
import net.neferett.linaris.faction.commands.setRank;
import net.neferett.linaris.faction.commands.TPA.TPA;
import net.neferett.linaris.faction.commands.TPA.TPAccept;
import net.neferett.linaris.faction.commands.TPA.TPAdeny;
import net.neferett.linaris.faction.commands.home.delhome;
import net.neferett.linaris.faction.commands.home.home;
import net.neferett.linaris.faction.commands.home.sethome;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.handlers.ScoreBoard;
import net.neferett.linaris.faction.handlers.SpawnItems;
import net.neferett.linaris.faction.listeners.CancelledEvents;
import net.neferett.linaris.faction.listeners.ChatHandling;
import net.neferett.linaris.faction.listeners.events.Achivements;
import net.neferett.linaris.faction.listeners.events.AntiCrop;
import net.neferett.linaris.faction.listeners.events.AnvilBreak;
import net.neferett.linaris.faction.listeners.events.AutoLapis;
import net.neferett.linaris.faction.listeners.events.DamageEvent;
import net.neferett.linaris.faction.listeners.events.DeathEvents;
import net.neferett.linaris.faction.listeners.events.EntityExplode;
import net.neferett.linaris.faction.listeners.events.InteractListener;
import net.neferett.linaris.faction.listeners.events.JoinAndLeave;
import net.neferett.linaris.faction.listeners.events.MoveListener;
import net.neferett.linaris.faction.shop.npc.CustomEntityType;
import net.neferett.linaris.faction.shop.npc.NPC;
import net.neferett.linaris.faction.spell.SpellEvents;
import net.neferett.linaris.faction.utils.CuboidRegion;
import net.neferett.linaris.faction.utils.RepairableItems;
import net.neferett.linaris.faction.utils.TimeUtils;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public class Main extends API {

	static Main instanceMain;

	public static Main getInstanceMain() {
		return instanceMain;
	}

	private CuboidRegion					cb;

	private CuboidRegion					cb2;

	private ClassesManager					cm;

	private Economy							econ	= null;

	protected HologramManager				hologramManager;
	private final HashMap<String, Integer>	pd		= new HashMap<>();

	private PlayerManagers					pm;

	private SpawnItems						sp;

	public Main() {
		super(ConfigReader.getInstance().getGameName(), "Default", 100);
		instanceMain = this;
	}

	@Override
	public void addRanks() {

		final AtomicInteger i = new AtomicInteger(0);

		this.cm.getClasses().forEach(e -> {
			this.addRanks(
					new RankAPI(i.getAndIncrement(), e.getClassname(), e.getColor(), e.getColor() + e.getLogo() + " ",
							0, 0, e.getChatcl().charAt(1), e.getChatcl() + e.getLogo() + " ", e.getLogo(), e.getTab()));
		});

	}

	public CuboidRegion getCb() {
		return this.cb;
	}

	public CuboidRegion getCb2() {
		return this.cb2;
	}

	public ClassesManager getCm() {
		return this.cm;
	}

	public Economy getEcon() {
		return this.econ;
	}

	public HologramManager getHologramManager() {
		return this.hologramManager;
	}

	public PlayerManagers getPlayerManager() {
		return this.pm;
	}

	public HashMap<String, Integer> getPlayersEffect() {
		return this.pd;
	}

	public SpawnItems getSp() {
		return this.sp;
	}

	private void loadPredicateProcessors() {
		BukkitAPI.get().addProcessPredicate(e -> {
			final Player p = e.getPlayer();
			if (ConfigReader.getInstance().getWarzone().isInside(e.getPlayer().getLocation()) && !e.getPlayer().isOp()
					&& e.getMessage().contains("claim") || e.getMessage().contains("unclaim")) {
				p.sendMessage("§cTu n'as pas le droit de claim le spawn !");
				return false;
			}
			return true;
		});
		BukkitAPI.get().addProcessPredicate(e -> {
			final Player p = e.getPlayer();
			if (DamageEvent.time.containsKey(p) && TimeUtils.CreateTestCoolDown(15).test(DamageEvent.time.get(p))) {
				p.sendMessage("§cVous êtes en combat vous ne pouvez pas faire cela !");
				return false;
			}
			return true;
		});
	}

	@Override
	public void onClose() {
		this.closeServer();
		Arrays.asList(NPC.values()).forEach(n -> n.dispawn());
	}

	@Override
	public void onLoading() {
		this.cm = new ClassesManager();
	}

	@Override
	public void onOpen() {
		this.pm = new PlayerManagers();
		this.hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();

		this.openServer();
		this.handleWorld();
		final World w = Bukkit.getWorld("world");
		w.setThundering(false);

		this.sp = new SpawnItems();

		RepairableItems.init();
		this.loadPredicateProcessors();

		this.setScoreBoard(ScoreBoard.class);

		this.RegisterAllEvents(new JoinAndLeave(), new CancelledEvents(), new ChatHandling(), new InteractListener(),
				new DeathEvents(), new MoveListener(), new AutoLapis(), new DamageEvent(), new AntiCrop(),
				new Achivements(), new AnvilBreak(), new EntityExplode(), new SpellEvents());
		this.w.setDifficulty(Difficulty.NORMAL);
		this.w.getEntities().forEach(e -> {
			if (e instanceof Monster && e.getLocation().getWorld().getName().equals("world"))
				e.remove();
		});
		this.sethandleRank();
		this.setAPIMode(true);
		this.setAnnounce();
		CustomEntityType.registerEntities();
		ConfigReader.getInstance().loadHolos();
		Arrays.asList(NPC.values()).forEach(npc -> npc.spawn());
		TaskManager.scheduleSyncRepeatingTask("Classement", () -> {
			if (Bukkit.getOnlinePlayers().size() >= 1)
				net.neferett.linaris.faction.handlers.Classement.getInstance().SignClassement();
		}, 0, 1 * 20);
		this.cb = new CuboidRegion(ConfigReader.getInstance().getPos1(), ConfigReader.getInstance().getPos2());
		this.cb2 = new CuboidRegion(ConfigReader.getInstance().getPos3(), ConfigReader.getInstance().getPos4());
		this.addHealthNameTag();

		this.sp.spawn();

		if (!this.setupEconomy())
			this.getServer().shutdown();
	}

	@Override
	public void RegisterCommands() {
		new Feed();
		new TPA();
		new TPAccept();
		new TPAdeny();
		new sethome();
		new delhome();
		new home();
		new SpawnCommand();
		new AddItem();
		new KitsCommand();
		new ClassesCommand();
		new MoneyManagement();
		new Back();
		new EnderChest();
		new CraftCommand();
		new SpawnerCommand();
		new Near();
		new AddLoc();
		this.getCommand("setrank").setExecutor(new setRank());
		this.getCommand("givekits").setExecutor(new GiveKits());
		this.getCommand("level").setExecutor(new LevelCommand());
		this.getCommand("pay").setExecutor(new PayCommand());
		this.getCommand("classement").setExecutor(new net.neferett.linaris.faction.commands.Classement());
	}

	private boolean setupEconomy() {
		if (this.getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		final RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (rsp == null)
			return false;
		this.econ = rsp.getProvider();
		return this.econ != null;
	}

}
