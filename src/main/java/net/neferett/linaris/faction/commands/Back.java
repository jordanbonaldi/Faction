package net.neferett.linaris.faction.commands;

import java.util.List;

import net.neferett.linaris.faction.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.utils.CuboidRegion;

public class Back extends CommandHandler {

	public Back() {
		super("back", (p) ->{
			final M_Player pa = PlayerManagers.get().getPlayer(Bukkit.getPlayer(p.getPlayername()));
			return pa.getCmds().contains("back");
		}, "ba");
		this.setErrorMsg("§cVous n'avez les privileges pour faire cela.");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		final M_Player p = PlayerManagers.get().getPlayer(arg0.getPlayer());

		if (Main.getInstanceMain().getCb().isInside(p.getLocation()) && ConfigReader.getInstance().warzoneSpawnCheck(p))
			p.teleport(p.getBackPos());
		else p.sendMessage("§cVous devez être au spawn !");
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
