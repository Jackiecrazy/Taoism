package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.api.NeedyLittleThings;
import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class TaoStatCapability implements ITaoStatCapability {
    public static final int MAXDOWNTIME = 100;
    private static final float MAXQI = 9.99f;
    private EntityLivingBase e;
    private float qi, ling, posture, swing;
    private int combo, ohcool;
    private float maxLing, maxPosture, maxStamina;
    private int lcd, pcd, scd, qcd;
    private int down;
    private long timey;
    private boolean swi, protecc, off;
    private int parry, dodge, protec;
    private float prevWidth, prevHeight;
    private ItemStack lastTickOffhand;
    private JUMPSTATE state = JUMPSTATE.GROUNDED;
    private ClingData cd=new ClingData(false, false, false, false);

    TaoStatCapability(EntityLivingBase elb) {
        e = elb;
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
        cd.toNBT(nbt);
        return nbt;
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
            setQiGracePeriod(CombatConfig.qiGrace);
        amount = 0;
        if (qi > MAXQI) {
            amount = qi - MAXQI;
            qi = MAXQI;
        }
        return amount;
    }

    @Override
    public boolean consumeQi(float amount) {
        if (qi < amount) return false;
        qi -= amount;
        return true;
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
        return consumePosture(amount, canStagger, e.getRevengeTarget());
    }

    @Override
    public void setClingDirections(ClingData dirs) {
        cd=dirs;
    }

    @Override
    public ClingData getClingDirections() {
        return cd;
    }

    @Override
    public float consumePosture(float amount, boolean canStagger, @Nullable EntityLivingBase assailant) {
        if (getDownTimer() > 0) return 0;//cancel all posture reduction when downed so you get back up with a buffer
        float cache = posture;
        posture -= amount;
        if (posture <= 0f) {
            boolean protect = isProtected();
            setProtected(false);//cancels ssp so you can regen posture without delay
            if ((cache >= getMaxPosture() / 4 && protect && CombatConfig.ssp) || !canStagger || getPosInvulTime() > 0) {
                //sudden stagger prevention
                posture = 0.01f;
                if (canStagger && getPosInvulTime() <= 0) setPosInvulTime(CombatConfig.ssptime);
                return 0f;
            }
            amount = -posture;
            posture = 0f;
            beatDown(assailant, amount);
            return amount;
        } else setPostureRechargeCD(CombatConfig.postureCD);
        //System.out.println(posture+" posture left on target");
        return 0f;
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
    public Tuple<Float, Float> getPrevSizes() {
        return new Tuple<>(prevWidth, prevHeight);
    }

    @Override
    public void setPrevSizes(float width, float height) {
        prevWidth = width;
        prevHeight = height;
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
        protec = Math.max(time, 0);
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
    public ItemStack getOffHand() {
        return lastTickOffhand == null ? ItemStack.EMPTY : lastTickOffhand;
    }

    @Override
    public void setOffHand(ItemStack is) {
        lastTickOffhand = is.copy();
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
        down = time;
    }

    private void beatDown(EntityLivingBase attacker, float overflow) {
        e.dismountRidingEntity();
        if (attacker != null)
            NeedyLittleThings.knockBack(e, attacker, overflow * 0.4F);
        int downtimer = MathHelper.clamp((int) (overflow * 40f), 40, MAXDOWNTIME);
        TaoCasterData.getTaoCap(e).setDownTimer(downtimer);
        //babe! it's 4pm, time for your flattening!
        //TaoCasterData.getTaoCap(e).setPrevSizes(e.width, e.height);//set this on the client as well
        TaoCasterData.forceUpdateTrackingClients(e);
        //float min = Math.min(e.width, e.height), max = Math.max(e.width, e.height);
        //NeedyLittleThings.setSize(e, max, min);
//        if (e instanceof EntityPlayer) {
//            EntityPlayer p = (EntityPlayer) e;
//            p.sendStatusMessage(new TextComponentTranslation("you have been staggered for " + downtimer / 20f + " seconds!"), true);
//        }
//        if (e.getRevengeTarget() instanceof EntityPlayer) {
//            EntityPlayer p = (EntityPlayer) e.getRevengeTarget();
//            p.sendStatusMessage(new TextComponentTranslation("the target has been staggered for " + downtimer / 20f + " seconds!"), true);
//        }
        //trip horse, trip person!
        if (e.isBeingRidden()) {
            for (Entity ent : e.getPassengers())
                if (ent instanceof EntityLivingBase) {
                    ent.removePassengers();
                    ITaoStatCapability cap = TaoCasterData.getTaoCap((EntityLivingBase) ent);
                    cap.consumePosture(cap.getMaxPosture() + 1, true);
                }
        }
        //System.out.println("target is downed for " + downtimer + " ticks!");
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
    }
}
