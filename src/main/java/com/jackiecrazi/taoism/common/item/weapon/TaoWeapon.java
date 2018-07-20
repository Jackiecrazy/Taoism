package com.jackiecrazi.taoism.common.item.weapon;

import java.util.ArrayList;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.api.alltheinterfaces.IAmModular;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICustomRange;
import com.jackiecrazi.taoism.api.alltheinterfaces.IElemental;
import com.jackiecrazi.taoism.api.alltheinterfaces.ISwingSpeed;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

public class TaoWeapon extends ItemSword implements IAmModular, ICustomRange, IElemental, ISwingSpeed {

	private Cache<ItemStack, String> namecache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(5, TimeUnit.SECONDS).build();
	
	public static ItemStack createRandomWeapon(EntityPlayer p, Random r) {
		ItemStack ret = new ItemStack(TaoItems.weap);
		int wsw = r.nextInt(TaoConfigs.weapc.getType(1).size());
		String s = StaticRefs.HANDLE;
		((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.weapc.lookup(s, wsw).matType()), wsw));

		wsw = r.nextInt(TaoConfigs.weapc.getType(0).size() + 1);
		if (wsw != 0) {
			s = StaticRefs.HEAD;
			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.weapc.lookup(s, wsw - 1).matType()), wsw));
		}

		wsw = r.nextInt(TaoConfigs.weapc.getType(2).size() + 1);
		if (wsw != 0) {
			s = StaticRefs.GUARD;
			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.weapc.lookup(s, wsw - 1).matType()), wsw));
		}

		wsw = r.nextInt(TaoConfigs.weapc.getType(3).size() + 1);
		if (wsw != 0) {
			s = StaticRefs.POMMEL;
			((TaoWeapon) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.weapc.lookup(s, wsw - 1).matType()), wsw));
		}

		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}

	public static ItemStack createWeapon(EntityPlayer p, PartData... pd) {
		ItemStack ret = new ItemStack(TaoItems.weap);
		for (PartData par : pd) {
			TaoItems.weap.setPart(par.getPart(), ret, par);
		}
		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}
	
	public String getItemStackDisplayName(ItemStack stack)
    {
		/*String name=namecache.getIfPresent(stack);
		if(name!=null)return name;
		name=getName(stack);
		
		if(!name.isEmpty())return name;*/
		return super.getItemStackDisplayName(stack);
    }

	private String getName(ItemStack stack) {
		String ret="";
		for(PartData pd:getParts(stack).values()){
			pd.getWeaponSW().getClassification();
		}
		return ret;
	}

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
				if (a != null && a.getMat() != null) tooltip.add(I18n.format(a.getMat()) + " " + I18n.format(TaoConfigs.weapc.lookup(a.getPart(), a.getOrdinal()).getName()));
			} catch (Exception e) {

			}
		}
		//increment in 5, max is 50
		for (int i = 0; i < getAffinities(stack).length; i++) {
			int pot = (int) getAffinities(stack)[i];
			//System.out.println(pot);
			int used = Math.round(pot / 2) * 2;
			if (used != 0) tooltip.add(IElemental.ELEMC[i] + "" + TextFormatting.ITALIC + I18n.format(IElemental.ELEMS[i] + used + ".inscription") + TextFormatting.RESET);
		}
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
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) fastspd(stack) - 4D, 0));
			multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getReach(null, stack) - 4d, 0));

			//System.out.println(spd(stack));
		}

		// TODO Some weapons should give you buffs in other things too
		return multimap;
	}

	@Override
	public float getHorizontalRange(EntityPlayer p, ItemStack is) {
		return TaoConfigs.weapc.lookup(StaticRefs.GUARD, getParts(is).get(StaticRefs.GUARD).getOrdinal()).getRange();
	}

	public int getItemEnchantability(ItemStack is) {
		return magicness(is);
	}

	public int getMaxDamage(ItemStack stack) {
		if (!stack.hasTagCompound()) return super.getMaxDamage(stack);
		return stack.getTagCompound().getInteger("dur");
	}

	/**
	 * this will always return something so use it in emergency situations
	 * 
	 * @param is
	 * @param key
	 * @return
	 */
	public PartData getPart(ItemStack is, String key) {
		NBTTagCompound ntc = getStorage(is);
		if(ntc.hasKey(key))
		return new PartData(ntc.getCompoundTag(key));
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> getPartNames(ItemStack is) {
		return new ArrayList<String>(Arrays.asList(StaticRefs.weaponparts));
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

	@Override
	public float getReach(EntityPlayer p, ItemStack is) {
		float reach = 0F;
		for (PartData pd : getParts(is).values()) {
			reach += TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal()).getRange();
			//System.out.println(reach+" "+pd.getPart());
		}

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

	@Override
	public float hungerUsed(ItemStack i) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValidAddition(ItemStack is, String s, PartData pd) {
		//check if the pd actually works
		ArrayList<String> perks = new ArrayList<String>();
		WeaponStatWrapper wsw = TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal());
		if (wsw == null) {
			//System.out.println("proceeding");
			return false;//can throw null
		}
		//System.out.println(getParts(is));
		//check 
		for (PartData p : getParts(is).values()) {
			//System.out.println(p);
			if (p != null) {
				//System.out.println(p.toString());
				if (p.isValid()) {
					if (TaoConfigs.weapc.lookup(p.getPart(), p.getOrdinal()) != null) {
						WeaponStatWrapper w = TaoConfigs.weapc.lookup(p.getPart(), p.getOrdinal());
						for (WeaponPerk wp : w.getPerks())
							if (wp != null) perks.add(wp.name);
					}
				}
			}
		}
		for (WeaponPerk wp : wsw.getPerks())
			if (wp != null) perks.add(wp.name);
		{
			//System.out.println(perks);
			for (String bl : wsw.getBlacklist()) {
				//System.out.println("blacklist is " + bl);
				if (perks.contains(bl) && !bl.isEmpty()) {
					//System.out.println(bl+" is in the blacklist");
					return false;
				}
			}
			for (String wl : wsw.getWhitelist()) {
				//System.out.println("whitelist is " + wl);
				if (!perks.contains(wl) && !wl.replace(" ", "").equals("")) {
					//System.out.println(wl+" is not in the whitelist");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isValidConfiguration(ItemStack is) {
		if (is == null) return false;
		if (is.getItem() != TaoItems.weap) return false;
		for (Entry<String, PartData> e : this.getParts(is).entrySet()) {
			if (!this.isValidAddition(is, e.getKey(), e.getValue())) return false;
		}
		return true;
	}

	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		finalStat(stack, deriveStats(stack));
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		return false;
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

	@Override
	public boolean setPart(String partName, ItemStack is, PartData addition) {
		//if (!partList.contains(partName)) return false;
		if (addition == null) {
			System.out.println("null parts what");
			return false;
		}
		if (!isValidAddition(is, partName, addition)) {
			//System.out.println("invalid");
			return false;
		}
		NBTTagCompound ntc = getStorage(is);
		ntc.setTag(partName, addition.writeToNBT(new NBTTagCompound()));
		is.getTagCompound().setTag("TaoistParts", ntc);
		//System.out.println("part set");
		return true;
	}

	@Override
	public float swingSpd(ItemStack i) {
		return getStorage(i).getFloat("spd");
	}

	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		for (PartData pd : getParts(stack).values()) {
			WeaponStatWrapper wsw = TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal());
			for (WeaponPerk wp : wsw.getPerks()) {
				if (wp != null) wp.hitEntity(attacker, target, stack);
			}
		}
		return super.hitEntity(stack, target, attacker);
	}

	public HashMap<WeaponPerk, Integer> getPerks(ItemStack is) {
		HashMap<WeaponPerk, Integer> ret = new HashMap<WeaponPerk, Integer>();
		for (PartData pd : getParts(is).values()) {
			for (WeaponPerk wp : TaoConfigs.weapc.lookup(pd).getPerks()) {
				if (!ret.containsKey(wp)) ret.put(wp, 0);
				else ret.put(wp, ret.get(wp) + 1);
			}
		}
		return ret;
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

	@SuppressWarnings("unchecked")
	public List<Material> pickList = Arrays.asList(new Material[] {
			Material.ANVIL,
			Material.IRON,
			Material.PISTON,
			Material.REDSTONE_LIGHT,
			Material.CIRCUITS,
			Material.PACKED_ICE,
			Material.ROCK,
			Material.ICE,
			Material.GLASS });
	@SuppressWarnings("unchecked")
	public List<Material> shovelList = Arrays.asList(new Material[] {
			Material.CAKE,
			Material.CLAY,
			Material.CRAFTED_SNOW,
			Material.GRASS,
			Material.GROUND,
			Material.CARPET,
			Material.SAND,
			Material.SNOW });
	@SuppressWarnings("unchecked")
	public List<Material> axeList = Arrays.asList(new Material[] {
			Material.CACTUS,
			Material.CLOTH,
			Material.CORAL,
			Material.GOURD,
			Material.WOOD });
	@SuppressWarnings("unchecked")
	public List<Material> scytheList = Arrays.asList(new Material[] {
			Material.LEAVES,
			Material.PLANTS,
			Material.VINE });

	public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
		if (getPartThatCanHarvestBlock(stack, state) != null) { return true; }
		//System.out.println(ret);
		return super.canHarvestBlock(state, stack);
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

	public float getDestroySpeed(ItemStack stack, IBlockState state) {

		float ret = super.getDestroySpeed(stack, state);
		if (getPartThatCanHarvestBlock(stack, state) != null) ret = Math.max(ret, getPartThatCanHarvestBlock(stack, state).getMatSW().getDigSpeed());
		return ret;
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

			WeaponStatWrapper wsw = TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal());
			if (wsw == null || MaterialsConfig.findMat(pd.getMat()) == null) {
				//System.out.println(TaoConfigs.weapc.lookup(pd.getPart(), pd.getOrdinal()));
				//System.out.println(MaterialsConfig.findMat(pd.getMat()));

				continue;
			}
			ret[0] += wsw.getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.mass;
			ret[1] += wsw.getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.damageOrSpringiness;
			//dur
			ret[2] += wsw.getDurabilityMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.durability;
			//ran
			ret[3] += wsw.getRange();
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
		if (numofparts != 0) for (int x = 0; x < ret.length; x++) {
			 ret[x] /= numofparts;//if (x < 4)
		}
		//System.out.println(ret);
		return ret;
	}

	private float fastatk(ItemStack is) {
		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getFloat("dam");
	}

	private float fastspd(ItemStack is) {
		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getFloat("spd");
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

	private NBTTagCompound getStorage(ItemStack is) {
		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getCompoundTag("TaoistParts");
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
