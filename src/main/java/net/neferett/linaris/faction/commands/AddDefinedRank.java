package net.neferett.linaris.faction.commands;

import net.neferett.linaris.BukkitAPI;
import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.classes.DefinedRank;
import net.neferett.linaris.faction.classes.DefinedRankManager;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.handlers.kits.Kits;

import java.util.Arrays;
import java.util.List;

public class AddDefinedRank extends CommandHandler {



	public AddDefinedRank() {
		super("addrank", p -> p.getRank().getModerationLevel() > 1);
		this.setErrorMsg("§cTu dois être §6Modérateur§c !");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		if (arg2.size() < 3) {
			arg0.sendMessage("§cUtilisation: /addrank <name> <kit|spell|command> <spells...|commands...|cooldown kit>");
			return;
		}

		DefinedRank rank = DefinedRankManager.getInstance().getRank(Integer.parseInt(arg2.get(1)));

		if (BukkitAPI.get().getRankManager().getRank(Integer.parseInt(arg2.get(1))) == null) {
			arg0.sendMessage("Rank doesn't exists");
			return;
		}

		if (rank == null)
			DefinedRankManager.getInstance().getRanks().add(rank = new DefinedRank(Integer.parseInt(arg2.get(1))));

		if (arg2.get(2).equalsIgnoreCase("kit")) {
			rank.setCooldown(Long.parseLong(arg2.get(3)));
			rank.setItems(Arrays.asList(arg0.getInventory().getContents()));
		} else if (arg2.get(2).equalsIgnoreCase("spell"))
			rank.setSpells(Arrays.asList(arg2.get(3).split(",")));
		else if (arg2.get(2).equalsIgnoreCase("command"))
			rank.setCommands(Arrays.asList(arg2.get(3).split(",")));


		ConfigReader.getInstance().pushRanks();
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
