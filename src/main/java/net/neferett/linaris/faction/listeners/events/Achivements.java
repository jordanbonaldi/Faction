package net.neferett.linaris.faction.listeners.events;

import org.bukkit.Achievement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class Achivements implements Listener {

	@EventHandler
	public void onAchieve(final PlayerAchievementAwardedEvent e) {
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());

		if (p.getAchievements().contains(e.getAchievement().name()))
			return;

		p.setAchievements(e.getAchievement().name());

		p.addMoney(p.getAchievements().size() * 2 + 25);

		p.sendMessage("§f§m===========§r §bObjectifs §f§m===============");
		p.sendMessage("");
		p.sendMessage("   §aVous avez reçu §e" + (p.getAchievements().size() * 2 + 25) + "$");
		p.sendMessage("   §aEn effectuant un objectif minecraft !");
		p.sendMessage("");
		p.sendMessage("§f§m===================================");
	}

}
