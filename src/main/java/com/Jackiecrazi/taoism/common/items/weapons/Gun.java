/*package com.Jackiecrazi.taoism.common.items.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageConcussion;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IBlunt;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;

@SuppressWarnings({ "rawtypes" })
public class Gun extends GenericTaoistWeapon implements IBlunt, ICustomRange {
	public Gun(ToolMaterial tawood) {
		super(tawood,"astick" + tawood.toString().toLowerCase());
		//this.setUnlocalizedName();
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.setMaxDamage(95);
		this.texName = "minecraft:stick";
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		if (entity.isEntityAlive()) {
			if (player.onGround) {
				int WGS = WuGongHandler.getThis(player).getLevel();
				if (!player.isSneaking()) {
					List targets = player.worldObj
							.getEntitiesWithinAABBExcludingEntity(player,
									entity.boundingBox.expand(1.2D, 1.1D, 1.2D));
					int count = 0;
					if (targets.size() > 1) {
						
						for (int var9 = 0; var9 < targets.size(); var9++) {
							Entity var10 = (Entity) targets.get(var9);
							if ((!var10.isDead)
									&& ((!(var10 instanceof EntityTameable)) || (!((EntityTameable) var10)
											.getOwner()
											.equals(player
													.getCommandSenderName())))
									&& ((var10 instanceof EntityLiving))
									&& (var10.getEntityId() != entity
											.getEntityId())
									&& ((!(var10 instanceof EntityPlayer)) || (((EntityPlayer) var10)
											.getCommandSenderName() != player
											.getCommandSenderName()))) {
								if (var10.isEntityAlive()) {
									NeedyLittleThings.attackWithDebuff(var10, player, Math.max(WGS/200f, 0.05F));
									//((EntityLiving) var10).attackEntityFrom(DamageConcussion.causeBrainDamageDirectly(player),(float) (1));
									((EntityLiving) var10)
											.addPotionEffect(new PotionEffect(
													Potion.moveSlowdown.id, 60,
													1));
									((EntityLiving) var10).hurtResistantTime = 0;
									count++;
								}

							}

						}
					}

					if ((count > 0) && (!player.worldObj.isRemote)) {
						player.playSound("random.successful_hit", 1.0F,
								0.9F + player.worldObj.rand.nextFloat() * 0.2F);
					}
				}
				else if ((!entity.isDead)
						&& ((!(entity instanceof EntityTameable)) || (!((EntityTameable) entity)
								.getOwner()
								.equals(player
										.getCommandSenderName())))
						&& ((entity instanceof EntityLiving))
						&& (entity.getEntityId() != entity
								.getEntityId())
						&& ((!(entity instanceof EntityPlayer)) || (((EntityPlayer) entity)
								.getCommandSenderName() != player
								.getCommandSenderName()))) {
					if (entity.isEntityAlive()) {
						entity.addVelocity(0, WGS / 10, 0);
					}
					
				}
			} else if(entity instanceof EntityLivingBase){
				((EntityLivingBase) entity).addPotionEffect(new PotionEffect(
						Potion.moveSlowdown.id, 60, 45));
				 //System.out.println("jump");
			}
		}
		//else  System.out.println("something is not right... entity alive: "+entity.isEntityAlive());
		return super.onLeftClickEntity(stack, player, entity);
	}

	public EnumAction getItemUseAction(ItemStack p_77661_1_) {
		return EnumAction.none;
	}

	public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p,
			int i) {
		super.onPlayerStoppedUsing(is, w, p, i);
		if (p.isSneaking()) {
			//onUltimate(p);
		}
		else{
			//TODO wu hua gun?
			System.out.println("hi");
		}
	}
	
	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return 6.2f;
	}

	@Override
	public int getUltimateTime() {
		return 100;
	}

	@Override
	public void saveState(NBTTagCompound tag) {

	}

	@Override
	public void readState(NBTTagCompound tag) {

	}

	@Override
	public void onUltimateTick(EntityPlayer p) {
		System.out.println("boom");
		//we are not ultimating for some goddamn reason
		super.onUltimateTick(p);
		p.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 30, 60));
		p.addPotionEffect(new PotionEffect(Potion.jump.id, 30, -29));
		List targets = p.worldObj.getEntitiesWithinAABBExcludingEntity(p,
				p.boundingBox.expand((double) 3.2D, 1.1D, 3.2D));
		int count = 0;
		if (!targets.isEmpty()) {
			for (int var9 = 0; var9 < targets.size(); var9++) {
				Entity var10 = (Entity) targets.get(var9);
				if ((!var10.isDead)
						&& ((!(var10 instanceof EntityTameable)) || (!((EntityTameable) var10)
								.getOwner().equals(p.getCommandSenderName())))
						&& ((var10 instanceof EntityLiving))
						&& (var10.getEntityId() != p.getEntityId())
						&& ((!(var10 instanceof EntityPlayer)) || (((EntityPlayer) var10)
								.getCommandSenderName() != p
								.getCommandSenderName()))) {
					if (var10.isEntityAlive()) {
						// NeedyLittleThings.attackWithDebuff(var10,
						// p,Math.max(((1/200)*WGS), 0.1F));
						var10.attackEntityFrom(
								DamageConcussion.causeBrainDamageDirectly(p),
								(float) 0.5F);
						((EntityLiving) var10)
								.addPotionEffect(new PotionEffect(
										Potion.moveSlowdown.id, 60, 45));
						((EntityLiving) var10).addPotionEffect(new PotionEffect(Potion.jump.id, 20, -29));
						((EntityLiving) var10).hurtResistantTime = 0;
						count++;
						System.out.println("boom");
					}

				}

			}

			if ((count > 0) && (!p.worldObj.isRemote)) {
				p.playSound("random.successful_hit", 1.0F,
						0.9F + p.worldObj.rand.nextFloat() * 0.2F);
			}
		}
	}

	@Override
	public float getUltimateCost() {
		return 1000f;
	}
	
	@Override
	public int getCDTime() {
		return 600;
	}
	

	@Override
	public float hungerUsed() {
		return 0.01F;
	}

	@Override
	public int swingSpd() {
		return 10;// 10
	}

	@Override
	protected void setParts() {
		this.parts.add(StaticRefs.SHAFT);
		this.parts.add(StaticRefs.LOOP);
	}

	@Override
	protected void setPointyBit() {
		this.offensivebit=StaticRefs.SHAFT;
	}

	

}
*/