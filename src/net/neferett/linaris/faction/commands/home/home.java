package net.neferett.linaris.faction.commands.home;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.DataReader;

public class home extends CommandHandler {

	public home() {
		super("home", p -> p != null, "maison");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		if (arg2.size() != 2) {
			final DataReader rd = PlayerManagers.get().getPlayer(arg0.getPlayer()).getDataReader();
			final List<String> homes = rd.getHomes();
			if (homes == null) {
				arg0.sendMessage("§7Voici vos §ehomes§7 §a" + 0 + "§7 au total !");
				arg0.sendMessage("§cAucun");
				return;
			} else
				arg0.sendMessage("§7Voici vos §ehomes§7 §a" + homes.size() + "§7 au total !");
			final AtomicInteger i = new AtomicInteger(0);
			final StringBuilder sb = new StringBuilder();
			homes.forEach(h -> {
				if (i.incrementAndGet() == homes.size())
					sb.append(h);
				else
					sb.append(h + ", ");
			});
			arg0.sendMessage(sb.toString());
			return;
		} else {
			final DataReader rd = PlayerManagers.get().getPlayer(arg0.getPlayer()).getDataReader();
			final List<String> homes = rd.getHomes();
			final Callable<Void> c = () -> {
				this.setErrorMsg("§cLe home §e" + arg2.get(1) + "§c n'existe pas !");
				this.onError(arg0);
				return null;
			};
			if (!homes.contains(arg2.get(1))) {
				try {
					c.call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
				return;
			}
			final Location loc = rd.getHome(arg2.get(1));
			if (loc != null) {
				arg0.sendMessage("§cTéléportation dans 5 secondes, ne bougez pas !");
				arg0.createTPWithDelay(arg0.getPlayerData().getRank().getVipLevel() > 3 ? 3
						: arg0.getPlayerData().getRank().getVipLevel() >= 1 ? 4 : 5, () -> loc);
			} else
				try {
					c.call();
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
