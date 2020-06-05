package com.jackiecrazi.taoism.client;

import com.jackiecrazi.taoism.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.lang.reflect.Field;

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

    Field hasBlock= ObfuscationReflectionHelper.findField(PlayerControllerMP.class,"field_78778_j");
    Field destroyPos=ObfuscationReflectionHelper.findField(PlayerControllerMP.class, "field_178895_c");


    public BlockPos getPlayerBreakingBlockCoords(EntityPlayer entityplayer)
    {
        if(entityplayer instanceof EntityPlayerSP)
        {
            try {
                PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
                boolean hb = hasBlock.getBoolean(controller);
                if (hb) {
                    return (BlockPos) destroyPos.get(controller);
                }
            }catch(Exception ignored){}
        }else
        {
            return super.getPlayerBreakingBlockCoords(entityplayer);
        }
        return null;
    }

    public boolean isBreakingBlock(EntityPlayer entityplayer)
    {
        if(entityplayer instanceof EntityPlayerSP)
        {
            try {
                PlayerControllerMP controller = Minecraft.getMinecraft().playerController;
                return hasBlock.getBoolean(controller);
            }catch(Exception ignored){}
        }else
        {
            return super.isBreakingBlock(entityplayer);
        }
        return false;
    }
}
