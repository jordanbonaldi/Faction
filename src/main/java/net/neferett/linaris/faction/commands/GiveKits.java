package net.neferett.linaris.faction.commands;

import net.neferett.linaris.api.ranks.RankManager;
import net.neferett.linaris.faction.classes.DefinedRankManager;
import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.neferett.linaris.faction.classes.Classes;
import net.neferett.linaris.faction.classes.ClassesManager;

public class GiveKits implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		if (arg0 instanceof Player)
			return false;
		if (arg1.getLabel().equalsIgnoreCase("givekits")) {
			final Player p = Bukkit.getPlayer(arg3[0]);

			System.out.println(arg3[0]);

			if (p == null)
				return false;
			if (ConfigReader.getInstance().isClass()) {
				final Classes c = ClassesManager.get().getClassByName(arg3[1]);
				if (c != null)
					c.getKit().giveKits(p);
				else
					arg0.sendMessage("§cKit inconnu !");
			} else
				DefinedRankManager.getInstance().getRank(Integer.parseInt(arg3[1])).getKit().giveKits(p);


		}
		return false;
	}

}
