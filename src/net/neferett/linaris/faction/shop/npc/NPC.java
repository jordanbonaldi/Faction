package net.neferett.linaris.faction.shop.npc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.line.TextLine;

import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.World;
import net.neferett.linaris.faction.Main;

public enum NPC {

	Armes("Armes", VillagerType.ARMES, Profession.LIBRARIAN, "world", 1250.486, 81, -254.505),
	Classes("Rangs", VillagerType.Classes, Profession.BLACKSMITH, "world", 1243.513, 83, -304.516),
	Divers("Divers", VillagerType.Divers, Profession.PRIEST, "world", 1252.496, 81, -271.416),
	Mineur("Mineur", VillagerType.Miner, Profession.BLACKSMITH, "world", 1215.615, 81, -264.381),
	Nourriture("Nourriture", VillagerType.FOOD, Profession.BLACKSMITH, "world", 1235.615, 81, -280.381),
	Potion("Potions", VillagerType.Potion, Profession.FARMER, "world", 1227.496, 81, -250.306),
	RankCap("Capitaine", VillagerType.Ranks, Profession.FARMER, "world", 1187.496, 83, -284.507),
	RankCol("Colonel", VillagerType.Ranks, Profession.FARMER, "world", 1180.496, 83, -317.507),
	RankCom("Commandant", VillagerType.Ranks, Profession.FARMER, "world", 1181.5, 83, -290.507),
	RankG("Général", VillagerType.Ranks, Profession.FARMER, "world", 1186.496, 83, -323.507),
	RankLt("Lieutenant", VillagerType.Ranks, Profession.FARMER, "world", 1214.496, 83, -285.507),
	RankMar("Maréchal", VillagerType.Ranks, Profession.FARMER, "world", 1213.496, 83, -324.507),
	RankMj("Major", VillagerType.Ranks, Profession.FARMER, "world", 1220.496, 83, -291.507);

	public enum VillagerType {
		ARMES("Armes"),
		Classes("Rangs"),
		Divers("Divers"),
		DROP("DropItems"),
		DROPLOC("DropLocs"),
		FOOD("Food"),
		Miner("Miner"),
		Potion("Potions"),
		Ranks("Grades");

		private String name;

		private VillagerType(final String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}

	private Villager		entity;
	private Hologram		h;
	private String			name;
	private Profession		p;
	private VillagerType	type;
	private String			worldName;

	private double			x, y, z;

	private NPC(final String name, final VillagerType type, final Profession p, final String worldName, final double x,
			final double y, final double z) {
		this.type = type;
		this.name = name;
		this.worldName = worldName;
		this.p = p;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void dispawn() {
		this.entity.remove();
		this.h.despawn();
	}

	public Villager getEntity() {
		return this.entity;
	}

	public String getName() {
		return this.name;
	}

	public VillagerType getType() {
		return this.type;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}

	@SuppressWarnings("deprecation")
	public void spawn() {
		Bukkit.getServer().getWorld(this.worldName).getChunkAt(
				new Location(Bukkit.getServer().getWorld(this.worldName), this.getX(), this.getY(), this.getZ()))
				.load(true);
		final World w = ((CraftWorld) Bukkit.getServer().getWorld(this.worldName)).getHandle();
		final EntityVillager v = new SpecialVillager(w);

		v.setPosition(this.getX(), this.getY(), this.getZ());
		v.setProfession(this.p.getId());

		w.addEntity(v);

		this.entity = (Villager) v.getBukkitEntity();
		this.entity.setRemoveWhenFarAway(false);

		this.h = new Hologram(this.entity.toString() + this.entity.getLocation(),
				new Location(Bukkit.getWorld(this.getWorldName()), this.getX(), 2.2 + this.getY(), this.getZ()));
		Main.getInstanceMain().getHologramManager().addActiveHologram(this.h);
		if (this.name.contains("Rangs"))
			this.h.addLine(new TextLine(this.h, "§b§l" + this.name));
		else if (this.type.equals(VillagerType.Ranks))
			this.h.addLine(new TextLine(this.h, "§e§lGrade §b§l" + this.name));
		else
			this.h.addLine(new TextLine(this.h, "§e§lSHOP §b§l" + this.name()));
	}

}
