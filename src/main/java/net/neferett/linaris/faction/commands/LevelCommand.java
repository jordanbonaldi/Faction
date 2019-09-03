package net.neferett.linaris.faction.commands;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.DataReader;

public class LevelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		if (!(arg0 instanceof Player))
			return false;
		if (arg1.getLabel().equalsIgnoreCase("level")) {
			final M_Player p = PlayerManagers.get().getPlayer((Player) arg0);
			if (arg3.length != 1) {
				final float ratio = p.getDeaths() <= 0 ? p.getKills() : p.getKills() / (float) p.getDeaths();
				p.sendMessage("§7Vous êtes au level§f: §e" + p.getLevel());
				p.sendMessage("");
				p.sendMessage("§7Kills§f: §e" + p.getKills());
				p.sendMessage("§7Morts§f: §e" + p.getDeaths());
				p.sendMessage("");
				p.sendMessage("§7Ratio§f: §a"
						+ (p.getDeaths() <= 0 ? p.getKills() : new DecimalFormat("####.##").format(ratio)));
				p.sendMessage("");
				p.sendMessage("§7Il vous manque §e" + p.getScore() + " Kills§7 pour passer au level suivant !");
			} else {
				final String player = arg3[0].toLowerCase();
				if (!new DataReader(player).isExists())
					p.sendMessage("§cLe joueur §e" + arg3[0] + " §cn'existe pas !");
				else {
					final DataReader rd = new DataReader(player);
					final int kill = rd.getKills();
					final int deaths = rd.getDeaths();
					final float ratio = deaths <= 0 ? kill : kill / deaths;
					p.sendMessage("§e" + arg3[0] + "§7 est au Level §e" + rd.getLevel());
					p.sendMessage("");
					p.sendMessage("§7Kills§f: §e" + kill);
					p.sendMessage("§7Morts§f: §e" + deaths);
					p.sendMessage("");
					p.sendMessage("§7Ratio§f: §a" + (deaths <= 0 ? kill : new DecimalFormat("####.##").format(ratio)));
					p.sendMessage("");
					p.sendMessage("§7Prochain niveau dans §e" + rd.getScore() + " Kills");
					p.sendMessage("");
					p.sendMessage(
							"§7Actuellement§f: " + (Bukkit.getPlayer(player) == null ? "§cHors-Ligne" : "§aEn ligne"));
				}
			}
		}
		return false;
	}

}
