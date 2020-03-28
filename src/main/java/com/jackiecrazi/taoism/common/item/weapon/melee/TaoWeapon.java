package com.jackiecrazi.taoism.common.item.weapon.melee;

import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.*;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.client.ClientEvents;
import com.jackiecrazi.taoism.client.KeyOverlord;
import com.jackiecrazi.taoism.moves.melee.MoveMultiStrike;
import com.jackiecrazi.taoism.networking.PacketExtendThyReach;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class TaoWeapon extends Item implements IAmModular, IElemental, IRange, ICombatManipulator, IStaminaPostureManipulable, ICombo, IDamageType, ISpecialSwitchIn, IChargeableWeapon, ITwoHanded {
    public static final ArrayList<Item> listOfWeapons = new ArrayList<>();
    //booleans only used when attacking to determine what type of attack it is
    public static boolean aerial = false, off = false, right = false;
    //booleans only used when attacking to determine the specific phase of attack
    public static boolean effect = true, aoe = true, spawn = true;
    private final double speed;
    private final int damageType;
    private final double dmg;
    private final float base;
    //0
    private List<Material> pickList = Arrays.asList(
            Material.ANVIL,
            Material.IRON,
            Material.PISTON,
            Material.REDSTONE_LIGHT,
            Material.CIRCUITS,
            Material.PACKED_ICE,
            Material.ROCK,
            Material.ICE,
            Material.GLASS);
    //1
    private List<Material> shovelList = Arrays.asList(
            Material.CAKE,
            Material.CLAY,
            Material.CRAFTED_SNOW,
            Material.GRASS,
            Material.GROUND,
            Material.CARPET,
            Material.SAND,
            Material.SNOW);
    //2
    private List<Material> axeList = Arrays.asList(
            Material.CACTUS,
            Material.CLOTH,
            Material.CORAL,
            Material.GOURD,
            Material.WOOD);
    //3
    private List<Material> scytheList = Arrays.asList(
            Material.LEAVES,
            Material.PLANTS,
            Material.VINE);

    public TaoWeapon(int damageType, double swingSpeed, double damage, float BasePostureConsumption) {
        super();
        this.damageType = damageType;
        speed = swingSpeed;
        this.setMaxDamage(0);
        this.setMaxStackSize(1);
        dmg = damage;
        base = BasePostureConsumption;
        this.setCreativeTab(Taoism.tabWea);
        String name = getClass().getSimpleName().substring(4).toLowerCase();
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        listOfWeapons.add(this);
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return base + (getDamDist(item) * amount);
    }

    @Override
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.NONE;
    }

//    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
//        //Taoism.net.sendToServer(new PacketMakeMove(entityLiving));
//        return true;
//    }
    /*
    First and foremost, attacks and defences can be "hard" or "soft".
An attack has a windup, wound and recovery phase. These phases preclude all other phases on that hand:
the windup phase can be interrupted by jumping, dodging, taking damage or directly inputting another attack command (e.g. left slash interrupted by right slash), so you can feint or be punished for late counters.
the wound phase is the only one that deals damage. This is generally comparatively long.
the recovery phase cannot be canceled except by starting another attack. Your options are basically to jump away or keep hitting. Only whiffs and hits give full recovery phase.
The block is automatically executed as long as you have an idle hand with a weapon or shield in the direction of the attack. It negates damage at a certain efficiency, converting it to posture.
Blocks can reduce their posture cost with a backpedal, and vice versa. Certain attacks cannot be blocked. Shields additionally block projectiles.
An attack will cancel another attack if both are in wound phase. This is called a parry. Due to the offensive nature of a parry, it is almost always usable.
Moves are "soft" or "hard". Soft-soft gives no recovery phase, soft-hard causes defense breach respectively, hard-hard leads to a disengage-able blade lock, enabling parry specials. Soft has less windup.
In a round of blow trading against a hit (m), you can hit back (yomi 1), which is countered by a dodge (yomi 2) that causes you to whiff, which is countered by a feint (yomi 3) while they're recovering, which is countered by the attack (m)
I should optimize sidesteps and perhaps vary the combos with movement keys, now that I'm going full overhaul. They could be fixed chains of combos (left swipe-right swipe) or dynamic (consult blade symphony), but then parrying becomes an issue...
     */

