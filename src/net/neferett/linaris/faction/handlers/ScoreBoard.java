package net.neferett.linaris.faction.handlers;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.entity.Player;

import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.utils.RainbowEffect;
import net.neferett.linaris.utils.ScoreBoardModule;
import net.neferett.linaris.utils.ScoreboardSign;

public class ScoreBoard extends ScoreBoardModule {

	RainbowEffect	NAME;
	String			title;

	public ScoreBoard(final BukkitAPI game) {
		super(game);

		this.NAME = new RainbowEffect("LINARIS", "§f§l", "§6§l", 40);

	}

	@Override
	public void onUpdate() {
		this.title = this.NAME.next();
	}

	@Override
	public void onUpdate(final Player p) {
		ScoreboardSign bar = ScoreboardSign.get(p);
		if (bar == null) {
			bar = new ScoreboardSign(p, this.title);
			bar.create();
		}

		final HashMap<Integer, String> lines = new HashMap<>();

		final M_Player pa = PlayerManagers.get().getPlayer(p);
		final DataReader rd = pa.getDataReader();

		String rank = pa.getRank().getName();
		rank = Character.toUpperCase(rank.charAt(0)) + rank.substring(1);

		bar.setObjectiveName(this.title);
		lines.put(15, "§8● §7§lClasse§f§l:");
		lines.put(14,
				"    §b➜" + (pa.getRank().getColor() == '7' ? "§f§l" : "§" + pa.getRank().getColor()) + " " + rank);
		lines.put(13, "§d");
		final float ratio = pa.getDeaths() <= 0 ? pa.getKills() : pa.getKills() / (float) pa.getDeaths();
		if (ConfigReader.getInstance().isMoney()) {
			lines.put(12, "§8●§e§l Monnaie§f§l:");
			lines.put(11, "    §b➜§f§l " + pa.getMoney() + "$");
		} else {
			lines.put(12, "§8●§e§l Ratio§f§l:");
			lines.put(11, "    §b➜§f§l " + (pa.getDeaths() <= 0 ? pa.getKills() == 0 ? "§cN/A" : pa.getKills()
					: new DecimalFormat("####.##").format(ratio)));
		}
		lines.put(10, "§8●§a§l Kills§f§l:");
		lines.put(9, "    §b➜§f§l " + (rd.getKills() == 0 ? "§c§lAucun" : rd.getKills()));
		lines.put(8, "§8● §c§lClassement§f§l:");
		lines.put(7, "    §b➜§f§l " + Classement.getInstance().getPlayerClassement(p) + "e");
		lines.put(6, "§8● §6§lLevel§f§l:");
		lines.put(5, "    §b➜§f§l " + rd.getLevel());
		lines.put(2, "§f");
		lines.put(1, "§e► play.linaris.fr");

		if (lines.isEmpty())
			return;
		for (int i = 1; i < 16; i++)
			if (!lines.containsKey(i)) {
				if (bar.getLine(i) != null)
					bar.removeLine(i);
			} else if (bar.getLine(i) == null)
				bar.setLine(i, lines.get(i));
			else if (!bar.getLine(i).equals(lines.get(i)))
				bar.setLine(i, lines.get(i));

	}

}
