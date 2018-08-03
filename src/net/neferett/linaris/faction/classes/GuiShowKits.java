package net.neferett.linaris.faction.classes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.handlers.kits.Kits;
import net.neferett.linaris.utils.ItemBuilder;
import net.neferett.linaris.utils.gui.GuiManager;
import net.neferett.linaris.utils.gui.GuiScreen;

public class GuiShowKits extends GuiScreen {

	private final Kits		k;
	private final GuiScreen	last;

	public GuiShowKits(final String name, final Player p, final GuiScreen last, final Kits k) {
		super("Items dans Kit " + name, 5, p, false);
		this.last = last;
		this.k = k;
		this.build();
	}

	@Override
	public void drawScreen() {

		this.k.getItems().forEach((item) -> {
			if (item != null && item.getType() != null)
				this.addItem(item);
		});
		this.setItemLine(new ItemBuilder(Material.ARROW).setTitle("§6Retour").build(), 5, 9);
	}

	@Override
	public void onClick(final ItemStack item, final InventoryClickEvent e) {
		if (e.getCurrentItem().getType().equals(Material.ARROW)
				&& e.getCurrentItem().getItemMeta().getDisplayName().contains("Retour"))
			GuiManager.openGui(this.last);
	}

	@Override
	public void onClose() {}

	@Override
	public void onOpen() {}

}
