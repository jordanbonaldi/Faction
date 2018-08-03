package net.neferett.linaris.faction.commands;

import java.util.List;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.handlers.ConfigReader;

public class SpawnCommand extends CommandHandler {

	public SpawnCommand() {
		super("spawn", p -> p != null);
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		arg0.sendMessage("§cTéléportation dans environ 5 secondes, ne bougez pas !");
		arg0.createTPWithDelay(
				arg0.getPlayerData().getRank().getVipLevel() > 3 ? 3
						: arg0.getPlayerData().getRank().getVipLevel() >= 1 ? 4 : 5,
				() -> ConfigReader.getInstance().getSpawn());
	}

	@Override
	public void onError(final Players arg0) {
		// TODO Auto-generated method stub

	}

}
