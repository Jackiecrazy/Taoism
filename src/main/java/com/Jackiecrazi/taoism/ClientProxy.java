package com.Jackiecrazi.taoism;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import com.Jackiecrazi.taoism.client.ClientEventHandler;
import com.Jackiecrazi.taoism.client.render.CompiledSkillRenders;
import com.Jackiecrazi.taoism.client.render.TaoRenders;

	public class ClientProxy extends CommonProxy {
		public static IIcon metal, wood, water, fire, earth, wind, thunder, yin, yang, sha;
		public static IIcon emptySlot,headdress,mirror,belt,faqi,amulet,ring,glove,back,cloak,wuji,gongfa;
		//private static final ClientEventHandler eventhandler = new ClientEventHandler();
		/*
		* For rendering a sphere, need ids for call lists for outside and inside
		*/
		public static int sphereIDOutside;
		public static int sphereIDInside;
	    @Override
	    public void preInit(FMLPreInitializationEvent e) {
	        super.preInit(e);
			MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	    }

	    @Override
	    public void init(FMLInitializationEvent e) {
	        super.init(e);

			
	    }

	    @Override
	    public void postInit(FMLPostInitializationEvent e) {
	        super.postInit(e);
	    }
	    @Override
	    public void initRenders(){
	    	CompiledSkillRenders.initLender();
	    	TaoRenders.drawEntity();
	    	TaoRenders.drawItem();
	    	saveSphereRender();
	    	LogManager.getLogger("Taoism").debug("Successfully registered renders");
	    }
	    
	    
		
		/*@SubscribeEvent
		public void onItemIconRegister(TextureStitchEvent.Pre e) {
			if(e.map.equals(Minecraft.getMinecraft().renderEngine.))
			emptySlot=e.map.registerIcon("taoism:gui/slot.png");
		}*/
	    
	    public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) 
	    {
	    	return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntityFromContext(ctx));
	    }
	    private static void saveSphereRender(){
	    	Sphere ball=new Sphere();
	    	ball.setDrawStyle(GLU.GLU_FILL);
	    	ball.setNormals(GLU.GLU_SMOOTH);
	    	ball.setOrientation(GLU.GLU_OUTSIDE);
	    	sphereIDOutside=GL11.glGenLists(1);
	    	GL11.glNewList(sphereIDOutside, GL11.GL_COMPILE);
	    	ResourceLocation rl=new ResourceLocation("taoism:hud/customColor.png");
	    	//System.out.println(rl.toString());
	    	Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
	    	ball.draw(0.5F, 32, 32);
	    	GL11.glEndList();

	    	ball.setOrientation(GLU.GLU_INSIDE);
	    	sphereIDInside = GL11.glGenLists(1);
	    	GL11.glNewList(sphereIDInside, GL11.GL_COMPILE);
	    	Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
	    	ball.draw(0.5F, 32, 32);
	    	GL11.glEndList();
	    }
	    
	}
