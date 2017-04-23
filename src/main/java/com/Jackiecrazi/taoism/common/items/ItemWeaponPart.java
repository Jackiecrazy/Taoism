package com.Jackiecrazi.taoism.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.Constants;

import com.Jackiecrazi.taoism.api.StaticRefs;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IElemental;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IModular;
import com.Jackiecrazi.taoism.common.items.resource.ItemResource;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWeaponPart extends Item implements IElemental, IModular {
	public IIcon[] icon;
	public static final String[] types = {StaticRefs.DEFOFF,StaticRefs.HANDLE,StaticRefs.GUARD,StaticRefs.FANCY};
	/*StaticRefs.CROWN,
	StaticRefs.DAOBLADE, StaticRefs.GUARD, StaticRefs.HANDLE,
	StaticRefs.HEAD, StaticRefs.KNOTS, StaticRefs.LOOP,
	StaticRefs.POMMEL, StaticRefs.PRONGS, StaticRefs.SHAFT,
	StaticRefs.SWORDBLADE, StaticRefs.TIP,StaticRefs.EDGE
	,StaticRefs.DEFOFF*/

	public ItemWeaponPart() {
		this.setHasSubtypes(true);
		this.setUnlocalizedName("weaponpart");
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs xCreativeTabs, List list) {
		/*for (int x = 0; x < types.length; x++) {
			ItemStack i=new ItemStack(this,1,x);
			i.setTagCompound(new NBTTagCompound());
			for(int a=0;a<ItemResource.types.length;a++){
				if(ItemResource.isMetalSta(a*1000))
					this.setPart(i, new ItemStack(ModItems.Resources,1,a*1000));
				list.add(i);
			}
		}*/
	}

	@Override
	public int[] getAffinities(ItemStack is) {
		return null;
	}

	@Override
	public void setAffinities(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAffinity(ItemStack is, int element, int affinity) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getAffinity(ItemStack is, int element) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addAffinity(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		// TODO Auto-generated method stub

	}

	/**
	 * Do realize that element 0 is the base.
	 * @see com.Jackiecrazi.taoism.api.allTheInterfaces.IModular#getParts(net.minecraft.item.ItemStack)
	 */
	@Override
	public ItemStack[] getParts(ItemStack is) {
		int x = 0;
		if (is.hasTagCompound()) {

			NBTTagList tag = is.getTagCompound().getTagList("TaoistParts",Constants.NBT.TAG_COMPOUND);
			x = tag.tagCount();
			//System.out.println(is.getUnlocalizedName()+" "+x);
		}
		ItemStack[] ret = new ItemStack[x];
		if(is.hasTagCompound()){
			NBTTagList tag = is.getTagCompound().getTagList("TaoistParts",Constants.NBT.TAG_COMPOUND);
			for(int y=0;y<tag.tagCount();y++){
				//System.out.println("inserting tag number "+y+" into output");
				ret[y]=ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(y));
			}
		}
		/*if (is.hasTagCompound()) {
			NBTTagCompound tag = is.getTagCompound().getCompoundTag(
					"TaoistParts");
			for (int y = 0; y < tag.func_150296_c().size(); y++) {
				ret[y] = ItemStack.loadItemStackFromNBT(tag
						.getCompoundTag("part" + y));
			}
		}
		 */
		//System.out.println(ret[0].getItemDamage());
		return ret;
	}
	/**
	 * Be sure to set it so it only consumes one item in your classes.
	 */
	@Override
	public void setPart(ItemStack base, ItemStack addition) {
		if (!base.hasTagCompound())
			base.setTagCompound(new NBTTagCompound());
		NBTTagList tag = base.getTagCompound().getTagList("TaoistParts", Constants.NBT.TAG_COMPOUND);
		for (int x = 0; x < Integer.MAX_VALUE; x++) {
			if(tag.getCompoundTagAt(x).hasNoTags())break;
			if(x==0&&Math.floorDiv(ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(0)).getItemDamage(),1000)==9){
				this.setBase(base, addition);
				return;
			}
			if(ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(x)).getItemDamage()==addition.getItemDamage()){
				ItemStack is=ItemStack.loadItemStackFromNBT(tag.getCompoundTagAt(x));
				is.stackSize++;
				tag.func_150304_a(x, is.writeToNBT(new NBTTagCompound()));
				return;
			}
		}
		NBTTagCompound item=addition.writeToNBT(new NBTTagCompound());
		tag.appendTag(item);//this is gonna be 0 the first time round mmkay?
		base.getTagCompound().setTag("TaoistParts", tag);
		//the below does not work.

		/*for (int x = 0; x < Integer.MAX_VALUE; x++) {
			if (!tag.hasKey("part" + x)) {
				NBTTagCompound item = new NBTTagCompound();
				tag.setTag("part" + x, addition.writeToNBT(item));
				System.out.println("added part as part "+x);
				break;
			} else if (ItemStack.loadItemStackFromNBT(
					tag.getCompoundTag("part" + x)).getItem() == addition
					.getItem()) {
				System.out.println("increasing size of current part");
				addition.stackSize += ItemStack.loadItemStackFromNBT(tag
						.getCompoundTag("part" + x)).stackSize;
				NBTTagCompound item = new NBTTagCompound();
				tag.setTag("part" + x, addition.writeToNBT(item));
				break;
			}

		}*/
	}
	/**
	 * Forcibly sets the base of the weapon. Bridge method for anvils
	 * @param base
	 * @param addition
	 */
	public void setBase(ItemStack base, ItemStack addition){
		if (!base.hasTagCompound())
			base.setTagCompound(new NBTTagCompound());
		NBTTagList tag = base.getTagCompound().getTagList("TaoistParts", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound item=addition.writeToNBT(new NBTTagCompound());
		tag.func_150304_a(0, item);
		//tag.appendTag(item);//this is gonna be 0 the first time round mmkay?
		base.getTagCompound().setTag("TaoistParts", tag);
	}
	
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister ir)
	{
		icon = new IIcon[types.length];
		for(int x=0;x< types.length;x++)
			icon[x]=ir.registerIcon("taoism:weaponparts/part_"+types[x].toLowerCase()+"_item");

	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName() + "." + types[stack.getItemDamage()%types.length];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int meta) {
		
		return this.icon[meta%icon.length];
	}
	
	@SuppressWarnings({ "unchecked"})
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		for(ItemStack i:getParts(stack))
			list.add(i.getItem().getUnlocalizedName(i)+": "+i.stackSize);
	}
	public boolean acceptsWood(int meta){
		return meta!=12;
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int i)
	{
		if(this.getParts(is).length==0){
			return super.getColorFromItemStack(is, i);
		}
		return ((ItemResource)getParts(is)[0].getItem()).getColorFromIS(getParts(is)[0], i) ;//ItemResource.getColorFromIS(this.getParts(is)[0], i);
	}

}
