package net.neferett.linaris.faction;

import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.HologramPlugin;
import lombok.Getter;
import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.api.API;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.faction.classes.ClassesManager;
import net.neferett.linaris.faction.classes.DefinedRankManager;
import net.neferett.linaris.faction.commands.*;
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
import net.neferett.linaris.faction.listeners.events.*;
import net.neferett.linaris.faction.shop.npc.CustomEntityType;
import net.neferett.linaris.faction.shop.npc.NPC;
import net.neferett.linaris.faction.spell.SpellEvents;
import net.neferett.linaris.faction.utils.CuboidRegion;
import net.neferett.linaris.faction.utils.RepairableItems;
import net.neferett.linaris.faction.utils.TimeUtils;
import net.neferett.linaris.utils.tasksmanager.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Main extends API {

	static Main instanceMain;

	public static Main getInstanceMain() {
		return instanceMain;
	}

	private CuboidRegion					cb;

	private CuboidRegion					cb2;

	private ClassesManager					cm;

	private HologramManager				hologramManager;
	private final HashMap<String, Integer>	pd		= new HashMap<>();

	private PlayerManagers					pm;

	private SpawnItems						sp;

	private DefinedRankManager 				definedRank;

	public Main() {
		super(ConfigReader.getInstance().getGameName(), "Default", 100);
		instanceMain = this;
	}

	@Override
	public void addRanks() {

		final AtomicInteger i = new AtomicInteger(0);

		if (ConfigReader.getInstance().isClass())
			this.cm.getClasses().forEach(e -> RankManager.getInstance().addRank(
					new RankAPI(i.getAndIncrement(), e.getClassname(), e.getColor(), e.getColor() + e.getLogo() + " ",
							0, 0, e.getChatcl().charAt(1), e.getChatcl() + e.getLogo() + " ", e.getLogo(), e.getTab(), null, 0, null)));

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
		Arrays.asList(NPC.values()).forEach(NPC::dispawn);
	}

	@Override
	public void onLoading() {
		if (ConfigReader.getInstance().isClass())
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

		if  (ConfigReader.getInstance().isClass())
			this.sethandleRank();
		else
			this.definedRank = new DefinedRankManager();

		this.setAPIMode(true);
		this.setAnnounce();
		CustomEntityType.registerEntities();
		ConfigReader.getInstance().loadHolos();
		Arrays.asList(NPC.values()).forEach(NPC::spawn);
		TaskManager.scheduleSyncRepeatingTask("Classement", () -> {
			if (Bukkit.getOnlinePlayers().size() >= 1)
				net.neferett.linaris.faction.handlers.Classement.getInstance().SignClassement();
		}, 0, 20);

		this.cb = new CuboidRegion(ConfigReader.getInstance().getPos1(), ConfigReader.getInstance().getPos2());
		if  (ConfigReader.getInstance().isWarzoneInSpawn())
			this.cb2 = new CuboidRegion(ConfigReader.getInstance().getPos3(), ConfigReader.getInstance().getPos4());

		this.addHealthNameTag();

		ConfigReader.getInstance().loadDefinedRanks();
		this.sp.spawn();
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
		new AddDefinedRank();
		this.getCommand("setrank").setExecutor(new setRank());
		this.getCommand("givekits").setExecutor(new GiveKits());
		this.getCommand("level").setExecutor(new LevelCommand());
		this.getCommand("pay").setExecutor(new PayCommand());
		this.getCommand("classement").setExecutor(new net.neferett.linaris.faction.commands.Classement());
	}

}
