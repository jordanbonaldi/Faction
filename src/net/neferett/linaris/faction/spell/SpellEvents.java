package net.neferett.linaris.faction.spell;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.utils.PlayerUtils;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public class SpellEvents implements Listener {

	@EventHandler
	public void DamageWithMagicWandItem(final EntityDamageByEntityEvent e) {
		if (e.getDamager() == null || !(e.getDamager() instanceof Player))
			return;
		final M_Player p = PlayerManagers.get().getPlayer((Player) e.getDamager());
		if (p.getItemInHand().getType() == null
				|| p.getItemInHand().getType() != null && !p.getItemInHand().equals(Material.STICK))
			return;
		final Spell s = p.getCurrent();
		if (s == null) {
			p.sendMessage("§cTu as débloqué aucun sort !");
			return;
		}
		s.getE().DamageEvent(e);
	}

	@EventHandler
	public void InteractWithSpecItem(final PlayerInteractEvent e) {
		if (e.getItem() == null || e.getItem().getType() != null && !e.getItem().getType().equals(Material.STICK))
			return;
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			final Spell s = p.getCurrent() == null ? null : p.nextSpell();
			if (s == null) {
				p.sendMessage("§cTu as débloqué aucun sort !");
				return;
			}
			p.setCurrent(s);
			e.getPlayer().sendMessage("§7Sort actuel§f: §e" + s.getE().getName());
		} else {
			if (p.getCurrent() == null) {
				p.sendMessage("§cTu n'as aucun sort !");
				return;
			} else if (Main.getInstanceMain().getCb().isInside(p.getLocation())
					&& Main.getInstanceMain().getCb2().isInside(p.getLocation())
					&& !ConfigReader.getInstance().getWarzoneSafe().isInside(p.getLocation())) {
				e.getPlayer().sendMessage("§cTu ne peux pas utiliser de sorts au spawn !");
				return;
			}
			p.getCurrent().getE().ClickEvent(e);
		}
	}

	@EventHandler
	public void onSwapItem(final PlayerItemHeldEvent e) {
		if (TaskManager.taskExist("held-" + e.getPlayer().getName().toLowerCase()))
			TaskManager.cancelTaskByName("held-" + e.getPlayer().getName().toLowerCase());
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());
		if (e.getPlayer().getInventory().getItem(e.getNewSlot()) == null)
			return;
		else if (e.getPlayer().getInventory().getItem(e.getNewSlot()).getType().equals(Material.STICK))
			if (p.getCurrent() == null)
				PlayerUtils.sendActionMessage(p.getPlayer(), "§cTu as débloqué aucun sort !");
			else
				p.getCurrent().getE().buildTargetTask(p, e.getPlayer().getInventory().getItem(e.getNewSlot()));

	}

}
