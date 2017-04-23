package com.Jackiecrazi.taoism.common.items.resource;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import com.Jackiecrazi.taoism.Taoism;
import com.Jackiecrazi.taoism.api.allTheInterfaces.ILianQiMaterial;
import com.Jackiecrazi.taoism.common.items.ModItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemResource extends Item implements ILianQiMaterial {
	//metals end with Ingot, woods end with Mu, quench ends with Quench
	public String[] types;
	Color[] colors;
	public IIcon[] icon;
	public IIcon[] weap;
	public ItemResource(String identifier,String[] types,Color[] color) {
		super();
		this.setCreativeTab(Taoism.TabTaoistMaterials);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("resource"+identifier);
		this.types=types;
		this.colors=color;
	}

	@Override
	public int[] getAffinities(ItemStack is) {
		//TODO wtf is this. Improve.
		int metal=0;
		int wood=0;
		int water=0;
		int fire=0;
		int earth=0;
		return new int[]{metal,wood,water,fire,earth};
	}
	/**
	 * Don't use this. Just don't.
	 */
	@Override
	public void setAffinities(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		//don't use this.
	}

	/**
	 * affinity only goes up to 100
	 */
	@Override
	public void setAffinity(ItemStack is, int element, int affinity) {
		int orig=is.getItemDamage();
		int type=(int) Math.floor(orig/1000)*1000;
		element*=100;
		affinity%=100;
		is.setItemDamage(type+element+affinity);
	}

	@Override
	public int getAffinity(ItemStack is, int element) {
		//5 digits max(65536)=64 variations+element identifier+element amount+quality (multiply w/ quality)
		//16 bits=64 variations(6 bits)+2 bits per element
		int dam=is.getItemDamage();
		if(Math.floor(dam/100)%10!=element)return 0;
		return (int) (Math.floor(dam/10)%10)*dam%10;
	}

	@Override
	public void addAffinity(ItemStack is, int metal, int wood, int water,
			int fire, int earth) {
		
	}
	@SideOnly(Side.CLIENT)
    @Override
    public void getSubItems(Item item, CreativeTabs xCreativeTabs, List list) {
        for(int x = 0; x < types.length; x++){
        		ItemStack is=new ItemStack(this, 1, (x*1000));
        		if(isMat(is))
            list.add(is);
        }
	}
	@SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
		if(meta<icon.length*1000)
        return this.icon[(int) Math.floor(meta/1000)];
		else return icon[0];
    }
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + types[(int) Math.floor((stack.getItemDamage()/1000)%types.length)];
    }

	@Override
	public boolean isMat(ItemStack i) {
		return (i.getItem()==ModItems.ResourceMetal?(i.getItemDamage()!=8000&&i.getItemDamage()!=9000):true);
	}
	public boolean isWood(int meta){
		int ref=Math.floorDiv(meta, 1000)%types.length;
		return types[ref].endsWith("Mu");
	}
	public boolean isMetal(int meta){
		int ref=Math.floorDiv(meta, 1000)%types.length;
		return types[ref].endsWith("Ingot");
	}
	public boolean isGem(int meta){
		int ref=Math.floorDiv(meta, 1000)%types.length;
		return types[ref].endsWith("Gem");
	}
	public boolean isQuench(int meta){
		int ref=Math.floorDiv(meta, 1000)%types.length;
		return types[ref].endsWith("Quench");
	}
	
	@SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir)
    {
    	icon = new IIcon[types.length];
    	weap=new IIcon[types.length];
    	for(int x=0;x< types.length;x++){
    	icon[x]=ir.registerIcon("taoism:materials/"+types[x].toLowerCase());
    	weap[x]=ir.registerIcon("taoism:materials/weap/"+types[x].toLowerCase());
    	}
    	
    }
    public int getColorFromIS(ItemStack is, int i)
    {
		/*Color ret=new Color(255,255,255);
		ResourceLocation texture = null;
		Item item = is.getItem();
		String textureName = is.getItem().getIcon(is, i).getIconName();//TODO get the texture from the model instead
		if(textureName != null){
			//System.out.println("iku");
			if(textureName.split(":").length == 1){
				textureName = "minecraft:" + textureName;
			}
			texture = new ResourceLocation(textureName.split(":")[0], "textures/items/materials/weap" + textureName.split(":materials")[1] + ".png");
		}
		
		
		if(texture != null){
			//System.out.println("goya");
			int[] colorBuff = new int[]{};
			try {
				colorBuff = TextureUtil.readImageData(Minecraft.getMinecraft().getResourceManager(), texture);
			} catch (IOException e) {
				//Taoism.logger.error("Caught exception while parsing texture to get color: ", e);
			}
			int[] red = new int[]{};
			int[] green = new int[]{};
			int[] blue = new int[]{};
			for(int c : colorBuff){
				Color col = new Color(c);
				if(col.getAlpha() > 0){
					red = ArrayUtils.add(red, col.getRed());
					green = ArrayUtils.add(green, col.getGreen());
					blue = ArrayUtils.add(blue, col.getBlue());
				}
			}
			if(red.length > 0 && green.length > 0 && blue.length > 0){
				int r = red[0];//NeedyLittleThings.avg(red[0], ArrayUtils.subarray(red, 1, red.length));
				int g = green[0];//NeedyLittleThings.avg(green[0], ArrayUtils.subarray(green, 1, green.length));
				int b = blue[0];//NeedyLittleThings.avg(blue[0], ArrayUtils.subarray(blue, 1, blue.length));
				color = r + ":" + g + ":" + b;
			}
			ret=new Color(colorBuff[0]);
			
		}
		
		ret=new Color(
				Integer.valueOf(color.substring(0, color.indexOf(':'))), 
				Integer.valueOf(color.substring(color.indexOf(':')+1, color.lastIndexOf(':'))), 
				Integer.valueOf(color.substring(color.lastIndexOf(':')+1)));
		//System.out.println(color);
*/		return colors[(Math.floorDiv(is.getItemDamage(),1000)%colors.length)].getRGB();
    }
    
    public static int pwrFromDam(int dam){
    	int trueDam=Math.floorDiv(dam, 1000);
    	
    	return trueDam;
    }
}
