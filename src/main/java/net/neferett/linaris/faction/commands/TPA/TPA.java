package net.neferett.linaris.faction.commands.TPA;

import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.api.PlayerLocalManager;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.utils.TimeUtils;

public class TPA extends CommandHandler {

	TimeUtils time = new TimeUtils();

	public TPA() {
		super("tpa", Objects::nonNull);
		this.setErrorMsg("§cVous n'avez pas la permission de faire ceci");
	}

	@Override
	public void cmd(final Players p, final String cmd, final List<String> args) {
		if (args.size() == 1) {
			p.sendMessage("§cVous devez specifier un joueur !");
			return;
		} else if (args.size() == 2 && Bukkit.getPlayer(args.get(1)) == null) {
			p.sendMessage("§cLe joueur §e" + args.get(1) + "§c n'existe pas !");
			return;
		} else if (p.getPlayerLocal().contains("tpa-cooldown") && p.getPlayerLocal().contains("tpa")
				&& TimeUtils.CreateTestCoolDown(30).test(p.getPlayerLocal().getLong("tpa-cooldown"))) {
			p.sendMessage("§7Vous avez déjà une demande de téléportation vers §e" + p.getPlayerLocal().get("tpa")
					+ "§7 qui expire dans §c" + TimeUtils.getTimeLeft(p.getPlayerLocal().getLong("tpa-cooldown"), 30)
					+ " secondes §7!");
			return;
		} else {
			p.getPlayerLocal().setLong("tpa-cooldown", System.currentTimeMillis());
			p.getPlayerLocal().set("tpa", args.get(1));
			p.sendMessage("§7Requête de téléportation envoyé à §e" + args.get(1));
			PlayerLocalManager.get().getPlayerLocal(args.get(1)).setLong("tpaask-cl", System.currentTimeMillis());
			PlayerLocalManager.get().getPlayerLocal(args.get(1)).set("tpaask", p.getName());
			Bukkit.getPlayer(args.get(1)).sendMessage("§e" + p.getName() + "§7 souhaite se téléporter a vous !");
			Bukkit.getPlayer(args.get(1))
					.sendMessage("§7Faites §c/tpaccept §7pour accepter le demande ou §c/tpdeny§7 pour refuser");
			Bukkit.getPlayer(args.get(1)).sendMessage("§7La demande expire dans §a30 secondes §7!");
		}
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
