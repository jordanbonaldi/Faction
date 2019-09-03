package net.neferett.linaris.faction.commands;

import java.util.List;
import java.util.Objects;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class MoneyManagement extends CommandHandler {

	public MoneyManagement() {
		super("money", Objects::nonNull, "argent", "fric", "aboulelathune", "thune", "nsm");
		this.setErrorMsg("§cBDD ERROR");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		final M_Player p = PlayerManagers.get().getPlayer(arg0.getPlayer());
		p.sendMessage("§7Vous avez actuellement §e" + p.getMoney() + "$");
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
