package com.jackiecrazi.taoism.common.item.weapon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.api.WeaponPerk;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.api.alltheinterfaces.IAmModular;
import com.jackiecrazi.taoism.api.alltheinterfaces.IElemental;
import com.jackiecrazi.taoism.common.item.TaoItems;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

public class TaoBow extends ItemBow implements IAmModular, IElemental {
	public static ItemStack createRandomBow(EntityPlayer p, Random r) {
		ItemStack ret = new ItemStack(TaoItems.bow);
		int wsw = r.nextInt(TaoConfigs.bowc.getType(0).size());
		String s = StaticRefs.STAVE;
		((TaoBow) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.bowc.lookup(s, wsw).matType()), wsw));
		wsw = r.nextInt(TaoConfigs.bowc.getType(1).size());
		s = StaticRefs.STRING;
		((TaoBow) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.bowc.lookup(s, wsw).matType()), wsw));
		wsw = r.nextInt(TaoConfigs.bowc.getType(2).size());
		s = StaticRefs.SIYAH;
		((TaoBow) ret.getItem()).setPart(s, ret, new PartData(s, MaterialsConfig.getRandomMat(r, TaoConfigs.bowc.lookup(s, wsw).matType()), wsw));

		if (p != null) ret.getItem().onCreated(ret, p.world, p);
		else ret.getItem().onCreated(ret, null, null);
		return ret;
	}

	//FIXME pulling releasing arrow check
	public TaoBow() {

		super();
		this.setUnlocalizedName("taobow");
		this.setRegistryName("taobow");
		this.setCreativeTab(Taoism.tabBow);
	}

	@Override
	public void addAffinity(ItemStack is, float... aff) {
		for (int a = 0; a < Math.min(NUMOFELEM, aff.length); a++) {
			setAffinity(is, a, aff[a] + getAffinity(is, a));
		}
	}

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		//TODO list stats like draw strength and durability
		for (PartData a : getParts(stack).values()) {//so this is now an empty list eh
			if (a != null && a.getMat() != null) tooltip.add(a.getMat() + " " + TaoConfigs.bowc.lookup(a.getPart(), a.getOrdinal()).getName());
			/*else {
				System.out.println(a.getDam());
			}*/
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
		return null;
	}

	@Override
	public float getAffinity(ItemStack is, int element) {
		return getStorage(is).getFloat("element" + element);
	}

	/**
	 * 
	 * @param item
	 * @param plus addition
	 * @return the original itemstack
	 */
	/*
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
	}*/

	public int getMaxDamage(ItemStack stack) {
		if (!stack.hasTagCompound()) return super.getMaxDamage(stack);
		return stack.getTagCompound().getInteger("dur");
	}

	@Nullable
	public PartData getPart(ItemStack is, String key) {
		NBTTagCompound ntc = getStorage(is);
		if (ntc.hasKey(key)) return new PartData(ntc.getCompoundTag(key));
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<String> getPartNames(ItemStack is) {
		return new ArrayList<String>(Arrays.asList(StaticRefs.bowparts));
	}

	@Override
	public HashMap<String, PartData> getParts(ItemStack is) {
		HashMap<String, PartData> ret = new HashMap<String, PartData>();
		NBTTagCompound ntc = getStorage(is);
		for (String s : getPartNames(is)) {
			if (ntc.hasKey(s)) {//so it does not have such a key
				//System.out.println(s);
				ret.put(s, new PartData(ntc.getCompoundTag(s)));
			}
		}
		return ret;
	}

	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			for (int a = 0; a < 90; a++)
				items.add(createRandomBow(null, Taoism.unirand));
		}
	}

	public boolean hasCustomEntity(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isValidAddition(ItemStack is, String s, PartData pd) {

		ArrayList<String> perks = new ArrayList<String>();
		WeaponStatWrapper wsw = TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal());
		if (wsw == null) {
			//System.out.println("proceeding");
			return false;//can throw null
		}
		//System.out.println(wsw.getName());
		for (PartData p : getParts(is).values())
			if (p != null) {
				if (TaoConfigs.bowc.lookup(p.getPart(), p.getOrdinal()) != null) {
					WeaponStatWrapper w = TaoConfigs.bowc.lookup(p.getPart(), p.getOrdinal());
					for (WeaponPerk wp : w.getPerks())
						if (wp != null) perks.add(wp.name);
				}
			}
		for (WeaponPerk wp : wsw.getPerks())
			if (wp != null) perks.add(wp.name);
		{
			//System.out.println(perks);
			for (String bl : wsw.getBlacklist()) {
				//System.out.println("blacklist is " + bl);
				if (perks.contains(bl) && !bl.isEmpty()) {
					System.out.println(bl + " is in the blacklist");
					return false;
				}
			}
			for (String wl : wsw.getWhitelist()) {
				//System.out.println("whitelist is " + wl);
				if (!perks.contains(wl) && !wl.isEmpty() && !wl.replace(" ", "").equals("")) {
					System.out.println(wl + " is not in the whitelist");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean isValidConfiguration(ItemStack is) {
		if (is == null) return false;
		if (is.getItem() != TaoItems.bow) return false;
		for (Entry<String, PartData> e : this.getParts(is).entrySet()) {
			if (!this.isValidAddition(is, e.getKey(), e.getValue())) return false;
		}
		return true;
	}

	//range: add all except guard
	//horizontal range: guard
	//speed: all
	//damage: all, each part deals their respective damage type
	//durability: average? sum?
	//elements: average

	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		finalStat(stack, deriveStats(stack));
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

	@Override
	public boolean setPart(String partName, ItemStack is, PartData addition) {
		//if (!partList.contains(partName)) return false;
		if (addition == null) {
			//System.out.println("null parts what");
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

	private float arrspd(ItemStack is) {
		int numofparts = 0;
		float ret = 0;
		for (String s : getPartNames(is)) {
			if (getPart(is, s) != null) {
				PartData pd = getPart(is, s);
				if (TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
					System.out.println(TaoConfigs.bowc.lookup(pd.getPart(),pd.getOrdinal()));
					//System.out.println(MaterialsConfig.findMat(pd.getMat()));

					continue;
				}
				ret += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.arrowSpeed;
				numofparts++;
			} else System.out.println("null part");
		}
		if (numofparts != 0) ret /= numofparts;
		//System.out.println(ret);
		return ret;
	}

	private float[] deriveStats(ItemStack is) {
		float[] ret = new float[9];
		int numofparts = 0;
		for (String s : getPartNames(is)) {
			if (getPart(is, s) != null) {
				PartData pd = getPart(is, s);
				if (TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
					//System.out.println(TaoConfigs.bowc.lookup(pd.getDam()));
					//System.out.println(MaterialsConfig.findMat(pd.getMat()));

					continue;
				}
				ret[0] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.mass;
				ret[1] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.damageOrSpringiness;
				//dur
				ret[2] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getDurabilityMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.durability;
				//ran
				ret[3] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getRange();
				//kin moku sui hi do
				ret[4] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityMetal;
				ret[5] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWood;
				ret[6] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWater;
				ret[7] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityFire;
				ret[8] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityEarth;
				//(part damage modifier * material damage)/((1/(part attack time modifier*material attack time)))
				numofparts++;
			} else System.out.println(s + " is a null part");
		}
		if (numofparts != 0) for (int x = 0; x < ret.length; x++) {
			ret[x] /= numofparts;
		}
		return ret;
	}

	private float[] fastStats(ItemStack is) {
		float[] ret = new float[9];
		int numofparts = 0;
		for (String s : getPartNames(is)) {
			if (getPart(is, s) != null) {
				PartData pd = getPart(is, s);
				if (TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
					System.out.println(TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()));
					System.out.println(MaterialsConfig.findMat(pd.getMat()));

					continue;
				}
				ret[0] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.mass;
				ret[1] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getDamageMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.damageOrSpringiness;
				//dur
				ret[2] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getDurabilityMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.durability;
				//ran
				ret[3] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getRange();
				//kin moku sui hi do
				ret[4] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityMetal;
				ret[5] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWood;
				ret[6] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityWater;
				ret[7] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityFire;
				ret[8] += TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getElementalMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.affinityEarth;
				//(part damage modifier * material damage)/((1/(part attack time modifier*material attack time)))
				numofparts++;
			} else System.out.println("null part");
		}
		if (numofparts != 0) for (int x = 0; x < ret.length; x++) {
			ret[x] /= numofparts;
		}
		return ret;
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

	//	public boolean isValidAddition(ItemStack base, ItemStack input){
	//		String[] parts = new String[4];
	//		
	//		for (PartData pd : getParts(input).values()) {
	//			String i=pd.getMat();
	//			if (i == null) continue;
	//			//if there is no overlap and each requirement is met then continue, else flash red and return false;
	//			if (parts[(WeaponConfig.read(i)[0])] == null) {
	//				parts[(WeaponConfig.read(i)[0])] = TaoConfigs.bowc.lookup(i).getName();
	//			} else {
	//				System.out.println("forgot");
	//				return false;
	//			}
	//		}
	//		ArrayList<String> perks = new ArrayList<String>();
	//		for (int x = 0; x < 2; x++)
	//			for (String s : parts) {
	//				WeaponStatWrapper wsw = TaoConfigs.bowc.lookup(s);
	//				if (wsw == null){
	//					System.out.println("proceeding");
	//					continue;//can throw null
	//				}
	//				if (x == 0) for (WeaponPerk wp : wsw.getPerks())
	//					if (wp != null) perks.add(wp.name);
	//					else {
	//						for (String bl : wsw.getBlacklist())
	//							if (perks.contains(bl)&&!bl.isEmpty()){
	//								System.out.println(bl+" is in the blacklist");
	//								return false;
	//							}
	//						for (String wl : wsw.getWhitelist())
	//							if (!perks.contains(wl)&&!wl.isEmpty()){
	//								System.out.println(wl+" is not in the whitelist");
	//								return false;
	//							}
	//					}
	//			}
	//		System.out.println("success!");
	//		return true;
	//	}

	/*public static ItemStack createBow(EntityPlayer p, World w, ItemStack... inputs) {
		ItemStack ret = new ItemStack(TaoItems.bow);
		for (ItemStack i : inputs)
			if (i != null && i.getItem() == TaoItems.parts) {
				((TaoBow) ret.getItem()).setPart(TaoConfigs.bowc.lookupType(i.getItemDamage()), ret, new PartData(i));
				//System.out.println("parts found");
			}
		ret.getItem().onCreated(ret, w, p);//npe here
		return ret;
	}*/

	private NBTTagCompound getStorage(ItemStack is) {
		if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getCompoundTag("TaoistParts");
	}

	private float spd(ItemStack is) {
		float ret = 0;
		String s = StaticRefs.STAVE;
		if (getPart(is, s) != null) {
			PartData pd = getPart(is, s);
			if (TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()) == null || MaterialsConfig.findMat(pd.getMat()) == null) {
				System.out.println("null stave like seriously this shouldn't even exist");
			}
			//System.out.println(TaoConfigs.bowc.lookup(pd.getPart(), pd.getDam()));
			//System.out.println(MaterialsConfig.findMat(pd.getMat()));//null!
			ret = TaoConfigs.bowc.lookup(pd.getPart(), pd.getOrdinal()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).msw.drawSpeed;
			//System.out.println(s + "   "+TaoConfigs.bowc.lookup(pd.getDam()).getSpeedMultiplier() * MaterialsConfig.findMat(pd.getMat()).mass);
		} else System.out.println("null part like seriously this shouldn't even exist");
		//System.out.println(""+ret);
		//System.out.println(numofparts);
		//System.out.println(ret);
		return ret * 20;
	}
	
	private int magicness(ItemStack is) {
		int ret=1;
		Collection<PartData> s=getParts(is).values();
		for(PartData pd:s){
			ret+=MaterialsConfig.findMat(pd.getMat()).msw.lingAbility;
		}
		ret/=s.size();
		return ret;
	}
	
	
	//HERE BE OVERRIDES
	
	private ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }

    protected boolean isArrow(ItemStack stack)
    {
        return stack.getItem() instanceof ItemArrow;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
    {
        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            boolean ghostArrow = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack ammo = this.findAmmo(entityplayer);

            int charge = this.getMaxItemUseDuration(stack) - timeLeft;
            charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, charge, !ammo.isEmpty() || ghostArrow);
            if (charge < 0){
            	//System.out.println("no charge");
            	return;
            }

            if (!ammo.isEmpty() || ghostArrow)
            {
                if (ammo.isEmpty())
                {
                    ammo = new ItemStack(Items.ARROW);
                }

                float velocity = getArrowVelocity(charge, stack);
                //System.out.println("vel check");
                if ((double)velocity >= 0.1D)
                {
                	//System.out.println("no vel");
                    boolean isInfinite = entityplayer.capabilities.isCreativeMode || (ammo.getItem() instanceof ItemArrow && ((ItemArrow) ammo.getItem()).isInfinite(ammo, stack, entityplayer));

                    if (!worldIn.isRemote)
                    {
                        ItemArrow itemarrow = (ItemArrow)(ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, ammo, entityplayer);
                        entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, velocity * 3.0F, 1.0F);

                        if (velocity == 1.0F)
                        {
                            entityarrow.setIsCritical(true);
                        }

                        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

                        if (power > 0)
                        {
                            entityarrow.setDamage(entityarrow.getDamage() + (double)power * 0.5D + 0.5D);
                        }

                        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

                        if (punch > 0)
                        {
                            entityarrow.setKnockbackStrength(punch);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
                        {
                            entityarrow.setFire(100);
                        }

                        stack.damageItem(1, entityplayer);

                        if (isInfinite || entityplayer.capabilities.isCreativeMode && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW))
                        {
                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                        }

                        worldIn.spawnEntity(entityarrow);
                    }

                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

                    if (!isInfinite && !entityplayer.capabilities.isCreativeMode)
                    {
                        ammo.shrink(1);

                        if (ammo.isEmpty())
                        {
                            entityplayer.inventory.deleteStack(ammo);
                        }
                    }

                    entityplayer.addStat(StatList.getObjectUseStats(this));
                }
            }
        }
    }

    /**
     * Gets the velocity of the arrow entity from the bow's charge
     */
    public float getArrowVelocity(int charge, ItemStack stack)
    {
        float secscharged = (float)charge;
        secscharged = secscharged/(float)spd(stack);

        if (secscharged > 1.0F)
        {
            secscharged = 1.0F;
        }
       // System.out.println(secscharged);
        return secscharged*arrspd(stack);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        boolean hasAmmo = !this.findAmmo(playerIn).isEmpty();

        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, hasAmmo);
        if (ret != null) return ret;

        if (!playerIn.capabilities.isCreativeMode && !hasAmmo)
        {
            return hasAmmo ? new ActionResult(EnumActionResult.PASS, itemstack) : new ActionResult(EnumActionResult.FAIL, itemstack);
        }
        else
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability(ItemStack is)
    {
        return magicness(is);
    }

}