//    @Override
//    public boolean onLeftClickEntity(final ItemStack stack, final EntityPlayer player, final Entity entity) {
//        return notSpecialing;
//    }

    public int getDamageType(ItemStack is) {
        return damageType;
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        return false;
    }

    public double getDamage() {
        return dmg;
    }

    public double getAttackSpeed() {
        return speed - 4d;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage(stack) - 1, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed(stack), 0));
            //for (int x = 0; x < IElemental.ATTRIBUTES.length; x++)
            //multimap.put(IElemental.ATTRIBUTES[x].getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAffinity(stack, x), 0));
        }
        // TODO Some weapons should give you buffs in other things too
        return multimap;
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {

        return false;
    }

    public double attackDamage(ItemStack stack) {
        return dmg;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        updateWielderData(stack, attacker);
        int chi = 0;
        boolean swing = true;
        if (attacker.hasCapability(TaoCasterData.CAP, null)) {
            ITaoStatCapability itsc = attacker.getCapability(TaoCasterData.CAP, null);
            itsc.setQi(itsc.getQi() + 1);
            chi = itsc.getQiFloored();
            swing = itsc.getSwing() >= 0.9f;
        }
        if (swing) {
            if (aoe) {
                aoe = false;
                aoe(stack, target, attacker, chi);
                TaoCasterData.getTaoCap(attacker).addQi(1f);
                aoe = true;
            }
            if (effect) {
                effect = false;
                applyEffects(stack, target, attacker, chi);
                effect = true;
            }
            if (spawn) {
                spawn = false;
                spawnExtraMoves(stack, target, attacker, chi);
                spawn = true;
            }
        }
        return super.hitEntity(stack, target, attacker);
    }

    void updateWielderData(ItemStack stack, EntityLivingBase attacker) {
        ITaoStatCapability itsc = attacker.getCapability(TaoCasterData.CAP, null);
        NBTTagCompound ntc = gettagfast(stack);
        ntc.setFloat("qi", itsc.getQi());
        ntc.setInteger("qifloor", itsc.getQiFloored());
    }

    /**
     * this implementation is for single handed weapons' offhand attack, double handed weapons will need to override this.
     */
    public boolean itemInteractionForEntity(ItemStack i, EntityPlayer p, EntityLivingBase target, EnumHand hand) {
//        if (hand == EnumHand.OFF_HAND)
//            if (!i.isEmpty()) {
//                //System.out.println("nonnull");
//
//                EntityLivingBase elb = NeedyLittleThings.raytraceEntities(p.world, p, getReach(p, i));
//                if (elb != null) {
//                    //experimental!
//                    Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
////                    NeedyLittleThings.swapItemInHands(p);
////                    p.attackTargetEntityWithCurrentItem(elb);
////                    NeedyLittleThings.swapItemInHands(p);
//                }
//            }
        return super.itemInteractionForEntity(i, p, target, hand);
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer p, EnumHand handIn) {
        if (isTwoHanded(p.getHeldItem(handIn))) {
            //can't put a two handed weapon in an offhand slot, you can't carry it!
            if (handIn == EnumHand.OFF_HAND)
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, p.getHeldItem(handIn));
            else {
                right = true;
                if (worldIn.isRemote) {
                    EntityLivingBase elb = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, p.getHeldItemOffhand()));
                    if (elb != null) {
                        Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), true));
                    }
                    p.swingArm(handIn);
                }
                right = false;
            }
        }

        if (handIn == EnumHand.OFF_HAND) {
            if (!p.getHeldItemOffhand().isEmpty()) {
                //System.out.println("nonnull");
                if (worldIn.isRemote) {
                    EntityLivingBase elb = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, p.getHeldItemOffhand()));
                    if (elb != null) {
                        Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
                    }
                    p.swingArm(handIn);
                    TaoCasterData.getTaoCap(p).setOffhandCool(0);
                }
