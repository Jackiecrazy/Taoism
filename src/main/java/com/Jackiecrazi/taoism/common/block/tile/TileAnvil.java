package com.Jackiecrazi.taoism.common.block.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import com.Jackiecrazi.taoism.api.allTheInterfaces.ILianQiMaterial;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IModular;
import com.Jackiecrazi.taoism.api.allTheInterfaces.IModularWeapon;
import com.Jackiecrazi.taoism.common.entity.EntityLevitatingItem;
import com.Jackiecrazi.taoism.common.items.ItemWeaponPart;
import com.Jackiecrazi.taoism.common.items.ModItems;
import com.Jackiecrazi.taoism.common.items.resource.ItemResource;
import com.Jackiecrazi.taoism.common.items.weapons.GenericTaoistWeapon;

public class TileAnvil extends TaoisticInvTE {
	private boolean isSmithing;
	private int index;
	public TileAnvil() {
		this.inv=new ItemStack[6];//leave one for edge, if any
	}
	public void setAnvilContent(ItemStack i,int slot){
		if(slot<inv.length){
			inv[slot]=i;
		}
	}
	public boolean setAnvilContent(ItemStack i){
		/*if(smithTool){
			for(int x=0;x<inv.length;x++){
				if(inv[x]==null){
					inv[x]=i;
					return true;
				}
				else if(inv[x].getItemDamage()==i.getItemDamage())return false;
			}
			return false;
		}
		else{
			inv[0]=i;
			return true;
		}*/
		inv[0]=i;
		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 1);
		return true;
	}
	public void dropContent(){
		ItemStack drop=null;
		if(inv[0]==null)return;
		if(isValidCombination()){
			GenericTaoistWeapon h=getResultantWeapon();
			drop=new ItemStack(h);
			for(int x=0;x<inv.length;x++){

				if(inv[x]!=null){
					if(inv[x].getItem() instanceof IModular){
						//System.out.println(inv[x].getUnlocalizedName());
						h.setPart(
								ItemWeaponPart.
								types[inv[x].
								      getItemDamage()], 
								      drop, 
								      inv[x]);
					}
				}

			}
		}
		else drop=inv[0];
		for(Entry<String,ItemStack> part:((GenericTaoistWeapon)drop.getItem()).getParts(drop).entrySet()){
			if(part.getValue().getItemDamage()==8000||part.getValue().getItemDamage()==9000)return;
		}
		
		if(!worldObj.isRemote)
			worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord, yCoord+1, zCoord, drop));

		reset();
	}
	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public String getInventoryName() {
		return "anvil";
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public void tick() {
		System.out.println();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack.getItem()==null?false:(stack.getItem() instanceof IModular||stack.getItem() instanceof ILianQiMaterial||stack.getItem()==ModItems.QiPu);
	}

	/*public boolean isSmithingTool() {
		return smithTool;
	}

	public void setIsSmithingTool(boolean smithTool) {
		this.smithTool = smithTool;
		if(smithTool){
			setIsSmithingPart(false);
			setHasTemplate(false);
		}
	}

	public boolean isSmithingPart() {
		return smithPart;
	}

	public void setIsSmithingPart(boolean smithPart) {
		this.smithPart = smithPart;
		if(smithPart){
			setIsSmithingTool(false);
			setHasTemplate(false);
		}
	}

	
	public void addToolMat(ItemStack addition){
		if(smithPart)
			((IModular)inv[0].getItem()).setPart(inv[0], addition);
	}
	public boolean hasTemplate() {
		return template;
	}
	public void setHasTemplate(boolean template) {
		this.template = template;
		if(template){
			setIsSmithingPart(false);
			setIsSmithingTool(false);
		}
	}
*/	
	public ItemStack getTool() {
		return inv[0];
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		if(inv[0]!=null)
		tag.setTag("weap", inv[0].writeToNBT(new NBTTagCompound()));
		/*tag.setBoolean("smithingPart", smithPart);
		tag.setBoolean("smithingTool", smithTool);
		tag.setBoolean("hasTemplate", template);*/
	}
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		if(tag.hasKey("weap"))
		inv[0]=ItemStack.loadItemStackFromNBT(tag.getCompoundTag("weap"));
		/*smithPart=tag.getBoolean("smithingPart");
		smithTool=tag.getBoolean("smithingTool");
		template=tag.getBoolean("hasTemplate");*/
	}
	private void reset(){
		/*smithPart=false;
		smithTool=false;
		template=false;*/
		for(int x=0;x<inv.length;x++)
			inv[x]=null;
	}
	@Override
	public void updateEntity() {
		super.updateEntity();

		boolean shouldReset=true;
		for(int x=0;x<inv.length;x++)
			if(inv[x]!=null)shouldReset=false;
		if(shouldReset)reset();

		/*for(int x=0;x<inv.length;x++)if(inv[x]!=null)
		System.out.println(inv[x].getUnlocalizedName());*/
		
		changeItemOrbit();
	}
	
	@SuppressWarnings("unchecked")
	public void changeItemOrbit(){//so it does not find ELI within the area on the server. Perhaps because the ELI only moves on the client?
		if(worldObj.isRemote)return;
		List<EntityLevitatingItem> ent=worldObj.getEntitiesWithinAABB(EntityLevitatingItem.class, AxisAlignedBB.getBoundingBox(xCoord-7,yCoord-3, zCoord-7, xCoord+7, yCoord+6, zCoord+7));
		//System.out.println(ent.toString());
		if(!ent.isEmpty()){//ent is empty on server at time
			//System.out.println(worldObj.isRemote);//is not remote
			Iterator<EntityLevitatingItem> i=ent.iterator();
			while(i.hasNext()){
				EntityLevitatingItem eli=i.next();
				//System.out.println("setting coords");
				eli.setXYZ(xCoord, yCoord, zCoord);
			}
		}
	}
	public boolean isValidCombination(){
		ArrayList<String> check=new ArrayList<String>();
		for(int x=0;x<inv.length;x++){
			if(inv[x]!=null&&inv[x].getItem() instanceof IModular)check.add(ItemWeaponPart.types[inv[x].getItemDamage()]);
		}
		//System.out.println(GenericTaoistWeapon.partsOfWeapon.toString());
		Collections.sort(check);
		return GenericTaoistWeapon.partsOfWeapon.containsKey(check);
	}
	private GenericTaoistWeapon getResultantWeapon(){
		ArrayList<String> check=new ArrayList<String>();
		for(int x=0;x<inv.length;x++){
			if(inv[x]!=null&&inv[x].getItem() instanceof IModular)check.add(ItemWeaponPart.types[inv[x].getItemDamage()]);
		}
		Collections.sort(check);
		return GenericTaoistWeapon.partsOfWeapon.get(check);
	}
	@SuppressWarnings("unchecked")
	public void requestFeeding(){
		//prioritize metal over wood, and finally quench
		List<EntityLevitatingItem> list=worldObj.getEntitiesWithinAABB(EntityLevitatingItem.class, AxisAlignedBB.getBoundingBox(this.xCoord-2.5, this.yCoord, this.zCoord-2.5, this.xCoord+2.5, this.yCoord+5, this.zCoord+2.5));
		if(!list.isEmpty()){
			for(EntityLevitatingItem eli:list){
				if(eli.getEntityItem().getItem() instanceof ILianQiMaterial&&this.isSmithing){
					ItemStack eaten=eli.getEntityItem();
					if(((ILianQiMaterial)eaten.getItem()).isMat(eaten)&&((ILianQiMaterial)eaten.getItem()).isMetal(eaten.getItemDamage())){
						eli.requestSacrifice();return;
					}
				}
			}
			for(EntityLevitatingItem eli:list){
				if(eli.getEntityItem().getItem() instanceof ILianQiMaterial&&this.isSmithing){
					ItemStack eaten=eli.getEntityItem();
					if(((ILianQiMaterial)eaten.getItem()).isMat(eaten)&&((ILianQiMaterial)eaten.getItem()).isWood(eaten.getItemDamage())){
						eli.requestSacrifice();return;
					}
				}
			}
			for(EntityLevitatingItem eli:list){
				if(eli.getEntityItem().getItem() instanceof ILianQiMaterial&&this.isSmithing){
					ItemStack eaten=eli.getEntityItem();
					if(((ILianQiMaterial)eaten.getItem()).isMat(eaten)&&((ILianQiMaterial)eaten.getItem()).isQuench(eaten.getItemDamage())){
						eli.requestSacrifice();return;
					}
				}
			}
		}

	}
	public void begin(){
		ItemStack is=new ItemStack(ModItems.ResourceMetal,1,9000);

		((GenericTaoistWeapon)inv[0].getItem()).setPart(((GenericTaoistWeapon)inv[0].getItem()).getParts().get(index), inv[0], is);
	}
	public void complete(){
		index++;
		if(index>=((GenericTaoistWeapon)inv[0].getItem()).getParts().size())dropContent();
		else{
			
			begin();
		}
	}
	public boolean isSmithing() {
		return isSmithing;
	}
	public void setSmithing(boolean isSmithing) {
		this.isSmithing = isSmithing;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean acceptsWood(){
		return index!=1;
	}
	@Override
	public void eatItem(ItemStack eaten) {
		if(eaten.getItem() instanceof ILianQiMaterial){
			if(((ILianQiMaterial)eaten.getItem()).isMat(eaten)){
				if(this.isSmithing()  &&  this.getTool()!=null  &&  this.getTool().getItem() instanceof GenericTaoistWeapon){
					//System.out.println("setting tool stuff");
					
					if( ((ItemResource)eaten.getItem()).isMetal(eaten.getItemDamage()) ||  ((ILianQiMaterial)eaten.getItem()).isWood(eaten.getItemDamage())&&this.acceptsWood()){// || (((GenericTaoistWeapon)this.getTool().getItem()).acceptsWood(this.getTool().getItemDamage()) ) ){
						ItemStack baseDam = ((GenericTaoistWeapon)this.getTool().getItem())
								.getParts(getTool()).get(((GenericTaoistWeapon) 
										getTool()			   
										.getItem())
										.getParts()//get all the part names
										.get(getIndex()));
						
						System.out.println(((GenericTaoistWeapon)this  //gets the index of the part
								.getTool()
								.getItem())
								.getParts().toString());
						
						System.out.println(baseDam.getUnlocalizedName());
						//if(((ILianQiMaterial)baseDam.getItem()).isMat(baseDam)){
							((IModularWeapon)this.getTool().getItem()).setPart(((GenericTaoistWeapon)this.getTool().getItem()).getParts().get(this.getIndex()),this.getTool(), eaten);
						//}
					}
					
					else if(((ItemResource)eaten.getItem()).isQuench(eaten.getItemDamage())){
						this.complete();
					}
					
				}
			}
		}
	}
}
