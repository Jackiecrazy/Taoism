package com.Jackiecrazi.taoism.common.taoistichandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.api.NeedyLittleThings;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.common.entity.ModEntities;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityDroppedWeapon;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntityMuRenZhuang;
import com.Jackiecrazi.taoism.common.entity.literaldummies.EntitySandbag;
import com.Jackiecrazi.taoism.common.items.ModItems;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.wuGong.WuGongHandler;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TaoisticEventHandler {
	int lcd;

	@EventHandler
	public void load(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new TaoisticEventHandler());
		FMLCommonHandler.instance().bus().register(new TaoisticEventHandler());
	}

	@SubscribeEvent
	public void sadism(AttackEntityEvent e) {// attack with more damage and
		// fire/magic/etc
		//e.entity.worldObj.addWeatherEffect(new EntityLightningBolt(e.entity.worldObj,e.entity.posX,e.entity.posY,e.entity.posZ));
		EntityPlayer player = (EntityPlayer) e.entityPlayer;
		Entity uke = e.target;
		// if(uke.hurtResistantTime!=0)System.out.println("hi");
		float rawWGlvl = (WuGongHandler.getThis(player).getLevel());
		float rawQLlvl = (XiuWeiHandler.getThis(player).getLevel());
		float processedWGlvl = (WuGongHandler.getThis(player).getLevel() / 100) + 0.01F;
		float processedQLlvl = (XiuWeiHandler.getThis(player).getLevel() / 100) + 0.01F;
		float WGxp = e.entity.worldObj.rand.nextFloat() * (processedWGlvl);
		if (NeedyLittleThings.isWearingFullSet(player, ModItems.wushuRibbon)) {
			WGxp *= 2;
			processedWGlvl *= 2;
		}
		float QLxp = e.entity.worldObj.rand.nextFloat() * (processedQLlvl);
		if ((player.getHeldItem() != null)) {
			if ((player.attackTime == 0)
					|| (player.getHeldItem().getItem() instanceof ISwingSpeed && ((ISwingSpeed) player
							.getHeldItem().getItem()).swingSpd() == player.attackTime)
							|| player.getHeldItem() == null || !WayofConfig.CDEnabled) {

				if (!player.worldObj.isRemote) {
					WuGongHandler.getThis(player).addXP(WGxp);
					// player.addExhaustion(1 / rawWGlvl);
					// System.out.println(1/rawWGlvl);
					if (player.getHeldItem() != null) {
						ItemStack held = player.getHeldItem();
						Item i = held.getItem();
						if (i instanceof ISwingSpeed) {
							player.attackTime = ((ISwingSpeed) i).swingSpd();
							if (!player.worldObj.isRemote)
								Taoism.net.sendTo(new PacketUpdateAttackTimer(
										((ISwingSpeed) i).swingSpd()),
										(EntityPlayerMP) player);

						}
					}

					if (uke instanceof EntitySandbag) {
						EntitySandbag sand = (EntitySandbag) uke;
						if (sand.getContents() != null) {
							ItemStack sa = sand.getContents();
							Block sandType = Block.getBlockFromItem(sa
									.getItem());
							WuGongHandler
							.getThis(player)
							.addXP(sandType == Blocks.soul_sand ? (float) (sa.stackSize
									/ processedWGlvl * 0.0002)
									: sandType == Blocks.sand ? (float) (sa.stackSize
											/ processedWGlvl * 0.0001)
											: 0.0001F);
							// System.out.println();
							player.addExhaustion((1 / 64) * sa.stackSize);
						}
					}
				}
			} else {
				e.setCanceled(true);
			}

		} else if (uke instanceof EntityMuRenZhuang) {

			float exp = QLxp;
			// System.out.println("added xp of value "+exp+" consuming "+rawQLlvl+" qi");
			if (player.getHeldItem() == null
					&& PlayerResourceStalker.get(player).subtractValues(
							WayofConfig.QiDWID, rawQLlvl))
				XiuWeiHandler.getThis(player).addXP(exp);

			player.addExhaustion((float) (1 / (rawQLlvl + 0.1)));
		} else {
			WuGongHandler.getThis(player).addXP(WGxp);
			// player.addExhaustion(1 / (rawWGlvl*3));
			XiuWeiHandler.getThis(player).addXP(QLxp / 4f);
		}
	}

	@SubscribeEvent
	public void masochism(LivingHurtEvent e) {
		// modify dealt damage here
		if (e.source.getEntity() != null) {

			Entity sadist = e.source.getEntity();

			EntityLivingBase masochist = (EntityLivingBase) e.entityLiving;
			double sadistX = sadist.posX;
			double sadistY = sadist.posY;
			double sadistZ = sadist.posZ;
			double masochistX = sadist.posX;
			double masochistY = sadist.posY;
			double masochistZ = sadist.posZ;
			if (sadist instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) sadist;
				int WGS = WuGongHandler.getThis(player).getLevel();

				if (player.getCurrentEquippedItem() != null) {
					String shakespeare = player.getCurrentEquippedItem()
							.getItem().getUnlocalizedName();

					if (player.getCurrentEquippedItem().getItem() instanceof GenericTaoistWeapon) {
						// random crit. It's a bad idea.
						/*
						 * float shamate = Skills.readit(player, shakespeare);
						 * double trellis = Math.random(); int shanyao = (int)
						 * (trellis * 1.5); switch (shanyao) { case 1:
						 * Skills.writeit(player, 1); break; case 2:
						 * Skills.writeit(player, 2); } if (Math.random() * 100
						 * < shamate) { for (int particle = 0; particle < 128;
						 * ++particle) { sadist.worldObj.spawnParticle("cloud",
						 * masochistX + Math.random(), masochistY +
						 * Math.random(), masochistZ + Math.random(), 0,
						 * Math.random(), 0); } e.ammount *= 2; }
						 */
						// set swing speed
						if (player.getCurrentEquippedItem().getItem() instanceof ISwingSpeed)
							player.attackTime = ((ISwingSpeed) player
									.getCurrentEquippedItem().getItem())
									.swingSpd();
						// sword's lightning buff
						if (player.getCurrentEquippedItem().getItem() instanceof ICustomRange) {
							// something goes here? I FORGOT! The below is a
							// placeholder that gives extra damage when
							// assassinating
							if (player.isInvisible()&&((ICustomRange)player.getCurrentEquippedItem().getItem()).getRange(player, player.getCurrentEquippedItem())<4)
								e.ammount *= 1.5;
						}
					}
					
				} else {
					// KENSHIRO!
					e.ammount *= Math.max(WGS / 20, 1);
					e.entity.hurtResistantTime = MathHelper.floor_double(Math
							.min(20, (20 / (WGS / 10 + 0.1))));
				}
				// basic fighting adds some buff, esp. for bare hands
				float buff = ((float) XiuWeiHandler.getThis(player)
						.getSkillAwesomeness(Skill.BFNAME) / 25F);
				buff *= player.getHeldItem() == null ? 1.5 : 1;
				e.ammount *= Math.max(buff, 1f);
			}

		}

		if(e.source.damageType.contains("taoistelement")){
			EntityLivingBase elb=e.entityLiving;
			IAttributeInstance damnerf;
			//heals based on element
			if(!(e.entityLiving instanceof EntityPlayer)){
				switch(DamageElemental.TaoistElement.valueOf(e.source.damageType.substring(e.source.damageType.lastIndexOf(".")+1).toUpperCase())){
				case METAL://metal makes water
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WATER);
					break;
				case WOOD://wood creates fire
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_FIRE);
					break;
				case WATER://water nourishes wood
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WOOD);
					break;
				case FIRE://fire burns to earth
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_EARTH);
					break;
				case EARTH://earth compresses to metal
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_METAL);
					break;
				case WIND://wind hastens thunder
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_THUNDER);
					break;
				case THUNDER://thunder helps yang
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YANG);
					break;
				case YIN://yin accumulates sha
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_SHA);
					break;
				case YANG://yang grows yin
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YIN);
					break;
				case SHA://sha creates wind
					damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WIND);
					break;
				default:
					damnerf=elb.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
					break;
				}
				e.entityLiving.heal((float) (2*e.ammount*damnerf.getAttributeValue()/100));
				e.setCanceled(true);
				return;
			}

			//decrease damage based on feeding element
			switch(DamageElemental.TaoistElement.valueOf(e.source.damageType.substring(e.source.damageType.lastIndexOf(".")+1).toUpperCase())){
			case METAL://metal
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_METAL);
				break;
			case WOOD://wood
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WOOD);
				break;
			case WATER://water
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WATER);
				break;
			case FIRE://fire
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_FIRE);
				break;
			case EARTH://earth
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_EARTH);
				break;
			case WIND://wind
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WIND);
				break;
			case THUNDER://thunder
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_THUNDER);
				break;
			case YIN://yin
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YIN);
				break;
			case YANG://yang
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YANG);
				break;
			case SHA://sha
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_SHA);
				break;
			default:
				damnerf=elb.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
				break;
			}
			if(elb instanceof EntityPlayer){
				e.ammount*=(100-damnerf.getAttributeValue());//percentage?
				e.ammount/=100;
			}
			else{
				elb.heal((float) (e.ammount*(100-damnerf.getAttributeValue())/100));
			}


			//increase damage
			switch(DamageElemental.TaoistElement.valueOf(e.source.damageType.substring(e.source.damageType.lastIndexOf(".")+1).toUpperCase())){
			case METAL://metal falls trees
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WOOD);
				break;
			case WOOD://wood splits earth
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_EARTH);
				break;
			case WATER://water extinguishes fire
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_FIRE);
				break;
			case FIRE://fire melts metal
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_METAL);
				break;
			case EARTH://earth absorbs water
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WATER);
				break;
			case WIND://wind dissipates yang
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YANG);
				break;
			case THUNDER://thunder dissipates yin
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_YIN);
				break;
			case YIN://yin stops wind
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_WIND);
				break;
			case YANG://yang oppresses sha
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_SHA);
				break;
			case SHA://sha presses thunder
				damnerf=elb.getEntityAttribute(ModEntities.RESISTANCE_THUNDER);
				break;
			default:
				damnerf=elb.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
				break;
			}
			e.ammount*=(1+(damnerf.getAttributeValue()/100));
		}

	}

	@SubscribeEvent
	public void evade(LivingAttackEvent e) {
		if (e.source.getEntity() != null) {

			Entity sadist = e.source.getEntity();

			EntityLivingBase masochist = (EntityLivingBase) e.entityLiving;
			double sadistX = sadist.posX;
			double sadistY = sadist.posY;
			double sadistZ = sadist.posZ;
			double masochistX = sadist.posX;
			double masochistY = sadist.posY;
			double masochistZ = sadist.posZ;
			if (e.entityLiving instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) masochist;
				int WGS = WuGongHandler.getThis(player).getLevel();
				int QLS = XiuWeiHandler.getThis(player).getLevel();
				Random r = player.worldObj.rand;
				if (QLS > 20
						&& r.nextInt(200) < QLS
						&& PlayerResourceStalker.get(player).subtractValues(
								WayofConfig.QiDWID, 40f)
								&& (e.source.isProjectile()
										|| e.source.getDamageType() == "mob" || e.source
										.getDamageType() == "player")) {
					// System.out.println("trigger");
					/*
					 * boolean doTP=false; double
					 * tpX=player.posX+r.nextInt(6)-3; double tpY=player.posY;
					 * double tpZ=player.posZ+r.nextInt(6)-3; if
					 * (player.isRiding()) { player.mountEntity((Entity)null); }
					 * if
					 * ((player.worldObj.blockExists(MathHelper.floor_double(tpX
					 * ), MathHelper.floor_double(tpY),
					 * MathHelper.floor_double(tpZ))&&(
					 * player.worldObj.blockExists(MathHelper.floor_double(tpX),
					 * MathHelper.floor_double(tpY+1),
					 * MathHelper.floor_double(tpZ))))){ for(int
					 * inc=-3;inc<5;inc++){
					 * if((player.worldObj.isAirBlock(MathHelper
					 * .floor_double(tpX), MathHelper.floor_double(tpY+inc),
					 * (int)(tpZ-0.4))&&(player.worldObj
					 * .isAirBlock(MathHelper.floor_double(tpX),
					 * (int)(tpY+1+inc), (int)(tpZ-0.4))))){ tpY+=inc;
					 * doTP=true; break; } } } else if(player.posY<0){ for(int
					 * inc=0;inc<256;inc++){ if(player.worldObj
					 * .canBlockSeeTheSky(MathHelper.floor_double(tpX),
					 * MathHelper.floor_double(tpY+inc),
					 * MathHelper.floor_double(tpZ))){ tpY+=inc; doTP=true; } }
					 * }
					 * 
					 * else{ doTP=true; } if(doTP)
					 * player.setPositionAndUpdate(tpX, tpY, tpZ); else
					 * player.worldObj.createExplosion(player, masochistX,
					 * masochistY, masochistZ, QLS/50, false);
					 * e.setCanceled(doTP);
					 */
					// this is not a good idea
					player.hurtResistantTime = 20;
					e.setCanceled(true);

				}
			}
		}
		// modify received damage here
		if (e.entityLiving != null) {
			EntityLivingBase masochist = e.entityLiving;
			if (masochist instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) masochist;
				int WGS = WuGongHandler.getThis(player).getLevel();
				// prevents falling to death
				if (e.source == DamageSource.fall) {
					float sky = e.ammount;
					// 255 damage is negated at lvl 150
					if (sky - Math.min(1.8 * WGS, sky) <= 0) {
						e.setCanceled(true);
					}

				}
			}
		}
		if (e.ammount == 0)
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void onElectricHit(EntityStruckByLightningEvent event) {
		if (event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entity;
			if (player.getCurrentEquippedItem() != null
					&& player.getCurrentEquippedItem().getItem() == ModItems.SwordCherry) {
				ItemStack itemstack = player.getCurrentEquippedItem();
				if (itemstack.hasTagCompound()) {
					int nb = itemstack.getTagCompound().getInteger("hitcount");
					if (nb < 9 && lcd == 0) {
						if (nb < 0) {
							itemstack.getTagCompound()
							.setInteger("hitcount", 0);
						}
						itemstack.getTagCompound().setInteger("hitcount",
								nb + 1);
						lcd = 18000;
					} else if (lcd == 0) {
						player.destroyCurrentEquippedItem();
						ChatComponentText component = new ChatComponentText(
								"dude, you destroyed it!");
						player.addChatComponentMessage(component);
					}
				} else {
					itemstack.setTagCompound(new NBTTagCompound());
					itemstack.getTagCompound().setInteger("hitcount", 1);
				}
				event.setCanceled(true);
				return;
			}
		}
	}

	/*
	 * public void update(ItemStack par1ItemStack, World par2World, Entity
	 * par3Entity, int par4, boolean par5){ if(lcd > 0){ lcd--; } if(lcd < 0){
	 * lcd = 0; } if(par3Entity instanceof EntityLivingBase && !(par3Entity
	 * instanceof EntityPlayer)){ DataWatcher inqiesiting =
	 * par3Entity.getDataWatcher(); float chum =
	 * inqiesiting.getWatchableObjectFloat(14); Random r = new Random(); int
	 * shub = r.nextInt(600); if(chum >= 3 && shub == 0){
	 * par2World.addWeatherEffect(new EntityLightningBolt(par2World,
	 * par3Entity.posX, par3Entity.posY, par3Entity.posZ)); } float sham; sham =
	 * (float) 0.0000001; inqiesiting.updateObject(14, chum+sham);
	 * 
	 * } }
	 */

	// more ascetic training starters. Villagers get a higher bonus than
	// monsters, then animals
	/*
	 * @SubscribeEvent public void ascetictraining(EntityJoinWorldEvent e){
	 * if(e.entity instanceof EntityLivingBase && !(e.entity instanceof
	 * EntityPlayer)){ EntityLivingBase en = (EntityLivingBase) e.entity;
	 * DataWatcher inqiesiting = en.getDataWatcher();
	 * if(en.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD){
	 * initialtraining = 1; } else if(en.getCreatureAttribute() ==
	 * EnumCreatureAttribute.ARTHROPOD) initialtraining =-1; else
	 * if(en.getEntityId() ==120) initialtraining =2; else initialtraining = 0;
	 * inqiesiting.addObject(14, initialtraining); } }
	 */
	@SubscribeEvent
	public void whee(LivingJumpEvent e) {
		EntityLivingBase l = e.entityLiving;
		// System.out.println("hi");
		if (l instanceof EntityPlayer) {

			EntityPlayer p = (EntityPlayer) l;
			int WGLvl = WuGongHandler.getThis(p).getLevel();
			float QLLvl = PlayerResourceStalker.get(p).getValues(
					WayofConfig.QiDWID);
			float buff = Math.min(WGLvl / 250f, 0.2000000000005f);
			if (PlayerResourceStalker.get(p).getIsMeditating()) {
				PlayerResourceStalker.get(p).setIsMeditating(false);
				p.motionY = 0;
			}
			if (p.isSneaking()) {
				buff += 0.1;
			}
			if (PlayerResourceStalker.get(p).subtractValues(WayofConfig.QiDWID,
					buff + 0.3f))
				;
			buff += Math.min(QLLvl / 2000, 0.275f);
			// System.out.println(buff);
			buff /= Math.max(p.inventory.getTotalArmorValue(), 1F);
			if (NeedyLittleThings.isWearingFullSet(p, ModItems.wushuPants))
				buff *= 2;

			p.motionY += buff;

		}

		// TODO otherwise buff some trained mobs' jump
	}

	@SubscribeEvent
	public void ignoreStuff(LivingSetAttackTargetEvent e) {
		if (e.target != null&&e.entity instanceof EntityLiving)
			if (e.target.isPotionActive(WayofConfig.HidePotID)
					&& e.entityLiving.func_94060_bK() != e.target) {

				((EntityLiving) e.entity).setAttackTarget(null);
			}
	}

	@SubscribeEvent
	public void rasengan(BreakSpeed e) {
		e.newSpeed = Math.max(e.originalSpeed,
				WuGongHandler.getThis(e.entityPlayer).getLevel() / 10);
	}

	@SubscribeEvent
	public void rasenganEnd(BreakEvent e) {
		if (e.getPlayer().getHeldItem() == null
				&& e.block.getMaterial() == Material.rock) {
			int lvl = WuGongHandler.getThis(e.getPlayer()).getLevel();
			int buff = PlayerResourceStalker.get(e.getPlayer()).subtractValues(
					WayofConfig.QiDWID,
					e.block.getBlockHardness(e.world, e.x, e.y, e.z)) ? lvl / 20
							: 0;
			WuGongHandler.getThis(e.getPlayer()).addXP(0.01f);
			ArrayList<ItemStack> drops = e.block.getDrops(e.world, e.x, e.y,
					e.z, e.blockMetadata, buff);
			if (!drops.isEmpty()) {
				Iterator<ItemStack> hi = drops.iterator();
				while (hi.hasNext()) {
					ItemStack bye = hi.next();
					e.world.spawnEntityInWorld(new EntityItem(e.world, e.x,
							e.y, e.z, bye));
				}
			}
			// Area break goes here
		}
	}

	@SubscribeEvent
	public void breakSuccess(HarvestCheck e) {
		EntityPlayer player = e.entityPlayer;
		int WGS = WuGongHandler.getThis(player).getLevel();
		Vec3 playerPos = Vec3.createVectorHelper(player.posX, player.posY,
				player.posZ);
		Vec3 bottom = player.getLookVec();
		MovingObjectPosition mop = player.worldObj.rayTraceBlocks(playerPos,
				bottom, true);
		if (mop != null) {
			if (mop.typeOfHit != null
					&& mop.typeOfHit == MovingObjectType.BLOCK) {
				if (e.block.getHarvestLevel(player.worldObj.getBlockMetadata(
						mop.blockX, mop.blockY, mop.blockZ)) < (WGS / 16))e.success=true;
			}
		}
	}

	@SubscribeEvent
	public void drops(HarvestDropsEvent e) {
		if (e.harvester != null) {
			EntityPlayer player = e.harvester;
			int lvl = WuGongHandler.getThis(player).getLevel();
			if (lvl / 20 > e.block.getHarvestLevel(e.blockMetadata)&&!player.capabilities.isCreativeMode) {
				ArrayList<ItemStack> drops = e.block.getDrops(e.world, e.x,
						e.y, e.z, e.blockMetadata, lvl);
				if (!drops.isEmpty()) {
					Iterator<ItemStack> hi = drops.iterator();
					while (hi.hasNext()) {
						ItemStack bye = hi.next();
						e.drops.add(bye);
					}
				}

			}
		}
	}
	
	@SubscribeEvent
	public void replaceDrop(EntityJoinWorldEvent e){
		if(e.entity instanceof EntityItem){
			if(((EntityItem)e.entity).getEntityItem()!=null
					&&((EntityItem)e.entity).getEntityItem().getItem() instanceof GenericTaoistWeapon
					&&!(e.entity instanceof EntityDroppedWeapon)){
				EntityDroppedWeapon replace=new EntityDroppedWeapon(e.world,e.entity.posX,e.entity.posY,e.entity.posZ,((EntityItem)e.entity).getEntityItem(),e.world.rand.nextFloat()*90,e.world.rand.nextFloat()*90,e.world.rand.nextFloat()*90);
				e.entity.setDead();
				if(!e.world.isRemote){
					//System.out.println("replacing");
				e.world.spawnEntityInWorld(replace);
				}
				e.setCanceled(true);
			}
		}
	}
}
