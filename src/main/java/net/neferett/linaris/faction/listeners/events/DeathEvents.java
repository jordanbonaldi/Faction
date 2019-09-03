package net.neferett.linaris.faction.listeners.events;

import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.spell.Spell;
import net.neferett.linaris.faction.utils.TimeUtils;

public class DeathEvents implements Listener {

	@EventHandler
	public void onPlayerDeath(final PlayerDeathEvent e) throws InterruptedException, ExecutionException {
		final M_Player p = PlayerManagers.get().getPlayer(e.getEntity());

		e.setDeathMessage("");
		if (p.getRank().getVipLevel() > 2)
			e.setKeepLevel(true);
		p.death(e.getEntity());

		Spell spell = null;
		M_Player killer = null;

		if (p.getLastDamageCause().getCause() == DamageCause.LIGHTNING
				|| p.getLastDamageCause().getCause() == DamageCause.BLOCK_EXPLOSION
				|| p.getLastDamageCause().getCause() == DamageCause.FIRE_TICK
				|| p.getLastDamageCause().getCause() == DamageCause.FALL
				|| p.getLastDamageCause().getCause() == DamageCause.FALLING_BLOCK
				|| p.getLastDamageCause().getCause() == DamageCause.WITHER) {
			if (p.isSpellon() && TimeUtils.CreateTestCoolDown(20).test(p.getSpelltimer())) {
				killer = p.getSpellshooter();
				spell = p.getSpellshooted();
			}
			p.setSpellon(false);
		}

		if (killer == null && !(e.getEntity().getKiller() instanceof Player))
			return;
		killer = killer == null ? PlayerManagers.get().getPlayer(p.getKiller()) : killer;

		Bukkit.broadcastMessage("§e" + p.getName() + " §7a été tué par §e" + killer.getName()
				+ (spell != null ? "§7 avec son sort " + spell.getE().getName() : ""));
		killer.kill();

	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e) {
		PlayerManagers.get().getPlayer(e.getPlayer()).teleport(ConfigReader.getInstance().getSpawn());
	}

}
