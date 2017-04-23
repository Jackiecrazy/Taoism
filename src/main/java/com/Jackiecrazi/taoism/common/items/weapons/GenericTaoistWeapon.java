package com.Jackiecrazi.taoism.common.items.weapons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.WayofConfig;
import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheDamageTypes.DamageElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ICustomRange;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ILianQiMaterial;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IModularWeapon;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingSpeed;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ISwingState;
import com.Jackiecrazi.taoism.common.items.ItemWeaponPart;
import com.Jackiecrazi.taoism.common.items.ModItems;
import com.Jackiecrazi.taoism.common.items.resource.ItemResource;
import com.Jackiecrazi.taoism.common.taoistichandlers.AnimationStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.PlayerResourceStalker;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;
import com.Jackiecrazi.taoism.networking.PacketExtendThyReach;
import com.Jackiecrazi.taoism.networking.PacketUpdateAttackTimer;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GenericTaoistWeapon extends ItemSword implements
ISwingState, ISwingSpeed, ICustomRange, IElemental, IModularWeapon {// IUltimate,
	protected String offensivebit;
	protected String texName;
	protected double atkDamBuf = 4;
	protected boolean isEdgy=false;
	protected ArrayList<String> parts=new ArrayList<String>();
	public static ArrayList<GenericTaoistWeapon> ListOfWeapons = new ArrayList<GenericTaoistWeapon>();
	public static ArrayList<Item> ListOfLoot = new ArrayList<Item>();
	public static HashMap<ArrayList<String>,GenericTaoistWeapon> partsOfWeapon=new HashMap<ArrayList<String>,GenericTaoistWeapon>();
	private IIcon[] icons; 
	private float range,hungerUsed;
	private int swingspd;
	public final static UUID MODIFIER_UUID = UUID.fromString("294093da-54f0-4c1b-9dbb-13b77534a84c");
	
	/**
	 * 
	 * @param tmat
	 * @param name
	 * @param range
	 * @param hunger
	 * @param spd
	 * @param edge
	 * @param partnames
	 */
	public GenericTaoistWeapon(ToolMaterial tmat, String name,float range, float hunger, int spd,String edge,String... partnames) {
		super(tmat);
		this.setUnlocalizedName(name+tmat.toString().toLowerCase());
		this.setTextureName("Taoism:yj");
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		//String nam = getRealName(this.getUnlocalizedName());
		if (!ListOfWeapons.contains(this))
			ListOfWeapons.add(this);
		if (tmat == ModItems.tawood)
			ListOfLoot.add(this);
		setPointyBit(StaticRefs.DEFOFF);//edge
		setParts(StaticRefs.DEFOFF,StaticRefs.GUARD,StaticRefs.HANDLE,StaticRefs.FANCY);//partnames
		
		this.range=range;
		hungerUsed=hunger;
		swingspd=spd;
		this.setMaxDamage(0);
		Collections.sort(parts);
		partsOfWeapon.put(parts, this);
	}
	/**
	 * Identical to the one without a boolean, but for things that don't have guards
	 */
	public GenericTaoistWeapon(boolean simple,ToolMaterial tmat, String name,float range, float hunger, int spd,String edge,String... partnames) {
		super(tmat);
		this.setUnlocalizedName(name+tmat.toString().toLowerCase());
		this.setTextureName("Taoism:yj");
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		//String nam = getRealName(this.getUnlocalizedName());
		if (!ListOfWeapons.contains(this))
			ListOfWeapons.add(this);
		if (tmat == ModItems.tawood)
			ListOfLoot.add(this);
		setPointyBit(StaticRefs.DEFOFF);//edge
		setParts(StaticRefs.DEFOFF,StaticRefs.HANDLE,StaticRefs.FANCY);//partnames
		
		this.range=range;
		hungerUsed=hunger;
		swingspd=spd;
		this.setMaxDamage(0);
		Collections.sort(parts);
		partsOfWeapon.put(parts, this);
	}
	
	
	protected void setPointyBit(String s){
		this.offensivebit=s;
	}

	protected void setParts(String... parts){
		for(String a:parts){
			this.parts.add(a);
		}
	}
	public static String getRealName(String fake) {
		return fake.replace("tawood", "").replace("tairon", "")
				.replace("tagem", "").replace("item.", "");
	}
	
	public ArrayList<String> getParts(){
		return this.parts;
	}
	
	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
			Entity entity) {
		AnimationStalker.getThis(player).updateAnimation(true, false, stack,
				(player.isAirBorne ? 2 : player.isSneaking() ? 1 : 0),
				player.inventory.currentItem);
		Taoism.net.sendToServer(new PacketExtendThyReach(entity.getEntityId(),true));
		//System.out.println(getParts(stack).toString());
		//System.out.println(getParts());
		return true;//super.onLeftClickEntity(stack, player, entity);
	}
	
	/**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    public boolean hitEntity(ItemStack is, EntityLivingBase uke, EntityLivingBase seme)
    {
    	if(sourceFromEdge(is,seme)!=null)
    	uke.attackEntityFrom(this.sourceFromEdge(is,seme), (float) seme.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
    	return true;
    }

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World w,
			EntityPlayer player, int i) {
			AnimationStalker.getThis(player).updateAnimation(
					true,
					true,
					stack,
					(!player.onGround ? 2 : player.isSneaking() ? 1
							: 0), player.inventory.currentItem);

		// TODO this is going under renovation ain't it?
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers();
		multimap.put(SharedMonsterAttributes.attackDamage
				.getAttributeUnlocalizedName(), new AttributeModifier(
						field_111210_e, "Weapon modifier", (double) this.atkDamBuf, 0));
		// TODO lots to do here. Modify item damage based on material, for one. Some weapons should give you buffs in other things too
		return multimap;
	}

	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return new EntityItem(world, location.posX,location.posY,location.posZ, itemstack);
	}

	public boolean hasCustomEntity(ItemStack stack) {
		return false;
	}
	
	@Override
	public int getEntityLifespan(ItemStack itemStack, World world) {
		return Integer.MAX_VALUE;
	}

	@Override
	public void saveState(NBTTagCompound tag) {

	}

	@Override
	public void readState(NBTTagCompound tag) {

	}

	/*
	 * @Override public void onUltimate(EntityPlayer p) { int WGLevel =
	 * WuGongHandler.getThis(p).getLevel(); if (!(p.worldObj.getTotalWorldTime()
	 * - p.getEntityData().getInteger("HissatsuTimeStart") > this
	 * .getUltimateTime() + this.getCDTime() &&
	 * PlayerResourceStalker.get(p).subtractValues( WayofConfig.QiDWID,
	 * this.getUltimateCost()) && WGLevel > 20 &&
	 * QiLiHandler.getThis(p).getSkillAwesomeness( "Ultimate" +
	 * getRealName(this.getUnlocalizedName())) > 0)) { return; }
	 * p.getEntityData().setLong("HissatsuTimeStart",
	 * p.worldObj.getTotalWorldTime());
	 * p.getEntityData().setInteger("TaoistUltimateSlot",
	 * p.inventory.currentItem); p.getEntityData()
	 * .setInteger("HissatsuDuration", this.getUltimateTime()); }
	 * 
	 * @Override public void onUltimateEnd(EntityPlayer p) {
	 * 
	 * }
	 * 
	 * @Override public void onUltimateTick(EntityPlayer p) { if
	 * (p.inventory.currentItem != p.getEntityData().getInteger(
	 * "TaoistUltimateSlot")) { p.inventory.currentItem =
	 * p.getEntityData().getInteger( "TaoistUltimateSlot"); } }
	 */

	public boolean onEntitySwing(EntityLivingBase el, ItemStack stack) {
		/*
		 * if(entityLiving.attackTime!=0){ entityLiving.swingProgressInt=0;
		 * entityLiving.swingProgress=0F; entityLiving.isSwingInProgress=false;
		 * return true; } else return false;
		 */
		if (!el.worldObj.isRemote && el instanceof EntityPlayer)
			Taoism.net.sendToAllAround(new PacketUpdateAttackTimer(
					((ISwingSpeed) this).swingSpd()), new TargetPoint(
							el.dimension, el.posX, el.posY, el.posZ, 64));
		return false;
	}

	public void doSwingItem(ItemStack stack, EntityPlayer entity) {
		if (entity.worldObj.isRemote) {
			entity.isSwingInProgress = true;
			entity.swingProgressInt = 0;
		} else {
			entity.swingItem();
		}
	}

	/*
	 * public EnumAction getItemUseAction(ItemStack p_77661_1_) { return
	 * EnumAction.none; }
	 */

	public void onUpdate(ItemStack is, World w, Entity e, int idk, boolean sth) {
		if (e instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) e;
			if (XiuWeiHandler.getThis(p).getSkillAwesomeness(Skill.WMNAME) > 0
					&& p.ticksExisted % 40 == 0&&PlayerResourceStalker.get(p).subtractValues(WayofConfig.LingLiDWID, 0.1f)) {
				is.damageItem(-(XiuWeiHandler.getThis(p)
						.getSkillAwesomeness(Skill.WMNAME)), p);
			}
		}
	}

	protected NBTTagCompound getAffinityTag(ItemStack is) {
		if (!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		return is.getTagCompound().getCompoundTag("TaoistAffinity");
	}
	
	//TODO fix this
	private DamageSource sourceFromEdge(ItemStack edgy,EntityLivingBase seme){
		if(this.getBase(edgy, offensivebit)==null)return null;
		ItemStack is= this.getBase(edgy, offensivebit);
		int[] aff=this.getAffinities(is); //TODO sort for largest mat
		int fintype=-1;
		int fin=0;
		for(int x=0;x<aff.length;x++){
			if(fin<aff[x]){
				fin=aff[x];
				fintype=x;
			}
		}
		return fintype==-1? seme instanceof EntityPlayer?DamageSource.causePlayerDamage((EntityPlayer)seme):DamageSource.causeMobDamage(seme):DamageElemental.causeElementDirectly(seme, DamageElemental.TaoistElement.values()[fintype]);
		//return a damage source based on material
		//if(is!=null &&is.getItem() instanceof IElemental)
	}

	@Override
	public int[] getAffinities(ItemStack is) {
		int[] ret = new int[10];
		for (int x = 0; x < ret.length; x++) {
			ret[x] = getAffinity(is, x);
		}
		return ret;
	}

	@Override
	public void setAffinities(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinity(is, 0, metal);
		setAffinity(is, 1, wood);
		setAffinity(is, 2, water);
		setAffinity(is, 3, fire);
		setAffinity(is, 4, earth);
	}

	@Override
	public void setAffinity(ItemStack is, int element, int affinity) {
		getAffinityTag(is).setInteger(element + "", affinity);
	}

	@Override
	public int getAffinity(ItemStack is, int element) {
		return getAffinityTag(is).getInteger(element + "");
	}

	@Override
	public void addAffinity(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		setAffinities(is, getAffinity(is, 0) + metal,
				getAffinity(is, 1) + wood, getAffinity(is, 2) + water,
				getAffinity(is, 3) + fire, getAffinity(is, 4) + earth);
	}

	/**
	 * returns a list of parts, as well as a list of additions in the format "additions"+number
	 */
	@Override
	public HashMap<String, ItemStack> getParts(ItemStack is) {
		HashMap<String, ItemStack> ret = new HashMap<String, ItemStack>();
		try{
		if (is.hasTagCompound()) {
			NBTTagCompound tag = is.getTagCompound().getCompoundTag(
					"TaoistParts");
			for (String key : parts) {
				ret.put(key, ItemStack.loadItemStackFromNBT(tag.getCompoundTag(key)));
			}
		}
		for(int x=0;x<is.getTagCompound().getTagList("TaoAdd", NBT.TAG_COMPOUND).tagCount();x++){
		ret.put("additions"+x, ItemStack.loadItemStackFromNBT(is.getTagCompound().getTagList("TaoAdd", NBT.TAG_COMPOUND).getCompoundTagAt(x)));
		}
		}
		catch(Exception e){
			
		}
		return ret;
	}

	@Deprecated
	public HashMap<String, HashMap<String, ItemStack>> getPartsOfParts(ItemStack is) {
		HashMap<String, HashMap<String, ItemStack>> ret = new HashMap<String, HashMap<String, ItemStack>>();
		if (is.hasTagCompound()) {
			NBTTagCompound tag = is.getTagCompound().getCompoundTag(
					"TaoistParts");
			
			for (String key : parts) {
				NBTTagList part=tag.getTagList(key, Constants.NBT.TAG_COMPOUND);
				HashMap<String, ItemStack> child = new HashMap<String, ItemStack>();
				// NOTICE CAN THROW NULLPOINTEREXCEPTION! Fix plz
				ItemStack iwp=ItemStack.loadItemStackFromNBT(part.getCompoundTagAt(0));
				child.put("base", ((ItemWeaponPart)iwp.getItem()).getParts(iwp)[0]);
				for (int x = 1; x < ((ItemWeaponPart)iwp.getItem()).getParts(iwp).length; x++) {
					child.put("module" + x, ((ItemWeaponPart)iwp.getItem()).getParts(iwp)[x]);
				}

				ret.put(key, child);
			}
			
		}
		//System.out.println(ret.get(StaticRefs.EDGE));
		return ret;
	}
	
	/**
	 * A more efficient way of getting the base, when the others aren't important
	 * @param base
	 * @param partName
	 * @return
	 */
	public ItemStack getBase(ItemStack is,String partName){
		ItemStack ret=null;
		if (is.hasTagCompound()) {
			NBTTagCompound tag = is.getTagCompound().getCompoundTag(
					"TaoistParts");
				NBTTagList part=tag.getTagList(partName, Constants.NBT.TAG_COMPOUND);
				// NOTICE CAN THROW NULLPOINTEREXCEPTION! Fix plz
				ret=ItemStack.loadItemStackFromNBT(part.getCompoundTagAt(0));
				
		}
		//System.out.println(ret.get(StaticRefs.EDGE));
		return ret;
	}

	public void setPartOfPart(String partName, ItemStack is, ItemStack addition) {
		/*//get part, then check if the base is of damage 8000 or 9000 and if so set the part instead.
		
		//hardcoded avoidance of a wooden edge
		if(is==null||addition==null||partName==null||!(addition.getItem() instanceof ILianQiMaterial))return;
		if(partName.equals(StaticRefs.SWORDBLADE)&&!((ILianQiMaterial)addition.getItem()).isMetal(addition.getItemDamage()))return;
		
		if (!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		
		//this is everything
		NBTTagCompound allParts = is.getTagCompound().getCompoundTag(
				"TaoistParts");
		if (getParts().contains(partName)) {
			
			//this the item that lays inside everything
			NBTTagList mod = allParts.getTagList(partName,Constants.NBT.TAG_COMPOUND);
			
			//this is the actual thing that this part embodies
			//ItemStack agh=ItemStack.loadItemStackFromNBT(mod);
			
			//and this is the list that it embodies
			//NBTTagList module=mod.getTagList("TaoistParts", Constants.NBT.TAG_COMPOUND);
			NBTTagCompound orig=mod.getCompoundTagAt(0);
			ItemStack part=ItemStack.loadItemStackFromNBT(orig);
			((ItemWeaponPart)part.getItem()).setPart(part, addition);
			
			mod.func_150304_a(0, part.writeToNBT(new NBTTagCompound()));
			allParts.setTag(partName, mod);
			is.getTagCompound().setTag("TaoistParts", allParts);
		}*/
		setPart(partName,is,addition);
	}
	
	/**
	 * Stores a part with an itemstack. If the part is already taken or is invalid, it is rerouted to setting a generic set for the tool.
	 */
	public boolean setPart(String partName, ItemStack is, ItemStack addition) {
		ItemStack aa=addition;
		if(!(addition.getItem() instanceof ILianQiMaterial)){
			return false;
		}
		if (!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		NBTTagCompound allParts = is.getTagCompound().getCompoundTag(
				"TaoistParts");
		if((allParts.getTag(partName)!=null
				&&ItemStack.loadItemStackFromNBT((NBTTagCompound)allParts.getTag(partName)).getItemDamage()!=9000
				&&ItemStack.loadItemStackFromNBT((NBTTagCompound)allParts.getTag(partName)).getItemDamage()!=8000)||partName==StaticRefs.FANCY){
			System.out.println("redirecting to additions");
			System.out.println(ItemStack.loadItemStackFromNBT((NBTTagCompound)allParts.getTag(partName)).getItemDamage());
			return setAdditions(is,addition);
		}
		else
			allParts.setTag(partName, aa.writeToNBT(new NBTTagCompound()));
		
		is.getTagCompound().setTag("TaoistParts", allParts);
		return true;
	}
	
	public boolean setAdditions(ItemStack is, ItemStack addition){
		if(!(addition.getItem() instanceof ILianQiMaterial)){
			return false;
		}
		if (!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		NBTTagList all = is.getTagCompound().getTagList("TaoAdd", NBT.TAG_COMPOUND);
		
		all.appendTag(addition.writeToNBT(new NBTTagCompound()));
		is.getTagCompound().setTag("TaoAdd", all);
		return true;
	}
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{/*if(!getParts(stack).isEmpty())
		for(Map.Entry<String, ItemStack> entry:getParts(stack).entrySet())
			
			list.add(
					entry.getKey()
					+": "
							+entry.getValue());*/
	}
	
	public int getRenderPasses(int metadata)
    {
		return this.parts.size();
    }
	@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIcon(stack, renderPass);
    }
	@SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int dam, int pass)
    {
        return icons[
                     (Arrays.asList(ItemWeaponPart.types)
                    		 .indexOf
                    		 (parts.get(pass)))];
    }
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister c)
    {
		icons=new IIcon[ItemWeaponPart.types.length];
		for(int x=0;x<icons.length;x++){
			//TextureMap.class
			if(this.parts.contains(ItemWeaponPart.types[x])&&!ItemWeaponPart.types[x].equals(StaticRefs.FANCY))
				
			icons[x]=c.registerIcon("taoism:weapon/"+getRealName(getUnlocalizedName())+ItemWeaponPart.types[x]);
			if(ItemWeaponPart.types[x].equals(StaticRefs.FANCY))icons[x]=c.registerIcon("taoism:transparent");
		}
        this.itemIcon = c.registerIcon(this.getIconString());
    }
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack is, int pass)
    {
		ItemStack rend=this.getParts(is).get(parts.get(pass));
		if(rend==null)return super.getColorFromItemStack(is, pass);
		//return 0;
		return ((ItemResource)rend.getItem()).getColorFromIS(rend, pass) ;
		//return ((ItemWeaponPart)rend.getItem()).getColorFromItemStack(rend, pass);
    }

	@Override
	public float getRange(EntityPlayer p, ItemStack is) {
		return range;
	}

	@Override
	public float hungerUsed() {
		return hungerUsed;
	}

	@Override
	public int swingSpd() {
		return swingspd;
	}
	
	public void onCreated(ItemStack is, World w, EntityPlayer p) {
		upgradeSword(is,getBase(is,offensivebit).getItemDamage());
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String out = "";
		try{
		out+= StatCollector.translateToLocal(getParts(stack).get(offensivebit).getUnlocalizedName());
		}
		catch(NullPointerException e){
			//out+=StatCollector.translateToLocal("mysterymaterial");
		}
		out += StatCollector.translateToLocal((getUnlocalizedName()+".name"));
		
		return out;
	}
	
	private int calcDealtDamage(ItemStack is){
		
		return 0;
	}
	
	private int calcMaxDamage(ItemStack is){
		
		return 0;
	}
	
	/**
	 * 
	 * @param item this better be an instance of itemsword
	 * @param plus addition
	 * @return the original itemstack
	 */
	public static ItemStack upgradedSword(ItemStack is,int plus){
		float swordDamage = 4 + ((ItemSword)is.getItem()).func_150931_i();
		AttributeModifier attackModifier = new AttributeModifier(MODIFIER_UUID, "Weapon Upgrade", plus + swordDamage, 0);
		NBTTagCompound modifierNBT = writeAttributeModifierToNBT(SharedMonsterAttributes.attackDamage, attackModifier);

		// Create the NBT structure needed by ItemStack#getAttributeModifiers
		NBTTagCompound stackTagCompound = is.getTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(modifierNBT);
		stackTagCompound.setTag("AttributeModifiers", list);

		// Create an ItemStack of the Item
		ItemStack stack = is.copy();

		// Set the stack's NBT to the modifier structure
		stack.setTagCompound(stackTagCompound);

		return stack;
	}
	
	public static void upgradeSword(ItemStack is,int plus){
		float swordDamage = 4 + ((ItemSword)is.getItem()).func_150931_i();
		AttributeModifier attackModifier = new AttributeModifier(MODIFIER_UUID, "Weapon Upgrade", plus + swordDamage, 0);
		NBTTagCompound modifierNBT = writeAttributeModifierToNBT(SharedMonsterAttributes.attackDamage, attackModifier);

		// Create the NBT structure needed by ItemStack#getAttributeModifiers
		NBTTagCompound stackTagCompound = is.getTagCompound();
		NBTTagList list = new NBTTagList();
		list.appendTag(modifierNBT);
		stackTagCompound.setTag("AttributeModifiers", list);

		// Set the stack's NBT to the modifier structure
		is.setTagCompound(stackTagCompound);
	}
	
	private static NBTTagCompound writeAttributeModifierToNBT(IAttribute attribute, AttributeModifier modifier) {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setString("AttributeName", attribute.getAttributeUnlocalizedName());
		nbttagcompound.setString("Name", modifier.getName());
		nbttagcompound.setDouble("Amount", modifier.getAmount());
		nbttagcompound.setInteger("Operation", modifier.getOperation());
		nbttagcompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
		nbttagcompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
		return nbttagcompound;
	}
	
	
}
