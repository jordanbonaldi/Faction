package net.neferett.linaris.faction.handlers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class Classement {

	public static Classement instance;

	public static Classement getInstance() {
		return instance == null ? new Classement() : instance;
	}

	HashMap<M_Player, Integer>	classement	= new HashMap<>();

	int							i			= 0;

	public Classement() {
		instance = this;
	}

	public int CalcClassement() {
		this.i = 0;
		this.classement.clear();
		Bukkit.getOnlinePlayers().stream().map(pl -> PlayerManagers.get().getPlayer(pl)).collect(Collectors.toList())
				.stream().sorted((pa1, pa2) -> pa2.getKills() - pa1.getKills()).collect(Collectors.toList())
				.forEach((pa) -> {
					++this.i;
					this.classement.put(pa, this.i);
				});
		return this.i;
	}

	public void getClassement(final Player p) {
		this.i = 0;
		p.sendMessage("§b§m===================================");
		Bukkit.getOnlinePlayers().stream().map(pl -> PlayerManagers.get().getPlayer(pl)).collect(Collectors.toList())
				.stream().sorted((pa1, pa2) -> pa2.getKills() - pa1.getKills())
				.filter(pa -> !pa.getPlayerData().contains("invisible") || !pa.getPlayerData().getBoolean("invisible"))
				.collect(Collectors.toList()).forEach((pa) -> {
					p.sendMessage("§e" + ++this.i + "§b. §7"
							+ (pa.getRank().getVipLevel() > 0 ? pa.getRank()
									.getPrefix(BukkitAPI.get().getPlayerDataManager().getPlayerData(p.getName())) + "§"
									+ pa.getRank().getColor() : "")
							+ pa.getPlayer().getName() + "§e avec §c" + pa.getKills() + " Kills");
				});
		p.sendMessage("§b§m===================================");
	}

	public M_Player getMPlayerClassement(final int nb) {
		this.CalcClassement();
		final Entry<M_Player, Integer> e = this.classement.entrySet().stream().filter(ed -> ed.getValue() == nb)
				.findFirst().orElse(null);
		if (e == null)
			return null;
		return e.getKey();
	}

	public int getPlayerClassement(final Player p) {
		this.CalcClassement();
		return this.classement.get(PlayerManagers.get().getPlayer(p));
	}

	public void SignClassement() {
		ConfigReader.getInstance().SignClassementCuboid().forEach(b -> {
			if (b.getType().equals(Material.WALL_SIGN)) {
				final Sign s = (Sign) b.getState();
				if (s.getLine(0).startsWith("#")) {
					final String ss = s.getLine(0).length() > 2 ? s.getLine(0).substring(1, 3).replace(" ", "")
							: s.getLine(0).substring(1);
					final int nb = Integer.parseInt(ss);
					final M_Player p = this.getMPlayerClassement(nb);
					if (p == null) {
						s.setLine(0, "#" + nb);
						s.setLine(1, "§c§lAucun joueur");
						s.setLine(2, "");
						s.setLine(3, "");
						s.update();
						return;
					}
					s.setLine(0, "#" + nb + " | " + p.getName());
					s.setLine(1, "§b§nGrade:");
					s.setLine(2, "§9" + p.getRank().getName());
					s.setLine(3, "§c§l" + p.getKills() + " Kills");
					s.update();
				}
			}
		});
	}

}
