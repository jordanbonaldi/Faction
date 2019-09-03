package net.neferett.linaris.faction.classes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.handlers.kits.Kits;
import net.neferett.linaris.faction.spell.Spell;
import net.neferett.linaris.utils.ItemBuilder;

public class Classes {

	private String				chatcl;
	private FileConfiguration	classfile;
	private String				classname;
	private List<String>		cmds;
	private String				color;
	private final File			file;
	private int					homes;
	private Kits				kit;
	private String				logo;
	private Classes				next	= null;
	private int					price;
	private int					pricetoken;
	private List<Spell>			spells;
	private int					tab;
	private String				tabdisplay;

	public Classes(final String classname, final File f) {
		this.classname = classname;
		this.file = f;
		if (!this.file.exists()) {
			System.out.println("CLASS " + classname + " NOT FOUND !!!");
			return;
		}
		System.out.println("New Class loaded [" + classname + "]");
		this.classfile = YamlConfiguration.loadConfiguration(this.file);
		try {
			this.classfile.save(this.file);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Classes buildClass() {
		this.price = this.classfile.getInt("Price");
		this.pricetoken = this.classfile.getInt("PriceToken");
		this.kit = this.buildKit();
		this.logo = this.classfile.getString("Logo");
		System.out.println("LOGO -> " + this.logo);
		this.chatcl = this.classfile.getString("ChatColor");
		this.tabdisplay = this.classfile.getString("TabDisplay");
		System.out.println(this.classfile.getStringList("Spells"));
		this.spells = this.classfile.getStringList("Spells").stream().map(Spell::getByName)
				.collect(Collectors.toList());
		System.out.println(this.spells);
		this.cmds = this.classfile.getStringList("cmds");
		this.color = this.classfile.getString("Color");
		this.tab = this.classfile.getInt("tab");
		this.homes = this.classfile.getInt("homes");
		return this;
	}

	public Kits buildKit() {
		return new Kits(this.classname, this.classfile.getLong("kit.cooldown"), this.getItems());
	}

	public String getChatcl() {
		return this.chatcl;
	}

	public FileConfiguration getClassfile() {
		return this.classfile;
	}

	public String getClassname() {
		return this.classname;
	}

	public List<String> getCmds() {
		return this.cmds;
	}

	public String getColor() {
		return this.color == null ? "§7" : this.color;
	}

	public int getHomes() {
		return this.homes;
	}

	@SuppressWarnings("deprecation")
	public ItemBuilder getItem() {
		ItemBuilder tmp;

		tmp = new ItemBuilder(new ItemStack(this.classfile.getInt("kit.disp.item")));
		if (this.classfile.getInt("kit.disp.level") != 0)
			tmp.addEnchantment(Enchantment.getById(this.classfile.getInt("kit.disp.enchantment")),
					this.classfile.getInt("kit.disp.level"));
		return tmp;
	}

	@SuppressWarnings("deprecation")
	public ItemStack[] getItems() {
		final int itemsnb = this.classfile.getInt("kit.itemnb");
		final ItemStack[] items = new ItemStack[itemsnb + 1];
		ItemBuilder tmp;

		for (int i = 1; i <= itemsnb; i++) {
			if (this.classfile.getString("kit.Item." + i + ".id").contains(":")) {
				final String a = this.classfile.getString("kit.Item." + i + ".id");
				tmp = new ItemBuilder(new ItemStack(Integer.parseInt(a.split(":")[0]),
						this.classfile.getInt("kit.Item." + i + ".nb", (short) Integer.parseInt(a.split(":")[1]))));
				System.out.println(Integer.parseInt(a.split(":")[1]));
			} else
				tmp = new ItemBuilder(new ItemStack(this.classfile.getInt("kit.Item." + i + ".id"),
						this.classfile.getInt("kit.Item." + i + ".nb")));
			if (this.classfile.get("kit.Item." + i + ".meta") != null)
				tmp.setDamage((short) this.classfile.getInt("kit.Item." + i + ".meta"));
			if (this.classfile.getInt("kit.Item." + i + ".enchants") != 0)
				for (int j = 1; j <= this.classfile.getInt("kit.Item." + i + ".enchants"); j++)
					tmp.addEnchantment(
							Enchantment.getById(this.classfile.getInt("kit.Item." + i + "." + j + ".enchantment")),
							this.classfile.getInt("kit.Item." + i + "." + j + ".level"));
			items[i - 1] = tmp.build();
		}
		return items;
	}

	public Kits getKit() {
		return this.kit;
	}

	public String getLogo() {
		return this.logo;
	}

	public int getPrice() {
		return this.price;
	}

	public int getPricetoken() {
		return this.pricetoken;
	}

	public List<Spell> getSpells() {
		return this.spells;
	}

	public int getTab() {
		return this.tab;
	}

	public String getTabdisplay() {
		return this.tabdisplay;
	}

	public String name(final M_Player pa) {
		return Character.toUpperCase(this.getClassname().charAt(0)) + this.getClassname().substring(1);
	}

	public Classes next() {
		if (this.next == null && !this.classfile.getString("next").equals("none"))
			this.next = ClassesManager.get().getClassByName(this.classfile.getString("next").toLowerCase());
		else if (this.classfile.getString("next").equals("none"))
			this.next = this;
		return this.next;
	}

}
