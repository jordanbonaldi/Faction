package net.neferett.linaris.faction.handlers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.neferett.linaris.faction.effects.Effects.CallBack;
import net.neferett.linaris.faction.shop.npc.NPC.VillagerType;
import net.neferett.linaris.utils.ItemBuilder;
import net.neferett.linaris.utils.Particles;
import net.neferett.linaris.utils.UtilParticles;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public class SpawnItems {

	class LocalItem {

		private final int			amount;
		private final short			data;
		private final List<String>	enchants;
		private ItemBuilder			ib;
		private final int			id;

		public LocalItem(final int id, final int amount, final short data, final List<String> enchants) {
			this.id = id;
			this.amount = amount;
			this.data = data;
			this.enchants = enchants;
		}

		@SuppressWarnings("deprecation")
		public LocalItem build() {
			this.ib = new ItemBuilder(Material.getMaterial(this.id), this.amount, this.data);
			this.deserializeEnchant();
			return this;
		}

		@SuppressWarnings({ "deprecation" })
		private void deserializeEnchant() {
			this.enchants.forEach(e -> {
				final String[] a = e.split("-");
				this.ib.addEnchantment(Enchantment.getById(Integer.parseInt(a[0])), Integer.parseInt(a[1]));
			});
		}

		public ItemStack getItems() {
			return this.ib.build();
		}
	}

	private List<ItemStack>	items;
	private List<Location>	l;

	public SpawnItems() {
		this.loadLocation();
		this.loadItems();
	}

	private List<String> getList(final VillagerType t, final String n) {
		final File f = new File("plugins/Faction/shop/" + t.getName());
		final FileConfiguration fi = YamlConfiguration.loadConfiguration(f);

		return fi.getStringList(n);
	}

	public ItemStack getRandomItem() {
		return this.items.get(new Random().nextInt(this.items.size()));
	}

	public void loadItems() {
		this.items = this.getList(VillagerType.DROP, "Items").stream().map(e -> {
			final List<String> list = Arrays.asList(e.split(":"));

			return new LocalItem(Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)),
					Short.parseShort(list.get(3)),
					list.stream().filter(la -> la.contains("-")).collect(Collectors.toList())).build().getItems();
		}).collect(Collectors.toList());
	}

	public void loadLocation() {
		this.l = this.getList(VillagerType.DROPLOC, "Locs").stream().map(e -> {
			final String[] l = e.split(":");
			return new Location(Bukkit.getWorld("world"), Integer.parseInt(l[0]), Integer.parseInt(l[1]),
					Integer.parseInt(l[2]));
		}).collect(Collectors.toList());
	}

	private void RunScheduledByTicks(final int sec, final CallBack call, final short tick) {
		TaskManager.scheduleSyncRepeatingTaskWithEnd("task" + call.toString(), call::call, 0, tick, sec);
	}

	public void spawn() {
		TaskManager.scheduleSyncRepeatingTask("SpawningItems", () -> {
			if (Bukkit.getOnlinePlayers().size() < 15)
				return;
			this.l.forEach(e -> {
				this.RunScheduledByTicks(5,
						() -> UtilParticles.generateParticleArroundPlayer(Particles.PORTAL,
								new Location(e.getWorld(), e.getX(), e.getY(), e.getZ()).add(0, 0.5, 0), 2, 1, 5),
						(short) 5);

				this.RunScheduledByTicks(5,
						() -> UtilParticles.generateParticleArroundPlayer(Particles.PORTAL,
								new Location(e.getWorld(), e.getX(), e.getY(), e.getZ()).add(0, 1, 0), 2, 1, 5),
						(short) 5);

				this.RunScheduledByTicks(5,
						() -> UtilParticles.generateParticleArroundPlayer(Particles.PORTAL,
								new Location(e.getWorld(), e.getX(), e.getY(), e.getZ()).add(0, 1.5, 0), 2, 1, 5),
						(short) 5);

				e.getWorld().dropItem(e.getBlock().getLocation().add(+.5, +1, +.5), this.getRandomItem())
						.setVelocity(new Vector(0, 0, 0));
			});
		}, 0, 120 * 20);
	}

}
