package net.neferett.linaris.faction.commands.TPA;

import java.util.List;

import org.bukkit.Bukkit;

import net.neferett.linaris.PlayersHandler.PlayerManager;
import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.api.PlayerLocalManager;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.utils.TimeUtils;

public class TPAdeny extends CommandHandler {

	public TPAdeny() {
		super("tpdeny", p -> PlayerLocalManager.get().getPlayerLocal(p.playername).contains("tpaask"), "tpno");
		this.setErrorMsg("§cVous n'avez aucune demande de téléportation en cours");
	}

	@Override
	public void cmd(final Players p, final String cmd, final List<String> args) {
		if (p.getPlayerLocal().contains("tpaask-cl")
				&& !TimeUtils.CreateTestCoolDown(30).test(p.getPlayerLocal().getLong("tpaask-cl"))) {
			final Players pa = PlayerManager.get().getPlayer(Bukkit.getPlayer(p.getPlayerLocal().get("tpaask")));
			p.sendMessage("§7La demande de téléportation a expirée !");
			pa.getPlayerLocal().remove("tpa");
			pa.getPlayerLocal().remove("tpa-cooldown");
			p.getPlayerLocal().remove("tpaask");
			p.getPlayerLocal().remove("tpaask-cl");
			return;
		} else if (Bukkit.getPlayer(p.getPlayerLocal().get("tpaask")) == null) {
			p.sendMessage("§7Demande annulée");
			p.sendMessage("§cLe joueur §e" + p.getPlayerLocal().get("tpaask") + "§c s'est déconnecté !");
			return;
		} else {
			final Players pa = PlayerManager.get().getPlayer(Bukkit.getPlayer(p.getPlayerLocal().get("tpaask")));
			p.sendMessage("§7Requête refusée !");
			pa.sendMessage("§e" + p.getName() + "§7 a refusé votre reqûete");
			pa.getPlayerLocal().remove("tpa");
			pa.getPlayerLocal().remove("tpa-cooldown");
			p.getPlayerLocal().remove("tpaask");
			p.getPlayerLocal().remove("tpaask-cl");
		}
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
