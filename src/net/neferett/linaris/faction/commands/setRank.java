package net.neferett.linaris.faction.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.neferett.linaris.api.API;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.DataReader;

public class setRank implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		if (arg0 instanceof Player)
			return false;
		if (arg3.length < 2) {
			arg0.sendMessage("§cUtilisation: /setrank <player> <id>");
			return false;
		} else {
			if (API.getInstance().getRank(arg3[1] + (arg3.length == 3 ? " " + arg3[2] : "")) == null) {
				arg0.sendMessage("§cGrade inconnu !");
				return false;
			}
			if (Bukkit.getPlayer(arg3[0]) == null) {
				final String player = arg3[0].toLowerCase();
				if (!new DataReader(player).isExists()) {
					arg0.sendMessage("§cLe joueur §e" + arg3[0] + " §cn'existe pas !");
					return false;
				} else {
					final DataReader rd = new DataReader(player);
					rd.setRank(arg3[1] + (arg3.length == 3 ? " " + arg3[2] : ""));
					arg0.sendMessage("§aGrade ajouté !");
					return true;
				}
			} else {
				final M_Player p = PlayerManagers.get().getPlayer(Bukkit.getPlayer(arg3[0]));
				p.setRank(arg3[1] + (arg3.length == 3 ? " " + arg3[2] : ""));
				arg0.sendMessage("§aGrade ajouté !");
			}
			return true;
		}
	}

}
