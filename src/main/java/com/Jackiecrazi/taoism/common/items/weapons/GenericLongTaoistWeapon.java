/*package com.Jackiecrazi.taoism.common.items.weapons;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.client.ExtendThyReachHelper;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;

public abstract class GenericLongTaoistWeapon extends GenericTaoistWeapon implements ICustomRange{

	public GenericLongTaoistWeapon(ToolMaterial p_i45356_1_) {
		super(p_i45356_1_,"nope");
		// TODO Auto-generated constructor stub
	}
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
    {
		if(entityLiving instanceof EntityPlayer){
			float reach=this.getRange((EntityPlayer)entityLiving, stack);
			MovingObjectPosition mov = ExtendThyReachHelper
					.getMouseOver(0, reach);

			if (mov != null && mov.entityHit != null
					&& mov.entityHit != (EntityPlayer)entityLiving
					&& mov.entityHit.hurtResistantTime == 0) {
				Taoism.net.sendToServer(new PacketExtendThyReach(mov.entityHit.getEntityId(),true));
				return true;
			}
		}
		return false;
    }

}
*/