package net.neferett.linaris.faction.classes;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.neferett.linaris.faction.Main;

public class ClassesManager {

	public static ClassesManager get() {
		return Main.getInstanceMain().getCm();
	}

	List<Classes>		classes	= new ArrayList<>();
	private final File	f;

	public ClassesManager() {
		this.f = new File("plugins/Faction/class/");
		Arrays.asList(Objects.requireNonNull(this.f.listFiles())).forEach(file -> {
			if (file.getName().endsWith(".class"))
				this.addClass(new Classes(file.getName().substring(0, file.getName().length() - 6), file).buildClass());
		});
	}

	public void addClass(final Classes c) {
		this.classes.add(c);
	}

	public Classes getClassByName(final String name) {
		return this.classes.stream().filter(n -> n.getClassname().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public List<Classes> getClasses() {
		return this.classes;
	}

}
