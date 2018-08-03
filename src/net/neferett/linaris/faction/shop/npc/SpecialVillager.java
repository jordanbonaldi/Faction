package net.neferett.linaris.faction.shop.npc;

import net.minecraft.server.v1_8_R3.EntityVillager;

public class SpecialVillager extends EntityVillager {

	public SpecialVillager(final net.minecraft.server.v1_8_R3.World world) {
		super(world);
	}

	@Override
	public void g(final double x, final double y, final double z) {}

	@Override
	public void t_() {
		super.t_();
		this.motX = 0;
		this.motY = 0;
		this.motZ = 0;
	}

}