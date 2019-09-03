package net.neferett.linaris.faction.commands.home;

import java.util.List;
import java.util.Objects;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.DataReader;

public class sethome extends CommandHandler {

	public sethome() {
		super("sethome", Objects::nonNull);
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		if (arg2.size() != 2) {
			arg0.sendMessage("§cUtilisation: /sethome <name>");
			return;
		} else {
			final M_Player pm = PlayerManagers.get().getPlayer(arg0.getPlayer());
			final DataReader rd = PlayerManagers.get().getPlayer(arg0.getPlayer()).getDataReader();
			final int i = pm.getHome();
			final List<String> homes = rd.getHomes();
			if (homes != null && homes.size() == i) {
				arg0.sendMessage("§cVous avez atteint votre nombre maximale de home qui est de §e" + i + "homes §c!");
				return;
			} else {
				arg0.sendMessage("§7Home §e" + arg2.get(1) + "§7 ajouté !");
				rd.addHome(arg0.getLocation(), arg2.get(1));
			}
		}
	}

	@Override
	public void onError(final Players arg0) {}

}
