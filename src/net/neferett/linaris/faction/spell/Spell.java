package net.neferett.linaris.faction.spell;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.neferett.linaris.faction.effects.SpellEffects;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.utils.RepairableItems;
import net.neferett.linaris.utils.BlockUtils;
import net.neferett.linaris.utils.Particles;
import net.neferett.linaris.utils.UtilParticles;

public enum Spell {

	Blindness("Aveuglement", new SpellEffects("§dAveuglement", Particles.SPELL_WITCH, 0, 0, 0, true) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 5));
				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 120), Fall("Jump", new SpellEffects("§bJump", Particles.REDSTONE, 255, 0, 0, false) {

		@Override
		public Actions onActions() {
			return new Actions() {
				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					final M_Player pp = PlayerManagers.get().getPlayer(e.getPlayer());
					pp.setSpellTimer(e.getPlayer(), "Fall");
					RunScheduledByTicks(15,
							() -> UtilParticles.drawParticleLine(e.getPlayer().getLocation().add(0, 255, 0),
									e.getPlayer().getLocation().add(0, 2, 0), Particles.EXPLOSION_NORMAL, 100, 0, 255,
									255),
							(short) 1);
					RunScheduledByTicks(1, () -> {
						e.getPlayer().setVelocity(e.getPlayer().getLocation()
								.add(0, e.getPlayer().getLocation().getBlockY() + 1, 0).getDirection().multiply(2));
					}, (short) 10);
				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 60 * 15), FireBall("Fireball", new SpellEffects("§5FireBall", Particles.VILLAGER_ANGRY, 0, 0, 0, true) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1 * 20, 1));
					p.setSpellTimer(e.getPlayer(), "FireBall");

					final Fireball f = e.getPlayer().launchProjectile(Fireball.class);
					f.setVelocity(f.getVelocity().multiply(50));

					p.setFireTicks(20);

					p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),
							2, false, false);

				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 180), Jail("Prison", new SpellEffects("§5Prison", Particles.SMOKE_LARGE, 0, 0, 0, true) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),
							1, false, false);

					getArroundBlocs(p)
							.forEach(b -> BlockUtils.temporaryChangeBlock(b, Material.BARRIER, (byte) 0, 10 * 20));

					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 * 20, 10));

				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 120), Mur("Mur", new SpellEffects("§dMur", Particles.SPELL_WITCH, 0, 0, 0, false) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					getWall(e.getPlayer())
							.forEach(b -> BlockUtils.temporaryChangeBlock(b, Material.BRICK, (byte) 0, 5 * 20));

				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 120), Reach("Teleport", new SpellEffects("§9Téléportation", Particles.SPELL, 0, 0, 0, true) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {

					RunScheduledByTicks(15,
							() -> UtilParticles.drawParticleLine(p.getLocation().add(0, 255, 0),
									p.getLocation().add(0, 2, 0), Particles.EXPLOSION_NORMAL, 100, 0, 255, 255),
							(short) 1);

					final Location l = p.getLocation();

					l.add(1, 0, 0);
					l.setYaw(e.getPlayer().getLocation().getYaw());
					l.setPitch(e.getPlayer().getLocation().getPitch());

					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 * 20, 10));

					e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 * 20, 10));

					e.getPlayer().playSound(p.getLocation(), Sound.PORTAL_TRAVEL, 100, 100);
					e.getPlayer().teleport(l);

				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 3600 * 6), Reparo("Reparo", new SpellEffects("§bReparo", Particles.HEART, 255, 0, 0, false) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {

					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.LEVEL_UP, 100, 100);

					Arrays.asList(e.getPlayer().getInventory().getContents()).forEach(a -> {
						if (a != null && a.getType() != null && RepairableItems.tools.contains(a.getType())) {
							if (a.getType().equals(Material.DIAMOND_AXE) && a.hasItemMeta()
									&& a.getItemMeta().getDisplayName() != null
									&& a.getItemMeta().getDisplayName().contains("magique"))
								return;
							a.setDurability((short) 0);
						}
					});
					Arrays.asList(e.getPlayer().getInventory().getArmorContents()).forEach(a -> {
						if (a != null && a.getType() != null && RepairableItems.tools.contains(a.getType()))
							a.setDurability((short) 0);
					});

					RunScheduledByTicks(5, () -> UtilParticles.generateParticleArroundPlayer(Particles.HEART,
							e.getPlayer().getLocation().add(0, 0.5, 0), 2, 1, 10), (short) 5);

					RunScheduledByTicks(5, () -> UtilParticles.generateParticleArroundPlayer(Particles.HEART,
							e.getPlayer().getLocation().add(0, 1, 0), 2, 1, 10), (short) 5);

					RunScheduledByTicks(5, () -> UtilParticles.generateParticleArroundPlayer(Particles.HEART,
							e.getPlayer().getLocation().add(0, 1.5, 0), 2, 1, 10), (short) 5);
				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 172800), SlowNess("Ralentissement", new SpellEffects("§9Ralentissement", Particles.REDSTONE, 0, 255, 255, true) {

		@Override
		public Actions onActions() {
			return new Actions() {

				@Override
				public void onClick(final PlayerInteractEvent e, final M_Player p) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 4));
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 10 * 20, 4));
				}

				@Override
				public boolean onDamage(final EntityDamageByEntityEvent e) {
					e.getDamager().sendMessage("§cTu ne peux pas faire de dégats avec cet item !");
					return true;
				}
			};
		}
	}, 120);

	public static Spell getByName(final String name) {
		return Arrays.asList(values()).stream().filter(n -> n.getName().equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	public static Spell next(final Spell current) {
		int i = 0;

		for (final Spell s : values()) {
			if (s.equals(current))
				return s.equals(values()[values().length - 1]) ? values()[0] : values()[i + 1];
			i++;
		}
		return null;
	}

	SpellEffects	e;
	String			name;
	int				time;

	Spell(final String name, final SpellEffects e, final int time) {
		this.e = e;
		this.name = name;
		this.time = time;
	}

	public SpellEffects getE() {
		return this.e;
	}

	public String getName() {
		return this.name;
	}

	public int getTime() {
		return this.time;
	}
}
