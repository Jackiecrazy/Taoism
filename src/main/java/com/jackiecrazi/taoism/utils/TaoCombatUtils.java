package com.jackiecrazi.taoism.utils;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.alltheinterfaces.IMove;
import com.jackiecrazi.taoism.api.alltheinterfaces.IStaminaPostureManipulable;
import com.jackiecrazi.taoism.api.alltheinterfaces.ITwoHanded;
import com.jackiecrazi.taoism.capability.TaoCasterData;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;

public class TaoCombatUtils {
    public static void executeMove(EntityLivingBase elb, byte moveCode) {
        ItemStack main = elb.getHeldItemMainhand();
        ItemStack off = elb.getHeldItemOffhand();
        if (main.getItem() instanceof IMove) {
            if (!main.hasTagCompound()) main.setTagCompound(new NBTTagCompound());
            main.getTagCompound().setByte("lastMove", main.getTagCompound().getByte("currentMove"));
            main.getTagCompound().setByte("currentMove", moveCode);
        }
        if ((!(main.getItem() instanceof ITwoHanded) || ((ITwoHanded) main.getItem()).isTwoHanded(main)) && off.getItem() instanceof IMove) {
            if (!off.hasTagCompound()) off.setTagCompound(new NBTTagCompound());
            off.getTagCompound().setByte("lastMove", off.getTagCompound().getByte("currentMove"));
            off.getTagCompound().setByte("currentMove", moveCode);
        }
    }

    public static ItemStack getAttackingItemStackSensitive(EntityLivingBase elb) {
        return TaoCasterData.getTaoCap(elb).isOffhandAttack() ? elb.getHeldItemOffhand() : elb.getHeldItemMainhand();
    }

