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
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
        return EnumPhase.IDLE;//TODO ?
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

    private void updateWielderDataEnd(ItemStack stack, EntityLivingBase attacker, EntityLivingBase target) {
        if (isDummy(stack) && attacker.getHeldItemMainhand() != stack) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            updateWielderDataEnd(attacker.getHeldItemMainhand(), attacker, target);
            return;
        }
        NBTTagCompound ntc = gettagfast(stack);
        ntc.setInteger("lastAttackedID", target.getEntityId());
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
            if (!offhand.isEmpty() && NeedyLittleThings.getCooledAttackStrengthOff(p, 0.5f) == 1f) {
                if (isDummy(offhand) && p.getHeldItemMainhand().getItem() != offhand.getItem()) {
                    p.setHeldItem(EnumHand.OFF_HAND, unwrapDummy(offhand));
                }
                if (isTwoHanded(offhand) && !isDummy(offhand)) {
                    return new ActionResult<>(EnumActionResult.FAIL, offhand);//no swinging 2-handed weapons on the offhand!
                }
//                if (worldIn.isRemote) {
//                    Entity elb = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, offhand));
//                    if (elb != null) {
//                        Taoism.net.sendToServer(new PacketExtendThyReach(elb.getEntityId(), false));
//                    }
//                    else {
//                        p.swingArm(handIn);
//                        TaoCasterData.getTaoCap(p).setOffhandCool(0);
//                    }
//                } else {
//                    EntityPlayerMP mp = (EntityPlayerMP) p;
//                    mp.getServerWorld().addScheduledTask(() -> {
//                        mp.swingArm(handIn);
//                        TaoCasterData.getTaoCap(mp).setOffhandCool(0);
//                    });
//                }
                Entity e = NeedyLittleThings.raytraceEntity(p.world, p, getReach(p, offhand));
                if (e != null) {
                    TaoCombatUtils.taoWeaponAttack(e, p, offhand, false, true);
                }
                float temp = p.getCooledAttackStrength(0.5f);
                p.swingArm(handIn);
                TaoCombatUtils.rechargeHand(p, EnumHand.MAIN_HAND, temp);
                TaoCasterData.getTaoCap(p).setOffhandCool(0);
                return new ActionResult<>(EnumActionResult.SUCCESS, offhand);
            }

        }
        return super.onItemRightClick(worldIn, p, handIn);
    }

    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        updateWielderDataStart(stack, attacker, target);
        int chi = 0;
        boolean swing = true;
        ITaoStatCapability itsc = TaoCasterData.getTaoCap(attacker);
        chi = itsc.getQiFloored();
        swing = !(attacker instanceof EntityPlayer) || itsc.getSwing() >= 0.9f;
        if (swing) {
            if (!gettagfast(stack).getBoolean("connect")) {
                gettagfast(stack).setBoolean("connect", true);
                if (getHand(stack) != null && !attacker.world.isRemote) {
                    float baseQi = ((float) NeedyLittleThings.getAttributeModifierHandSensitive(TaoEntities.QIRATE, attacker, getHand(stack)));
                    itsc.addQi(baseQi);
                    TaoCasterData.forceUpdateTrackingClients(attacker);
                }
            }
            if (!gettagfast(stack).getBoolean("effect")) {
                gettagfast(stack).setBoolean("effect", true);
                applyEffects(stack, target, attacker, chi);
                gettagfast(stack).setBoolean("effect", false);
            }
            if (!gettagfast(stack).getBoolean("spawning")) {
                gettagfast(stack).setBoolean("spawning", true);
                queueExtraMoves(stack, target, attacker, chi);
                gettagfast(stack).setBoolean("spawning", false);
            }
        }
        updateWielderDataEnd(stack, attacker, target);
        return super.hitEntity(stack, target, attacker);
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    /**
     * gradually discharges the weapon
     */
    public void onUpdate(ItemStack stack, World w, Entity e, int slot, boolean onMainhand) {
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
                if (stack.isItemDamaged()) stack.setItemDamage(stack.getItemDamage() - 1);
            } else {
                stack.setItemDamage(0);
                tag.removeTag("lastMove");
            }
            //TODO update swing and proc damage when it should
            //multihit shenanigans
            if (tag.hasKey("multiHitTarget")) {
                //multiHitFrom/Till/Target/Interval
                Entity victim = w.getEntityByID(tag.getInteger("multiHitTarget"));
                if (victim != null && NeedyLittleThings.getDistSqCompensated(elb, victim) < getReach(elb, stack) * getReach(elb, stack)) {
                    long from = tag.getLong("multiHitFrom");
                    long till = tag.getLong("multiHitTill");
                    long curr = w.getTotalWorldTime();
                    int interval = tag.getInteger("multiHitInterval");
                    if (till >= curr && from != curr && (curr - from) % interval == 0) {
                        TaoCombatUtils.rechargeHand(elb, getHand(stack), 1);
                        victim.hurtResistantTime = 0;
                        TaoCombatUtils.taoWeaponAttack(victim, elb, stack, onMainHand, false);
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
                if (diffItem || !onOffhand || !stillTwoHanded) {//FIXME last slot overflow to first slot bug
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

    public double attackDamage(ItemStack stack) {
        return dmg;
    }

    protected double speed(ItemStack stack) {
        return speed - 4d;
    }

    protected float getQiAccumulationRate(ItemStack is) {
        return isCharged(null, is) ? 0 : qiRate;
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

    @Nullable
    protected EnumHand getHand(ItemStack item) {
        if (isDummy(item)) return EnumHand.OFF_HAND;
        if (gettagfast(item).hasKey("off")) {
            return gettagfast(item).getBoolean("off") ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
        }
        return null;
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
        EnumHand hand = getHand(stack);
        if (hand == null) {
            hand = elb.swingingHand;
        }
        if (hand != null && (TaoCombatUtils.getHandCoolDown(elb, hand) > 0.9f || NeedyLittleThings.raytraceEntity(elb.world, elb, getReach(elb, stack)) != null)) {//FIXME hand cooldown will not work if attacking single target because order weirdness
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
        if (enchantment.equals(Enchantment.getEnchantmentByLocation("sweeping"))) return false;
        if (enchantment.equals(Enchantment.getEnchantmentByLocation("unbreaking"))) return false;
        if (enchantment.equals(Enchantment.getEnchantmentByLocation("mending"))) return false;
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
                float swing = NeedyLittleThings.getCooledAttackStrengthOff(Minecraft.getMinecraft().player, 1f);
                float newSwing = ClientEvents.okuyasu.getFloat(Minecraft.getMinecraft().getItemRenderer());
                newSwing += MathHelper.clamp((swing * swing * swing) - newSwing, -0.4F, 0.4F);
                ClientEvents.zaHando.setFloat(Minecraft.getMinecraft().getItemRenderer(), newSwing);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return changed;
    }

    /**
     * AoE
     */
    protected void aoe(ItemStack stack, EntityLivingBase attacker, int chi) {

    }

    /**
     * default method! Override for complex weapons!
     */
    protected void statDesc(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.WHITE + I18n.format("taoism.weaponReach", getReach(null, stack)) + TextFormatting.RESET);
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

    /**
     * @return 0 pick, 1 shovel, 2 axe, 3 scythe
     */
    protected boolean[] harvestable(ItemStack is) {
        return new boolean[]{false, false, false, false};
    }

    protected boolean isBeingHeld(EntityLivingBase elb, ItemStack stack) {
        return elb.getHeldItemMainhand() == stack || elb.getHeldItemOffhand() == stack;
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
        splash(attacker, NeedyLittleThings.raytraceEntity(attacker.world, attacker, getReach(attacker, is)), is, angleAllowance, attacker.world.getEntitiesInAABBexcluding(null, attacker.getEntityBoundingBox().grow(getReach(attacker, is)), NeedyLittleThings.VALID_TARGETS::test));
    }

    protected void splash(EntityLivingBase attacker, Entity ignored, ItemStack is, int degrees, List<Entity> targets) {
        boolean sweep = false;
        for (Entity target : targets) {
            if (target == attacker || attacker.isRidingOrBeingRiddenBy(target)) continue;
            //!NeedyLittleThings.isFacingEntity(attacker,target)||
            if ((degrees != 360 && !NeedyLittleThings.isFacingEntity(attacker, target, degrees)) || NeedyLittleThings.getDistSqCompensated(target, attacker) > getReach(attacker, is) * getReach(attacker, is) || target == ignored)
                continue;
            TaoCombatUtils.rechargeHand(attacker, getHand(is), TaoCasterData.getTaoCap(attacker).getSwing());
            if (attacker instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) attacker;
                TaoCombatUtils.taoWeaponAttack(target, p, is, getHand(is) == EnumHand.MAIN_HAND, false);
            } else attacker.attackEntityAsMob(target);
            sweep = true;
        }
        if (sweep && attacker instanceof EntityPlayer) {
            ((EntityPlayer) attacker).spawnSweepParticles();
        }
    }

    protected int getQiFromStack(ItemStack stack) {
        return gettagfast(stack).getInteger("qifloor");
    }

    protected void splash(EntityLivingBase attacker, ItemStack stack, List<Entity> targets) {
        splash(attacker, null, stack, 90, targets);
    }

    @Override
    public boolean canCharge(EntityLivingBase wielder, ItemStack item) {
        /*
        At 9+ qi, long press attack to charge weapon. Once charged, attack (swing) to begin the sequence.
         */
        return (wielder instanceof EntityPlayer && ((EntityPlayer) wielder).isCreative()) || (TaoCasterData.getTaoCap(wielder).getQi() > 9 && !isCharged(wielder, item));
    }

    @Override
    public void chargeWeapon(EntityLivingBase attacker, ItemStack item, int ticks) {
        if (isDummy(item) && attacker.getHeldItemMainhand() != item) {//better safe than sorry...
            //forward it to the main item, then do nothing as the main item will forward it back.
            chargeWeapon(attacker, attacker.getHeldItemMainhand(), ticks);
            return;
        }
        gettagfast(item).setBoolean("charge", true);
        gettagfast(item).setLong("chargedAtTime", attacker.world.getTotalWorldTime());
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
    }

    @Override
    public boolean isCharged(EntityLivingBase elb, ItemStack item) {
        return gettagfast(item).getBoolean("charge");
    }

    @Override
    public int getChargedTime(EntityLivingBase elb, ItemStack item) {
        return (int) (elb.world.getTotalWorldTime() - gettagfast(item).getLong("chargedAtTime"));
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
     * Used by rope dart and other charge type weapons
     *
     * @return how much to discharge per second
     */
    protected int dischargePerSecond() {
        return 1;
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

//    public EnumPhase getPhase(final ItemStack stack){
//
//    }

    protected void multiHit(EntityLivingBase attacker, ItemStack stack, Entity target, int duration, int interval) {
        if (gettagfast(stack).getLong("multiHitTill") < attacker.world.getTotalWorldTime()) {
            stack.setTagInfo("multiHitTarget", new NBTTagInt(target.getEntityId()));
            stack.setTagInfo("multiHitFrom", new NBTTagLong(attacker.world.getTotalWorldTime()));
            stack.setTagInfo("multiHitTill", new NBTTagLong(attacker.world.getTotalWorldTime() + duration));
            stack.setTagInfo("multiHitInterval", new NBTTagInt(interval));
        }
    }

    public boolean canBlock(EntityLivingBase defender, ItemStack item) {
        return true;
    }

    @Override
    public void parrySkill(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
    }

    @Override
    public void onBlock(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item) {
        //because tonfas.
    }

    @Override
    public float postureDealtBase(EntityLivingBase attacker, EntityLivingBase defender, ItemStack item, float amount) {
        return itemPostureMultiplier * (getDamDist(item)) * amount;
    }

    @Override
    public void onSwitchIn(ItemStack stack, EntityLivingBase elb) {
        if (getHand(stack) == EnumHand.OFF_HAND) {
            TaoCasterData.getTaoCap(elb).setOffhandCool(2);
        }
    }

    @Override
    public boolean canAttack(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return attacker!=target; //getReach(attacker, item) * getReach(attacker, item) > NeedyLittleThings.getDistSqCompensated(attacker, target); //screw it.
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

    @Override
    public float finalDamageMods(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack stack, float orig) {
        return orig;
    }

    @Override
    public int armorIgnoreAmount(DamageSource ds, EntityLivingBase attacker, EntityLivingBase target, ItemStack item, float orig) {
        return 0;
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
    protected void queueExtraMoves(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker, int chi) {

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
