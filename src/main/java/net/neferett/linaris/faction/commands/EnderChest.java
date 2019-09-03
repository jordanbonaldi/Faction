package net.neferett.linaris.faction.commands;

import java.util.List;

import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.Bukkit;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class EnderChest extends CommandHandler {

	public EnderChest() {
		super("ec", (p) -> {
			final M_Player pa = PlayerManagers.get().getPlayer(Bukkit.getPlayer(p.getPlayername()));
			return pa.getCmds().contains("ec");
		}, "enderchest");
		this.setErrorMsg("§cVous n'avez les privileges pour faire cela.");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		arg0.openInventory(arg0.getEnderChest());
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
