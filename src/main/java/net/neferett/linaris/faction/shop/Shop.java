package net.neferett.linaris.faction.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.shop.npc.NPC.VillagerType;
import net.neferett.linaris.utils.gui.GuiScreen;

public class Shop extends GuiScreen {

	private final File				file;
	private final FileConfiguration	shop;

	public Shop(final String name, final VillagerType t, final Player p) {
		super("Shop " + name, 6, p, false);
		this.file = new File("plugins/Faction/shop/" + t.getName());
		this.shop = YamlConfiguration.loadConfiguration(this.file);
		try {
			this.shop.save(this.file);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.build();
	}

	@Override
	public void drawScreen() {
		final AtomicInteger slot = new AtomicInteger(0);
		this.getItems().forEach(items -> {
			this.setItem(items.getItemBuild(), slot.getAndIncrement());
		});
	}

	List<ShopItems> getItems() {
		final List<String> l = this.shop.getStringList("Items");
		final List<ShopItems> items = new ArrayList<>();
		l.forEach(li -> {
			final List<String> list = Arrays.asList(li.split(":"));
			items.add(new ShopItems(list.get(0), Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)),
					Short.parseShort(list.get(3)), Integer.parseInt(list.get(4)), Integer.parseInt(list.get(5)),
					Integer.parseInt(list.get(6)),
					list.stream().filter(la -> la.contains("-")).collect(Collectors.toList())));
		});
		return items;
	}

	@Override
	public void onClick(final ItemStack item, final InventoryClickEvent event) {
		final ShopItems i = this.getItems().stream().filter(s -> s.getItemBuild().isSimilar(item)).findFirst()
				.orElse(null);

		if (i == null)
			return;

		if (event.isLeftClick())
			i.buy((Player) event.getWhoClicked());
		else if (event.isRightClick())
			i.sell((Player) event.getWhoClicked());
	}

	@Override
	public void onClose() {}

	@Override
	public void onOpen() {}

}
