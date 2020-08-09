package com.jackiecrazi.taoism.common.item.weapon.melee;

import com.google.common.collect.Multimap;
import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.MoveCode;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.*;
import com.jackiecrazi.taoism.capability.ITaoStatCapability;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.client.ClientEvents;
import com.jackiecrazi.taoism.client.KeyBindOverlord;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.handler.TaoCombatHandler;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import com.jackiecrazi.taoism.utils.TaoMovementUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class TaoWeapon extends Item implements IAmModular, IElemental, IRange, ICombatManipulator, IStaminaPostureManipulable, ICombo, IDamageType, ISpecialSwitchIn, IChargeableWeapon, ITwoHanded, IMove {
    public static final ArrayList<Item> listOfWeapons = new ArrayList<>();
    protected static final UUID QI_MODIFIER = UUID.fromString("8e948b44-7560-11ea-bc55-0242ac130003");
    protected static final UUID QI_EXECUTION = UUID.fromString("8e948b44-7560-11ea-bc55-0242ac130013");
    //booleans only used when attacking to determine what type of attack it is
    //0
    private static final List<Material> pickList = Arrays.asList(
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
    private static final List<Material> shovelList = Arrays.asList(
            Material.CAKE,
            Material.CLAY,
            Material.CRAFTED_SNOW,
            Material.GRASS,
            Material.GROUND,
            Material.CARPET,
            Material.SAND,
            Material.SNOW);
    //2
    private static final List<Material> axeList = Arrays.asList(
            Material.CACTUS,
            Material.CLOTH,
            Material.CORAL,
            Material.GOURD,
            Material.WOOD);
    //3
    private static final List<Material> scytheList = Arrays.asList(
            Material.LEAVES,
            Material.PLANTS,
            Material.VINE);
    private final double speed;
    private final int damageType;
    private final double dmg;
    private final float itemPostureMultiplier;
    protected float qiRate = 0.25f;

    public TaoWeapon(int damageType, double swingSpeed, double damage, float attackPostureMultiplier) {
        super();
        this.damageType = damageType;
        speed = swingSpeed;
        this.setMaxDamage(getMaxChargeTime());
        this.setMaxStackSize(1);
        dmg = damage;
        itemPostureMultiplier = attackPostureMultiplier;
        this.setCreativeTab(Taoism.tabWea);
        String name = getClass().getSimpleName().toLowerCase();
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        listOfWeapons.add(this);
        this.addPropertyOverride(new ResourceLocation("offhand"), (stack, w, elb) -> {
            if (elb != null) {
                if (elb.getHeldItemOffhand() == stack && (!isTwoHanded(stack) || isDummy(stack))) return 1;
            }
            return 0;
        });
    }

    public boolean isTwoHanded(ItemStack is) {
        return false;
    }

    /**
     * @return whether the item is a dummy, i.e. alt attack
     */
    protected boolean isDummy(ItemStack item) {
        return gettagfast(item).getBoolean("taodummy");
    }

    protected NBTTagCompound gettagfast(ItemStack is) {
        if (is.getItem() instanceof TaoWeapon) {
            if (!is.hasTagCompound()) is.setTagCompound(new NBTTagCompound());
            return is.getTagCompound();
        }
        return new NBTTagCompound();
    }

    public EnumPhase getPhase(final ItemStack stack) {
        return EnumPhase.IDLE;
    }

    protected void setQiAccumulationRate(float amount) {
        qiRate = amount;
    }

    public double getDamage() {
        return dmg;
    }

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

    protected void oncePerHit(EntityLivingBase attacker, EntityLivingBase target, ItemStack is) {

    }

    private void updateWielderDataEnd(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target) {
        if (isDummy(stack) && attacker.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            updateWielderDataEnd(attacker.getHeldItemMainhand(), attacker, target);
            return;
        }
        NBTTagCompound ntc = gettagfast(stack);
        setLastAttackedEntity(stack, target);
        ntc.setBoolean("connect", false);
        ntc.setBoolean("effect", false);
        //ntc.setByte("lastMove", new MoveCode(true, ).toByte());
    }

    protected MoveCode getLastMove(ItemStack stack) {
        return new MoveCode(gettagfast(stack).getByte("lastMove"));
    }

    protected MoveCode getCurrentMove(ItemStack stack) {
        return new MoveCode(gettagfast(stack).getByte("currentMove"));
    }

    protected boolean isOffhandEmpty(ItemStack stack) {
        return gettagfast(stack).getBoolean("dual");
    }

    protected Entity getLastAttackedEntity(World w, ItemStack stack) {
        if (!gettagfast(stack).hasKey("lastAttackedID")) return null;
        return w.getEntityByID(gettagfast(stack).getInteger("lastAttackedID"));
    }

    /**
     * when you don't really need that lookup
     */
    protected int getLastAttackedEntityID(ItemStack stack) {
        return gettagfast(stack).getInteger("lastAttackedID");
    }

    /**
     * use sparingly.
     */
    protected void setLastAttackedEntity(ItemStack stack, @Nullable Entity e) {
        if (e == null) gettagfast(stack).removeTag("lastAttackedID");
        else gettagfast(stack).setInteger("lastAttackedID", e.getEntityId());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (canHarvestBlock(state, stack)) {
            return 15.0F;
        } else {
            return 1.5f;
        }
    }

    /**
     * Called when the equipped item is right clicked.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer p, EnumHand handIn) {
        if (handIn == EnumHand.OFF_HAND) {
            ItemStack offhand = p.getHeldItemOffhand();
            if (!offhand.isEmpty() && TaoCombatHandler.lastRightClickTime.getOrDefault(p.getEntityId(), 0L) + 5 < worldIn.getTotalWorldTime()) {
                if (isDummy(offhand) && p.getHeldItemMainhand().getItem() != offhand.getItem()) {
                    p.setHeldItem(EnumHand.OFF_HAND, unwrapDummy(offhand));
                }
                if (isTwoHanded(offhand) && !isDummy(offhand)) {
                    return new ActionResult<>(EnumActionResult.FAIL, offhand);//no swinging 2-handed weapons on the offhand!
                }
                //setAttackFlag(p, offhand, true);
                //p.setActiveHand(EnumHand.OFF_HAND);
                Entity e = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, offhand));
                if (e != null) {
                    TaoCombatUtils.taoWeaponAttack(e, p, offhand, false, true);
                }
                float temp = p.getCooledAttackStrength(0.5f);
                //p.randomUnused1 = temp;
                p.swingArm(handIn);
                TaoCombatUtils.rechargeHand(p, EnumHand.MAIN_HAND, temp, true);
                TaoCasterData.getTaoCap(p).setOffhandCool(0);
                if (!worldIn.isRemote)
                    TaoCombatHandler.lastRightClickTime.put(p.getEntityId(), worldIn.getTotalWorldTime());
                return new ActionResult<>(EnumActionResult.SUCCESS, offhand);
            }
            if (!worldIn.isRemote) {
                TaoCombatHandler.lastRightClickTime.put(p.getEntityId(), worldIn.getTotalWorldTime());
            }
        }
        return super.onItemRightClick(worldIn, p, handIn);
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        //updateWielderDataStart(stack, attacker, target);
        int chi = 0;
        boolean swing = true;
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(attacker);
        chi = itsc.getQiFloored();
        swing = !(attacker instanceof EntityPlayer) || itsc.getSwing() >= 0.9f;
        if (swing) {
            if (!gettagfast(stack).getBoolean("connect")) {
                gettagfast(stack).setBoolean("connect", true);
                if (getHand(stack) != null && !attacker.world.isRemote && !isCharged(attacker, attacker.getHeldItemMainhand()) && !isCharged(attacker, attacker.getHeldItemOffhand())) {
                    float baseQi = ((float) NeedyLittleThings.getAttributeModifierHandSensitive(TaoEntities.QIRATE, attacker, getHand(stack)));
                    itsc.addQi(baseQi);
                    TaoCasterData.forceUpdateTrackingClients(attacker);
                }
                oncePerHit(attacker, target, stack);
            }
            if (!gettagfast(stack).getBoolean("effect")) {
                gettagfast(stack).setBoolean("effect", true);
                applyEffects(stack, target, attacker, chi);
                gettagfast(stack).setBoolean("effect", false);
            }
            if (!gettagfast(stack).getBoolean("spawning") && gettagfast(stack).getLong("multiHitTill") < attacker.world.getTotalWorldTime()) {
                gettagfast(stack).setBoolean("spawning", true);
                followUp(stack, target, attacker, chi);
                gettagfast(stack).setBoolean("spawning", false);
            }
        }
        updateWielderDataEnd(stack, attacker, target);
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (harvestable(itemstack)[3]) {
            if (entity.world.isRemote) {
                return false;
            }
            if (entity instanceof net.minecraftforge.common.IShearable) {
                net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) entity;
                BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
                if (target.isShearable(itemstack, entity.world, pos)) {
                    java.util.List<ItemStack> drops = target.onSheared(itemstack, entity.world, pos,
                            net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));

                    java.util.Random rand = new java.util.Random();
                    for (ItemStack stack : drops) {
                        net.minecraft.entity.item.EntityItem ent = entity.entityDropItem(stack, 1.0F);
                        ent.motionY += rand.nextFloat() * 0.05F;
                        ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    /**
     * gradually discharges the weapon
     */
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onHand) {
        if (e instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) e;
            ItemStack offhand = elb.getHeldItemOffhand();
            boolean onOffhand = offhand == stack;
            ItemStack mainhand = elb.getHeldItemMainhand();
            boolean onMainHand = mainhand == stack;
            final NBTTagCompound tag = gettagfast(stack);
            if (onMainHand) {
                setHandState(stack, EnumHand.MAIN_HAND);
                boolean single = offhand.isEmpty() || (isDummy(offhand) && offhand.getItem() == mainhand.getItem());
                tag.setBoolean("dual", single);
            } else if (onOffhand) {
                setHandState(stack, EnumHand.OFF_HAND);
                tag.removeTag("dual");
            } else {
                setHandState(stack, null);
                tag.removeTag("dual");
            }
            //discharge weapon
            if (onMainHand || onOffhand) {
                if (stack.isItemDamaged()) stack.setItemDamage(stack.getItemDamage() - chargePerTick(stack));
            } else {
                stack.setItemDamage(0);
                tag.removeTag("lastMove");
            }
            //multihit shenanigans
            if (tag.hasKey("multiHitTarget")) {
                //multiHitFrom/Till/Target/Interval
                Entity victim = w.getEntityByID(tag.getInteger("multiHitTarget"));
                if (victim != null) {
                    long from = tag.getLong("multiHitFrom");
                    long till = tag.getLong("multiHitTill");
                    long curr = w.getTotalWorldTime();
                    int interval = tag.getInteger("multiHitInterval");
                    if (till >= curr && from != curr && (curr - from) % interval == 0) {
                        performScheduledAction(elb, victim, stack, curr - from, interval);
                    }
                    if (curr == till) {
                        endScheduledAction(elb, victim, stack, interval);
                    }
                }
            }
            //two handed shenanigans
            if (isTwoHanded(stack)) {
                //main hand, update offhand dummy
                if (onMainHand && !onOffhand) {
                    if (!isDummy(offhand) || offhand.getItem() != mainhand.getItem()) {
                        elb.setHeldItem(EnumHand.OFF_HAND, makeDummy(mainhand, offhand));
                    }
                    offhand.setItemDamage(stack.getItemDamage());
                    if (!dummyMatchMain(offhand, stack)) {
                        NBTTagCompound cop = gettagfast(mainhand).copy();
                        cop.setBoolean("taodummy", gettagfast(offhand).getBoolean("taodummy"));
                        cop.setTag("sub", gettagfast(offhand).getCompoundTag("sub"));
                        offhand.setTagCompound(cop);
                    }
                }
            }
            if (isDummy(stack)) {
                boolean diffItem = mainhand.getItem() != stack.getItem();
                boolean stillTwoHanded = isTwoHanded(mainhand);
                if (diffItem || !onOffhand || !stillTwoHanded) {
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
            if (!w.isRemote) {
                final Entity collidee = TaoMovementUtils.collidingEntity(elb);
                if ((onMainHand || onOffhand) && collidee != null) {
//                    System.out.println(collidee.getEntityId());
//                    System.out.println(getBuff(stack, "collidingWith"));
//                    System.out.println(collidee.getEntityId() != getBuff(stack, "collidingWith"));
                    if (collidee.getEntityId() != getBuff(stack, "collidingWith")) {
                        if (onCollideWithEntity(elb, collidee, stack)) {
                            setBuff(elb, stack, "collidingWith", collidee.getEntityId());
                        }
                    }
                } else setBuff(elb, stack, "collidingWith", 0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (isDummy(stack)) {
            tooltip.add(I18n.format("weapon.dummy"));
            if (KeyBindOverlord.isShiftDown()) {
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
        if (KeyBindOverlord.isShiftDown()) {
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
        if (KeyBindOverlord.isControlDown()) {
            perkDesc(stack, worldIn, tooltip, flagIn);
        } else tooltip.add(TextFormatting.YELLOW + I18n.format("weapon.ctrl") + TextFormatting.RESET);

    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage(stack) - 1, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed(stack), 0));
            multimap.put(TaoEntities.QIRATE.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", getQiAccumulationRate(stack) * (1 + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack) / 4f), 0));
            multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", getTrueReach(null, stack) - 3, 0));
            multimap.put(TaoEntities.MAXPOSTURE.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) / 10f, 1));
            multimap.put(TaoEntities.POSREGEN.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) / 5f, 1));
            //for (int x = 0; x < IElemental.ATTRIBUTES.length; x++)
            //multimap.put(IElemental.ATTRIBUTES[x].getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAffinity(stack, x), 0));
        } else if (equipmentSlot == EntityEquipmentSlot.OFFHAND) {
            multimap.put(TaoEntities.MAXPOSTURE.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) / 10f, 1));
            multimap.put(TaoEntities.POSREGEN.getName(), new AttributeModifier(QI_MODIFIER, "Weapon modifier", EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) / 5f, 1));
        }
        return multimap;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        if (harvestable(itemstack)[3]) {
            if (player.world.isRemote || player.capabilities.isCreativeMode) {
                return false;
            }
            Block block = player.world.getBlockState(pos).getBlock();
            if (block instanceof net.minecraftforge.common.IShearable) {
                net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) block;
                if (target.isShearable(itemstack, player.world, pos)) {
                    java.util.List<ItemStack> drops = target.onSheared(itemstack, player.world, pos,
                            net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));
                    java.util.Random rand = new java.util.Random();

                    for (ItemStack stack : drops) {
                        float f = 0.7F;
                        double d = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        double d1 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        double d2 = (double) (rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
                        net.minecraft.entity.item.EntityItem entityitem = new net.minecraft.entity.item.EntityItem(player.world, (double) pos.getX() + d, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
                        entityitem.setDefaultPickupDelay();
                        player.world.spawnEntity(entityitem);
                    }
                    player.addStat(net.minecraft.stats.StatList.getBlockStats(block));
                    player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        EnumHand hand = getHand(stack);
        if (hand == null) {
            hand = player.swingingHand;
        }
        if (hand == null && player.isSwingInProgress) {
            //expensive? perhaps.
            hand = player.getHeldItemMainhand() == stack ? EnumHand.MAIN_HAND : player.getHeldItemOffhand() == stack ? EnumHand.OFF_HAND : null;
        }
        if (hand != null)
            TaoCasterData.getTaoCap(player).setSwing(TaoCombatUtils.getHandCoolDown(player, hand));
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        if (isDummy(entityItem.getItem())) {
            entityItem.setDead();
            return true;
        }
        return super.onEntityItemUpdate(entityItem);
    }

    public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack) {
        if (!(elb instanceof EntityPlayer)) {
            boolean onMainHand = elb.getHeldItemMainhand() == stack, onOffhand = elb.getHeldItemOffhand() == stack;
            ItemStack offhand = elb.getHeldItemOffhand(), mainhand = elb.getHeldItemMainhand();
            NBTTagCompound tag = gettagfast(stack);
            if (onMainHand) {
                setHandState(stack, EnumHand.MAIN_HAND);
                boolean single = offhand.isEmpty() || (isDummy(offhand) && offhand.getItem() == mainhand.getItem());
                tag.setBoolean("dual", single);
            } else if (onOffhand) {
                setHandState(stack, EnumHand.OFF_HAND);
                tag.removeTag("dual");
            } else {
                setHandState(stack, null);
                tag.removeTag("dual");
            }
            //discharge weapon
            if (onMainHand || onOffhand) {
                if (stack.isItemDamaged()) stack.setItemDamage(stack.getItemDamage() - chargePerTick(stack));
            } else {
                stack.setItemDamage(0);
                tag.removeTag("lastMove");
            }
            //two handed shenanigans
            if (isTwoHanded(stack)) {
                //main hand, update offhand dummy
                if (onMainHand && !onOffhand) {
                    if (!isDummy(offhand) || offhand.getItem() != mainhand.getItem()) {
                        elb.setHeldItem(EnumHand.OFF_HAND, makeDummy(mainhand, offhand));
                    }
                    offhand.setItemDamage(stack.getItemDamage());
                    if (!dummyMatchMain(offhand, stack)) {
                        NBTTagCompound cop = gettagfast(mainhand).copy();
                        cop.setBoolean("taodummy", gettagfast(offhand).getBoolean("taodummy"));
                        cop.setTag("sub", gettagfast(offhand).getCompoundTag("sub"));
                        offhand.setTagCompound(cop);
                    }
                }
            }
            if (isDummy(stack)) {
                boolean diffItem = mainhand.getItem() != stack.getItem();
                boolean stillTwoHanded = isTwoHanded(mainhand);
                if (diffItem || !onOffhand || !stillTwoHanded) {
                    //check where to unwrap the stack to
                    ItemStack unwrap = unwrapDummy(stack);
                    if (onOffhand) {
                        elb.setHeldItem(EnumHand.OFF_HAND, unwrap);
                    }
                }
            }
        } else {
            elb.getAttributeMap().onAttributeModified(elb.getEntityAttribute(EntityPlayer.REACH_DISTANCE));
        }
        EnumHand hand = getHand(stack);
        if (hand == null) {
            hand = elb.swingingHand;
        }
        float range = getReach(elb, stack);
        Entity rte = NeedyLittleThings.raytraceEntity(elb.world, elb, range);
        if (hand != null && (TaoCombatUtils.getHandCoolDown(elb, hand) > 0.9f || rte != null)) {
            //Well, ya got me. By all accounts, it doesn't make sense.
            TaoCasterData.getTaoCap(elb).setOffhandAttack(hand == EnumHand.OFF_HAND);
            //TaoCasterData.getTaoCap(entityLiving).setSwing(TaoCombatUtils.getHandCoolDown(entityLiving, hand));//commented out because this causes swing to reset before damage dealt
            aoe(stack, elb, TaoCasterData.getTaoCap(elb).getQiFloored());
            gettagfast(stack).setBoolean("connect", false);
        }
        return super.onEntitySwing(elb, stack);
    }

    @Override
    public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        return false;
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        if (state.getBlock() == Blocks.WEB) return true;
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

    public int getItemEnchantability(ItemStack stack) {
        return 14;
    }

    /**
     * Checks whether an item can be enchanted with a certain enchantment. This applies specifically to enchanting an item in the enchanting table and is called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)}; check the individual implementation for reference.
     * By default this will check if the enchantment type is valid for this item type.
     *
     * @param stack       the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.type != null && (enchantment.type.canEnchantItem(Items.IRON_SWORD));
    }

    /**
     * Here's a little lesson in trickery!
     */
    @SideOnly(value = Side.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean changed) {
        if (getHand(oldStack) == EnumHand.OFF_HAND) {
            if (!changed) try {
                float swing = TaoCombatUtils.getCooledAttackStrengthOff(Minecraft.getMinecraft().player, 1f);
                float newSwing = ClientEvents.okuyasu.getFloat(Minecraft.getMinecraft().getItemRenderer());
                newSwing += MathHelper.clamp((swing * swing * swing) - newSwing, -0.4F, 0.4F);
                ClientEvents.zaHando.setFloat(Minecraft.getMinecraft().getItemRenderer(), newSwing);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return changed;
    }    @Override
    public int getComboLength(EntityLivingBase wielder, ItemStack is) {
        return 1;
    }

    public double attackDamage(ItemStack stack) {
        return dmg;
    }

    protected double speed(ItemStack stack) {
        return speed - 4d;
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return isCharged(null, is) ? 0 : qiRate;
    }

    /**
     * default method! Override for complex weapons!
     */
    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", getTrueReach(null, stack)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.YELLOW + I18n.format("taoism.weaponDefMult", postureMultiplierDefend(null, null, stack, 0)) + TextFormatting.RESET);
        tooltip.add(TextFormatting.RED + I18n.format("taoism.weaponAttMult", postureDealtBase(null, null, stack, 1)) + TextFormatting.RESET);
    }

    abstract protected void perkDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn);

    private ItemStack unwrapDummy(ItemStack from) {
        NBTTagCompound sub = from.getSubCompound("sub");
        if (sub != null) {
            ItemStack is = new ItemStack(sub);
            if (!is.hasTagCompound() || is.getTagCompound().hasNoTags()) is.setTagCompound(null);
            return is;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public float getReach(EntityLivingBase p, ItemStack is) {
        if (getTrueReach(p, is) == 0) return 0;
        return getExtraReach(p, is);
    }

    public abstract float getTrueReach(EntityLivingBase elb, ItemStack is);

    protected float getExtraReach(EntityLivingBase elb, ItemStack is) {
        if (elb != null && elb.getEntityAttribute(EntityPlayer.REACH_DISTANCE) != null) {
            double ret = NeedyLittleThings.getAttributeModifierHandSensitive(EntityPlayer.REACH_DISTANCE, elb, getHand(is)) - 2d;
            return (float) ret;
        }
        return 0;
    }

    @Nullable
    protected EnumHand getHand(ItemStack item) {
        if (isDummy(item)) return EnumHand.OFF_HAND;
        if (gettagfast(item).hasKey("off")) {
            return gettagfast(item).getBoolean("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        }
        return null;
    }

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    protected boolean[] harvestable(ItemStack is) {
        return new boolean[]{false, false, false, false};
    }

    protected boolean onCollideWithEntity(EntityLivingBase elb, Entity collidingEntity, ItemStack stack) {
        return true;
    }

    /**
     * AoE
     */
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {

    }

    protected double getDamageAgainst(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack) {
        return NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_DAMAGE, attacker, getHand(stack)) + EnchantmentHelper.getModifierForCreature(stack, target.getCreatureAttribute());
    }

    private void setHandState(ItemStack is, @Nullable EnumHand hand) {
        if (hand != null) {
            gettagfast(is).setBoolean("off", hand == EnumHand.OFF_HAND);
        } else {
            gettagfast(is).removeTag("off");
        }
    }

    private ItemStack makeDummy(ItemStack main, ItemStack wrapping) {
        while (isDummy(wrapping)) {
            wrapping = unwrapDummy(wrapping);
        }
        ItemStack ret = new ItemStack(this);
        ret.setTagCompound(gettagfast(main).copy());
        gettagfast(ret).setTag("sub", wrapping.writeToNBT(new NBTTagCompound()));
        gettagfast(ret).setBoolean("taodummy", true);
        //ret.addEnchantment(Enchantment.getEnchantmentByID(10),1);
        return ret;
    }

    private boolean dummyMatchMain(ItemStack is, ItemStack compare) {
        NBTTagCompound nbt = gettagfast(is).copy();
        nbt.removeTag("taodummy");
        nbt.removeTag("sub");
        return nbt.equals(compare.getTagCompound());
    }

    public int getDamageType(ItemStack is) {
        return damageType;
    }

    protected void splash(EntityLivingBase attacker, ItemStack is, int angleAllowance) {
        splash(attacker, is, angleAllowance, angleAllowance);
    }

    protected void splash(EntityLivingBase attacker, ItemStack is, int horAngle, int vertAngle) {
        if (attacker.world.isRemote) return;
        splash(attacker, NeedyLittleThings.raytraceEntity(attacker.world, attacker, getReach(attacker, is)), is, horAngle, vertAngle, attacker.world.getEntitiesInAABBexcluding(null, attacker.getEntityBoundingBox().grow(getReach(attacker, is)), TaoCombatUtils.VALID_TARGETS::test));
    }

    protected int getQiFromStack(ItemStack stack) {
        return gettagfast(stack).getInteger("qifloor");
    }

    protected void splash(EntityLivingBase attacker, ItemStack stack, List<Entity> targets) {
        splash(attacker, null, stack, 90, targets);
    }

    protected void splash(EntityLivingBase attacker, Entity ignored, ItemStack is, int degrees, List<Entity> targets) {
        splash(attacker, ignored, is, degrees, degrees, targets);
    }

    protected void additionalSplashAction(EntityLivingBase attacker, Entity target, ItemStack is) {

    }

    protected void splash(EntityLivingBase attacker, Entity ignored, ItemStack is, int horDeg, int vertDeg, List<Entity> targets) {
        if (attacker.world.isRemote) return;
        boolean sweep = false;
        for (Entity target : targets) {
            if (target == attacker || attacker.isRidingOrBeingRiddenBy(target)) continue;
            //!NeedyLittleThings.isFacingEntity(attacker,target)||
            if ((horDeg != 360 && vertDeg != 360 && !NeedyLittleThings.isFacingEntity(attacker, target, horDeg, vertDeg)) || NeedyLittleThings.getDistSqCompensated(target, attacker) > getReach(attacker, is) * getReach(attacker, is) || target == ignored)
                continue;
            TaoCombatUtils.attackAtStrength(attacker, target, getHand(is), TaoCasterData.getTaoCap(attacker).getSwing(), TaoCombatUtils.causeLivingDamage(attacker));
            additionalSplashAction(attacker, target, is);
            sweep = true;
        }
        if (sweep && attacker instanceof EntityPlayer) {
            ((EntityPlayer) attacker).spawnSweepParticles();
        }
    }

    @Override
    public boolean canCharge(EntityLivingBase wielder, ItemStack item) {
        /*
        At 9+ qi, long press attack to charge weapon. Once charged, attack (swing) to begin the sequence.
         */
        if ((wielder instanceof EntityPlayer && ((EntityPlayer) wielder).isCreative())) {
            TaoCasterData.getTaoCap(wielder).setQi(10);
            return true;
        }
        if ((TaoCasterData.getTaoCap(wielder).getQi() < 9)) {
            if (wielder instanceof EntityPlayer)
                ((EntityPlayer) wielder).sendStatusMessage(new TextComponentTranslation("weapon.notenoughqi"), true);
            return false;
        }
        if (isCharged(wielder, item)) {
            if (wielder instanceof EntityPlayer)
                ((EntityPlayer) wielder).sendStatusMessage(new TextComponentTranslation("weapon.alreadycharged"), true);
            return false;
        }
        return true;
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item) {
        if (isDummy(item) && attacker.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            chargeWeapon(attacker, attacker.getHeldItemMainhand());
            return;
        }
        gettagfast(item).setBoolean("charge", true);
        gettagfast(item).setLong("chargedAtTime", attacker.world.getTotalWorldTime());
        //attacker.getEntityAttribute(TaoEntities.QIRATE).removeModifier(QI_EXECUTION);
        //attacker.getEntityAttribute(TaoEntities.QIRATE).applyModifier(new AttributeModifier(QI_EXECUTION, "executing", -1, 2));
    }

    @Override
    public void dischargeWeapon(EntityLivingBase elb, ItemStack item) {
        if (isDummy(item) && elb.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            dischargeWeapon(elb, elb.getHeldItemMainhand());
            return;
        }
        gettagfast(item).setBoolean("charge", false);
        gettagfast(item).setLong("chargedAtTime", 0);
        gettagfast(item).setLong("startAt", 0);
        elb.getEntityAttribute(TaoEntities.QIRATE).removeModifier(QI_EXECUTION);
    }

    @Override
    public boolean isCharged(EntityLivingBase elb, ItemStack item) {
        return gettagfast(item).getBoolean("charge");
    }

    @Override
    public int getChargedTime(EntityLivingBase elb, ItemStack item) {
        if (elb == null) return 0;
        return (int) (elb.world.getTotalWorldTime() - gettagfast(item).getLong("chargedAtTime"));
    }

    @Override
    public int getMaxChargeTime() {
        return 0;
    }

    protected void setLastAttackedRangeSq(EntityLivingBase attacker, ItemStack item, float range) {
        if (isDummy(item) && attacker.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            setLastAttackedRangeSq(attacker, attacker.getHeldItemMainhand(), range);
            return;
        }
        if (range != 0f) {
            gettagfast(item).setFloat("lastAttackedRange", range);
        } else {
            gettagfast(item).removeTag("lastAttackedRange");
        }
    }

    protected float getLastAttackedRangeSq(ItemStack is) {
        return gettagfast(is).getFloat("lastAttackedRange");
    }

    /**
     * Used by kusarigama and other charge type weapons
     *
     * @return how much to discharge per second
     */
    protected int chargePerTick(ItemStack is) {
        return 0;
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

    public boolean isBroken(ItemStack stack) {
        return this.getMaxDamage(stack) - stack.getItemDamage() <= 1;
    }

    protected void scheduleExtraAction(EntityLivingBase attacker, ItemStack stack, Entity target, int duration, int interval) {
        if (isDummy(stack) && attacker.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            scheduleExtraAction(attacker, attacker.getHeldItemMainhand(), target, duration, interval);
            setBuff(attacker, attacker.getHeldItemMainhand(), "multiHitHand", -1);
            return;
        }
        if (gettagfast(stack).getLong("multiHitTill") < attacker.world.getTotalWorldTime()) {
            setBuff(attacker, stack, "multiHitHand", 1);
            stack.setTagInfo("multiHitTarget", new NBTTagInt(target.getEntityId()));
            stack.setTagInfo("multiHitFrom", new NBTTagLong(attacker.world.getTotalWorldTime()));
            stack.setTagInfo("multiHitTill", new NBTTagLong(attacker.world.getTotalWorldTime() + duration));
            stack.setTagInfo("multiHitInterval", new NBTTagInt(interval));
        }
    }

    protected void performScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, long l, int interval) {
        if (NeedyLittleThings.getDistSqCompensated(elb, victim) < getReach(elb, stack) * getReach(elb, stack)) {
            TaoCombatUtils.rechargeHand(elb, getHand(stack), 1, false);
            victim.hurtResistantTime = 0;
            TaoCombatUtils.taoWeaponAttack(victim, elb, stack, isTwoHanded(stack) ? getBuff(stack, "multiHitHand") > 0 : getHand(stack) == EnumHand.MAIN_HAND, false);
        }
    }

    protected void endScheduledAction(EntityLivingBase elb, Entity victim, ItemStack stack, int interval) {

    }

    public boolean canBlock(EntityLivingBase defender, Entity attacker, ItemStack item, boolean recharged, float amount) {
        return recharged && NeedyLittleThings.isFacingEntity(defender, attacker, 120);
    }

    @Override
    public void onParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
    }

    @Override
    public void onOtherHandParry(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {

    }

    @Override
    public float postureDealtBase(@Nullable EntityLivingBase attacker, @Nullable EntityLivingBase defender, ItemStack item, float amount) {
        return itemPostureMultiplier * (getDamDist(item)) * (float) dmg;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            TaoCasterData.getTaoCap(elb).setOffhandCool(2);
        }
    }

    @Override
    public boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
//        if (NeedyLittleThings.raytraceEntity(attacker.world, attacker, 5) == target && NeedyLittleThings.getDistSqCompensated(attacker, target) > getReach(attacker, item) * getReach(attacker, item))
//            return false;
        if (isTwoHanded(item) && getHand(item) == EnumHand.OFF_HAND && !isDummy(item)) return false;
        return attacker != target; //getReach(attacker, item) * getReach(attacker, item) > NeedyLittleThings.getDistSqCompensated(attacker, target); //screw it.
    }

    public Event.Result critCheck(EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float crit, boolean vanCrit) {
        return Event.Result.DEFAULT;
        //recharged, fallen more than 0 blocks, not on ground, not on ladder, not in water, not blind, not riding, target is ELB
    }

    @Override
    public float critDamage(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1.5f;
    }

    @Override
    public float damageMultiplier(EntityLivingBase attacker, EntityLivingBase target, ItemStack item) {
        return 1f;
    }

    public void attackStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        updateWielderDataStart(stack, attacker, target);
        target.hurtResistantTime = 0;
    }

    @Override
    public float knockback(EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig;
    }

    @Override
    public float hurtStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig;
    }

