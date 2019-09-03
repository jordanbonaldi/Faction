package net.neferett.linaris.faction.listeners.events;

import java.util.HashMap;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.utils.TimeUtils;

public class DamageEvent implements Listener {

	public static HashMap<Player, Long> time = new HashMap<>();

	@EventHandler
	public void damage(final EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player || e.getDamager() instanceof CraftArrow)
				|| !(e.getEntity() instanceof Player))
			return;

		if (Main.getInstanceMain().getCb().isInside(e.getEntity().getLocation()) &&
			(!ConfigReader.getInstance().isWarzoneInSpawn() || Main.getInstanceMain().getCb2().isInside(e.getEntity().getLocation())
			&& !ConfigReader.getInstance().getWarzoneSafe().isInside(e.getEntity().getLocation()))
		)
			return;

		final CraftArrow arr = e.getDamager() instanceof CraftArrow ? (CraftArrow) e.getDamager() : null;
		final Player p = arr != null && arr.getShooter() instanceof Player ? (Player) arr.getShooter()
				: (Player) e.getDamager();
		if (!time.containsKey(p) || time.containsKey(p) && !TimeUtils.CreateTestCoolDown(15).test(time.get(p)))
			p.sendMessage(
					"§cVous entrez en combat, vous devez attendre §e15 secondes§c pour pouvoir vous deconnecter !");
		if (!time.containsKey(e.getEntity()) || time.containsKey(e.getEntity())
				&& !TimeUtils.CreateTestCoolDown(15).test(time.get(e.getEntity())))
			e.getEntity().sendMessage(
					"§cVous entrez en combat, vous devez attendre §e15 secondes§c pour pouvoir vous deconnecter !");
		time.put(p, System.currentTimeMillis());
		time.put((Player) e.getEntity(), System.currentTimeMillis());
	}

	@EventHandler
	public void damageatspawn(final EntityDamageEvent e) {
		if (e.getEntity() instanceof Villager) {
			final Villager v = (Villager) e.getEntity();
			if (Main.getInstanceMain().getCb().isInside(e.getEntity().getLocation()) && v.getName().contains("SHOP"))
				e.setCancelled(true);
			return;
		}
		if (!(e.getEntity() instanceof Player))
			return;
		if (Main.getInstanceMain().getCb().isInside(e.getEntity().getLocation()) &&
				(!ConfigReader.getInstance().isWarzoneInSpawn() || Main.getInstanceMain().getCb2().isInside(e.getEntity().getLocation())
				&& !ConfigReader.getInstance().getWarzoneSafe().isInside(e.getEntity().getLocation()))
		)
			e.setCancelled(true);
	}
}
