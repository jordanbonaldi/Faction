package net.neferett.linaris.faction.commands;

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

			if (p == null)
				return false;
			final Classes c = ClassesManager.get().getClassByName(arg3[1]);
			if (c != null)
				c.getKit().giveKits(p);
			else
				arg0.sendMessage("§cKit inconnu !");
		}
		return false;
	}

}
