package net.neferett.linaris.faction.shop;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.utils.ItemBuilder;

public class ShopItems {

	private final int			amount;
	private final int			buy;
	private final short			data;
	private final List<String>	enchant;
	private final ItemBuilder	ib;
	private final ItemBuilder	ibgive;
	private final int			id;
	private final String		name;
	private final int			sell;
	private final int			sellamount;

	@SuppressWarnings("deprecation")
	public ShopItems(final String name, final int id, final int amount, final short data, final int buy, final int sell,
			final int sellamount, final List<String> enchants) {
		this.id = id;
		this.amount = amount;
		this.sellamount = sellamount;
		this.name = name;
		this.data = data;
		this.enchant = enchants;
		this.ibgive = new ItemBuilder(Material.getMaterial(id), amount, data);
		this.ib = new ItemBuilder(Material.getMaterial(id), amount, data);
		this.buy = buy;
		this.sell = sell;
	}

	public void buy(final Player p) {
		final M_Player pa = PlayerManagers.get().getPlayer(p);
		if (this.canBuy(pa)) {
			pa.delMoney(this.buy);
			this.GiveItem(p);
			p.sendMessage(
					"§7Vous venez d'acheter §c" + this.amount + " x " + this.name + "§7 pour §e" + this.buy + "$");
			p.sendMessage("§7Il vous reste maintenant §e" + pa.getMoney() + "$");
		} else
			p.sendMessage("§cVous n'avez pas assez d'argent il vous manque " + (this.buy - pa.getMoney()) + "!");
	}

	private boolean canBuy(final M_Player pa) {
		return pa.getMoney() >= this.buy;
	}

	@SuppressWarnings("deprecation")
	private void deserializeEnchant(final ItemBuilder ib) {
		this.enchant.forEach(e -> {
			final String[] a = e.split("-");
			ib.addEnchantment(Enchantment.getById(Integer.parseInt(a[0])), Integer.parseInt(a[1]));
		});
	}

	public ItemStack getDefaultItem() {
		this.deserializeEnchant(this.ibgive);
		return this.ibgive.build();
	}

	public ItemStack getItemBuild() {
		this.ib.setTitle("§6" + Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1));
		this.deserializeEnchant(this.ib);
		this.ib.addLores("", "§7Nombre§f: §e" + this.amount, "", "§7Achat§f: §e" + this.buy, "");
		if (this.sell != 0)
			this.ib.addLores("§7Vente§f: §e" + this.sell + "§7 x " + this.sellamount, "",
					"§bClique droit pour vendre !");
		this.ib.addLores("§aClique gauche pour acheter !");
		return this.ib.build();
	}

	private void GiveItem(final Player p) {
		p.getInventory().addItem(this.getDefaultItem());
		p.sendMessage("§eVous venez de recevoir vos items !");
	}

	@SuppressWarnings("deprecation")
	public void sell(final Player p) {
		if (this.sell != 0 && p.getInventory().contains(Material.getMaterial(this.id), this.sellamount)) {
			final List<ItemStack> itemstab = Arrays.asList(p.getInventory().getContents());
			for (final ItemStack items : itemstab)
				if (items != null && items.getType() != null && !items.getType().equals(Material.AIR)
						&& items.getType().equals(Material.getMaterial(this.id)) && items.getDurability() == this.data
						&& !(items.containsEnchantment(Enchantment.DURABILITY)
								&& items.getEnchantmentLevel(Enchantment.DURABILITY) == 100))
					if (items.getAmount() == this.sellamount) {
						items.setType(Material.AIR);
						PlayerManagers.get().getPlayer(p).addMoney(this.sell);
						break;
					} else if (items.getAmount() > this.sellamount) {
						items.setAmount(items.getAmount() - this.sellamount);
						PlayerManagers.get().getPlayer(p).addMoney(this.sell);
						break;
					}
			p.getInventory().setContents(itemstab.toArray(new ItemStack[itemstab.size()]));
			p.updateInventory();
		} else if (this.sell == 0)
			p.sendMessage("§cCet item ne peut pas être vendu !");
		else
			p.sendMessage("§cVous n'avez pas le/les items nécessaires !");
	}

}
