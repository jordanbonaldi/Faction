package net.neferett.linaris.faction.listeners;

import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.entity.MPlayer;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class ChatHandling implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerChat(final AsyncPlayerChatEvent event) {
		final M_Player player = PlayerManagers.get().getPlayer(event.getPlayer());

		final MPlayer mp = MPlayer.get(player.getPlayer());
		event.setCancelled(true);
		final String facname = mp.getFactionName() == null ? "" : mp.getFactionName() + " ";
		if (player.getRank().getModerationLevel() < 1)
			PlayerManagers.get().ActionOnPlayers((p) -> {
				final MPlayer mpp = MPlayer.get(p);
				if (mp.hasFaction()) {
					final String color = mpp.getFactionName().equals(mp.getFactionName()) ? "§a"
							: mpp.getFaction().getRelationWish(mp.getFaction()).getColor() + "";
					p.sendMessage(color + mp.getRole().getPrefix() + facname + " " + "§" + player.getRank().getColor()
							+ (ConfigReader.getInstance().isClass() ? player.getClasses().getLogo() :  player.getRank().getLogo())+ " " + player.getName() + " §f: "
							+ event.getMessage().trim());
				} else
					p.sendMessage("§" + player.getRank().getColor() + (ConfigReader.getInstance().isClass() ? player.getClasses().getLogo() :  player.getRank().getLogo()) + " "
							+ player.getName() + " §f: " + event.getMessage().trim());
			});
		else
			PlayerManagers.get().ActionOnPlayers((p) -> {
				p.sendMessage(player.getRank().getPrefix(player.getPlayerData()) + player.getName() + "§"
						+ player.getRank().getColor() + " : " + event.getMessage().trim().replace("&", "§"));
			});

	}

}
