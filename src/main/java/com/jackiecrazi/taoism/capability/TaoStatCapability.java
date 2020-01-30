package com.jackiecrazi.taoism.capability;

import com.jackiecrazi.taoism.config.CombatConfig;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;

public class TaoStatCapability implements ITaoStatCapability {
    private float qi, ling, posture;
    private int combo, swing, ohcool;
    private float maxLing, maxPosture, maxStamina;
    private int lcd, pcd, scd, down;
    private long timey;
    private boolean swi, protecc;
    private int parry, dodge, protec;
    private float prevWidth, prevHeight;

    @Override
    public float getPosture() {
        return posture;
    }

    @Override
    public void setPosture(float amount) {
        posture = MathHelper.clamp(amount, 0f, getMaxPosture());
        if (posture == getMaxPosture()) setProtected(true);
    }

    @Override
    public float addPosture(float amount) {
        posture += amount;
        if (posture >= getMaxPosture()) {
            posture = getMaxPosture();
            setProtected(true);
        }
        return posture;
    }

    @Override
    public int getDownTimer() {
        return down;
    }

    @Override
    public void setDownTimer(int time) {
        down = time;
    }

    @Override
    public boolean consumePosture(float amount, boolean canStagger) {
        float cache = posture;
        posture -= amount;
        if (posture <= 0f) {
            setProtected(false);//cancels ssp so you can regen posture without delay
            if ((cache >= getMaxPosture() / 4 && isProtected() && CombatConfig.ssp) || !canStagger || getPosInvulTime() > 0) {
                //sudden stagger prevention
                posture = 0.01f;
                if (canStagger && getPosInvulTime() <= 0) setPosInvulTime(CombatConfig.ssptime);
                return true;
            }
            posture = 0f;
            return false;
        } else setPostureRechargeCD(CombatConfig.postureCD);
        //System.out.println(posture+" posture left on target");
        return true;
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
    public float getQi() {
        return qi;
    }

    @Override
    public void setQi(float amount) {
        qi = amount;
    }

    @Override
    public float addQi(float amount) {
        qi += amount;
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
    public int getSwing() {
        return swing;
    }

    @Override
    public void setSwing(int amount) {
        swing = amount;
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
        maxPosture = amount;
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
    public int getPosInvulTime() {
        return protec;
    }

    @Override
    public void setPosInvulTime(int time) {
        protec = Math.max(time,0);
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
    public boolean isProtected() {
        return protecc;
    }

    @Override
    public void setProtected(boolean protect) {
        protecc = protect;
    }


}
