package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.Taoism;
import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.api.allthedamagetypes.EntityDamageSourceTaoIndirect;
import com.jackiecrazi.taoism.api.alltheinterfaces.ICombatManipulator;
import com.jackiecrazi.taoism.common.entity.TaoEntities;
import com.jackiecrazi.taoism.config.CombatConfig;
import com.jackiecrazi.taoism.networking.PacketUpdateClientPainful;
import com.jackiecrazi.taoism.utils.TaoCombatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class TaoStatCapability implements ITaoStatCapability {
    public static final int MAXDOWNTIME = 100;
    private static final UUID STOPMOVING = UUID.fromString("ba89f1ca-e8a4-47a2-ad79-eb06a9bd0d77");
    private static final UUID ARMORDOWN = UUID.fromString("ba89f1ca-e8a4-47a2-ad79-eb06a9bd0d78");
    private static final float MAXQI = 9.99f;
    private WeakReference<EntityLivingBase> e;
    private Entity target;//cached for faster lookup
    private int zTarget;
    private float qi, ling, posture, swing;
    private int combo, ohcool;
    private float maxLing, maxPosture, recordedDamage;
    private int lcd, pcd, scd, qcd;
    private int down, bind, root;
    private long timey;
    private boolean swi, protecc, off, recording, sprint, head;
    private int parry, dodge, protec;
    private float prevWidth, prevHeight;
    private WeakReference<ItemStack> lastTickOffhand;
    private JUMPSTATE state = JUMPSTATE.GROUNDED;
    private ClingData cd = new ClingData(false, false, false, false);
    private int recordTimer = 0, cannonball = 0, taunt = -1;

    TaoStatCapability(EntityLivingBase elb) {
        e = new WeakReference<>(elb);
    }

    /**
     * @return the entity's width multiplied by its height, multiplied by 5 and added armor%, and finally rounded
     */
    private static float getMaxPosture(EntityLivingBase elb) {
        double maxPosBeforeArmor = elb.getEntityAttribute(TaoEntities.MAXPOSTURE).getAttributeValue();
        float armor = 1 + (elb.getTotalArmorValue() / 20f);
        return Math.round(maxPosBeforeArmor * armor);
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getPostureRegenAmount(EntityLivingBase elb, int ticks) {
        float posMult = (float) elb.getEntityAttribute(TaoEntities.POSREGEN).getAttributeValue();
        float nausea = elb instanceof EntityPlayer || elb.getActivePotionEffect(MobEffects.NAUSEA) == null ? 0 : (elb.getActivePotionEffect(MobEffects.NAUSEA).getAmplifier() + 1) * 0.05f;
        float armorMod = 1f - ((float) elb.getTotalArmorValue() / 40f);
        float healthMod = elb.getHealth() / elb.getMaxHealth();
        if (TaoCasterData.getTaoCap(elb).getDownTimer() > 0) {
            return TaoCasterData.getTaoCap(elb).getMaxPosture() * armorMod * posMult * healthMod / (1.5f * MAXDOWNTIME);
        }
        if (posMult < 0)
            return (ticks * 0.05f) * posMult / (armorMod * healthMod);
        return ((ticks * 0.05f) * armorMod * posMult * healthMod) - nausea;
    }

    /**
     * unified to prevent discrepancy and allow easy tweaking in the future
     */
    private static float getQiDecayAmount(float currentQi, int ticks) {
        return 0.005f * ticks * (currentQi + 1) / 8;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("qi", getQi());
        nbt.setFloat("ling", getLing());
        nbt.setFloat("swing", getSwing());
        nbt.setFloat("posture", getPosture());
        nbt.setInteger("parry", getParryCounter());
        nbt.setInteger("combo", getComboSequence());
        nbt.setFloat("maxling", getMaxLing());
        nbt.setFloat("maxposture", getMaxPosture());
        nbt.setInteger("lcd", getLingRechargeCD());
        nbt.setInteger("pcd", getPostureRechargeCD());
        nbt.setInteger("scd", getStaminaRechargeCD());
        nbt.setInteger("qcd", getQiGracePeriod());
        nbt.setLong("lastupdate", getLastUpdatedTime());
        nbt.setBoolean("switch", isSwitchIn());
        nbt.setInteger("ohcool", getOffhandCool());
        nbt.setBoolean("protecc", isProtected());
        nbt.setInteger("down", getDownTimer());
        nbt.setInteger("dodge", getRollCounter());
        nbt.setFloat("prevWidth", getPrevSizes().getFirst());
        nbt.setFloat("prevHeight", getPrevSizes().getSecond());
        nbt.setInteger("protec", getPosInvulTime());
        nbt.setBoolean("off", isOffhandAttack());
        nbt.setInteger("jump", getJumpState().ordinal());
        nbt.setTag("offhandInfo", getOffHand().writeToNBT(new NBTTagCompound()));
        nbt.setFloat("recDam", getRecordedDamage());
        nbt.setBoolean("reccing", recording);
        nbt.setBoolean("sprintTemp", isInCombatMode());
        cd.toNBT(nbt);
        nbt.setInteger("bind", getBindTime());
        nbt.setInteger("root", getRootTime());
        nbt.setInteger("recordTimer", recordTimer);
        nbt.setInteger("spinny", cannonball);
        nbt.setInteger("lookingAt", zTarget);
        nbt.setBoolean("head", head);
        return nbt;
    }

    private Tuple<Float, Float> getPrevSizes() {
        return new Tuple<>(prevWidth, prevHeight);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        setQi(nbt.getFloat("qi"));
        setLing(nbt.getFloat("ling"));
        setSwing(nbt.getFloat("swing"));
        setPosture(nbt.getFloat("posture"));
        setParryCounter(nbt.getInteger("parry"));
        setComboSequence((nbt.getInteger("combo")));
        setMaxLing(nbt.getFloat("maxling"));
        setMaxPosture(nbt.getFloat("maxposture"));
        setLingRechargeCD(nbt.getInteger("lcd"));
        setPostureRechargeCD(nbt.getInteger("pcd"));
        setStaminaRechargeCD(nbt.getInteger("scd"));
        setQiGracePeriod(nbt.getInteger("qcd"));
        setLastUpdatedTime(nbt.getLong("lastupdate"));
        setSwitchIn(nbt.getBoolean("switch"));
        setOffhandCool(nbt.getInteger("ohcool"));
        setProtected(nbt.getBoolean("protecc"));
        setDownTimer(nbt.getInteger("down"));
        setRollCounter(nbt.getInteger("dodge"));
        setPrevSizes(nbt.getFloat("prevWidth"), nbt.getFloat("prevHeight"));
        setPosInvulTime(nbt.getInteger("protec"));
        setOffHand(new ItemStack(nbt.getCompoundTag("offhandInfo")));
        setOffhandAttack(nbt.getBoolean("off"));
        setJumpState(ITaoStatCapability.JUMPSTATE.values()[nbt.getInteger("jump")]);
        setClingDirections(new ClingData(nbt));
        setBindTime(nbt.getInteger("bind"));
        setRootTime(nbt.getInteger("root"));
        setRecordedDamage(nbt.getFloat("recDam"));
        toggleCombatMode(nbt.getBoolean("sprintTemp"));
        recording = nbt.getBoolean("reccing");
        recordTimer = nbt.getInteger("recordTimer");
        //only happens on the client
        zTarget = nbt.getInteger("lookingAt");
        cannonball = nbt.getInteger("spinny");
        head = nbt.getBoolean("head");
    }

    private void setPrevSizes(float width, float height) {
        prevWidth = width;
        prevHeight = height;
    }

    @Override
    public void update(final int ticks) {
        EntityLivingBase elb = e.get();
        if (elb == null) return;
        if (!elb.isEntityAlive() || elb.world.isRemote) return;

        recordTimer++;
        cannonball--;
        if (isRecordingDamage() && recordTimer > 200) {
            stopRecordingDamage(elb.getRevengeTarget());
        }
        if (getForcedLookAt() != null && getForcedLookAt().isDead) {
            setForcedLookAt(null);
        }
        float percentage = getPosture() / getMaxPosture();
        setMaxPosture(getMaxPosture(elb));//a horse has 20 posture right off the bat, just saying
        setPosture(getMaxPosture() * percentage);
        //brings it to a tidy sum of 10 for the player, 20 with full armor.
        setMaxLing(10f);
        if (elb.onGround && getJumpState() != ITaoStatCapability.JUMPSTATE.GROUNDED) {
            //elb.setSprinting(false);
            setJumpState(ITaoStatCapability.JUMPSTATE.GROUNDED);
        }
        float lingMult = (float) elb.getEntityAttribute(TaoEntities.LINGREGEN).getAttributeValue();
        int diff = ticks;
        //spirit power recharge
        if (getLingRechargeCD() >= diff) setLingRechargeCD(getLingRechargeCD() - diff);
        else {
            diff -= getLingRechargeCD();
            addLing(diff * lingMult);
            setLingRechargeCD(0);
        }
        if (getDownTimer() > 0) {
            setDownTimer(getDownTimer() - ticks);
            if (getDownTimer() <= 0) {
                int overflow = -getDownTimer();
                setDownTimer(0);
                addPosture(getPostureRegenAmount(elb, overflow));
            } else if (elb.motionY > 0 && (!elb.noClip || !elb.world.isAirBlock(elb.getPosition().down()))) {
                elb.motionY = -0.1;
                elb.velocityChanged = true;
            }
        }
        diff = ticks;
        if (getPostureRechargeCD() <= diff || !isProtected()) {
            if (isProtected())
                diff -= getPostureRechargeCD();
            setPostureRechargeCD(0);
            addPosture(getPostureRegenAmount(elb, diff));
        } else setPostureRechargeCD(getPostureRechargeCD() - ticks);
        diff = ticks;
        //value updating
        if (ticks > 0) {//so apparently time can randomly run backwards. Hmm.
            setPosInvulTime(getPosInvulTime() - diff);
            addParryCounter(diff);
            addRollCounter(diff);
        }
        if (getBindTime() > 0)
            setBindTime(getBindTime() - 1);
        if (getRootTime() > 0)
            setRootTime(getRootTime() - 1);
        setOffhandCool(getOffhandCool() + ticks);
        diff = ticks - getQiGracePeriod();
        //qi decay
        if (diff > 0) {
            if (!consumeQi(getQiDecayAmount(getQi(), diff), 0))
                setQi(0);
        } else setQiGracePeriod(-diff);

        if (!(elb instanceof EntityPlayer))
            if (elb.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED) != null)
                setSwing(getSwing() + ticks * (float) elb.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue());
            else setSwing(getSwing() + ticks * 2);
        setLastUpdatedTime(elb.world.getTotalWorldTime());
        sync();
    }

    @Override
    public float getQi() {
        return qi;
    }

    @Override
    public void setQi(float amount) {
        qi = MathHelper.clamp(amount, 0, MAXQI);
    }

    @Override
    public float addQi(float amount) {
        qi += amount;
        if (amount > 0)
            setQiGracePeriod(CombatConfig.qiGrace * 20);
        amount = 0;
        if (qi > MAXQI) {
            amount = qi - MAXQI;
            qi = MAXQI;
        }
        return amount;
    }

    @Override
    public boolean consumeQi(float amount, float above) {
        boolean ret = true;
        if (qi < amount + above) ret = false;
        int qibefore = (int) qi;
        qi -= amount;
        if (qi < 0) qi = 0;
        if (getQiFloored() < qibefore)
            setQiGracePeriod(CombatConfig.qiGrace * 20);
        sync();
        return ret;
    }

    @Override
    public float getLing() {
        return ling;
    }

    @Override
    public void setLing(float amount) {
        ling = MathHelper.clamp(amount, 0f, getMaxLing());
    }

    @Override
    public float addLing(float amount) {
        setLing(amount + ling);
        return amount;
    }

    @Override
    public boolean consumeLing(float amount) {
        if (ling < amount) return false;
        ling -= amount;
        setLingRechargeCD(CombatConfig.lingCD);
        return true;
    }

    @Override
    public JUMPSTATE getJumpState() {
        return state;
    }

    @Override
    public void setJumpState(JUMPSTATE js) {
        state = js;
    }

    @Override
    public ClingData getClingDirections() {
        return cd;
    }

    @Override
    public void setClingDirections(ClingData dirs) {
        cd = dirs;
    }

    @Override
    public float getSwing() {
        return swing;
    }

    @Override
    public void setSwing(float amount) {
        swing = amount;
    }

    @Override
    public float getPosture() {
        return posture;
    }

    @Override
    public void setPosture(float amount) {
        posture = MathHelper.clamp(amount, 0f, maxPosture);
        if (posture >= getMaxPosture()) setProtected(true);
        //if (posture == 0) beatDown(null, 0);
    }

    @Override
    public float addPosture(float amount) {
        setPosture(posture + amount);
        return posture;
    }

    @Override
    public float consumePosture(float amount, boolean canStagger) {
        return consumePosture(amount, canStagger, false, null);
    }

    @Override
    public float consumePosture(float amount, boolean canStagger, EntityLivingBase assailant) {
        return consumePosture(amount, canStagger, false, assailant);
    }

    @Override
    public float consumePosture(float amount, boolean canStagger, boolean force, @Nullable EntityLivingBase assailant) {
        if (getDownTimer() > 0 || getPosInvulTime() > 0)
            return 0;//cancel all posture reduction when downed so you get back up with a buffer
        float prevPos = posture;
        posture -= amount;
        EntityLivingBase elb = e.get();
        if (posture <= 0f && elb != null) {
            boolean protect = isProtected();
            setProtected(false);//cancels ssp so you can regen posture without delay
            if ((protect && prevPos > getMaxPosture() / 5f && CombatConfig.ssp && !force) || !canStagger || getPosInvulTime() > 0) {
                //sudden stagger prevention
                posture = 0.01f;
                if (canStagger && getPosInvulTime() <= 0) setPosInvulTime(CombatConfig.ssptime);
                elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, Taoism.unirand.nextFloat() * 0.4f + 0.8f, Taoism.unirand.nextFloat() * 0.4f + 0.8f);
                return 0f;
            }
            amount = -posture;
            posture = 0f;
            beatDown(assailant, amount);
            sync();
            return amount;
        } else {
            setPostureRechargeCD(CombatConfig.postureCD);
            sync();
            //System.out.println(posture+" posture left on target");
            return 0f;
        }
    }

    @Override
    public int getParryCounter() {
        return parry;
    }

    @Override
    public void setParryCounter(int amount) {
        parry = amount;
    }

    @Override
    public void addParryCounter(int amount) {
        parry += amount;
    }

    @Override
    public int getRollCounter() {
        return dodge;
    }

    @Override
    public void setRollCounter(int amount) {
        dodge = amount;
    }

    @Override
    public void addRollCounter(int amount) {
        dodge += amount;
    }

    @Override
    public int getComboSequence() {
        return combo;
    }

    @Override
    public void setComboSequence(int amount) {
        combo = amount;
    }

    @Override
    public void incrementComboSequence(int amount) {
        combo += amount;
    }

    @Override
    public void copy(ITaoStatCapability from) {
        setMaxLing(from.getMaxLing());
        setMaxPosture(from.getMaxPosture());
        setQi(from.getQi());
        setQiGracePeriod(from.getQiGracePeriod());
        setLing(from.getLing());
        setSwing(from.getSwing());
        setPosture(from.getPosture());
        setParryCounter(from.getParryCounter());
        setComboSequence(from.getComboSequence());
        setOffhandCool(from.getOffhandCool());
        setSwitchIn(from.isSwitchIn());
        setProtected(from.isProtected());
        setDownTimer(from.getDownTimer());
        setRollCounter(from.getRollCounter());
        setPostureRechargeCD(from.getPostureRechargeCD());
        setStaminaRechargeCD(from.getStaminaRechargeCD());
        setPosInvulTime(from.getPosInvulTime());
        setLastUpdatedTime(from.getLastUpdatedTime());
        setOffHand(from.getOffHand());
        setOffhandAttack(from.isOffhandAttack());
        setJumpState(from.getJumpState());
        setClingDirections(from.getClingDirections());
        setBindTime(from.getBindTime());
        setRootTime(from.getRootTime());
        setRecordedDamage(from.getRecordedDamage());
        recording = from.isRecordingDamage();
        head = from.willDropHead();
    }

    @Override
    public float getMaxLing() {
        return maxLing;
    }

    @Override
    public void setMaxLing(float amount) {
        maxLing = amount;
    }

    @Override
    public float getMaxPosture() {
        return maxPosture;
    }

    @Override
    public void setMaxPosture(float amount) {
        float percentage = posture / maxPosture;
        maxPosture = amount;
        setPosture(percentage * amount);
    }

    @Override
    public int getLingRechargeCD() {
        return lcd;
    }

    @Override
    public void setLingRechargeCD(int amount) {
        lcd = amount;
    }

    @Override
    public int getPostureRechargeCD() {
        return pcd;
    }

    @Override
    public void setPostureRechargeCD(int amount) {
        pcd = amount;
    }

    @Override
    public int getStaminaRechargeCD() {
        return scd;
    }

    @Override
    public void setStaminaRechargeCD(int amount) {
        scd = amount;
    }

    @Override
    public int getQiGracePeriod() {
        return qcd;
    }

    @Override
    public void setQiGracePeriod(int amount) {
        qcd = amount;
    }

    @Override
    public int getPosInvulTime() {
        return protec;
    }

    @Override
    public void setPosInvulTime(int time) {
        int temp = protec;
        protec = Math.max(time, 0);
        if (temp > 0 && protec == 0) sync();
    }

    @Override
    public long getLastUpdatedTime() {
        return timey;
    }

    @Override
    public void setLastUpdatedTime(long time) {
        timey = time;
    }

    @Override
    public boolean isSwitchIn() {
        return swi;
    }

    @Override
    public void setSwitchIn(boolean suichi) {
        swi = suichi;
    }

    @Override
    public int getOffhandCool() {
        return ohcool;
    }

    @Override
    public void setOffhandCool(int oc) {
        ohcool = oc;
    }

    @Override
    public boolean isOffhandAttack() {
        return off;
    }

    @Override
    public void setOffhandAttack(boolean off) {
        this.off = off;
    }

    @Override
    public boolean isInCombatMode() {
        return sprint;
    }

    @Override
    public void toggleCombatMode(boolean on) {
        this.sprint = on;
    }

    @Override
    public ItemStack getOffHand() {
        return lastTickOffhand == null || lastTickOffhand.get() == null ? ItemStack.EMPTY : lastTickOffhand.get();
    }

    @Override
    public void setOffHand(ItemStack is) {
        lastTickOffhand = new WeakReference<>(is);
    }

    @Override
    public boolean isProtected() {
        return protecc;
    }

    @Override
    public void setProtected(boolean protect) {
        protecc = protect;
    }

    @Override
    public int getDownTimer() {
        return down;
    }

    @Override
    public void setDownTimer(int time) {
        if (time < 0) time = 0;
        EntityLivingBase elb = e.get();
        if (elb != null && time == 0 && time != down) {
            elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(STOPMOVING);
            //elb.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMORDOWN);
        }
        down = time;
    }

    @Override
    public int getBindTime() {
        return bind;
    }

    @Override
    public void setBindTime(int time) {
        if (bind != 0 && e.get() != null) {
            TaoCombatUtils.rechargeHand(e.get(), EnumHand.MAIN_HAND, 0, true);
            TaoCombatUtils.rechargeHand(e.get(), EnumHand.OFF_HAND, 0, true);
        }
        bind = time;
    }

    @Override
    public int getRootTime() {
        return root;
    }

    @Override
    public void setRootTime(int time) {
        EntityLivingBase elb = e.get();
        if (elb != null)
            if (time == 0 && root != 0) {
                //elb.setNoGravity(false);
                elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(STOPMOVING);
            } else if (root == 0 && time != 0 && !(elb instanceof EntityPlayer)) {
                //elb.setNoGravity(true);
                elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(STOPMOVING);
                elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(STOPMOVING, "rooted", -1, 2));
            }
        root = time;
    }

    @Override
    public int getRecordingTime() {
        return recordTimer;
    }

    @Override
    public float getRecordedDamage() {
        return recordedDamage;
    }

    @Override
    public void setRecordedDamage(float amount) {
        recordedDamage = amount;
    }

    @Override
    public void addRecordedDamage(float amount) {
        setRecordedDamage(getRecordedDamage() + amount);
    }

    @Override
    public boolean isRecordingDamage() {
        return recording;
    }

    @Override
    public void startRecordingDamage() {
        recording = true;
        recordedDamage = 0;
        recordTimer = 0;
        sync();
    }

    @Override
    public void stopRecordingDamage(EntityLivingBase elb) {
        if (!recording) return;
        final EntityLivingBase target = e.get();
        recording = false;
        recordTimer = 0;
        sync();
        if (target != null) {
            float damage = getRecordedDamage();
            setRecordedDamage(0);
            DamageSource ds = EntityDamageSourceTaoIndirect.causeProxyDamage(elb, null).setDamageBypassesArmor().setDamageIsAbsolute();
            if (elb != null) {
                if (elb == target) return;//bootleg invulnerability!
                ItemStack is = TaoCombatUtils.getAttackingItemStackSensitive(elb);
                if (is.getItem() instanceof ICombatManipulator) {
                    damage = ((ICombatManipulator) is.getItem()).onStoppedRecording(ds, elb, target, is, damage);
                }
            }
            while (damage > 0) {
                target.hurtResistantTime = 0;
                target.attackEntityFrom(ds, Math.min(damage, 3));
                damage -= 3;
            }
        }
    }

    @Override
    public void sync() {
        EntityLivingBase elb = e.get();
        if (elb == null) return;
        if (elb.world.isRemote) return;//throw new UnsupportedOperationException("what are you doing?");
        PacketUpdateClientPainful pucp = new PacketUpdateClientPainful(elb);
        if (elb instanceof EntityPlayerMP) {
            Taoism.net.sendTo(pucp, (EntityPlayerMP) elb);
        }
        Taoism.net.sendToAllTracking(pucp, elb);
    }

    @Override
    public int getCannonballTime() {
        return cannonball;
    }

    @Override
    public void setCannonballTime(int duration) {
        cannonball = duration;
    }

    @Override
    public int getTauntID() {
        return taunt;
    }

    @Override
    public void tauntedBy(EntityLivingBase elb) {
        if (elb == null) {
            taunt = -1;
        } else {
            taunt = elb.getEntityId();
            if (e.get() instanceof EntityLiving)
                ((EntityLiving) e.get()).setAttackTarget(elb);
        }
    }

    @Override
    public Entity getForcedLookAt() {
        if (e.get() != null) {
            //if (target != e.get()) return target;
            Entity targ = e.get().world.getEntityByID(zTarget);
            //target = targ;
            return targ;
        }
        return null;
    }

    @Override
    public void setForcedLookAt(Entity e) {
        if (e == null) zTarget = -1;
        else zTarget = e.getEntityId();
        target = this.e.get();
    }

    @Override
    public boolean willDropHead() {
        return head;
    }

    @Override
    public void setMustDropHead(boolean toggle) {
        head = toggle;
    }

    private void beatDown(EntityLivingBase attacker, float overflow) {
        EntityLivingBase elb = e.get();
        if (elb == null) return;
        elb.dismountRidingEntity();
        if (attacker != null) {
            NeedyLittleThings.knockBack(elb, attacker, 1.5f, true, false);
        }
        elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(STOPMOVING);
        elb.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(STOPMOVING, "downed", -1, 2));
        //elb.getEntityAttribute(SharedMonsterAttributes.ARMOR).removeModifier(ARMORDOWN);
        //elb.getEntityAttribute(SharedMonsterAttributes.ARMOR).applyModifier(new AttributeModifier(ARMORDOWN, "downed", -9, 0));
        setDownTimer(MAXDOWNTIME);
        elb.world.playSound(null, elb.posX, elb.posY, elb.posZ, SoundEvents.ENTITY_ZOMBIE_BREAK_DOOR_WOOD, SoundCategory.PLAYERS, Taoism.unirand.nextFloat() * 0.4f + 0.8f, Taoism.unirand.nextFloat() * 0.4f + 0.8f);
        sync();
        if (elb.isBeingRidden()) {
            for (Entity ent : elb.getPassengers())
                if (ent instanceof EntityLivingBase) {
                    ITaoStatCapability cap = TaoCasterData.getTaoCap((EntityLivingBase) ent);
                    cap.consumePosture(cap.getMaxPosture() + 1, true, true, null);
                }
            elb.removePassengers();
        }
    }
}
