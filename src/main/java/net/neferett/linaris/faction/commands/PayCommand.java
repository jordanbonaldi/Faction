package net.neferett.linaris.faction.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;

public class PayCommand implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		if (!ConfigReader.getInstance().isMoney())
			return false;
		if (!(arg0 instanceof Player))
			return false;
		if (arg3.length != 2) {
			arg0.sendMessage("§c/pay <player> <montant>");
			return false;
		} else if (arg3.length > 8 || arg3[1].charAt(0) == '-')
			return false;
		if (Bukkit.getPlayer(arg3[0]) == null || Bukkit.getPlayer(arg3[0]).getName().equalsIgnoreCase(arg0.getName())) {
			arg0.sendMessage("§cLe joueur " + arg3[1] + " n'existe pas !");
			return false;
		}
		if (arg1.getLabel().equalsIgnoreCase("pay")) {
			final M_Player p = PlayerManagers.get().getPlayer((Player) arg0);

			final int montant = Integer.parseInt(arg3[1]);
			if (p.getMoney() - montant > 0) {
				p.delMoney(montant);
				arg0.sendMessage("§7Paiement de §e" + montant + "$ §7envoyé a §e" + arg3[0]);
			} else {
				arg0.sendMessage("§cIl vous manque §e" + (montant - p.getMoney()) + "$§c pour faire ce paiement !");
				return false;
			}

			Bukkit.getPlayer(arg3[0]).getPlayer().sendMessage("§7Vous venez de recevoir §e" + Integer.parseInt(arg3[1])
					+ "$§7 de la part de §e" + arg0.getName());
			PlayerManagers.get().getPlayer(Bukkit.getPlayer(arg3[0])).addMoney(Integer.parseInt(arg3[1]));
		}

		return false;
	}

}