//    public EnumPhase getPhase(final ItemStack stack){
//
//    }

    @Override
    public float damageStart(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return 0;
    }

    @Override
    public float onBeingHurt(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount) {
        return amount;
    }

    @Override
    public float onBeingDamaged(DamageSource ds, EntityLivingBase defender, ItemStack item, float amount) {
        return amount;
    }

    @Override
    public float onStoppedRecording(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return orig;
    }

    /**
     * generally applies some effects
     */
    protected void applyEffects(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    /**
     * spawns entities etc.
     */
    protected void followUp(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

    }

    protected void setBuff(EntityLivingBase elb, ItemStack is, String name, int amount) {
        if (isDummy(is) && elb.getHeldItemMainhand() != is)
            setBuff(elb, elb.getHeldItemMainhand(), name, amount);
        gettagfast(is).setInteger(name, amount);
    }

    protected int getBuff(ItemStack is, String name) {
        return gettagfast(is).getInteger(name);
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


    public float newCooldown(EntityLivingBase elb, ItemStack is) {
        afterSwing(elb, is);
        return getCombo(elb, is) != getComboLength(elb, is) - 1 ? 0.8f : 0f;
    }


    @Override
    public long lastAttackTime(EntityLivingBase elb, ItemStack is) {
        if (isCharged(elb, is)) return elb.world.getTotalWorldTime();
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


    protected void afterSwing(EntityLivingBase elb, ItemStack is) {
    }


}
