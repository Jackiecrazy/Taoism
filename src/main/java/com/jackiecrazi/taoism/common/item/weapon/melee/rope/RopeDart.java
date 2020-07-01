package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityRopeDart;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Taoism.MODID)
public class RopeDart extends TaoWeapon {
    public static final int MAXRANGESQ = 64;

    /*
     * A rope weapon that charges up into fast, unblockable and constricting hits. High speed and range, medium power and defense, low combo
     * Two handed.
     * Lots of wrapping motions that burst into a quick release, can also continuously throw if needed. Almost like a dance, until it kills you.
     * 镖打回头
     * Stores up charge when in hand, to a cap, released during an attack. Cannot block or parry.
     * Normal attack, notably, throws out a projectile instead of actually attacking, damage and velocity determined by charge.
     * This means it naturally ignores non-shield blocks.
     * Alt attack is an arcing overhead smash. This inflicts light bonking damage and, if not blocked or parried, will bind hit target
     *      After this, main hand will disengage the rope, and offhand is freed.
     *      TODO:
     *      After bind, become capable of parrying and delivers a critical punch in the main hand with range 2
     *      If binding entity, lasso down with offhand, retrieves the rope and inflicts half max posture damage
     *      If parried or blocked, disarm opponent until retrieved with offhand
     *
     * On the same vein:
     * Meteor hammer: more swinging, stunning+kb hits, power+
     * Flying claws: rip and grapple, pull enemies close or grapple away, speed+
     *
     * While equipped, the dart orbits around you in a set pattern. Saves summoning and killing, and gives a passive hit aura
     * At the same time, letting anyone get close is potentially fatal.
     */
    public RopeDart() {
        super(2, 4, 5, 0);
        this.addPropertyOverride(new ResourceLocation("thrown"), (stack, w, elb) -> gettagfast(stack).hasKey("dartID") || isEngaged(stack) ? 1 : 0);
    }

    private boolean isEngaged(ItemStack is) {
        return gettagfast(is).hasKey("bindID") && gettagfast(is).getInteger("bindID") != gettagfast(is).getInteger("dartID");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {
        if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof RopeDart) {
            ItemStack is = Minecraft.getMinecraft().player.getHeldItemMainhand();
            Entity e = Minecraft.getMinecraft().world.getEntityByID(((RopeDart) is.getItem()).getRopedTo(is));
            EntityPlayer p=Minecraft.getMinecraft().player;
            if (e != null) {
                double doubleX = p.prevPosX + (p.posX - p.prevPosX) * event.getPartialTicks();
                double doubleY = p.prevPosY + (p.posY - p.prevPosY) * event.getPartialTicks();
                double doubleZ = p.prevPosZ + (p.posZ - p.prevPosZ) * event.getPartialTicks();
                Vec3d vec = e.getPositionVector();
                Vec3d pvec = p.getPositionVector();
                double vx = vec.x;
                double vy = vec.y;
                double vz = vec.z;
                double px = pvec.x;
                double py = pvec.y;
                double pz = pvec.z;

                GL11.glPushMatrix();
                //GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_DEPTH_TEST);


                GL11.glLineWidth(2);
                GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
                GL11.glColor3f(0.8f, 0.8f, 0.8f);

                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                GL11.glBegin(GL11.GL_LINE_STRIP);

                GL11.glVertex3d(px, py, pz);
                GL11.glVertex3d(vx, vy, vz);

                GL11.glEnd();
                //GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
        }
    }

    private int getRopedTo(ItemStack is) {
        return gettagfast(is).getInteger("bindID");
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return new PartDefinition[0];
    }

    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        return 0;
    }

