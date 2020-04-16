package com.jackiecrazi.taoism.common.item.weapon.melee;

import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.*;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.client.ClientEvents;
import com.jackiecrazi.taoism.client.KeyOverlord;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
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
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class TaoWeapon extends Item implements IAmModular, IElemental, IRange, ICombatManipulator, IStaminaPostureManipulable, ICombo, IDamageType, ISpecialSwitchIn, IChargeableWeapon, ITwoHanded {//, IMove
    public static final ArrayList<Item> listOfWeapons = new ArrayList<>();
    protected static final UUID QI_MODIFIER = UUID.fromString("8e948b44-7560-11ea-bc55-0242ac130003");
    //booleans only used when attacking to determine what type of attack it is
    public static boolean aerial = false,
    /**
     * THIS IS ONLY USED FOR UTILS. DO NOT USE FOR ATTACK DETERMINATION!
     */
    off = false;
    //booleans only used when attacking to determine the specific phase of attack
    public static boolean effect = true, aoe = true, spawn = true;
    private final double speed;
    private final int damageType;
    private final double dmg;
    private final float base;
    protected float buffer = 0f;
    private float qiRate = 1f;
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
        this.setMaxDamage(16384);
        this.setMaxStackSize(1);
        dmg = damage;
        base = BasePostureConsumption;
        this.setCreativeTab(Taoism.tabWea);
        String name = getClass().getSimpleName().toLowerCase();
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        listOfWeapons.add(this);
        this.addPropertyOverride(new ResourceLocation("offhand"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, @Nullable World w, @Nullable EntityLivingBase elb) {
                if (elb != null) {
                    if (elb.getHeldItemOffhand() == stack && (!isTwoHanded(stack) || isDummy(stack))) return 1;
                }
                return 0;
            }
        });
    }

    protected TaoWeapon setQiAccumulationRate(float amount) {
        qiRate = amount;
        return this;
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return base * (getDamDist(item) * amount);
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return qiRate;
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

    @Override
    public EnumAction getItemUseAction(final ItemStack stack) {
        return EnumAction.NONE;
    }

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

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage(stack) - 1, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed(stack), 0));
            multimap.put(TaoEntities.QIRATE.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", getQiAccumulationRate(stack), 0));
            //for (int x = 0; x < IElemental.ATTRIBUTES.length; x++)
            //multimap.put(IElemental.ATTRIBUTES[x].getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAffinity(stack, x), 0));
        }
        // TODO Some weapons should give you buffs in other things too
        return multimap;
    }

    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        stack.setTagInfo("startCD", new NBTTagFloat(Taoism.getAtk(entityLiving)));
        stack.setTagInfo("startTick", new NBTTagInt(entityLiving.ticksExisted));
        return super.onEntitySwing(entityLiving, stack);
    }

    public double getDurabilityForDisplay(ItemStack stack) {
        return (double) (getMaxChargeTime() - getChargeTimeLeft(null, stack)) / (double) getMaxChargeTime();
    }

    public double attackDamage(ItemStack stack) {
        return dmg;
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        updateWielderDataStart(stack, attacker, target);
        int chi = 0;
        boolean swing = true;
        if (attacker.hasCapability(TaoCasterData.CAP, null)) {
            ITaoStatCapability itsc = attacker.getCapability(TaoCasterData.CAP, null);
            chi = itsc.getQiFloored();
            swing = !(attacker instanceof EntityPlayer) || itsc.getSwing() >= 0.9f * NeedyLittleThings.getCooldownPeriod(attacker);
        }
        if (swing) {
            target.hurtResistantTime = 1;
            if (aoe) {
                aoe = false;
                aoe(stack, target, attacker, chi);
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
        updateWielderDataEnd(stack, attacker, target);
        return super.hitEntity(stack, target, attacker);
    }

    private void updateWielderDataStart(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target) {
        if (isDummy(stack) && attacker.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            updateWielderDataStart(attacker.getHeldItemMainhand(), attacker, target);
            return;
        }
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(attacker);
        NBTTagCompound ntc = gettagfast(stack);
        ntc.setFloat("qi", itsc.getQi());
        ntc.setInteger("qifloor", itsc.getQiFloored());
        //ntc.setByte("lastMove", new MoveCode(true, ).toByte());
    }

    private void updateWielderDataEnd(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target) {
        if (isDummy(stack) && attacker.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            updateWielderDataEnd(attacker.getHeldItemMainhand(), attacker, target);
            return;
        }
        NBTTagCompound ntc = gettagfast(stack);
        ntc.setInteger("lastAttackedID", target.getEntityId());
        //ntc.setByte("lastMove", new MoveCode(true, ).toByte());
    }

    protected MoveCode getLastMove(ItemStack stack) {
        return new MoveCode(gettagfast(stack).getByte("lastMove"));
    }

    protected MoveCode getCurrentMove(ItemStack stack) {
        return new MoveCode(gettagfast(stack).getByte("currentMove"));
    }

    protected int getQiFromStack(ItemStack stack) {
        return gettagfast(stack).getInteger("qifloor");
    }

    protected Entity getLastAttackedEntity(World w, ItemStack stack) {
        if (!gettagfast(stack).hasKey("lastAttackedID")) return null;
        return w.getEntityByID(gettagfast(stack).getInteger("lastAttackedID"));
    }

    /**
     * Called when the equipped item is right clicked.
     */
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer p, EnumHand handIn) {
        if (handIn == EnumHand.OFF_HAND) {
            ItemStack offhand = p.getHeldItemOffhand();
            if (!offhand.isEmpty()) {
                if (isDummy(offhand) && p.getHeldItemMainhand().getItem() != offhand.getItem()) {
                    p.setHeldItem(EnumHand.OFF_HAND, unwrapDummy(offhand));
                }
                if (isTwoHanded(offhand) && !isDummy(offhand)) {
                    return new ActionResult<>(EnumActionResult.FAIL, offhand);//no swinging 2-handed weapons on the offhand!
                }
                //System.out.println("nonnull");
                else if (worldIn.isRemote) {
                    Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, offhand));
                    if (elb != null) {
                        Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
                    }
                    p.swingArm(handIn);
                    TaoCasterData.getTaoCap(p).setOffhandCool(0);
                }
            }

        }
        return super.onItemRightClick(worldIn, p, handIn);
    }

    protected void splash(EntityLivingBase attacker, List<Entity> targets) {
        splash(attacker, targets, true);
    }

    protected void splash(EntityLivingBase attacker, List<Entity> targets, boolean enforceInFront) {
        for (Entity target : targets) {

            if (target == attacker) continue;
            ItemStack is = off ? attacker.getHeldItemOffhand() : attacker.getHeldItemMainhand();
            //!NeedyLittleThings.isFacingEntity(attacker,target)||
            if ((!NeedyLittleThings.isFacingEntity(attacker, target) && enforceInFront) || NeedyLittleThings.getDistSqCompensated(target, attacker) > getReach(attacker, is) * getReach(attacker, is))
                continue;
            try {
                if (off)
                    TaoCasterData.getTaoCap(attacker).setOffhandCool(TaoCasterData.getTaoCap(attacker).getSwing());
                else Taoism.setAtk(attacker, TaoCasterData.getTaoCap(attacker).getSwing());
            } catch (Exception ignored) {
            }
            if (attacker instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) attacker;
                NeedyLittleThings.taoWeaponAttack(target, p, attacker.getHeldItem(off ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND), !off, false);
            } else attacker.attackEntityAsMob(target);
        }
    }

    protected void splash(EntityLivingBase attacker, EntityLivingBase target, double diameter) {
        splash(attacker, attacker.world.getEntitiesInAABBexcluding(target, target.getEntityBoundingBox().grow(diameter / 2d, 1.5d, diameter / 2d), null));
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        chargeWeapon(attacker, defender, item, getMaxChargeTime());
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, int ticks) {
        if (isDummy(item) && attacker.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            chargeWeapon(attacker, defender, attacker.getHeldItemMainhand(), ticks);
            return;
        }
        item.setItemDamage(ticks);
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        if (isDummy(item) && elb.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            dischargeWeapon(elb, elb.getHeldItemMainhand());
            return;
        }
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

    private ItemStack makeDummy(ItemStack main, ItemStack wrapping) {
        while (isDummy(wrapping)) {
            wrapping = unwrapDummy(wrapping);
        }
        ItemStack ret = new ItemStack(this);
        ret.setTagCompound(gettagfast(main).copy());
        gettagfast(ret).setTag("sub", wrapping.writeToNBT(new NBTTagCompound()));
        gettagfast(ret).setBoolean("taodummy", true);
        gettagfast(ret).setTag("linked", main.writeToNBT(new NBTTagCompound()));
        //ret.addEnchantment(Enchantment.getEnchantmentByID(10),1);
        return ret;
    }

    private ItemStack unwrapDummy(ItemStack from) {
        NBTTagCompound sub = from.getSubCompound("sub");
        if (sub != null) {
            return new ItemStack(sub);
        }
        return ItemStack.EMPTY;
    }

    /**
     * @return whether the item is a dummy, i.e. alt attack
     */
    private boolean isDummy(ItemStack item) {
        return gettagfast(item).getBoolean("taodummy");
    }

    @Nullable
    protected EnumHand getHand(ItemStack item) {
        if (isDummy(item)) return EnumHand.OFF_HAND;
        if (gettagfast(item).hasKey("off")) {
            return gettagfast(item).getBoolean("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        }
        return null;
    }

    private void setHandState(ItemStack is, @Nullable EnumHand hand) {
        if (hand != null) {
            gettagfast(is).setBoolean("off", hand == EnumHand.OFF_HAND);
        } else {
            gettagfast(is).removeTag("off");
        }
    }

    private boolean dummyMatchMain(ItemStack is, ItemStack compare) {
        return isDummy(is) && ItemStack.areItemStacksEqual(new ItemStack(gettagfast(is).getCompoundTag("linked")), compare);
    }

    /**
     * gradually discharges the weapon
     */
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            boolean onOffhand = elb.getHeldItemOffhand() == stack;
            boolean onMainHand = elb.getHeldItemMainhand() == stack;
            //discharge weapon
            if (onMainHand || onOffhand) {
                if (stack.isItemDamaged()) stack.damageItem(-1, elb);
            } else {
                stack.setItemDamage(0);
                gettagfast(stack).removeTag("lastMove");
            }
            //TODO update swing and proc damage when it should
            //two handed shenanigans
            if (isTwoHanded(stack)) {
                //main hand, update offhand dummy
                if (onMainHand && !onOffhand) {
                    if (!dummyMatchMain(elb.getHeldItemOffhand(), stack)) {
                        elb.setHeldItem(EnumHand.OFF_HAND, makeDummy(elb.getHeldItemMainhand(), elb.getHeldItemOffhand()));
                    }
                    elb.getHeldItemOffhand().setItemDamage(stack.getItemDamage());
                }
                if (isDummy(stack)) {
                    boolean diffItem = elb.getHeldItemMainhand().getItem() != stack.getItem();
                    if (diffItem || !onOffhand) {//FIXME last slot overflow to first slot bug
                        //check where to unwrap the stack to
                        ItemStack unwrap = unwrapDummy(stack);
                        if (onOffhand) {
                            elb.setHeldItem(EnumHand.OFF_HAND, unwrap);
                        } else {
                            // Get the entity's main inventory
                            final IItemHandler mainInventory = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                            // If this item is in their main inventory and it can be extracted.
                            if (mainInventory != null && mainInventory.getStackInSlot(slot) == stack && mainInventory.extractItem(slot, stack.getCount(), true) != null) {
                                mainInventory.extractItem(slot, stack.getCount(), false);
                                mainInventory.insertItem(slot, unwrap, false);
                            }
                        }
                    }
                }

            }
            if (onMainHand) {
                setHandState(stack, EnumHand.MAIN_HAND);
            } else if (onOffhand) {
                setHandState(stack, EnumHand.OFF_HAND);
            } else {
                setHandState(stack, null);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (isDummy(stack)) {
            tooltip.add(I18n.format("weapon.dummy"));
            if (KeyOverlord.isShiftDown()) {
//            int pot = (int) getAffinities(stack)[i];
//            //System.out.println(pot);
//            int used = Math.round(pot / 2) * 2;
//            if (used != 0)
//                tooltip.add(IElemental.ELEMC[i] + "" + TextFormatting.ITALIC + I18n.format(IElemental.ELEMS[i] + used + ".inscription") + TextFormatting.RESET);
                statDesc(stack, worldIn, tooltip, flagIn);
            } else {
                tooltip.add(I18n.format("weapon.shift"));
            }
            return;
        }
//        for (Map.Entry<String, String> a : getParts(stack).entrySet()) {
//            try {
//                tooltip.add(I18n.format(a.getValue() + " " + a.getKey()));
//            } catch (Exception ignored) {
//
//            }
//        }
        //increment in 5, max is 50
        if (KeyOverlord.isShiftDown()) {
//            int pot = (int) getAffinities(stack)[i];
//            //System.out.println(pot);
//            int used = Math.round(pot / 2) * 2;
//            if (used != 0)
//                tooltip.add(IElemental.ELEMC[i] + "" + TextFormatting.ITALIC + I18n.format(IElemental.ELEMS[i] + used + ".inscription") + TextFormatting.RESET);
            statDesc(stack, worldIn, tooltip, flagIn);
        } else {
            tooltip.add(I18n.format("weapon.shift"));
        }
        //perks
        if (KeyOverlord.isControlDown()) {
            perkDesc(stack, worldIn, tooltip, flagIn);
        } else tooltip.add(TextFormatting.YELLOW + I18n.format("weapon.ctrl") + TextFormatting.RESET);

    }

    protected double speed(ItemStack stack) {
        return speed - 4d;
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

    public void setCombo(EntityLivingBase wielder, ItemStack stack, int to) {
        if (isDummy(stack) && wielder.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            setCombo(wielder, wielder.getHeldItemMainhand(), to);
            return;
        }
        gettagfast(stack).setInteger("combo", to % getComboLength(wielder, stack));
    }

    public long lastAttackTime(EntityLivingBase elb, ItemStack is) {
        return gettagfast(is).getLong("lastAttack");
    }

    public void updateLastAttackTime(EntityLivingBase wielder, ItemStack stack) {
        if (isDummy(stack) && wielder.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            updateLastAttackTime(wielder, wielder.getHeldItemMainhand());
            return;
        }
        gettagfast(stack).setLong("lastAttack", wielder.world.getTotalWorldTime());
    }

    public boolean isBroken(ItemStack stack) {
        return this.getMaxDamage(stack) - stack.getItemDamage() <= 1;
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        boolean[] h = harvestable(stack);
        if (h[0] && pickList.contains(state.getMaterial())) {
            return true;
        }
        if (h[1] && shovelList.contains(state.getMaterial())) {
            return true;
        }
        if (h[2] && axeList.contains(state.getMaterial())) {
            return true;
        }
        if (h[3] && scytheList.contains(state.getMaterial())) {
            return true;
        }
        //System.out.println(ret);
        return super.canHarvestBlock(state, stack);
    }

    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        afterSwing(elb, is);

        return getCombo(elb, is) != getComboLength(elb, is) - 1 ? 0.8f : 0f;
    }

//    public EnumPhase getPhase(final ItemStack stack){
//
//    }

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

    public boolean canBlock(EntityLivingBase defender, ItemStack item) {
        return true;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            TaoCasterData.getTaoCap(elb).setOffhandCool(2);
        }
    }

    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
        dischargeWeapon(elb, is);
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {

    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        buffer = orig;
        return orig;
    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
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
