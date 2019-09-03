package net.neferett.linaris.faction.listeners.events;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.neferett.linaris.faction.classes.ClassesGUI;
import net.neferett.linaris.faction.classes.RanksGui;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.handlers.ConfigReader;
import net.neferett.linaris.faction.shop.Shop;
import net.neferett.linaris.faction.shop.npc.NPC;
import net.neferett.linaris.faction.shop.npc.NPC.VillagerType;
import net.neferett.linaris.utils.gui.GuiManager;

public class InteractListener implements Listener {

	@EventHandler
	public void blockChestInterract(final PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getType() != null
				&& (event.getItem().getType().equals(Material.LAVA_BUCKET)
						|| event.getItem().getType().equals(Material.WATER_BUCKET))
				&& ConfigReader.getInstance().getWarzone().isInside(event.getPlayer().getLocation())
				&& !event.getPlayer().isOp())
			event.setCancelled(true);
		if (event.getItem() != null && event.getItem().getType().equals(Material.ENDER_PEARL)
				&& ConfigReader.getInstance().getWarzone().isInside(event.getPlayer().getLocation())) {
			event.getPlayer().sendMessage("§cTu ne peux pas utiliser d'enderpearl dans la warzone !");
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void InteractWithNPCSWithLeftClick(final EntityDamageByEntityEvent e) {
		if (!e.getDamager().getLocation().getWorld().getName().equals("world"))
			return;
		if (!(e.getDamager() instanceof Player) && e.getEntity() instanceof Villager) {
			e.setCancelled(true);
			return;
		}
		if (e.getEntity() instanceof Villager)
			Arrays.asList(NPC.values()).forEach(npc -> {
				if (npc.getEntity().equals(e.getEntity())) {
					if (npc.getType().equals(VillagerType.Classes))
						GuiManager.openGui(new ClassesGUI(PlayerManagers.get().getPlayer((Player) e.getDamager())));
					else if (npc.getType().equals(VillagerType.Ranks))
						GuiManager.openGui(
								new RanksGui(npc.getName(), PlayerManagers.get().getPlayer((Player) e.getDamager())));
					else
						GuiManager.openGui(new Shop(npc.getName(), npc.getType(), (Player) e.getDamager()));
					e.setCancelled(true);
					return;
				}
			});
	}

	@EventHandler
	public void InteractWithNPCSWithRightClick(final PlayerInteractEntityEvent e) {
		if (!e.getPlayer().getLocation().getWorld().getName().equals("world"))
			return;
		if (e.getRightClicked() instanceof Villager)
			Arrays.asList(NPC.values()).forEach(npc -> {
				if (npc.getEntity().equals(e.getRightClicked())) {
					if (npc.getType().equals(VillagerType.Classes))
						GuiManager.openGui(new ClassesGUI(PlayerManagers.get().getPlayer(e.getPlayer())));
					else if (npc.getType().equals(VillagerType.Ranks))
						GuiManager.openGui(new RanksGui(npc.getName(), PlayerManagers.get().getPlayer(e.getPlayer())));
					else
						GuiManager.openGui(new Shop(npc.getName(), npc.getType(), e.getPlayer()));
					e.setCancelled(true);
					return;
				}
			});
	}

	@EventHandler
	public void onDoubleClick(final InventoryClickEvent e) {
		e.setCancelled(e.getClick().isLeftClick() && e.getClick().equals(ClickType.DOUBLE_CLICK));
	}

}