    @Override
    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", 8) + TextFormatting.RESET);
        tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponDefMult", postureMultiplierDefend(null, null, stack, 0)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttMult", postureDealtBase(null, null, stack, 1)) + TextFormatting.RESET);
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return !isEngaged(is);
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onMainhand) {
        super.onUpdate(stack, w, e, slot, onMainhand);
        if (!w.isRemote) {
            if (gettagfast(stack).hasKey("dartID")
                    && e.world.getEntityByID(
                    gettagfast(stack).getInteger("dartID")) == null) {
                gettagfast(stack).removeTag("dartID");
                gettagfast(stack).removeTag("connected");
            }
            if (gettagfast(stack).hasKey("bindID") && w.getEntityByID(getRopedTo(stack)) != null && onMainhand) {
                Entity ent = w.getEntityByID(getRopedTo(stack));
                if (ent instanceof EntityLivingBase) {
                    TaoCasterData.getTaoCap((EntityLivingBase) ent).setBindTime(10);
                }
            }
            else setRopedTo(stack, null);
        }
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase elb, ItemStack is) {
        if (!elb.world.isRemote) {
            if (elb.world.getEntityByID(gettagfast(is).getInteger("dartID")) == null) {
                EntityRopeDart erd = new EntityRopeDart(elb.world, elb, getHand(is));
                if (getHand(is) == EnumHand.MAIN_HAND) {
//                    if (isEngaged(is)) {
//                        Entity e = elb.world.getEntityByID(((RopeDart) is.getItem()).getRopedTo(is));
//                        if (e != null) {
//                            Vec3d relativePosVec = e.getPositionVector().subtract(elb.getPositionVector());
//                            Vec3d flatRelPosVec = new Vec3d(relativePosVec.x, 0, relativePosVec.z);
//                            Vec3d highRelPosVec = new Vec3d(0, relativePosVec.y, 0);
//                            float yaw = (float) Math.acos(flatRelPosVec.lengthVector());
//                            float pitch = (float) Math.asin(highRelPosVec.lengthVector());
//                            erd.setPositionAndRotation(e.posX, e.posY, e.posZ, yaw, pitch);
//                            erd.shoot(elb, pitch, yaw, 0.0F, 0.5f + (getMaxChargeTime() - getChargeTimeLeft(elb, is)) / 10f, 0.0F);
//                        }
//                    } else
                        erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 0.5f + (getMaxChargeTime() - is.getItemDamage()) / 10f, 0.0F);
                } else {
                    erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 1f, 0.0F);
                }
                elb.world.spawnEntity(erd);
                if (getHand(is) == EnumHand.OFF_HAND) {
                    is = elb.getHeldItemMainhand();
                }
                gettagfast(is).setInteger("dartID", erd.getEntityId());
                setRopedTo(is, erd);
            }

        }
        return super.onEntitySwing(elb, is);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) stack.getItemDamage() / (double) getMaxChargeTime();
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.parry") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.projectile") + TextFormatting.RESET);
        tooltip.add(I18n.format("ropedart.projectile"));
        tooltip.add(I18n.format("ropedart.damage"));
        tooltip.add(I18n.format("ropedart.recharge"));
        tooltip.add(I18n.format("ropedart.alt"));
        tooltip.add(I18n.format("ropedart.berserk"));
        tooltip.add(I18n.format("ropedart.disengage"));
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, ItemStack item) {
        return isEngaged(item);
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        super.onSwitchIn(stack, elb);
        stack.setItemDamage(getMaxChargeTime());
    }

    @Override
    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
    }

    @Override
    public boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return gettagfast(item).hasKey("dartID");
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return getHand(item) == EnumHand.MAIN_HAND && item.getItemDamage() == 0 ? Event.Result.ALLOW : Event.Result.DENY;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.MAIN_HAND && item.getItemDamage() == 0 ? 1.5f : 1;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return getHand(item) == EnumHand.MAIN_HAND ? 1.5f - (item.getItemDamage() / 20f) : 0.5f;
    }

    @Override
    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        super.attackStart(ds, attacker, target, stack, orig);
        ds.setProjectile();
    }

    @Override
    public float finalDamageMods(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        if (ds.isProjectile()) {
            stack.setItemDamage(getMaxChargeTime() / 2);
        }
        return orig;
    }

    @Override
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            if (!isEngaged(stack))
                setRopedTo(attacker.getHeldItemMainhand(), target);
        } else if (isEngaged(stack)) setRopedTo(stack, null);
    }

    @Override
    public int getMaxChargeTime() {
        return 20;
    }

    private void setRopedTo(ItemStack is, Entity binder) {
        if (binder == null) gettagfast(is).removeTag("bindID");
        else gettagfast(is).setInteger("bindID", binder.getEntityId());
    }

    @Override
    public float postureMultiplierDefend(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

}
