package net.neferett.linaris.faction.commands;

import java.util.List;

import net.neferett.linaris.PlayersHandler.Players;
import net.neferett.linaris.commands.CommandHandler;
import net.neferett.linaris.faction.classes.ClassesGUI;
import net.neferett.linaris.faction.events.players.PlayerManagers;
import net.neferett.linaris.utils.gui.GuiManager;

public class ClassesCommand extends CommandHandler {

	public ClassesCommand() {
		super("classes", p -> p != null, "class", "classe", "clazz", "cl", "c");
		this.setErrorMsg("§cUne erreur c'est produite !");
	}

	@Override
	public void cmd(final Players arg0, final String arg1, final List<String> arg2) {
		GuiManager.openGui(new ClassesGUI(PlayerManagers.get().getPlayer(arg0.getPlayer())));
	}

	@Override
	public void onError(final Players arg0) {
		arg0.DisplayErrorMessage();
	}

}
