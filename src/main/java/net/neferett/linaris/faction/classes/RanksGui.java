package net.neferett.linaris.faction.classes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.utils.ItemBuilder;
import net.neferett.linaris.utils.NBTItem;
import net.neferett.linaris.utils.TimeUtils;
import net.neferett.linaris.utils.gui.GuiManager;
import net.neferett.linaris.utils.gui.GuiScreen;

public class RanksGui extends GuiScreen {

	private static enum PriceType {

		Token("Tokens");

		public static PriceType getTypeByString(final String a) {
			return Arrays.asList(values()).stream().filter(e -> e.getName().equalsIgnoreCase(a)).findFirst()
					.orElse(null);
		}

		private String name;

		private PriceType(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	public interface ShopInterface {

		public Callable<Integer> Money();

		public void removeMoney();

	}

	private final Classes	clazz;

	private final String	name;

	private final M_Player	p;

	public RanksGui(final String name, final M_Player p) {
		super("Grade " + name + " " + ClassesManager.get().getClassByName(name).getLogo(), 1, p.getPlayer(), false);
		this.p = p;
		this.name = name;
		this.clazz = ClassesManager.get().getClassByName(name);
		this.build();
	}

	private List<String> cmdsLores() {
		final List<String> l = this.clazz.getCmds().stream()
				.map(m -> "§f- §b" + Character.toUpperCase(m.charAt(0)) + m.substring(1)).collect(Collectors.toList());
		if (l.size() == 0)
			l.add("§cAucun");
		return l;
	}

	@Override
	public void drawScreen() {
		this.setItemLine(
				new NBTItem(new ItemBuilder(Material.STICK).setTitle("§6Sorts").addLores(this.spellLores()).build())
						.getItem(),
				1, 2);

		this.setItemLine(
				new NBTItem(new ItemBuilder(Material.DIAMOND_CHESTPLATE).addLores("", "§aClique pour voir le kit")
						.setTitle("§7Kit §e" + this.name).build()).setBoolean("show", true).getItem(),
				1, 4);

		this.setItemLine(
				new NBTItem(new ItemBuilder(Material.STICK).setTitle("§6Commandes").addLores(this.cmdsLores()).build())
						.getItem(),
				1, 6);

		if (this.p.getClasses() == this.clazz) {
			this.setItemLine(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 15).setTitle("§6Achat")
					.addLores("", "§cVous avez déja ce grade", "").build(), 1, 9);
			return;
		}
		this.setItemLine(this.setBuyButton(PriceType.Token), 1, 9);
	}

	public void onBuy(final ShopInterface i, final PriceType type) {
		i.removeMoney();
		for (int j = 0; j < 100; j++)
			this.p.sendMessage("");
		this.p.sendMessage("§7Vous venez d'acheter le grade §e" + this.name + "§7 !");
		try {
			this.p.sendMessage("§7Il vous reste §e" + i.Money().call() + type.getName());
		} catch (final Exception e) {
			e.printStackTrace();
		}
//		this.p.setRank(this.clazz.getClassname());
		this.p.sendMessage("");
		this.p.sendMessage("§aVous êtes maintenant §e" + this.name + "§a !");
		this.p.sendMessage("");
		this.p.closeInventory();
	}

	@Override
	public void onClick(final ItemStack item, final InventoryClickEvent event) {

		final NBTItem nbt = new NBTItem(item);
		if (nbt.hasKey("type") && nbt.hasKey("buy") && !TimeUtils.CreateTestCoolDown(5).test(this.p.getCltime())) {
			final PriceType type = PriceType.getTypeByString(nbt.getString("type"));
			if (nbt.getBoolean("buy"))
				this.onBuy(new ShopInterface() {

					@Override
					public Callable<Integer> Money() {
						return () -> RanksGui.this.p.getPlayerData().getTokens();
					}

					@Override
					public void removeMoney() {
						RanksGui.this.p.setCltime();
						try {
							RanksGui.this.p.getPlayerData().setInt("tokens",
									this.Money().call() - RanksGui.this.clazz.getPricetoken());
						} catch (final Exception e) {
							e.printStackTrace();
						}
					}
				}, PriceType.getTypeByString(nbt.getString("type")));
			else if (TimeUtils.CreateTestCoolDown(5).test(this.p.getCltime())) {
				this.p.sendMessage("§cAttendez entre chaque achat !");
				return;
			} else {
				this.p.sendMessage("§7Tu n'as pas assez de §c" + type.toString() + "§7 pour acheter ceci !");
				this.p.sendMessage(
						"§7Il te manque §c" + (this.clazz.getPricetoken() - this.p.getPlayerData().getTokens()) + " "
								+ type.getName() + "§7 pour acheter ceci !");
				this.p.closeInventory();
			}
			return;
		} else if (nbt.hasKey("show") && nbt.getBoolean("show"))
			GuiManager.openGui(new GuiShowKits(this.name, this.p.getPlayer(), this, this.clazz.getKit()));

	}

	@Override
	public void onClose() {}

	@Override
	public void onOpen() {}

	public ItemStack setBuyButton(final PriceType type) {
		final Boolean canbuy = this.p.getPlayerData().getTokens() - this.clazz.getPricetoken() >= 0;
		final ItemBuilder b = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, canbuy ? (short) 5 : (short) 15);
		b.setTitle("§7Acheter§f:");
		b.addLores("", "§7Prix§f: " + (canbuy ? "§a" : "§c") + this.clazz.getPricetoken() + " Tokens");
		if (canbuy)
			b.addLores("", "§c" + type.toString() + " §7actuel§f: §e" + " " + type.getName(), "",
					"§7" + type.toString() + " apres l'achat§f: §a"
							+ (this.p.getPlayerData().getTokens() - this.clazz.getPricetoken()) + " Tokens",
					"", "§eClique pour acheter !");
		else
			b.addLores("",
					"§c" + type.toString() + " §7actuel§f: §e" + this.p.getPlayerData().getTokens() + " "
							+ type.getName(),
					"",
					"§7Il vous manque§f: §c" + (this.clazz.getPricetoken() - this.p.getPlayerData().getTokens())
							+ " Tokens",
					"", "§cVous n'avez pas assez de", "§e" + type.toString() + " §cpour acheter ce grade !");
		return new NBTItem(b.build()).setString("type", type.getName()).setBoolean("buy", canbuy).getItem();
	}

	private List<String> spellLores() {
		final List<String> l = this.clazz.getSpells().stream().filter(e -> e != null)
				.map(m -> "§f- " + m.getE().getName()).collect(Collectors.toList());
		if (l.size() == 0)
			l.add("§cAucun");
		return l;
	}

}
