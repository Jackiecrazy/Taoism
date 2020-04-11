package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy {

    public void preinit(FMLPreInitializationEvent event) {

        super.preinit(event);
        this.models=new ModelBase[] {
                null
                //new ModelTaoArmor()
        };
		/*for (WeaponStatWrapper a : WeaponConfig.enabledParts.values()) {
			int damage = WeaponConfig.lookupDamage(a.getName());
			ModelLoader.setCustomModelResourceLocation(TaoItems.parts, damage, new ModelResourceLocation(TaoItems.parts.getRegistryName(), "test=" + damage));
			ModelLoader.setCustomModelResourceLocation(TaoItems.blueprint, damage, new ModelResourceLocation(TaoItems.blueprint.getRegistryName(), "inventory"));

		}*/
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);

    }

    public void init(FMLInitializationEvent event) {
        /*
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                //System.out.println(tintIndex);
                TaoWeapon tw = (TaoWeapon) stack.getItem();
                PartData p = tw.getPart(stack, TaoConfigs.weapc.lookupType(tintIndex));
                if (p != null && MaterialsConfig.findMat(p.getMat()) != null)
                    return MaterialsConfig.findMat(p.getMat()).msw.color.getRGB();
                else {
                    //System.out.println("lel");
                    return -1;
                }
            }

        }, TaoItems.weap);

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (stack.hasTagCompound()) {
                    return MaterialsConfig.findMat(stack.getTagCompound().getString("dict")).msw.color.getRGB();
                } else return -1;
            }

        }, TaoItems.part);
        Taoism.logger.debug("gave color to the masses");
         */
    }

    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntityFromContext(ctx));
    }

}
