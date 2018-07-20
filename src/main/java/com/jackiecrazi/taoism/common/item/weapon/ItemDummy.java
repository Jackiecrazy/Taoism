package com.jackiecrazi.taoism.common.item.weapon;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartData;
import com.jackiecrazi.taoism.api.WeaponStatWrapper;
import com.jackiecrazi.taoism.config.MaterialsConfig;
import com.jackiecrazi.taoism.config.TaoConfigs;

public class ItemDummy extends Item {
	public ItemDummy(){
		this.setUnlocalizedName("taodum").setHasSubtypes(true).setRegistryName("taodum").setCreativeTab(Taoism.tabBlu);
		/*this.addPropertyOverride(new ResourceLocation("broken"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
            	if(!stack.hasTagCompound())stack.setTagCompound(new NBTTagCompound());
                return stack.getTagCompound().getInteger("dam");
            }
        });*/
	}
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)){
			for(WeaponStatWrapper wsw:TaoConfigs.weapc.enabledParts.values()){
				for(int aaa=0;aaa<5;aaa++)
				items.add(new PartData(wsw.getClassification(), MaterialsConfig.getRandomMat(itemRand, wsw.matType()), wsw.getOrdinal()).toStack());
			}
		}
	}
}
