package net.neferett.linaris.faction.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.neferett.linaris.faction.handlers.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.events.players.M_Player;
import net.neferett.linaris.faction.events.players.PlayerManagers;

public class SpawnerCommand extends CommandHandler {

	private final List<EntityType> types = new ArrayList<>();

	public SpawnerCommand() {
		super("mob", (p) -> {
			final M_Player pa = PlayerManagers.get().getPlayer(Bukkit.getPlayer(p.getPlayername()));
			return pa.getCmds().contains("mob");
		});
		this.setErrorMsg("§cVous n'avez les privileges pour faire cela.");
		this.types.add(EntityType.COW);
		this.types.add(EntityType.PIG);
		this.types.add(EntityType.SHEEP);
		this.types.add(EntityType.ZOMBIE);
		this.types.add(EntityType.PIG_ZOMBIE);
		this.types.add(EntityType.SKELETON);
		this.types.add(EntityType.SPIDER);
		this.types.add(EntityType.CHICKEN);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {

		if (arg2.size() != 2) {
			arg0.sendMessage("§cUtilisation: /mob <type>");
			return;
		}

		final EntityType type = EntityType.fromName(arg2.get(1));

		if (type == null || !type.isSpawnable() || !type.isAlive() || !this.types.contains(type)) {
			arg0.sendMessage("§cType inconnu !");
			arg0.sendMessage("§7Types possibles§f: ");
			final StringBuilder b = new StringBuilder();

			this.types.forEach(e -> {
				b.append(e.getName() + " ");
			});
			arg0.sendMessage("§c" + b.toString());
			return;
		}

		final Player p = Bukkit.getPlayer(arg0.getName());

		final Block b = p.getTargetBlock((HashSet<Byte>) null, 10);

		System.out.println(b);

		if (b == null) {
			arg0.sendMessage("§cBloc trop loin !");
			return;
		} else if (!b.getType().equals(Material.MOB_SPAWNER)) {
			arg0.sendMessage("§cLe bloc n'est pas un spawner !");
			return;
		}
		final BlockState state = b.getState();

		final CreatureSpawner spawner = (CreatureSpawner) state;
		spawner.setSpawnedType(type);

		arg0.sendMessage("§aSpawner changé en §f: §e" + type.getName());

	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
