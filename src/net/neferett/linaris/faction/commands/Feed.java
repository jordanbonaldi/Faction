package net.neferett.linaris.faction.commands;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class Feed extends CommandHandler {

	public static HashMap<String, Boolean> waiting = new HashMap<>();

	public Feed() {
		super("feed", (p) -> {
			final M_Player pa = PlayerManagers.get().getPlayer(Bukkit.getPlayer(p.getPlayername()));
			return pa.getClasses().getCmds().contains("feed");
		}, "nourriture", "bouf");
		this.setErrorMsg("§cVous n'avez les privileges pour faire cela.");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		arg0.setFoodLevel(20);
		arg0.sendMessage("§aVous avez été rassasié");
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
