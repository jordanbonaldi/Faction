package net.neferett.linaris.faction.listeners.events;

import java.util.ArrayList;

import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

public class AutoLapis implements Listener {
	public ArrayList<EnchantingInventory>	inventories	= new ArrayList<>();
	private final ItemStack					lapis;

	public AutoLapis() {
		final Dye d = new Dye();
		d.setColor(DyeColor.BLUE);
		this.lapis = d.toItemStack();
		this.lapis.setAmount(3);
	}

	@EventHandler
	public void closeInventoryEvent(final InventoryCloseEvent e) {
		if (e.getInventory() instanceof EnchantingInventory)
			if (this.inventories.contains(e.getInventory())) {
				e.getInventory().setItem(1, null);
				this.inventories.remove(e.getInventory());
			}
	}

	@EventHandler
	public void enchantItemEvent(final EnchantItemEvent e) {

		e.getEnchanter().setLevel(e.getEnchanter().getLevel() - e.getExpLevelCost());
		if (this.inventories.contains(e.getInventory()))
			e.getInventory().setItem(1, this.lapis);
	}

	@EventHandler
	public void inventoryClickEvent(final InventoryClickEvent e) {
		if (e.getClickedInventory() instanceof EnchantingInventory)
			if (this.inventories.contains(e.getInventory()) && e.getSlot() == 1)
				e.setCancelled(true);
	}

	@EventHandler
	public void openInventoryEvent(final InventoryOpenEvent e) {
		if (e.getInventory() instanceof EnchantingInventory) {
			e.getInventory().setItem(1, this.lapis);
			this.inventories.add((EnchantingInventory) e.getInventory());
		}
	}
}