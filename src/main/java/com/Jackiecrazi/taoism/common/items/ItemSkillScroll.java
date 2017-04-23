package com.Jackiecrazi.taoism.common.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.Skill;
import com.Jackiecrazi.taoism.common.taoistichandlers.skillHandlers.qiLi.XiuWeiHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSkillScroll extends Item {
	private final static String name="name";
	public static ArrayList<String> types = new ArrayList<String>();
	private static final Random r=new Random();
	public IIcon iconcommon,iconuncommon,iconrare,iconepic;
	public IIcon[] icons=new IIcon[4];
	public ItemSkillScroll() {
		//types.add("dummy");
		this.setCreativeTab(Taoism.TabTaoistWeapon);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("TaoisticScroll");
		this.setMaxStackSize(1);
		this.setFull3D();
		Iterator<String> scrolls = Skill.getAllSkillNames().iterator();
		while (scrolls.hasNext()){
			//System.out.println("found another scroll");
			types.add(scrolls.next());
			
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs xCreativeTabs, List list) {
		ItemStack scroll;
		for (int x = 0; x < types.size(); x++) {
			scroll=new ItemStack(ModItems.Scroll);
			scroll.setTagCompound(new NBTTagCompound());
			scroll.stackTagCompound.setString(name, types.get(x));
			list.add(scroll);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list,
			boolean par4) {
		list.add(EnumChatFormatting.GOLD
				+ StatCollector.translateToLocal("suffix.scroll"
						+ getName(stack) + ".1"));
		list.add(EnumChatFormatting.GOLD
				+ StatCollector.translateToLocal("suffix.scroll"
						+ getName(stack) + ".2"));
		list.add(getName(stack));
	}
@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String out = "";
		// prefix
		out += StatCollector.translateToLocal("preprefix.scroll");
		out += Boolean.valueOf(StatCollector
				.translateToLocal("scroll.namefirst")) ? StatCollector
						.translateToLocal(getName(stack) == "" ? "???"
								: getName(stack))
				: "";
		out += StatCollector.translateToLocal("sufprefix.scroll");
		// actual name
		out += StatCollector.translateToLocal("scroll."+this.getRarity(stack));
		// suffix
		out += StatCollector.translateToLocal("presuffix.scroll");
		out += Boolean.valueOf(StatCollector
				.translateToLocal("scroll.namelast")) ? StatCollector
				.translateToLocal(getName(stack) == "" ? "???"
						: getName(stack))
				: "";
		out += StatCollector.translateToLocal("sufsuffix.scroll");
		return out;
	}
	public static int rarity(ItemStack i){
		int ret=0;
		if(getName(i).contains("Ultimate"))ret+=2;
		if(getName(i).contains(Skill.APNAME))ret=2;
		if(getName(i).contains(Skill.WMNAME))ret=3;
		return ret;
	}
	public EnumRarity getRarity(ItemStack itemstack) {
		EnumRarity answer;
		switch(rarity(itemstack)){
		
		case 1:answer=EnumRarity.uncommon;break;
		case 2:answer=EnumRarity.rare;break;
		case 3:answer=EnumRarity.epic;break;
		default:answer=EnumRarity.common;
		}
		return answer;
		//return rarity(itemstack.getItemDamage())?EnumRarity.rare: EnumRarity.common;
	}
	public int getMaxItemUseDuration(ItemStack s)
    {
        return (rarity(s)*60+60);
    }
	public boolean hasCustomEntity(ItemStack stack)
    {
        return false;
    }
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
    {
        return null;
    }
	public ItemStack onItemRightClick(ItemStack is, World w, EntityPlayer p)
    {
		if(!XiuWeiHandler.getThis(p).isSkillMaxed(getName(is))){
			p.setItemInUse(is, this.getMaxItemUseDuration(is));
			//System.out.println("validated");
		}
		else if(w.isRemote){
			ChatComponentText component = new ChatComponentText(StatCollector.translateToLocal("learnSkillPointless"));
			p.addChatComponentMessage(component);
		}
		
        return is;
    }
	

    public ItemStack onEaten(ItemStack is, World w, EntityPlayer p)
    {
    	if(XiuWeiHandler.getThis(p).getSkillAwesomeness(getName(is))<Skill.skillNamesAndMax.get(getName(is))){
    		int coolness = XiuWeiHandler.getThis(p).getSkillAwesomeness(getName(is))+1;
			XiuWeiHandler.getThis(p).setSkillAwesomeness(getName(is), coolness);
    		if(w.isRemote){
    		ChatComponentText component = new ChatComponentText(StatCollector.translateToLocal("learnSkillPrefix")+StatCollector.translateToLocal(getName(is))+StatCollector.translateToLocal("learnSkillSuffix")+coolness+StatCollector.translateToLocal("learnSkillSufSuffix"));
			p.addChatComponentMessage(component);
    		}
    		if(w.rand.nextInt(10)>XiuWeiHandler.getThis(p).getSkillAwesomeness("ArtifactPreservation")&&!p.capabilities.isCreativeMode){
        		p.destroyCurrentEquippedItem();
        	}
    	}
        return is;
    }

	
    public void onPlayerStoppedUsing(ItemStack is, World w, EntityPlayer p, int usedur) {
    	float thoroughness=usedur/this.getMaxItemUseDuration(is);
    	if(p.capabilities.isCreativeMode)thoroughness=2f;
    	if(w.rand.nextFloat()<thoroughness){
    		int inc=XiuWeiHandler.getThis(p).getSkillAwesomeness(getName(is))+1;
    		Integer max = Skill.skillNamesAndMax.get(getName(is));
			if(inc>max)inc=max;
    		XiuWeiHandler.getThis(p).setSkillAwesomeness(getName(is), inc);
    		is.damageItem(1, p);
    		if(w.isRemote){
        		ChatComponentText component = new ChatComponentText(StatCollector.translateToLocal("learnSkillPrefix")+StatCollector.translateToLocal(getName(is))+StatCollector.translateToLocal("learnSkillSuffix")+inc+StatCollector.translateToLocal("learnSkillSufSuffix"));
    			p.addChatComponentMessage(component);
        		}
    	}
    	if(w.rand.nextInt(10)>XiuWeiHandler.getThis(p).getSkillAwesomeness("ArtifactPreservation")&&!p.capabilities.isCreativeMode){
    		p.destroyCurrentEquippedItem();
    	}
    }

	
    public EnumAction getItemUseAction(ItemStack p_77661_1_)
    {
        return EnumAction.block;
    }
  //TODO don't use any of these. use dur based on player and chance disintegrate on use
    /*

	public double getDurabilityForDisplay(ItemStack stack)
    {
    	//System.out.println((double)stack.getItemDamageForDisplay() / (double)stack.getMaxDamage());
        return (double)stack.getItemDamageForDisplay() / (double)stack.getMaxDamage();
    }
    
    public void onCreated(ItemStack i, World w, EntityPlayer p) {
    	getNBT(i).setInteger(dur, w.rand.nextInt(32));
    }*/

    private static NBTTagCompound getNBT(ItemStack i){
    	NBTTagCompound ret=i.getTagCompound();
    	if(ret==null){
    		ret=new NBTTagCompound();
    		i.setTagCompound(ret);
    	}
    	return i.getTagCompound();
    }
    
    private static String getName(ItemStack i){
    	return getNBT(i).getString(name);
    }
    private static void setNameRandomly(ItemStack i){
    	getNBT(i).setString(name, (String) Skill.skillNamesAndMax.keySet().toArray()[r.nextInt(Skill.skillNamesAndMax.size())]);
    }
    public int getMaxDamage(ItemStack stack)
    {
    	int ret=Skill.skillNamesAndMax.containsKey(getName(stack))?Skill.skillNamesAndMax.get(getName(stack)):1;
    	return ret;
    }
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }
    public void onUpdate(ItemStack is, World w, Entity e, int idk, boolean sth) {
    	if(!Skill.skillNamesAndMax.containsKey(getName(is))){
			setNameRandomly(is);
		}
		if(e instanceof EntityPlayer){
			EntityPlayer p=(EntityPlayer)e;
				int unproficiency=Skill.skillNamesAndMax.get(getName(is))-XiuWeiHandler.getThis(p).getSkillAwesomeness(getName(is));
				if(unproficiency!=is.getItemDamage())
				is.setItemDamage(unproficiency);
				//System.out.println(getName(is));
		}
		
		
	}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir)
    {
    	icons[0]=ir.registerIcon("taoism:ScrollBamboo");
    	icons[1]=ir.registerIcon("taoism:ScrollSteel");
    	icons[2]=ir.registerIcon("taoism:ScrollJade");
    	icons[3]=ir.registerIcon("taoism:ScrollSoul");
    	
    }
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack)
    {
    	//System.out.println("hi");
        return icons[rarity(stack)];
    }
}
