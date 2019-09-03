package net.neferett.linaris.faction.shop.npc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EntityVillager;

/**
 * Classe permettant d'enregistrer par NMS toutes les entités modifiées
 * 
 * @author BlackPhantom
 *
 */
public enum CustomEntityType {

	VILLAGER("Villager", 120, EntityType.VILLAGER, EntityVillager.class, SpecialVillager.class);

	/*
	 * Since 1.7.2 added a check in their entity registration, simply bypass it
	 * and write to the maps ourself.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void a(final Class paramClass, final String paramString, final int paramInt) {
		try {
			((Map) getPrivateStatic(EntityTypes.class, "c")).put(paramString, paramClass);
			((Map) getPrivateStatic(EntityTypes.class, "d")).put(paramClass, paramString);
			((Map) getPrivateStatic(EntityTypes.class, "e")).put(Integer.valueOf(paramInt), paramClass);
			((Map) getPrivateStatic(EntityTypes.class, "f")).put(paramClass, Integer.valueOf(paramInt));
			((Map) getPrivateStatic(EntityTypes.class, "g")).put(paramString, Integer.valueOf(paramInt));
		} catch (final Exception exc) {
			// Unable to register the new class.
		}
	}

	/**
	 * A convenience method.
	 *
	 * @param clazz
	 *            The class.
	 * @param f
	 *            The string representation of the private static field.
	 * @return The object found
	 * @throws Exception
	 *             if unable to get the object.
	 */
	@SuppressWarnings("rawtypes")
	private static Object getPrivateStatic(final Class clazz, final String f) throws Exception {
		final Field field = clazz.getDeclaredField(f);
		field.setAccessible(true);
		return field.get(null);
	}

	/**
	 * Register our entities.
	 */
	public static void registerEntities() {
		for (final CustomEntityType entity : values()) /* Get our entities */
			a(entity.getCustomClass(), entity.getName(), entity.getID());
		/* Get all biomes on the server */
		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class, "biomes");
		} catch (final Exception exc) {
			return;
		}
		for (final BiomeBase biomeBase : biomes) {
			if (biomeBase == null)
				break;
			for (final String field : new String[] { "at", "au", "av", "aw" }) // Lists
																				// that
																				// hold
																				// all
																				// entity
																				// types
				try {
					final Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					final List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					for (final BiomeMeta meta : mobList)
						for (final CustomEntityType entity : values())
							if (entity.getNMSClass().equals(
									meta.b)) /*
												 * Test if the entity has the
												 * custom entity type
												 */
								meta.b = entity.getCustomClass(); // Set it's
																	// meta to
																	// our
																	// custom
																	// class's
																	// meta
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Unregister our entities to prevent memory leaks. Call on disable.
	 */
	@SuppressWarnings("rawtypes")
	public static void unregisterEntities() {
		for (final CustomEntityType entity : values()) {
			// Remove our class references.
			try {
				((Map) getPrivateStatic(EntityTypes.class, "d")).remove(entity.getCustomClass());
			} catch (final Exception e) {
				e.printStackTrace();
			}

			try {
				((Map) getPrivateStatic(EntityTypes.class, "f")).remove(entity.getCustomClass());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		for (final CustomEntityType entity : values())
			try {
				a(entity.getNMSClass(), entity.getName(), entity.getID());
			} catch (final Exception e) {
				e.printStackTrace();
			}

		BiomeBase[] biomes;
		try {
			biomes = (BiomeBase[]) getPrivateStatic(BiomeBase.class,
					"biomes"); /* Get all biomes again */
		} catch (final Exception exc) {
			return;
		}
		for (final BiomeBase biomeBase : biomes) {
			if (biomeBase == null)
				break;

			for (final String field : new String[] { "at", "au", "av",
					"aw" }) /* The entity list */
				try {
					final Field list = BiomeBase.class.getDeclaredField(field);
					list.setAccessible(true);
					@SuppressWarnings("unchecked")
					final List<BiomeMeta> mobList = (List<BiomeMeta>) list.get(biomeBase);

					for (final BiomeMeta meta : mobList)
						for (final CustomEntityType entity : values())
							if (entity.getCustomClass().equals(meta.b))
								meta.b = entity
										.getNMSClass(); /*
														 * Set the entities meta
														 * back to the NMS one
														 */
				} catch (final Exception e) {
					e.printStackTrace();
				}
		}
	}

	private Class<? extends EntityInsentient>	customClass;

	private EntityType							entityType;

	private int									id;

	private String								name;

	private Class<? extends EntityInsentient>	nmsClass;

	private CustomEntityType(final String name, final int id, final EntityType entityType,
			final Class<? extends EntityInsentient> nmsClass, final Class<? extends EntityInsentient> customClass) {
		this.name = name;
		this.id = id;
		this.entityType = entityType;
		this.nmsClass = nmsClass;
		this.customClass = customClass;
	}

	public Class<? extends EntityInsentient> getCustomClass() {
		return this.customClass;
	}

	public EntityType getEntityType() {
		return this.entityType;
	}

	public int getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Class<? extends EntityInsentient> getNMSClass() {
		return this.nmsClass;
	}
}