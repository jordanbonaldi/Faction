package net.neferett.linaris.faction.effects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.faction.spell.Spell;
import net.neferett.linaris.utils.Particles;
import net.neferett.linaris.utils.PlayerUtils;
import net.neferett.linaris.utils.TimeUtils;
import net.neferett.linaris.utils.UtilParticles;
import net.neferett.linaris.utils.tasksmanager.TaskManager;

public abstract class SpellEffects extends Effects {

	String		name;
	Particles	p;
	int			r, g, b;
	boolean		targetable;

	public SpellEffects(final String name, final Particles p, final int r, final int g, final int b,
			final boolean targetable) {
		super("SpellEffects");
		this.name = name;
		this.r = r;
		this.g = g;
		this.b = b;
		this.p = p;
		this.targetable = targetable;
	}

	@Override
	public void buildTargetTask(final M_Player p, final ItemStack i) {
		TaskManager.scheduleSyncRepeatingTask("held-" + p.getName().toLowerCase(), () -> {
			if (!p.isSpellExecutable() && !p.isOp())
				PlayerUtils.sendActionMessage(p.getPlayer(),
						"§7Sort§f: " + p.getCurrent().getE().getName() + "  §f-  §7Rechargement§f: "
								+ TimeUtils.getTimeLeftToString(p.getTime(), p.getCurrent().getTime()));
			else if (p.getCurrent().getE().isTargetable()) {
				if (Main.getInstanceMain().getCb().isInside(p.getLocation())
						&& (!ConfigReader.getInstance().isWarzoneInSpawn() || !Main.getInstanceMain().getCb2().isInside(p.getLocation())))
					PlayerUtils.sendActionMessage(p.getPlayer(), "§cSorts inutilisablent dans cette zone !"
							+ "  §f-  §7Sort§f: " + p.getCurrent().getE().getName());
				else {
					final Player pa = p.getCurrent().getE().getEntityNear(p.getPlayer(), 100, 10, 30);
					if (pa != null && !pa.equals(p) && Main.getInstanceMain().getCb().isInside(pa.getLocation())
							&& !Main.getInstanceMain().getCb2().isInside(pa.getLocation())) {
						PlayerUtils.sendActionMessage(p.getPlayer(),
								"§e" + pa.getName() + "§f: §cCe joueur est dans une zone protégé !"
										+ "  §f-  §7Sort§f: " + p.getCurrent().getE().getName());
						return;
					} else if (pa != null && !pa.equals(p.getPlayer()) && !p.getCurrent().equals(Spell.Reach)
							&& this.checkFactionAction(p.getPlayer(), pa.getPlayer()))
						return;
					else
						PlayerUtils
								.sendActionMessage(p.getPlayer(),
										"§7Joueur visé§f: §e"
												+ (pa == null ? "§cAucun (Trop loin)"
														: pa.equals(p.getPlayer()) ? "§cAucun (Trop proche)"
																: pa.getName())
												+ "  §f-  §7Sort§f: " + p.getCurrent().getE().getName());
				}
			} else
				PlayerUtils.sendActionMessage(p.getPlayer(), "§7Sort§f: " + p.getCurrent().getE().getName());
		}, 0, 1 * 20);
	}

