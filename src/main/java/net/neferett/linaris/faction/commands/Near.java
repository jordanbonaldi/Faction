package net.neferett.linaris.faction.commands;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.utils.EntityUtils;

public class Near extends CommandHandler {

	public static HashMap<String, Boolean> waiting = new HashMap<>();

	public Near() {
		super("near", (p) -> {
			final M_Player pa = PlayerManagers.get().getPlayer(Bukkit.getPlayer(p.getPlayername()));
			return pa.getCmds().contains("near");
		}, "n");
		this.setErrorMsg("§cVous n'avez les privileges pour faire cela.");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		final Player p = Bukkit.getPlayer(arg0.getName());
		final List<Entity> ps = EntityUtils.getNearbyEntities(p.getLocation(), 150).stream()
				.filter(ent -> ent instanceof Player && !ent.getName().equals(p.getName()))
				.collect(Collectors.toList());

		if (ps.size() == 0)
			arg0.sendMessage("§cPersonne aux alentours !");
		else
			ps.forEach(ent -> {
				final M_Player pe = PlayerManagers.get().getPlayer(Bukkit.getPlayer(ent.getName()));
				arg0.sendMessage(pe.getRank().getPrefix(pe.getPlayerData()) + ent.getName() + " §f-> §e"
						+ (int) ent.getLocation().distance(p.getLocation()) + "m" + " §7avec §c"
						+ (int) Bukkit.getPlayer(pe.getName()).getHealth() + " pv");
			});
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