    public static ItemStack getParryingItemStack(EntityLivingBase attacker, EntityLivingBase elb, float amount) {
        ItemStack main = elb.getHeldItemMainhand(), off = elb.getHeldItemOffhand();
        boolean mainRec = NeedyLittleThings.getCooledAttackStrength(elb, 0.5f) > 0.8f, offRec = NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f) > 0.8f;
        float defMult = 42;//meaning of life, the universe and everything
        ItemStack ret = ItemStack.EMPTY;
        //shield and sword block
        if ((main.getItem().isShield(main, elb) || contains(main.getItem())) && mainRec) {
            ret = main;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        if ((off.getItem().isShield(off, elb) || contains(off.getItem())) && offRec) {
            ret = off;
            defMult = CombatConfig.defaultMultiplierPostureDefend;
        }
        //offhand
        if (offRec && off.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) off.getItem()).canBlock(elb, off) && ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount) <= defMult) {
            defMult = ((IStaminaPostureManipulable) off.getItem()).postureMultiplierDefend(attacker, elb, off, amount);
            ret = off;
        }
        //mainhand
        if (mainRec && main.getItem() instanceof IStaminaPostureManipulable && ((IStaminaPostureManipulable) main.getItem()).canBlock(elb, main) && ((IStaminaPostureManipulable) main.getItem()).postureMultiplierDefend(attacker, elb, main, amount) <= defMult) {
            ret = main;
        }
        return ret;
    }

    private static boolean contains(Item i) {
        if (i.getRegistryName() == null) return false;
        for (String s : CombatConfig.parryCapableItems) {
            if (s.equals(i.getRegistryName().toString())) return true;
        }
        return false;
    }

    public static float postureAtk(EntityLivingBase defender, EntityLivingBase attacker, ItemStack attack, float amount) {
        return attack.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) attack.getItem()).postureDealtBase(attacker, defender, attack, amount) : amount * CombatConfig.defaultMultiplierPostureAttack;
    }

    public static float postureDef(EntityLivingBase defender, EntityLivingBase attacker, ItemStack defend, float amount) {
        return (defender.onGround ? defender.isSneaking() ? 0.5f : 1f : 2f) *
                (defend.getItem() instanceof IStaminaPostureManipulable ? ((IStaminaPostureManipulable) defend.getItem()).postureMultiplierDefend(attacker, defender, defend, amount) : CombatConfig.defaultMultiplierPostureDefend);
    }

    public static void rechargeHand(EntityLivingBase elb, EnumHand hand, float percent) {
        if (!(elb instanceof EntityPlayer)) return;
        double totalSec = 20 / NeedyLittleThings.getAttributeModifierHandSensitive(SharedMonsterAttributes.ATTACK_SPEED, elb, hand);
        //if (percent != 0f)//this is because this is called in tickStuff on the first tick after cooldown starts, so constant resetting would just make the weapon dysfunctional
        switch (hand) {
            case OFF_HAND:
                TaoCasterData.getTaoCap(elb).setOffhandCool((int) (percent * totalSec));
                break;
            case MAIN_HAND:
                Taoism.setAtk(elb, (int) (percent * totalSec));
                break;
        }
    }

    public static float getHandCoolDown(EntityLivingBase elb, EnumHand hand) {
        if (elb instanceof EntityPlayer)
            switch (hand) {
                case OFF_HAND:
                    return NeedyLittleThings.getCooledAttackStrengthOff(elb, 0.5f);
                case MAIN_HAND:
                    return NeedyLittleThings.getCooledAttackStrength(elb, 0.5f);
            }
        return 1f;
    }

    /**
     * copy-pasted from EntityPlayer, as-is.
     */
    public static void taoWeaponAttack(Entity targetEntity, EntityPlayer player, ItemStack stack, boolean main, boolean updateOff) {
        taoWeaponAttack(targetEntity, player, stack, main, updateOff, DamageSource.causePlayerDamage(player));
    }

    /**
     * copy-pasted from EntityPlayer, as-is.
     */
    public static void taoWeaponAttack(Entity targetEntity, EntityPlayer player, ItemStack stack, boolean main, boolean updateOff, DamageSource ds) {
        {
            if (updateOff) {
                TaoCasterData.getTaoCap(player).setOffhandAttack(!main);
            }
            if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(player, targetEntity)) return;
            if (targetEntity.canBeAttackedWithItem()) {
                if (!targetEntity.hitByEntity(player)) {
                    IAttributeInstance toUse = new AttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                    IAttributeInstance att = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                    toUse.setBaseValue(att.getBaseValue());
                    for (AttributeModifier am : att.getModifiers()) {
                        if (!am.getName().equals("Weapon modifier"))
                            toUse.applyModifier(am);
                        //System.out.println(toUse.getAttributeValue());
                    }
                    for (AttributeModifier atm : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()))
                        toUse.applyModifier(atm);
                    float damage = (float) toUse.getAttributeValue();
                    float damageMods;

                    if (targetEntity instanceof EntityLivingBase) {
                        damageMods = EnchantmentHelper.getModifierForCreature(stack, ((EntityLivingBase) targetEntity).getCreatureAttribute());
                    } else {
                        damageMods = EnchantmentHelper.getModifierForCreature(stack, EnumCreatureAttribute.UNDEFINED);
                    }

                    float cooldown = main ?
                            player.getCooledAttackStrength(0.5F) :
                            NeedyLittleThings.getCooledAttackStrengthOff(player, 0.5f);
                    //System.out.println(cooldown+String.valueOf(player.world.isRemote));
                    damage = damage * (0.2F + cooldown * cooldown * 0.8F);
                    damageMods = damageMods * cooldown;
                    if (main)
                        player.resetCooldown();
                    else TaoCasterData.getTaoCap(player).setOffhandCool(0);

                    if (damage > 0.0F || damageMods > 0.0F) {
                        boolean recharged = cooldown > 0.9F;
                        boolean knockback = false;
                        int kbamnt = 0;
                        kbamnt = kbamnt + EnchantmentHelper.getKnockbackModifier(player);

                        if (player.isSprinting() && recharged) {
                            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                            ++kbamnt;
                            knockback = true;
                        }

                        boolean crit = recharged && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase;
                        crit = crit && !player.isSprinting();

                        net.minecraftforge.event.entity.player.CriticalHitEvent critevent = net.minecraftforge.common.ForgeHooks.getCriticalHit(player, targetEntity, crit, crit ? 1.5F : 1.0F);
                        crit = critevent != null;
                        if (crit) {
                            damage *= critevent.getDamageModifier();
                        }

                        damage = damage + damageMods;
                        boolean sword = false;
                        double speed = (double) (player.distanceWalkedModified - player.prevDistanceWalkedModified);

                        if (recharged && !crit && !knockback && player.onGround && speed < (double) player.getAIMoveSpeed()) {

                            if (stack.getItem() instanceof ItemSword) {
                                sword = true;
                            }
                        }

                        float health = 0.0F;
                        boolean burning = false;
                        int fireLevel = EnchantmentHelper.getFireAspectModifier(player);

                        if (targetEntity instanceof EntityLivingBase) {
                            health = ((EntityLivingBase) targetEntity).getHealth();

                            if (fireLevel > 0 && !targetEntity.isBurning()) {
                                burning = true;
                                targetEntity.setFire(1);
                            }
                        }

                        double motionX = targetEntity.motionX;
                        double motionY = targetEntity.motionY;
                        double motionZ = targetEntity.motionZ;
                        boolean dealDamage = targetEntity.attackEntityFrom(ds, damage);

                        if (dealDamage) {
                            if (kbamnt > 0) {
                                if (targetEntity instanceof EntityLivingBase) {
                                    NeedyLittleThings.knockBack(((EntityLivingBase) targetEntity), player, (float) kbamnt * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                    //((EntityLivingBase) targetEntity).knockBack(player, (float) kbamnt * 0.5F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                } else {
                                    targetEntity.addVelocity((double) (-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float) kbamnt * 0.5F), 0.1D, (double) (MathHelper.cos(player.rotationYaw * 0.017453292F) * (float) kbamnt * 0.5F));
                                }

                                player.motionX *= 0.6D;
                                player.motionZ *= 0.6D;
                                player.setSprinting(false);
                            }

                            if (sword) {
                                float sweepratio = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;

                                for (EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                                    if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && NeedyLittleThings.getDistSqCompensated(player, entitylivingbase) < 9.0D) {
                                        entitylivingbase.knockBack(player, 0.4F, (double) MathHelper.sin(player.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                        entitylivingbase.attackEntityFrom(ds, sweepratio);
                                    }
                                }

                                player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                                player.spawnSweepParticles();
                            }

                            if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                                ((EntityPlayerMP) targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                                targetEntity.velocityChanged = false;
                                targetEntity.motionX = motionX;
                                targetEntity.motionY = motionY;
                                targetEntity.motionZ = motionZ;
                            }

                            if (crit) {
                                player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                                player.onCriticalHit(targetEntity);
                            }

                            if (!crit && !sword) {
                                if (recharged) {
                                    player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                                } else {
                                    player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                                }
                            }

                            if (damageMods > 0.0F) {
                                player.onEnchantmentCritical(targetEntity);
                            }

                            player.setLastAttackedEntity(targetEntity);

                            if (targetEntity instanceof EntityLivingBase) {
                                EnchantmentHelper.applyThornEnchantments((EntityLivingBase) targetEntity, player);
                            }

                            EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                            Entity entity = targetEntity;

                            if (targetEntity instanceof MultiPartEntityPart) {
                                IEntityMultiPart ientitymultipart = ((MultiPartEntityPart) targetEntity).parent;

                                if (ientitymultipart instanceof EntityLivingBase) {
                                    entity = (EntityLivingBase) ientitymultipart;
                                }
                            }

                            if (!stack.isEmpty() && entity instanceof EntityLivingBase) {
                                ItemStack beforeHitCopy = stack.copy();
                                stack.hitEntity((EntityLivingBase) entity, player);

                                if (stack.isEmpty()) {
                                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, EnumHand.MAIN_HAND);
                                    player.setHeldItem(main ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND, ItemStack.EMPTY);
                                }
                            }

                            if (targetEntity instanceof EntityLivingBase) {
                                float dealtdamage = health - ((EntityLivingBase) targetEntity).getHealth();
                                player.addStat(StatList.DAMAGE_DEALT, Math.round(dealtdamage * 10.0F));

                                if (fireLevel > 0) {
                                    targetEntity.setFire(fireLevel * 4);
                                }

                                if (player.world instanceof WorldServer && dealtdamage > 2.0F) {
                                    int k = (int) ((double) dealtdamage * 0.5D);
                                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double) (targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D);
                                }
                            }

                            player.addExhaustion(0.1F);
                        } else {
                            player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);

                            if (burning) {
                                targetEntity.extinguish();
                            }
                        }
                    }
//                    if (updateOff) {
//                        TaoWeapon.off = false;
//                    }
                }
            }
        }
    }
}
