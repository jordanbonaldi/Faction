package net.neferett.linaris.faction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Classement implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender arg0, final Command arg1, final String arg2, final String[] arg3) {
		if (!(arg0 instanceof Player))
			return false;
		if (arg1.getLabel().equalsIgnoreCase("Classement")) {
			final Player p = (Player) arg0;
			net.neferett.linaris.faction.handlers.Classement.getInstance().getClassement(p);
		}
		return false;
	}

}
