package net.neferett.linaris.faction.commands.home;

import java.util.List;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.DataReader;

public class delhome extends CommandHandler {

	public delhome() {
		super("delhome", p -> p != null);
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		if (arg2.size() != 2) {
			arg0.sendMessage("§cUtilisation: /delhome <name>");
			return;
		} else {
			final DataReader rd = PlayerManagers.get().getPlayer(arg0.getPlayer()).getDataReader();
			final List<String> homes = rd.getHomes();

			if (homes == null || !homes.contains(arg2.get(1))) {
				arg0.sendMessage("§cLe home §e" + arg2.get(1) + "§c n'existe pas !");
				return;
			} else {
				rd.removeHome(arg2.get(1));
				arg0.sendMessage("§cHome §e" + arg2.get(1) + "§c supprimé !");
			}
		}
	}

	@Override
	public void onError(final Players arg0) {}

}