	@Override
	public void ClickEvent(final PlayerInteractEvent e) {
		final M_Player p = PlayerManagers.get().getPlayer(e.getPlayer());
		if (this.name == null) {
			e.getPlayer().sendMessage("§cVous n'avez débloqué aucun sort !");
			e.setCancelled(true);
		} else if (!p.isSpellExecutable() && !e.getPlayer().isOp())
			p.sendMessage(
					"§7Tu dois attendre encore " + TimeUtils.getTimeLeftToString(p.getTime(), p.getCurrent().getTime())
							+ " §7pour pouvoir utilisé ce sort !");
		else {
			if (this.isTargetable()) {
				final Player near = this.getEntityNear(e.getPlayer(), 100, 3, 30);

				if (near != null && !near.equals(e.getPlayer())) {

					if (Main.getInstanceMain().getCb().isInside(near.getLocation())
							&& !Main.getInstanceMain().getCb2().isInside(near.getLocation())) {
						p.sendMessage("§cCe joueur est dans une zone protégé !");
						e.setCancelled(true);
						return;
					}
					if (!p.getCurrent().equals(Spell.Reach) && this.checkFaction(e.getPlayer(), near)) {
						e.setCancelled(true);
						return;
					}
					p.setTime();
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.GHAST_FIREBALL, 100, 100);
					UtilParticles.drawParticleLine(e.getPlayer().getLocation().add(0, 1.5, 0),
							near.getLocation().add(0, 1.5, 0), this.p, 100, this.r, this.g, this.b);
					this.onActions().onClick(e, PlayerManagers.get().getPlayer(near));
					near.sendMessage("§e" + e.getPlayer().getName() + "§7 vient d'utiliser le sort " + this.getName()
							+ "§7 sur vous !");
					near.playSound(near.getLocation(), Sound.WITHER_DEATH, 100, 100);

				} else if (near != null && near.equals(e.getPlayer()))
					e.getPlayer().sendMessage(
							"§cLe joueur doit etre a plus de 3 blocs de toi sinon le sort risque de se retourner contre toi !");
				else
					e.getPlayer().sendMessage("§cAucun joueur visé !");
			} else {
				p.setTime();
				e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.GHAST_FIREBALL, 100, 100);
				this.onActions().onClick(e, null);
			}
			e.setCancelled(true);
		}
	}

	@Override
	public void DamageEvent(final EntityDamageByEntityEvent e) {
		if (this.name == null) {
			e.getDamager().sendMessage("§cVous n'avez débloqué aucun sort !");
			e.setCancelled(true);
		}

		e.setCancelled(this.onActions().onDamage(e));
	}

	public List<Block> getArroundBlocs(final M_Player p) {
		return new ArrayList<>(Arrays.asList(new Block[] { p.getLocation().add(1, 0, 0).getBlock(),
				p.getLocation().add(1, 1, 0).getBlock(), p.getLocation().add(0, 0, 1).getBlock(),
				p.getLocation().add(0, 1, 1).getBlock(), p.getLocation().add(0, 2, 0).getBlock(),
				p.getLocation().subtract(1, 0, 0).getBlock(), p.getLocation().subtract(1, -1, 0).getBlock(),
				p.getLocation().subtract(0, 0, 1).getBlock(), p.getLocation().subtract(0, -1, 1).getBlock() }));
	}

	public List<Block> getArroundSquaredBlocs(final M_Player p) {
		return new ArrayList<>(Arrays
				.asList(new Block[] { p.getLocation().add(2, 0, 0).getBlock(), p.getLocation().add(2, 1, 0).getBlock(),
						p.getLocation().add(2, 2, 0).getBlock(), p.getLocation().add(2, -1, 0).getBlock(),
						p.getLocation().add(2, 0, 1).getBlock(), p.getLocation().add(2, 1, 1).getBlock(),
						p.getLocation().add(2, 2, 1).getBlock(), p.getLocation().add(2, -1, 1).getBlock(),
						p.getLocation().add(2, 0, -1).getBlock(), p.getLocation().add(2, -1, -1).getBlock(),
						p.getLocation().add(2, 1, -1).getBlock(), p.getLocation().add(2, 2, -1).getBlock(),
						p.getLocation().add(2, 0, -2).getBlock(), p.getLocation().add(2, 1, -2).getBlock(),
						p.getLocation().add(2, 2, -2).getBlock(), p.getLocation().add(2, -1, -2).getBlock(),
						p.getLocation().add(2, 0, 2).getBlock(), p.getLocation().add(2, 1, 2).getBlock(),
						p.getLocation().add(2, 2, 2).getBlock(), p.getLocation().add(2, -1, 2).getBlock(),
						p.getLocation().add(0, 0, 2).getBlock(), p.getLocation().add(0, 1, 2).getBlock(),
						p.getLocation().add(0, 2, 2).getBlock(), p.getLocation().add(0, -1, 2).getBlock(),
						p.getLocation().add(1, 0, 2).getBlock(), p.getLocation().add(1, 1, 2).getBlock(),
						p.getLocation().add(1, 2, 2).getBlock(), p.getLocation().add(1, -1, 2).getBlock(),
						p.getLocation().add(-1, 0, 2).getBlock(), p.getLocation().add(-1, 1, 2).getBlock(),
						p.getLocation().add(-1, 2, 2).getBlock(), p.getLocation().add(-1, -1, 2).getBlock(),
						p.getLocation().add(-2, 0, 2).getBlock(), p.getLocation().add(-2, 1, 2).getBlock(),
						p.getLocation().add(-2, 2, 2).getBlock(), p.getLocation().add(-2, -1, 2).getBlock(),
						p.getLocation().add(-2, 0, 0).getBlock(), p.getLocation().add(-2, 1, 0).getBlock(),
						p.getLocation().add(-2, 2, 0).getBlock(), p.getLocation().add(-2, -1, 0).getBlock(),
						p.getLocation().add(-2, 0, -1).getBlock(), p.getLocation().add(-2, 1, -1).getBlock(),
						p.getLocation().add(-2, 2, -1).getBlock(), p.getLocation().add(-2, -1, -1).getBlock(),
						p.getLocation().add(-2, 0, 1).getBlock(), p.getLocation().add(-2, 1, 1).getBlock(),
						p.getLocation().add(-2, 2, 1).getBlock(), p.getLocation().add(-2, -1, 1).getBlock(),
						p.getLocation().add(-2, 0, -2).getBlock(), p.getLocation().add(-2, 1, -2).getBlock(),
						p.getLocation().add(-2, 2, -2).getBlock(), p.getLocation().add(-2, -1, -2).getBlock(),
						p.getLocation().add(-1, 0, -2).getBlock(), p.getLocation().add(-1, 1, -2).getBlock(),
						p.getLocation().add(-1, 2, -2).getBlock(), p.getLocation().add(-1, -1, -2).getBlock(),
						p.getLocation().add(0, 0, -2).getBlock(), p.getLocation().add(0, 1, -2).getBlock(),
						p.getLocation().add(0, 2, -2).getBlock(), p.getLocation().add(0, -1, -2).getBlock(),
						p.getLocation().add(1, 0, -2).getBlock(), p.getLocation().add(1, 1, -2).getBlock(),
						p.getLocation().add(1, 2, -2).getBlock(), p.getLocation().add(1, -1, -2).getBlock(),
						p.getLocation().add(0, 2, 0).getBlock(), p.getLocation().add(1, 2, 0).getBlock(),
						p.getLocation().add(-1, 2, 0).getBlock(), p.getLocation().add(1, 2, 1).getBlock(),
						p.getLocation().add(-1, 2, -1).getBlock(), p.getLocation().add(-1, 2, 1).getBlock(),
						p.getLocation().add(1, 2, -1).getBlock(), p.getLocation().add(0, 2, -1).getBlock(),
						p.getLocation().add(0, 2, 1).getBlock(), p.getLocation().add(0, -1, 0).getBlock(),
						p.getLocation().add(1, -1, 0).getBlock(), p.getLocation().add(-1, -1, 0).getBlock(),
						p.getLocation().add(1, -1, 1).getBlock(), p.getLocation().add(-1, -1, -1).getBlock(),
						p.getLocation().add(-1, -1, 1).getBlock(), p.getLocation().add(1, -1, -1).getBlock(),
						p.getLocation().add(0, -1, -1).getBlock(), p.getLocation().add(0, -1, 1).getBlock() }));
	}

	public String getCardinalDirection(final Player player) {
		double rotation = (player.getLocation().getYaw() - 90.0F) % 360.0F;
		if (rotation < 0.0D)
			rotation += 360.0D;
		if (0.0D <= rotation && rotation < 22.5D)
			return "N";
		if (22.5D <= rotation && rotation < 67.5D)
			return "NE";
		if (67.5D <= rotation && rotation < 112.5D)
			return "E";
		if (112.5D <= rotation && rotation < 157.5D)
			return "SE";
		if (157.5D <= rotation && rotation < 202.5D)
			return "S";
		if (202.5D <= rotation && rotation < 247.5D)
			return "SW";
		if (247.5D <= rotation && rotation < 292.5D)
			return "W";
		if (292.5D <= rotation && rotation < 337.5D)
			return "NW";
		if (337.5D <= rotation && rotation < 360.0D)
			return "N";
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public List<Block> getWall(final Player p) {
		if (!(this.getCardinalDirection(p).equals("E") || this.getCardinalDirection(p).equals("W")
				|| this.getCardinalDirection(p).equals("SW") || this.getCardinalDirection(p).equals("NE")))
			return new ArrayList<>(Arrays.asList(new Block[] {
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 0, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 1, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 2, 0).getBlock(),

					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 0, 1).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 1, 1).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 2, 1).getBlock(),

					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 0, -1).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 1, -1).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 2, -1).getBlock(), }));
		else
			return new ArrayList<>(Arrays.asList(new Block[] {
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 0, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 1, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(0, 2, 0).getBlock(),

					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(1, 0, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(1, 1, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(1, 2, 0).getBlock(),

					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(-1, 0, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(-1, 1, 0).getBlock(),
					p.getEyeLocation().add(p.getLocation().getDirection().multiply(4)).add(-1, 2, 0).getBlock(), }));
	}

	public boolean isTargetable() {
		return this.targetable;
	}

}
