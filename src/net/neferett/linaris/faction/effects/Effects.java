package net.neferett.linaris.faction.effects;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.massivecraft.factions.entity.MPlayer;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.utils.EntityUtils;
import net.neferett.linaris.utils.PlayerUtils;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public abstract class Effects {

	public interface Actions {
		public void onClick(PlayerInteractEvent e, M_Player p);

		public boolean onDamage(EntityDamageByEntityEvent e);
	}

	public interface CallBack {
		public void call();
	}

	public String effectname;

	public Effects(final String name) {
		this.effectname = name;
	}

	public abstract void buildTargetTask(final M_Player p, final ItemStack i);

	public boolean checkFaction(final Player i, final Player to) {
		final MPlayer shooter = MPlayer.get(i);
		final MPlayer receiver = MPlayer.get(to);

		if (!shooter.hasFaction() || !receiver.hasFaction())
			return false;
		if (shooter.getFaction().getName().equals(receiver.getFaction().getName())) {
			i.sendMessage("§cTu ne peux pas faire cela sur un membre de ta faction !");
			return true;
		}
		return false;
	}

	public boolean checkFactionAction(final Player i, final Player to) {
		final MPlayer shooter = MPlayer.get(i);
		final MPlayer receiver = MPlayer.get(to);

		if (!shooter.hasFaction() || !receiver.hasFaction())
			return false;
		if (shooter.getFaction().getName().equals(receiver.getFaction().getName())) {
			PlayerUtils.sendActionMessage(i, "§e" + to.getName() + "§f: §cCe joueur est dans votre §dFaction §c!");
			return true;
		}
		return false;
	}

	public abstract void ClickEvent(final PlayerInteractEvent e);

	public abstract void DamageEvent(EntityDamageByEntityEvent e);

	public Player getEntityNear(final Player p, final int range, final int min, final int max) {
		final Player pa = EntityUtils.getPlayerNear(p, range);
		if (pa == null)
			return pa;
		if (pa.getLocation().distance(p.getLocation()) > max)
			return null;
		else if (pa.getLocation().distance(p.getLocation()) <= max && pa.getLocation().distance(p.getLocation()) <= min)
			return p;
		else if (pa.getLocation().distance(p.getLocation()) <= max && pa.getLocation().distance(p.getLocation()) > min)
			return pa.equals(p) ? null : pa;
		return pa;
	}

	public String getName() {
		return this.effectname;
	}

	public abstract Actions onActions();

	public void RunLater(final int sec, final CallBack call) {
		TaskManager.runTaskLater(() -> {
			try {
				call.call();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}, sec * 20);
	}

	public void RunScheduled(final int sec, final CallBack call) {
		TaskManager.scheduleSyncRepeatingTaskWithEnd("task" + call.toString(), () -> call.call(), 0, 1 * 20, sec);
	}

	public void RunScheduledByTicks(final int sec, final CallBack call, final short tick) {
		TaskManager.scheduleSyncRepeatingTaskWithEnd("task" + call.toString(), () -> call.call(), 0, tick, sec);
	}
}
