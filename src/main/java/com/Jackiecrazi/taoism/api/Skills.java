package com.Jackiecrazi.taoism.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class Skills {
	public int getYari(EntityPlayer p) {
		int x = 1;
		NBTTagCompound kakaka = p.getEntityData().getCompoundTag("yari");
		if (kakaka != null) {
			int s = kakaka.getInteger("yari");
			return s;
		} else {
			p.getEntityData().setInteger("yari", 1);
			return x;
		}
	}

	public static void writeit(EntityPlayer p, int f) {
		String ekkamai = p.getHeldItem().getUnlocalizedName();
		int khrungthep = p.getEntityData().getInteger(ekkamai);
		if (khrungthep < 1000) {
			p.getEntityData().setInteger(ekkamai, khrungthep + f);
		}
	}

	public static int readit(EntityPlayer p, String ekkamai) {
		ekkamai = p.getHeldItem().getUnlocalizedName();
		int khrungthep = p.getEntityData().getInteger(ekkamai);
		return khrungthep;
	}

	public int relation;
}
