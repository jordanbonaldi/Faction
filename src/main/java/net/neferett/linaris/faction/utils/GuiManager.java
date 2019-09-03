package net.neferett.linaris.faction.utils;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class GuiManager {

	@SuppressWarnings("rawtypes")
	static HashMap<String, Class> openGuis = new HashMap<>();

	public static void closePlayer(final Player p) {
		if (openGuis.containsKey(p.getName()))
			openGuis.remove(p.getName());
	}

	@SuppressWarnings("rawtypes")
	public static boolean isOpened(final Class clas) {
		for (final Class cla : openGuis.values())
			if (cla.equals(clas))
				return true;
		return false;
	}

	public static boolean isPlayer(final Player p) {
		if (openGuis.containsKey(p.getName()))
			return true;
		return false;
	}

	public static GuiScreen openGui(final GuiScreen gui) {

		openPlayer(gui.getPlayer(), gui.getClass());
		gui.open();
		return gui;
	}

	@SuppressWarnings("rawtypes")
	public static void openPlayer(final Player p, final Class gui) {
		if (openGuis.containsKey(p.getName())) {
			openGuis.remove(p.getName());
			openGuis.put(p.getName(), gui);
		} else
			openGuis.put(p.getName(), gui);
	}
}
