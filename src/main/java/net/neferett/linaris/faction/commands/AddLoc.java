package net.neferett.linaris.faction.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.Main;
import net.neferett.linaris.faction.shop.npc.NPC.VillagerType;

public class AddLoc extends CommandHandler {

	private File				file;

	private YamlConfiguration	filec;

	public AddLoc() {
		super("addloc", p -> p.getRank().getModerationLevel() > 1);
		this.setErrorMsg("§cTu dois être §6Modérateur§c !");
	}

	public void addLoc(final Location l) {
		final List<String> list = this.filec.getStringList("Locs");
		list.add(l.getX() + ":" + l.getBlockY() + ":" + l.getBlockZ());
		this.filec.set("Locs", list);
		try {
			this.filec.save(this.file);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {

		this.file = new File("plugins/Faction/shop/" + VillagerType.DROPLOC.getName());
		this.filec = YamlConfiguration.loadConfiguration(this.file);
		try {
			this.filec.save(this.file);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		this.addLoc(arg0.getLocation());
		Main.getInstanceMain().getSp().loadLocation();

		arg0.sendMessage("§aLocation ajouté!");
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
