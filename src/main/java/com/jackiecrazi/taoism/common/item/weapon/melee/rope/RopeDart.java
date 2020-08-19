package com.jackiecrazi.taoism.common.item.weapon.melee.rope;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.PartDefinition;
import com.jackiecrazi.taoism.api.StaticRefs;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.common.entity.projectile.weapons.EntityRopeDart;
import com.jackiecrazi.taoism.common.item.weapon.melee.TaoWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Taoism.MODID)
public class RopeDart extends TaoWeapon {
    public static final int MAXRANGESQ = 64;
    private final PartDefinition[] parts = {
            StaticRefs.HANDLE,
            StaticRefs.ROPE
    };

    /*
     * A rope weapon that charges up into fast, unblockable and constricting hits. High speed and range, medium power and defense, low combo
     * Two handed.
     * Lots of wrapping motions that burst into a quick release, can also continuously throw if needed. Almost like a dance, until it kills you.
     * 镖打回头
     * Stores up charge when in hand, to a cap, released during an attack. Cannot block or parry.
     * Normal attack, notably, throws out a projectile instead of actually attacking, damage and velocity determined by charge.
     * This means it naturally ignores non-shield blocks.
     *
     * On the same vein:
     * Meteor hammer: offhand is an arc attack that consumes charge at some rate
     * Flying claws: rip and grapple, pull enemies close or grapple away, speed+
     *
     * Redesign: charge up in combat for trick shots
     * charge is gained by hitting enemies
     * In hand it starts out at 0 charge, hitting an enemy allows it to gain a charge, with no cap
     *  charge is converted into qi upon returning, with bonus based on charge (e.g. 1 = 0.25, 2 = 0.6, 3 = 1.1)
     *  charge also increases damage of the dart while it is flying
     * A: It is no longer automatically retrieved, and will only return to hand if you shift
     * This means it whizzes by your head very often. When it does, it'll slow down slightly for a second to facilitate B
     * B: While the dart is out, normal attack is a punch that deals more knockback
     * Right click is a kick that consumes 1 posture to deal full damage and 3 posture to the enemy
     * Both of these attacks have range 3 radius 90, and if they hit the dart, the dart is sent back out with extra charge
     * C: If the normal attack does not hit the dart, it commands the dart to fire in that direction without giving extra charge
     * So you can, for instance, quickly turn around to fire the dart at an enemy behind you
     * Hitting a block will cause you to lose all charge, so react fast!
     */
    public RopeDart() {
        super(2, 2, 5, 0);
        this.addPropertyOverride(new ResourceLocation("thrown"), (stack, w, elb) -> isThrown(stack) && (w == null || w.getEntityByID(getDartID(stack)) != null) ? 1 : 0);
    }

    private boolean isThrown(ItemStack is) {
        return gettagfast(is).hasKey("dartID");
    }

