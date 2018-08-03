package net.neferett.linaris.faction.commands;

import java.util.List;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.kits.Kits;
import net.neferett.linaris.utils.TimeUtils;

public class KitsCommand extends CommandHandler {

	public KitsCommand() {
		super("kit", p -> p != null, "kits", "k", "tmort");
		this.setErrorMsg("§cUne erreur c'est produite !");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		final M_Player pl = PlayerManagers.get().getPlayer(arg0.getPlayer());
		final Kits kit = pl.getClasses().getKit();
		if (kit.isExists(pl.getName())
				&& TimeUtils.CreateTestCoolDown(kit.getCool()).test(kit.getPlayer(pl.getName()))) {
			pl.sendMessage("§7Tu dois attente encore §e"
					+ TimeUtils.getTimeLeftToString(kit.getPlayer(pl.getName()), kit.getCool()));
			return;
		} else {
			kit.setPlayer(pl.getName());
			pl.getPlayer().sendMessage("§aVous venez de recevoir le kit §b" + pl.getClasses().name(pl));
			kit.giveKits(pl.getPlayer());
			return;
		}
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