//                if (!worldIn.isRemote&&elb!=null) {
//                    NeedyLittleThings.swapItemInHands(p);
//                    p.attackTargetEntityWithCurrentItem(elb);
//                    NeedyLittleThings.swapItemInHands(p);
//                }
            }

        }
        return super.onItemRightClick(worldIn, p, handIn);
    }

    protected void splash(EntityLivingBase attacker, List<Entity> targets) {
        for (Entity target : targets) {

            if (target == attacker) continue;
            ItemStack is = off ? attacker.getHeldItemOffhand() : attacker.getHeldItemMainhand();
            if (NeedyLittleThings.getDistSqCompensated(target, attacker) > getReach(attacker, is) * getReach(attacker, is))
                continue;
            try {
                if (off)
                    TaoCasterData.getTaoCap(attacker).setOffhandCool(TaoCasterData.getTaoCap(attacker).getSwing());
                else Taoism.atk.setInt(attacker, TaoCasterData.getTaoCap(attacker).getSwing());
            } catch (IllegalAccessException ignored) {
            }
            if (attacker instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) attacker;
                NeedyLittleThings.taoWeaponAttack(target, p, attacker.getHeldItem(off ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), !off, false);
            } else attacker.attackEntityAsMob(target);
        }
    }

    protected void splash(EntityLivingBase attacker, EntityLivingBase target, double radius) {
        splash(attacker, attacker.world.getEntitiesInAABBexcluding(target, target.getEntityBoundingBox().grow(radius, 1.5d, radius), null));
    }

    protected void coolDown(EntityLivingBase attacker, int duration) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) attacker;
            p.getCooldownTracker().setCooldown(this, duration);
        }
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, int ticks) {
        item.damageItem(ticks, defender);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        item.setItemDamage(0);
    }

    @Override
    public boolean isCharged(EntityLivingBase elb, ItemStack item) {
        return item.getItemDamage() != 0;
    }

    @Override
    public int getChargeTimeLeft(EntityLivingBase elb, ItemStack item) {
        return item.getItemDamage();
    }

    /**
     * gradually discharges the weapon
     */
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            if (onHand) {
                if (stack.isItemDamaged()) stack.damageItem(-1, elb);
            } else stack.setItemDamage(0);
            //TODO stuff
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        for (Map.Entry<String, String> a : getParts(stack).entrySet()) {
            try {
                tooltip.add(I18n.format(a.getValue() + " " + a.getKey()));
            } catch (Exception ignored) {

            }
        }
        //increment in 5, max is 50
        if (KeyOverlord.isShiftDown()) {
//            int pot = (int) getAffinities(stack)[i];
//            //System.out.println(pot);
//            int used = Math.round(pot / 2) * 2;
//            if (used != 0)
//                tooltip.add(IElemental.ELEMC[i] + "" + TextFormatting.ITALIC + I18n.format(IElemental.ELEMS[i] + used + ".inscription") + TextFormatting.RESET);
            statDesc(stack, worldIn, tooltip, flagIn);
        } else {
            tooltip.add(I18n.format("taoism.shiftweapon"));
        }
        //perks
        if (KeyOverlord.isControlDown()) {
            perkDesc(stack, worldIn, tooltip, flagIn);
        } else tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponctrl") + TextFormatting.RESET);

    }

    abstract protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn);

    /**
     * default method! Override for complex weapons!
     */
    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", getReach(null, stack)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponDefMult", postureMultiplierDefend(null, null, stack, 0)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttBase", postureDealtBase(null, null, stack, 0)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.GREEN + I18n.format("taoism.weaponAttMult", getDamDist(stack)) + TextFormatting.RESET);
    }

    private double speed(ItemStack stack) {
        return getAttackSpeed();
    }

    public float getAffinity(ItemStack is, int element) {
        if (is.isEmpty()) return 0f;
        if (getElementBase(is).hasKey("element" + element)) return getElementBase(is).getFloat("element" + element);
        float ret = 0;
        if (is.getItem() instanceof IAmModular) {
            IAmModular iam = (IAmModular) is.getItem();
            for (String pd : iam.getParts(is).values()) {
                //ret += pd.getMatSW().affinity()[element % ELEMS.length] * pd.getWeaponSW().getElementalMultiplier();
            }
        }
        return ret;
    }

    protected NBTTagCompound gettagfast(ItemStack is) {
        if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
        return is.getTagCompound();
    }

    public int getCombo(EntityLivingBase wielder, ItemStack is) {
        return gettagfast(is).getInteger("combo");
    }

    public void setCombo(EntityLivingBase wielder, ItemStack is, int to) {
        if (!is.hasTagCompound())
            is.setTagCompound(new NBTTagCompound());
        assert is.getTagCompound() != null;
        is.getTagCompound().setInteger("combo", to % getComboLength(wielder, is));
    }

    public long lastAttackTime(EntityLivingBase elb, ItemStack is) {
        return gettagfast(is).getLong("lastAttack");
    }

    public void updateLastAttackTime(EntityLivingBase elb, ItemStack is) {
        gettagfast(is).setLong("lastAttack", elb.world.getTotalWorldTime());
    }

    public boolean isBroken(ItemStack stack) {
        return this.getMaxDamage(stack) - stack.getItemDamage() <= 1;
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        boolean[] h = harvestable(stack);
        if (pickList.contains(state.getMaterial()) && h[0]) {
            return true;
        }
        if (shovelList.contains(state.getMaterial()) && h[1]) {
            return true;
        }
        if (axeList.contains(state.getMaterial()) && h[2]) {
            return true;
        }
        if (scytheList.contains(state.getMaterial()) && h[3]) {
            return true;
        }
        //System.out.println(ret);
        return super.canHarvestBlock(state, stack);
    }

    protected void multiHit(EntityLivingBase attacker, Entity target, int interval, int duration) {
        attacker.world.spawnEntity(new MoveMultiStrike(attacker, target, interval, duration));
    }


    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    protected boolean[] harvestable(ItemStack is) {
        return new boolean[]{false, false, false, false};
    }

    /**
     * Here's a little lesson in trickery!
     */
    @SideOnly(value = Side.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean changed) {
        if (!changed) try {
            float swing = NeedyLittleThings.getCooledAttackStrengthOff(Minecraft.getMinecraft().player, 1f);
            float newSwing = ClientEvents.okuyasu.getFloat(Minecraft.getMinecraft().getItemRenderer());
            newSwing += MathHelper.clamp((swing * swing * swing) - newSwing, -0.4F, 0.4F);
            ClientEvents.zaHando.setFloat(Minecraft.getMinecraft().getItemRenderer(), newSwing);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return changed;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {

    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {

    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return orig;
    }

    @Override
    public float damageEnd(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return orig;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return orig;
    }

    public boolean isTwoHanded(ItemStack is) {
        return false;
    }

    /**
     * AoE
     */
    protected void aoe(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    /**
     * generally applies some effects
     */
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    /**
     * spawns entities etc.
     */
    protected void spawnExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    public void onBlock(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //because tonfas.
    }
}
