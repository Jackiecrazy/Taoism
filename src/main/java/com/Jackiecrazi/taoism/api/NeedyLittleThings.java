package com.jackiecrazi.taoism.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.oredict.OreDictionary;

import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;

public class NeedyLittleThings {
	public final static UUID MODIFIER_UUID = UUID.fromString("294093da-54f0-4c1b-9dbb-13b77534a84c");


	public static void taoWeaponAttack(Entity targetEntity, EntityPlayer player, EnumHand hand) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) return;
        if (targetEntity.canBeAttackedWithItem())
        {
            if (!targetEntity.hitByEntity(player))
            {
            	
            	ItemStack is = player.getHeldItem(hand);
            	ICustomRange tw=is.getItem() instanceof ICustomRange?(ICustomRange)is.getItem():null;
            	
                float attack = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                float extraAttack;

                if (targetEntity instanceof EntityLivingBase)
                {
                    extraAttack = EnchantmentHelper.getModifierForCreature(is, ((EntityLivingBase)targetEntity).getCreatureAttribute());
                }
                else
                {
                    extraAttack = EnchantmentHelper.getModifierForCreature(is, EnumCreatureAttribute.UNDEFINED);
                }

                float spamDebuff = player.getCooledAttackStrength(0.5F);
                attack = attack * (0.2F + spamDebuff * spamDebuff * 0.8F);
                extraAttack = extraAttack * spamDebuff;
                player.resetCooldown();

                if (attack > 0.0F || extraAttack > 0.0F)
                {
                    boolean charged = spamDebuff > 0.9F;
                    boolean sprinting = false;
                    int knockback = 0;
                    knockback = knockback + EnchantmentHelper.getKnockbackModifier(player);

                    if (player.isSprinting() && charged)
                    {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        ++knockback;
                        sprinting = true;
                    }

                    boolean crit = charged && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
                    crit = crit && !player.isSprinting();

                    net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, targetEntity, crit, crit ? 1.5F : 1.0F);
                    crit = hitResult != null;
                    if (crit)
                    {
                        attack *= hitResult.getDamageModifier();
                    }

                    attack = attack + extraAttack;
                    boolean shouldSweep = false;
                    double dist = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);

                    if (charged && !crit && !sprinting && player.onGround && dist < (double)player.getAIMoveSpeed())
                    {
                        ItemStack itemstack = player.getHeldItem(hand);

                        if (itemstack.getItem() instanceof ItemSword)
                        {
                            shouldSweep = true;
                        }
                    }

                    float health = 0.0F;
                    boolean burnt = false;
                    int fireenchantlvl = EnchantmentHelper.getFireAspectModifier(player);

                    if (targetEntity instanceof EntityLivingBase)
                    {
                        health = ((EntityLivingBase)targetEntity).getHealth();

                        if (fireenchantlvl > 0 && !targetEntity.isBurning())
                        {
                            burnt = true;
                            targetEntity.setFire(1);
                        }
                    }

                    double motionx = targetEntity.motionX;
                    double motiony = targetEntity.motionY;
                    double motionz = targetEntity.motionZ;
                    boolean canattack = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(player), attack);

                    if (canattack)
                    {
                        if (knockback > 0)
                        {
                            if (targetEntity instanceof EntityLivingBase)
                            {
                                ((EntityLivingBase)targetEntity).knockBack(player, (float)knockback * 0.5F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                            }
                            else
                            {
                                targetEntity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * (float)knockback * 0.5F));
                            }

                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (shouldSweep)
                        {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * attack;
                            double stretch= tw==null?1d:tw.getHorizontalRange(player, is)+1;
                            for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(stretch, 0.25D, stretch)))
                            {
                                if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSq(entitylivingbase) < 9.0D)
                                {
                                    entitylivingbase.knockBack(player, 0.4F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                    entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
                                }
                            }

                            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                            player.spawnSweepParticles();
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged)
                        {
                            ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = motionx;
                            targetEntity.motionY = motiony;
                            targetEntity.motionZ = motionz;
                        }

                        if (crit)
                        {
                            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                            player.onCriticalHit(targetEntity);
                        }

                        if (!crit && !shouldSweep)
                        {
                            if (charged)
                            {
                                player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                            else
                            {
                                player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (extraAttack > 0.0F)
                        {
                            player.onEnchantmentCritical(targetEntity);
                        }

                        player.setLastAttackedEntity(targetEntity);

                        if (targetEntity instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        ItemStack itemstack1 = is;
                        Entity entity = targetEntity;

                        if (targetEntity instanceof MultiPartEntityPart)
                        {
                            IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;

                            if (ientitymultipart instanceof EntityLivingBase)
                            {
                                entity = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase)
                        {
                            ItemStack beforeHitCopy = itemstack1.copy();
                            itemstack1.hitEntity((EntityLivingBase)entity, player);

                            if (itemstack1.isEmpty())
                            {
                                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, hand);
                                player.setHeldItem(hand, ItemStack.EMPTY);
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase)
                        {
                            float f5 = health - ((EntityLivingBase)targetEntity).getHealth();
                            player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));

                            if (fireenchantlvl > 0)
                            {
                                targetEntity.setFire(fireenchantlvl * 4);
                            }

                            if (player.world instanceof WorldServer && f5 > 2.0F)
                            {
                                int k = (int)((double)f5 * 0.5D);
                                ((WorldServer)player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                            }
                        }

                        player.addExhaustion(0.1F);
                    }
                    else
                    {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                        if (burnt)
                        {
                            targetEntity.extinguish();
                        }
                    }
                }
            }
        }
    }
	
	public static int avg(int orig, int[] additions) {
		for (int a : additions)
			orig += a;
		return (int) (orig / additions.length);
	}

	public static TaoistPosition[] bresenham(double x1, double y1, double z1, double x2, double y2, double z2) {

		double p_x = x1;
		double p_y = y1;
		double p_z = z1;
		double d_x = x2 - x1;
		double d_y = y2 - y1;
		double d_z = z2 - z1;
		int N = (int) Math.ceil(Math.max(Math.abs(d_x), Math.max(Math.abs(d_y), Math.abs(d_z))));
		double s_x = d_x / N;
		double s_y = d_y / N;
		double s_z = d_z / N;
		//System.out.println(N);
		TaoistPosition[] out = new TaoistPosition[N];
		if (out.length == 0) {
			//System.out.println("nay!");
			return out;
		}
		out[0] = new TaoistPosition((int) p_x, (int) p_y, (int) p_z);
		for (int ii = 1; ii < N; ii++) {
			p_x += s_x;
			p_y += s_y;
			p_z += s_z;
			out[ii] = new TaoistPosition((int) p_x, (int) p_y, (int) p_z);
		}
		return out;
	}

	public static int firstCap(String str) {        
	    for(int i=0; i<str.length(); i--) {
	        if(Character.isUpperCase(str.charAt(i))) {
	            return i;
	        }
	    }
	    return -1;
	}
	
	@Nullable
	public static String firstOredictName(ItemStack i){
		if(i==null)return null;
		try{
		return OreDictionary.getOreName(OreDictionary.getOreIDs(i)[0]);
		}catch(IndexOutOfBoundsException e){
			return "ingotIron";
		}
	}

	public static RayTraceResult getMouseOverWithoutClient(EntityLivingBase p, float dist)
	{
		RayTraceResult mop = null;
		if (p != null)
		{
			if (p.world != null)
			{
				Vec3d pos = new Vec3d(p.posX, p.posY+p.getEyeHeight(), p.posZ);
				Vec3d lookvec = p.getLookVec();
				Vec3d intpvec=p.getLook(dist);
				Vec3d vec32 = pos.addVector(intpvec.x * dist, intpvec.y * dist, intpvec.z * dist);
		        mop=p.world.rayTraceBlocks(pos, vec32, false, false, true);
				double distance=dist;
				double calcdist = distance;
				//distance = calcdist;
				if (mop != null)
				{
					calcdist = mop.hitVec.distanceTo(pos);
				}
				
				
				Vec3d var8 = pos.addVector(lookvec.x * distance, lookvec.y * distance, lookvec.z * distance);
				Entity pointedEntity = null;
				float var9 = 1.0F;
				List<Entity> list = p.world.getEntitiesWithinAABBExcludingEntity(p, p.getCollisionBoundingBox().offset(lookvec.x * distance, lookvec.y * distance, lookvec.z * distance).expand(var9, var9, var9));
				double d = calcdist;
				
				for (Entity entity : list)
				{
					if (entity.canBeCollidedWith())
					{
						float bordersize = entity.getCollisionBorderSize();
						AxisAlignedBB aabb = entity.getCollisionBoundingBox().expand(bordersize, bordersize, bordersize);
						RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
						
						if (aabb.contains(pos))
						{
							if (0.0D < d || d == 0.0D)
							{
								pointedEntity = entity;
								d = 0.0D;
							}
						} else if (mop0 != null)
						{
							double d1 = pos.distanceTo(mop0.hitVec);
							
							if (d1 < d || d == 0.0D)
							{
								pointedEntity = entity;
								d = d1;
							}
						}
					}
				}
				
				if (pointedEntity != null && (d < calcdist || mop == null))
				{
					mop = new RayTraceResult(pointedEntity);
				}
			}
		}
		return mop;
	}
	
	public static NBTTagCompound getTaoisticNBT(NBTTagCompound tc) {
		return (NBTTagCompound) (tc.getTag("Taoism") == null ? new NBTTagCompound() : tc.getTag("Taoism"));
	}
	
	public static boolean isUltimating(EntityPlayer player) {
		return player.world.getTotalWorldTime() - player.getEntityData().getLong("HissatsuTimeStart") < player.getEntityData().getInteger("HissatsuDuration");
	}

	public static boolean isWearingFullSet(EntityPlayer p, Item reference) {
		Item helm = null, chest = null, leg = null, boot = null;
		boolean ret = false;
		if (p.inventory.armorItemInSlot(0) != null) helm = p.inventory.armorItemInSlot(0).getItem();
		if (p.inventory.armorItemInSlot(1) != null) chest = p.inventory.armorItemInSlot(1).getItem();
		if (p.inventory.armorItemInSlot(2) != null) leg = p.inventory.armorItemInSlot(2).getItem();
		if (p.inventory.armorItemInSlot(3) != null) boot = p.inventory.armorItemInSlot(3).getItem();
		if (helm != null && chest != null && leg != null && boot != null) {
//			if (reference instanceof ClothingWushu) {
//				ret = helm == TaoItems.wushuRibbon && chest == TaoItems.wushuShirt && leg == TaoItems.wushuPants && boot == TaoItems.wushuShoes;
//			}
		}
		return ret;
	}

	public static Vec3d lookVector(Entity player) {
		float rotationYaw = player.rotationYaw, rotationPitch = player.rotationPitch;
		float vx = -MathHelper.sin(rad(rotationYaw)) * MathHelper.cos(rad(rotationPitch));
		float vz = MathHelper.cos(rad(rotationYaw)) * MathHelper.cos(rad(rotationPitch));
		float vy = -MathHelper.sin(rad(rotationPitch));
		return new Vec3d(vx, vy, vz);
	}
	
	public static EntityLivingBase raytraceEntities(World world, EntityPlayer player, double range) {
        HitResult result = new HitResult();
        Vec3d pos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d segment = player.getLookVec();
        segment = pos.addVector(segment.x * range, segment.y * range, segment.z * range);
        result.setBlockHit(world.rayTraceBlocks(pos, segment, false, true, true));
        double collidePosX, collidePosY, collidePosZ;
        if (result.blockHit != null) {
            collidePosX = result.blockHit.hitVec.x;
            collidePosY = result.blockHit.hitVec.y;
            collidePosZ = result.blockHit.hitVec.z;
        }
        else {
            Vec3d end = player.getLookVec().scale(range);
            collidePosX = end.x;
            collidePosY = end.y;
            collidePosZ = end.z;
        }

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(Math.min(pos.x, collidePosX), Math.min(pos.y, collidePosY), Math.min(pos.z, collidePosZ), Math.max(pos.x, collidePosX), Math.max(pos.y, collidePosY), Math.max(pos.z, collidePosZ)).grow(1, 1, 1));
        EntityLivingBase closest = null;
        for (EntityLivingBase entity : entities) {
            if (entity == player) {
                continue;
            }
            float pad = entity.getCollisionBorderSize();
            AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(pad, pad, pad);
            RayTraceResult hit = aabb.calculateIntercept(pos, segment);
            if (aabb.contains(pos) || hit != null) {
                result.addEntityHit(entity);
                if (closest == null || player.getDistanceSq(closest) > player.getDistanceSq(entity)) closest = entity;
            }
        }
        return closest;
    }
	
	public static class HitResult {
        private RayTraceResult blockHit;

        private List<EntityLivingBase> entities = new ArrayList<>();

        public RayTraceResult getBlockHit() {
            return blockHit;
        }

        public void setBlockHit(RayTraceResult blockHit) {
            this.blockHit = blockHit;
        }

        public void addEntityHit(EntityLivingBase entity) {
            entities.add(entity);
        }
    }
	
	public static int posmod(int i, int n) {
		return ((i % n) + n) % n;
	}

	public static double posNegDouble(Random r) {
		return r.nextBoolean() ? -r.nextDouble() : r.nextDouble();
	}

	public static float rad(float angle) {
		return angle * (float) Math.PI / 180;
	}

	public static void serverParticle(World w, String name, double x, double y, double z, double velx, double vely, double velz, int repeat) {
		//Taoism.net.sendToAllAround(new PacketSpawnParticle(name, x, y, z, velx, vely, velz, 3d, repeat), new TargetPoint(w.provider.dimensionId, x, y, z, 32d));
	}//the 3 here is a placeholder

	/**
	 * @return the original itemstack
	 */
	public static ItemStack upgrade(ItemStack is, float plusdmg, float plusspd) {
		float swordDamage = 4 + ((ItemSword) is.getItem()).getAttackDamage();
		AttributeModifier attackModifier = new AttributeModifier(MODIFIER_UUID, "Weapon Upgrade", plusdmg + swordDamage, 0);
		NBTTagCompound modifierNBT = writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_DAMAGE, attackModifier);
		float swordSpd = 1.6f;//4 + ((ItemSword) is.getItem()).getAttackDamage();
		AttributeModifier spdModifier = new AttributeModifier(MODIFIER_UUID, "Weapon Upgrade", plusspd * swordSpd, 0);
		NBTTagCompound spdNBT = writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_SPEED, spdModifier);

		// Create the NBT structure needed by ItemStack#getAttributeModifiers
		NBTTagCompound stackTagCompound = is.getTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(modifierNBT);
		list.appendTag(spdNBT);
		stackTagCompound.setTag("AttributeModifiers", list);

		// Create an ItemStack of the Item
		ItemStack stack = is.copy();

		// Set the stack's NBT to the modifier structure
		stack.setTagCompound(stackTagCompound);

		return stack;
	}

	public static ItemStack upgradedSpeed(ItemStack is, float plus) {
		float swordSpd = 1.6f;//4 + ((ItemSword) is.getItem()).getAttackDamage();
		AttributeModifier spdModifier = new AttributeModifier(MODIFIER_UUID, "Weapon Upgrade", plus + swordSpd, 0);
		NBTTagCompound spdNBT = writeAttributeModifierToNBT(SharedMonsterAttributes.ATTACK_SPEED, spdModifier);

		// Create the NBT structure needed by ItemStack#getAttributeModifiers
		NBTTagCompound stackTagCompound = is.getTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(spdNBT);
		stackTagCompound.setTag("AttributeModifiers", list);

		// Create an ItemStack of the Item
		ItemStack stack = is.copy();

		// Set the stack's NBT to the modifier structure
		stack.setTagCompound(stackTagCompound);

		return stack;
	}

	private static NBTTagCompound writeAttributeModifierToNBT(IAttribute attribute, AttributeModifier modifier) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setString("AttributeName", attribute.getName());
		nbttagcompound.setString("Name", modifier.getName());
		nbttagcompound.setDouble("Amount", modifier.getAmount());
		nbttagcompound.setInteger("Operation", modifier.getOperation());
		nbttagcompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
		nbttagcompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
		return nbttagcompound;
	}

	public static void writeToTaoisticNBT(NBTTagCompound compound, String tag, Object data) {
		NBTTagCompound master = compound.getTag("Taoism") == null ? new NBTTagCompound() : (NBTTagCompound) compound.getTag("Taoism");
		NBTTagCompound spr = new NBTTagCompound();
		if (data instanceof ItemStack) {
			ItemStack is = (ItemStack) data;
			master.setTag(tag, is.writeToNBT(spr));
		} else if (data == int.class) {
			master.setInteger(tag, (Integer) data);
		} else if (data == float.class) {
			master.setFloat(tag, (Float) data);
		} else if (data == short.class) {
			master.setShort(tag, (Short) data);
		} else if (data == long.class) {
			master.setLong(tag, (Long) data);
		} else if (data == String.class) {
			master.setString(tag, (String) data);
		}
		compound.setTag("Taoism", master);
	}
	
}
