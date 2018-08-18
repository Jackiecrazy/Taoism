package com.jackiecrazi.taoism.common.item.weapon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponPerk.HandlePerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.api.alltheinterfaces.IAmModular;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;
import com.jackiecrazi.taoism.api.alltheinterfaces.IElemental;
import com.jackiecrazi.taoism.api.alltheinterfaces.ISwingSpeed;
import com.jackiecrazi.taoism.client.KeyOverlord;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.AbstractWeaponConfigOverlord;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;

public class TaoWeapon extends ItemSword implements IAmModular, ICustomRange, IElemental, ISwingSpeed {

	public static ItemStack createRandomWeapon(EntityPlayer p, Random r) {
		ItemStack ret = new ItemStack(TaoItems.weap);
		String s = StaticRefs.HANDLE;
		int wsw = r.nextInt(TaoConfigs.weapc.getType(s).size());

		((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, AbstractWeaponConfigOverlord.lookup(s, wsw).matType()), wsw));
		s = StaticRefs.HEAD;
		wsw = r.nextInt(TaoConfigs.weapc.getType(s).size() + 1);
		if (wsw != 0) {

			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, AbstractWeaponConfigOverlord.lookup(s, wsw - 1).matType()), wsw));
		}

		s = StaticRefs.GUARD;
		wsw = r.nextInt(TaoConfigs.weapc.getType(s).size() + 1);
		if (wsw != 0) {
			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, AbstractWeaponConfigOverlord.lookup(s, wsw - 1).matType()), wsw));
		}

		s = StaticRefs.POMMEL;
		wsw = r.nextInt(TaoConfigs.weapc.getType(s).size() + 1);
		if (wsw != 0) {
			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, AbstractWeaponConfigOverlord.lookup(s, wsw - 1).matType()), wsw));
		}

		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}

	public static ItemStack createWeapon(EntityPlayer p, ArrayList<PartData> pd) {
		ItemStack ret = new ItemStack(TaoItems.weap);
		for (PartData par : pd) {
			if (par.getWeaponSW().isHandle()) TaoItems.weap.setPart(par.getPart(), ret, par);
		}
		for (PartData par : pd) {
			TaoItems.weap.setPart(par.getPart(), ret, par);
		}
		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}

	public static ItemStack createWeapon(EntityPlayer p, PartData... pd) {
		ItemStack ret = new ItemStack(TaoItems.weap);
		for (PartData par : pd) {
			if (par.getWeaponSW().isHandle()) TaoItems.weap.setPart(par.getPart(), ret, par);
		}
		for (PartData par : pd) {
			TaoItems.weap.setPart(par.getPart(), ret, par);
		}
		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}

	private Cache<ItemStack, String> namecache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.SECONDS).build();

	private List<Material> pickList = Arrays.asList(new Material[] {
			Material.ANVIL,
			Material.IRON,
			Material.PISTON,
			Material.REDSTONE_LIGHT,
			Material.CIRCUITS,
			Material.PACKED_ICE,
			Material.ROCK,
			Material.ICE,
			Material.GLASS });
	
	private List<Material> shovelList = Arrays.asList(new Material[] {
			Material.CAKE,
			Material.CLAY,
			Material.CRAFTED_SNOW,
			Material.GRASS,
			Material.GROUND,
			Material.CARPET,
			Material.SAND,
			Material.SNOW });
	
	private List<Material> axeList = Arrays.asList(new Material[] {
			Material.CACTUS,
			Material.CLOTH,
			Material.CORAL,
			Material.GOURD,
			Material.WOOD });

	private List<Material> scytheList = Arrays.asList(new Material[] {
			Material.LEAVES,
			Material.PLANTS,
			Material.VINE });

	public TaoWeapon() {
		super(ToolMaterial.IRON);
		this.setUnlocalizedName("weapon");
		this.setRegistryName("taoweapon");
		this.setCreativeTab(Taoism.tabWea);
		this.setFull3D();
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {

			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				return 0;
			}

		});
	}

	@Override
	public void addAffinity(ItemStack is, float... aff) {
		for (int a = 0; a < Math.min(NUMOFELEM, aff.length); a++) {
			setAffinity(is, a, aff[a] + getAffinity(is, a));
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		for (PartData a : getParts(stack).values()) {
			try {
				if (a != null && a.getMat() != null) tooltip.add(I18n.format(a.getMat()) + " " + I18n.format(AbstractWeaponConfigOverlord.lookup(a.getPart(), a.getOrdinal()).getName()));
			} catch (Exception e) {

			}
		}
		//increment in 5, max is 50
		if (KeyOverlord.isShiftDown()) for (int i = 0; i < getAffinities(stack).length; i++) {
			int pot = (int) getAffinities(stack)[i];
			//System.out.println(pot);
			int used = Math.round(pot / 2) * 2;
			if (used != 0) tooltip.add(IElemental.ELEMC[i] + "" + TextFormatting.ITALIC + I18n.format(IElemental.ELEMS[i] + used + ".inscription") + TextFormatting.RESET);
		}
		else {
			tooltip.add(I18n.format("taoism.shiftweapon"));
		}
		//perks
		if (KeyOverlord.isControlDown()) {
			tooltip.add(I18n.format(TextFormatting.BOLD + "part.perks") + TextFormatting.RESET);
			for (WeaponPerk hp : this.getPerks(stack).keySet()) {
				if (hp != null) tooltip.add(TextFormatting.BOLD + I18n.format("perk." + hp.name + ".name") + TextFormatting.RESET);
			}
		} else tooltip.add(TextFormatting.YELLOW + I18n.format("part.ctrl") + TextFormatting.RESET);
	}

	public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker)
    {
		return getPerks(stack).containsKey(WeaponPerk.CLEAVE)||getPerks(stack).containsKey(WeaponPerk.AXE);
    }
	
	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
		if (getPartThatCanHarvestBlock(stack, state) != null) { return true; }
		//System.out.println(ret);
		return super.canHarvestBlock(state, stack);
	}

	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityItem(world, location.posX, location.posY, location.posZ, itemstack);
	}

	@Override
	public float[] getAffinities(ItemStack is) {
		float[] ret = new float[NUMOFELEM];
		for (int a = 0; a < NUMOFELEM; a++)
			ret[a] = getAffinity(is, a);
		return ret;
	}

	@Override
	public float getAffinity(ItemStack is, int element) {
		return getStorage(is).getFloat("element" + element);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier> create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) fastatk(stack), 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) fastspd(stack) - 4d, 0));
			multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getReach(null, stack) - 4d, 0));

			//System.out.println(spd(stack));
		}

		// TODO Some weapons should give you buffs in other things too
		return multimap;
	}

	public float getDestroySpeed(ItemStack stack, IBlockState state) {

		float ret = super.getDestroySpeed(stack, state);
		if (getPartThatCanHarvestBlock(stack, state) != null) ret = Math.max(ret, getPartThatCanHarvestBlock(stack, state).getMatSW().getDigSpeed());
		return ret;
	}

	@Override
	public float getHorizontalRange(EntityPlayer p, ItemStack is) {
		return AbstractWeaponConfigOverlord.lookup(StaticRefs.GUARD, getParts(is).get(StaticRefs.GUARD).getOrdinal()).getRange();
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		String repairName = "";

		for (String s : getPartNames(toRepair)) {
			if (getParts(toRepair).get(s) != null) {
				repairName = getParts(toRepair).get(s).getMat();
				break;
			}
		}
		if (!repairName.isEmpty() && MaterialsConfig.findMat(repair).msw.equals(MaterialsConfig.findMat(repairName))) return true;
		return false;
	}

	public int getItemEnchantability(ItemStack is) {
		return magicness(is);
	}

	public String getItemStackDisplayName(ItemStack stack) {
		/*String name=namecache.getIfPresent(stack);
		if(name!=null)return name;
		name=getName(stack);
		
		if(!name.isEmpty())return name;*/
		return super.getItemStackDisplayName(stack);
	}

	public int getMaxDamage(ItemStack stack) {
		if (!stack.hasTagCompound()) return super.getMaxDamage(stack);
		return stack.getTagCompound().getInteger("dur");
	}

	@Override
	public String[] getPartNames(ItemStack is) {
		return StaticRefs.weaponparts;
	}

	@Nullable
	@Override
	public HashMap<String, PartData> getParts(ItemStack is) {
		HashMap<String, PartData> ret = new HashMap<String, PartData>();
		NBTTagCompound ntc = getStorage(is);
		for (String s : getPartNames(is)) {
			if (ntc.hasKey(s)) ret.put(s, new PartData(ntc.getCompoundTag(s)));
		}
		return ret;
	}

	@Nullable
	public PartData getPartThatCanHarvestBlock(ItemStack is, IBlockState state) {
		for (PartData pd : getParts(is).values())
			for (WeaponPerk wp : pd.getWeaponSW().getPerks()) {
				if (wp != null) {
					if (wp.equals(WeaponPerk.PICK) && pickList.contains(state.getMaterial())) {
						return pd;
					} else if (wp.equals(WeaponPerk.AXE) && axeList.contains(state.getMaterial())) {
						return pd;
					} else if (wp.equals(WeaponPerk.SCYTHE) && scytheList.contains(state.getMaterial())) {
						return pd;
					} else if (wp.equals(WeaponPerk.SHOVEL) && shovelList.contains(state.getMaterial())) { return pd; }
				}
			}
		return null;
	}



	@Override
	public float getReach(EntityPlayer p, ItemStack is) {
		float reach = 0F;
		for (PartData pd : getParts(is).values()) {
			if (!pd.getPart().equals(StaticRefs.GUARD)) reach += AbstractWeaponConfigOverlord.lookup(pd).getRange();
			//System.out.println(reach+" "+pd.getPart());
		}
		//System.out.println(reach);
		return reach;
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(createWeapon((EntityPlayer) null, new PartData(StaticRefs.HEAD, "ingotIron", 2), new PartData(StaticRefs.HANDLE, "logWood", 1), new PartData(StaticRefs.GUARD, "ingotIron", 1), new PartData(StaticRefs.POMMEL, "ingotIron", 1)).setStackDisplayName("Plain Old Sword"));
			items.add(createWeapon((EntityPlayer) null, new PartData(StaticRefs.HEAD, "ingotIron", 16), new PartData(StaticRefs.HANDLE, "logWood", 4), new PartData(StaticRefs.GUARD, "gemDiamond", 8), new PartData(StaticRefs.POMMEL, "ingotIron", 3)).setStackDisplayName("The All-Miner"));
			for (int a = 0; a < 88; a++)
				items.add(createRandomWeapon(null, Taoism.unirand));
		}
	}

	public boolean hasCustomEntity(ItemStack stack) {
		return false;
	}

	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		for (PartData pd : getParts(stack).values()) {
			WeaponStatWrapper wsw = AbstractWeaponConfigOverlord.lookup(pd.getPart(), pd.getOrdinal());
			for (WeaponPerk wp : wsw.getPerks()) {
				if (wp != null) wp.hitEntity(attacker, target, stack);
			}
		}
		return super.hitEntity(stack, target, attacker);
	}

	@Override
	public float hungerUsed(ItemStack i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isBroken(ItemStack stack){
		return this.getMaxDamage(stack)-this.getDamage(stack)<=1;
	}

	/*private float atk(ItemStack is) {
		int numofparts = 0;
		float ret = 0;
		for (String s : getPartNames(is)) {
			if (getPart(is, s) != null) {
				PartData pd = getPart(is, s);
				if (TaoConfigs.weapc.lookup(pd.getDam()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
					//System.out.println(TaoConfigs.weapc.lookup(pd.getDam()));
					//System.out.println(MaterialsConfig.findMat(pd.getMat()));

					continue;
				}
				ret += TaoConfigs.weapc.lookup(pd.getDam()).getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).damageOrSpringiness;
				numofparts++;
			} //else System.out.println("null part");
		}
		if (numofparts != 0) ret /= numofparts;
		return ret;
	}*/

	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
		//System.out.println(hand.equals(EnumHand.OFF_HAND));
		if(hand.equals(EnumHand.OFF_HAND)&&getReach(playerIn,stack)*getReach(playerIn,stack)>=playerIn.getDistanceSq(target)){
			Taoism.net.sendToServer(new PacketExtendThyReach(target.getEntityId(), false));
			playerIn.swingArm(hand);
			return true;
		}
        return false;
    }

	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		finalStat(stack, deriveStats(stack));
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		//final HandlePerk handle = getPart(stack, StaticRefs.HANDLE).getWeaponSW().getHandle();
		//if ((handle.equals(HandlePerk.LONG)||handle.equals(HandlePerk.FLEXIBLE)) && !entityLiving.getHeldItem(EnumHand.OFF_HAND).isEmpty()) return true;
		return false;
	}

	public ActionResult<ItemStack> onItemRightClick(World w, EntityPlayer p, EnumHand h) {
		ItemStack is = p.getHeldItem(h);
		HandlePerk handle = getPart(is, StaticRefs.HANDLE).getWeaponSW().getHandle();
		if (handle.equals(HandlePerk.LONG) ){
			//if(h.equals(EnumHand.MAIN_HAND))
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, is);
		}
		else if (h.equals(EnumHand.OFF_HAND)) {
			p.swingArm(h);
		}
		return super.onItemRightClick(w, p, h);
	}
	@Override
	public void setAffinities(ItemStack is, float... affinities) {
		for (int a = 0; a < Math.min(NUMOFELEM, affinities.length); a++) {
			setAffinity(is, a, affinities[a]);
		}
	}
	@Override
	public void setAffinity(ItemStack is, int element, float affinity) {
		getStorage(is).setFloat("element" + element, affinity);
	}


	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@Override
	public float swingSpd(ItemStack i) {
		return getStorage(i).getFloat("spd");
	}

	private float[] deriveStats(ItemStack is) {
		//range: add all except guard
		//horizontal range: guard
		//speed: all
		//damage: all, each part deals their respective damage type
		//durability: average? sum?
		//elements: average

		float[] ret = new float[9];
		int numofparts = 0;
		for (PartData pd : getParts(is).values()) {

			WeaponStatWrapper wsw = AbstractWeaponConfigOverlord.lookup(pd);
			if (wsw == null || MaterialsConfig.findMat(pd.getMat()) == null) {
				//System.out.println(TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal()));
				//System.out.println(MaterialsConfig.findMat(pd.getMat()));

				continue;
			}
			ret[0] += wsw.getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.mass;
			float bigdamage = wsw.getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.damageOrSpringiness;
			//if(ret[1]<bigdamage)ret[1]=bigdamage;
			ret[1] += bigdamage;
			//dur
			ret[2] += wsw.getDurabilityMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.durability;
			//ran
			if (!pd.getWeaponSW().getClassification().equals(StaticRefs.GUARD)) ret[3] += wsw.getRange();
			//kin moku sui hi do
			ret[4] += wsw.getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityMetal;
			ret[5] += wsw.getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWood;
			ret[6] += wsw.getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWater;
			ret[7] += wsw.getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityFire;
			ret[8] += wsw.getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityEarth;

			//(part damage modifier * material damage)/((1/(part attack time modifier*material attack time)))
			numofparts++;
			//} else System.out.println("null part");
		}
		if (numofparts != 0) for (int x = 4; x < ret.length; x++) {
			ret[x] /= numofparts;//if (x < 4)
			
		}
		ret[2]*=(4/numofparts);
		//ret[0]/=4;
		//System.out.println(ret);
		return ret;
	}

	private float fastatk(ItemStack is) {
		float atk = 0F;
		int counter = 0;
		for (PartData pd : getParts(is).values()) {
			if (!pd.getPart().equals(StaticRefs.GUARD)) {
				//if(atk<pd.getWeaponSW().getDamageMultiplier() * pd.getMatSW().damageOrSpringiness;)
				atk += pd.getWeaponSW().getDamageMultiplier() * pd.getMatSW().damageOrSpringiness;
				counter++;
			}
			//System.out.println(reach+" "+pd.getPart());
		}
		if (counter != 0) atk /= counter;
		//System.out.println(reach);
		return atk;
	}

	private float fastspd(ItemStack is) {
		float spd = 0F;
		int counter = 0;
		for (PartData pd : getParts(is).values()) {
			if (pd.getWeaponSW().getClassification().equals(StaticRefs.POMMEL)) spd += pd.getWeaponSW().getSpeedMultiplier() * pd.getMatSW().mass;
			spd += pd.getWeaponSW().getSpeedMultiplier() * pd.getMatSW().mass;
			counter++;
			//System.out.println(reach+" "+pd.getPart());
		}
		//System.out.println(reach);
		if (counter != 0) spd /= (counter);
		return spd;
	}

	private void finalStat(ItemStack is, float... stats) {

		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setFloat("spd", stats[0]);
		is.getTagCompound().setFloat("dam", stats[1]);
		is.getTagCompound().setInteger("dur", (int) stats[2]);
		is.getTagCompound().setFloat("ran", stats[3]);
		setAffinities(is, stats[4], stats[5], stats[6], stats[7], stats[8]);
		//upgrade(is, stats[1], stats[0]);
	}

	private String getName(ItemStack stack) {
		String ret = "";
		for (PartData pd : getParts(stack).values()) {
			pd.getWeaponSW().getClassification();
		}
		return ret;
	}


	private int magicness(ItemStack is) {
		int ret = 1;
		Collection<PartData> s = getParts(is).values();
		for (PartData pd : s) {
			ret += MaterialsConfig.findMat(pd.getMat()).msw.lingAbility;
		}
		ret /= s.size();
		return ret;
	}

	/*private float spd(ItemStack is) {
		int numofparts = 0;
		float ret = 0;
		for (String s : getPartNames(is)) {
			if (getPart(is, s) != null) {
				PartData pd = getPart(is, s);
				if (TaoConfigs.weapc.lookup(pd.getDam()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
					//System.out.println(TaoConfigs.weapc.lookup(pd.getDam()));
					//System.out.println(MaterialsConfig.findMat(pd.getMat()));

					continue;
				}
				ret += TaoConfigs.weapc.lookup(pd.getDam()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).mass;
				//System.out.println(s + "   "+TaoConfigs.weapc.lookup(pd.getDam()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).mass);
				if (TaoConfigs.weapc.lookup(pd.getDam()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).mass != 0) numofparts++;
			}// else System.out.println("null part");
		}
		//System.out.println(""+ret);
		if (numofparts != 0) ret /= (numofparts);
		//System.out.println(numofparts);
		//System.out.println(ret);
		return ret * 1.6f;
	}*/

}
