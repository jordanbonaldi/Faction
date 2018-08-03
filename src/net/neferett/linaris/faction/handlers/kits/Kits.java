package net.neferett.linaris.faction.handlers.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.utils.TimeUtils;

public class Kits {

	private static final HashMap<String, Long>	cooldown	= new HashMap<>();
	private final long							cool;
	private List<ItemStack>						items		= new ArrayList<>();
	private final String						name;

	private final TimeUtils						timer		= new TimeUtils();

	public Kits(final String name, final long cooldown, final ItemStack... items) {
		this.name = name;
		this.cool = cooldown;
		this.items = Arrays.asList(items);
	}

	public long getCool() {
		return this.cool;
	}

	public HashMap<String, Long> getCooldown() {
		return cooldown;
	}

	public List<ItemStack> getItems() {
		return this.items;
	}

	public String getName() {
		return this.name;
	}

	public long getPlayer(final String a) {
		return cooldown.get(a.toLowerCase());
	}

	public TimeUtils getTimer() {
		return this.timer;
	}

	public void giveKits(final Player p) {
		this.getItems().forEach(item -> {
			if (item != null && item.getType() != null)
				p.getInventory().addItem(item);
		});
	}

	public boolean isExists(final String a) {
		return cooldown.containsKey(a.toLowerCase());
	}

	public void registerCoolDown() {

	}

	public void setPlayer(final String a) {
		cooldown.put(a.toLowerCase(), System.currentTimeMillis());
	}

}