    private int getDartID(ItemStack is) {
        return gettagfast(is).getInteger("dartID");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {
//        if (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof RopeDart) {
//            ItemStack is = Minecraft.getMinecraft().player.getHeldItemMainhand();
//            Entity e = Minecraft.getMinecraft().world.getEntityByID(((RopeDart) is.getItem()).getDartID(is));
//            EntityPlayer p = Minecraft.getMinecraft().player;
//            if (e != null) {
//                double doubleX = p.prevPosX + (p.posX - p.prevPosX) * event.getPartialTicks();
//                double doubleY = p.prevPosY + (p.posY - p.prevPosY) * event.getPartialTicks();
//                double doubleZ = p.prevPosZ + (p.posZ - p.prevPosZ) * event.getPartialTicks();
//                Vec3d vec = e.getPositionVector();
//                Vec3d pvec = p.getPositionVector();
//                double vx = vec.x;
//                double vy = vec.y;
//                double vz = vec.z;
//                double px = pvec.x;
//                double py = pvec.y;
//                double pz = pvec.z;
//
//                GL11.glPushMatrix();
//                //GL11.glDisable(GL11.GL_LIGHTING);
//                GL11.glDisable(GL11.GL_TEXTURE_2D);
//                GL11.glDisable(GL11.GL_DEPTH_TEST);
//
//
//                GL11.glLineWidth(2);
//                GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
//                GL11.glColor3f(0.8f, 0.8f, 0.8f);
//
//                GL11.glEnable(GL11.GL_LINE_SMOOTH);
//                GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
//                GL11.glBegin(GL11.GL_LINE_STRIP);
//
//                GL11.glVertex3d(px, py, pz);
//                GL11.glVertex3d(vx, vy, vz);
//
//                GL11.glEnd();
//                //GL11.glEnable(GL11.GL_LIGHTING);
//                GL11.glEnable(GL11.GL_TEXTURE_2D);
//                GL11.glEnable(GL11.GL_DEPTH_TEST);
//                GL11.glPopMatrix();
//            }
//        }
    }

    @Override
    public PartDefinition[] getPartNames(ItemStack is) {
        return parts;
    }

    @Override
    public boolean isTwoHanded(ItemStack is) {
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        super.onUpdate(stack, w, e, slot, onHand);
        if (!w.isRemote) {
            if (gettagfast(stack).hasKey("dartID")
                    && e.world.getEntityByID(
                    gettagfast(stack).getInteger("dartID")) == null) {
                gettagfast(stack).removeTag("dartID");
                gettagfast(stack).removeTag("connected");
            }
        }
    }

    @Override
    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", 8f) + TextFormatting.RESET);
        tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponDefMult", postureMultiplierDefend(null, null, stack, 0)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttMult", 0f) + TextFormatting.RESET);
    }

    @Override
    protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.DARK_RED + I18n.format("weapon.hands") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_RED + I18n.format("ropedart.parry") + TextFormatting.RESET);
        tooltip.add(TextFormatting.DARK_GREEN + I18n.format("weapon.projectile") + TextFormatting.RESET);
        tooltip.add(I18n.format("ropedart.projectile"));
        tooltip.add(I18n.format("ropedart.attack"));
        tooltip.add(I18n.format("ropedart.pain"));
        tooltip.add(I18n.format("ropedart.loss"));
        tooltip.add(I18n.format("ropedart.punch"));
        tooltip.add(I18n.format("ropedart.alt"));
        tooltip.add(I18n.format("ropedart.trick"));
        tooltip.add(I18n.format("ropedart.accel"));
    }

    @Override
    public float getTrueReach(EntityLivingBase p, ItemStack is) {
        return isThrown(is) || getHand(is) == EnumHand.OFF_HAND ? 2 : 0;
    }

    @Override
    protected void aoe(ItemStack is, EntityLivingBase elb, int chi) {
        if (getHand(is) == EnumHand.MAIN_HAND || TaoCasterData.getTaoCap(elb).consumePosture(1, false) == 0)
            splash(elb, elb, is, 360, elb.world.getEntitiesWithinAABB(EntityRopeDart.class, elb.getEntityBoundingBox().grow(2)));
        if (!elb.world.isRemote) {
            if (getHand(is) == EnumHand.MAIN_HAND) {
                if (!isThrown(is)) {
                    EntityRopeDart erd = new EntityRopeDart(elb.world, elb, getHand(is));
                    erd.shoot(elb, elb.rotationPitch, elb.rotationYaw, 0.0F, 0.5f, 0.0F);
                    elb.world.spawnEntity(erd);
                    if (getHand(is) == EnumHand.OFF_HAND) {
                        is = elb.getHeldItemMainhand();
                    }
                    gettagfast(is).setInteger("dartID", erd.getEntityId());
                } else {
                    if (elb.isSneaking()) {
                        EntityRopeDart erd = getDartEntity(is, elb);
                        if (erd != null) erd.setDead();
                    }
                }
            }
        }
    }

    @Override
    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return (isThrown(item) || getHand(item) == EnumHand.OFF_HAND) && super.canBlock(defender, attacker, item, recharged, amount);
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        if (isDartAttack(item)) {
            return 0;
        } else if (TaoCasterData.getTaoCap(attacker).consumePosture(1, false) == 0) {
            return 4;
        }
        return 0;
    }

    @Override
    public boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return attacker != target && (isThrown(item) || getHand(item) == EnumHand.OFF_HAND);
    }

    @Override
    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        if (isDartAttack(item) && getDartEntity(item, attacker) != null)
            return Objects.requireNonNull(getDartEntity(item, attacker)).getCharge() > 5 ? Event.Result.ALLOW : Event.Result.DENY;
        return Event.Result.DEFAULT;
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        if (isDartAttack(item) && getDartEntity(item, attacker) != null)
            return 1 + Objects.requireNonNull(getDartEntity(item, attacker)).getCharge() / 10f;
        return getHand(item) == EnumHand.MAIN_HAND ? 1 : 0.5f;
    }

    @Override
    public float onKnockingBack(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return !isDartAttack(stack) && getHand(stack) == EnumHand.MAIN_HAND ? orig * 1.2f : orig;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        if (isDartAttack(item) && getDartEntity(item, attacker) != null && (Objects.requireNonNull(getDartEntity(item, attacker)).getCharge() > 5))
            return 5;
        return 0;
    }

    private boolean isDartAttack(ItemStack is) {
        return gettagfast(is).getBoolean("dartAttack");
    }

    private EntityRopeDart getDartEntity(ItemStack is, EntityLivingBase elb) {
        if (isThrown(is) && elb.world.getEntityByID(getDartID(is)) != null)
            return (EntityRopeDart) elb.world.getEntityByID(getDartID(is));
        return null;
    }

    @Override
    public float postureMultiplierDefend(Entity attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return 1f;
    }

}
