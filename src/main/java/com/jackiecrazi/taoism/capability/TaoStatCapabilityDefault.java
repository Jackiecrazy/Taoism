package com.jackiecrazi.taoism.capability;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;

public class TaoStatCapabilityDefault implements ITaoStatCapability {

    @Override
    public float getQi() {
        return 0;
    }

    @Override
    public void setQi(float amount) {

    }

    @Override
    public float addQi(float amount) {
        return 0;
    }

    @Override
    public boolean consumeQi(float amount) {
        return false;
    }

    @Override
    public float getLing() {
        return 0;
    }

    @Override
    public void setLing(float amount) {

    }

    @Override
    public float addLing(float amount) {
        return 0;
    }

    @Override
    public boolean consumeLing(float amount) {
        return false;
    }

    @Override
    public JUMPSTATE getJumpState() {
        return null;
    }

    @Override
    public void setJumpState(JUMPSTATE js) {

    }

    @Override
    public ClingData getClingDirections() {
        return null;
    }

    @Override
    public void setClingDirections(ClingData dirs) {

    }

    @Override
    public float getSwing() {
        return 0;
    }

    @Override
    public void setSwing(float amount) {

    }

    @Override
    public float getPosture() {
        return 0;
    }

    @Override
    public void setPosture(float amount) {

    }

    @Override
    public float addPosture(float amount) {
        return 0;
    }

    @Override
    public float consumePosture(float amount, boolean canStagger) {
        return 0;
    }

    @Override
    public float consumePosture(float amount, boolean canStagger, EntityLivingBase assailant) {
        return 0;
    }

    @Override
    public int getParryCounter() {
        return 0;
    }

    @Override
    public void setParryCounter(int amount) {

    }

    @Override
    public void addParryCounter(int amount) {

    }

    @Override
    public int getRollCounter() {
        return 0;
    }

    @Override
    public void setRollCounter(int amount) {

    }

    @Override
    public void addRollCounter(int amount) {

    }

    @Override
    public Tuple<Float, Float> getPrevSizes() {
        return null;
    }

    @Override
    public void setPrevSizes(float width, float height) {

    }

    @Override
    public int getComboSequence() {
        return 0;
    }

    @Override
    public void setComboSequence(int amount) {

    }

    @Override
    public void incrementComboSequence(int amount) {

    }

    @Override
    public void copy(ITaoStatCapability from) {

    }

    @Override
    public float getMaxLing() {
        return 0;
    }

    @Override
    public void setMaxLing(float amount) {

    }

    @Override
    public float getMaxPosture() {
        return 0;
    }

    @Override
    public void setMaxPosture(float amount) {

    }

    @Override
    public int getLingRechargeCD() {
        return 0;
    }

    @Override
    public void setLingRechargeCD(int amount) {

    }

    @Override
    public int getPostureRechargeCD() {
        return 0;
    }

    @Override
    public void setPostureRechargeCD(int amount) {

    }

    @Override
    public int getStaminaRechargeCD() {
        return 0;
    }

    @Override
    public void setStaminaRechargeCD(int amount) {

    }

    @Override
    public int getQiGracePeriod() {
        return 0;
    }

    @Override
    public void setQiGracePeriod(int amount) {

    }

    @Override
    public int getPosInvulTime() {
        return 0;
    }

    @Override
    public void setPosInvulTime(int time) {

    }

    @Override
    public long getLastUpdatedTime() {
        return 0;
    }

    @Override
    public void setLastUpdatedTime(long time) {

    }

    @Override
    public boolean isSwitchIn() {
        return false;
    }

    @Override
    public void setSwitchIn(boolean suichi) {

    }

    @Override
    public int getOffhandCool() {
        return 0;
    }

    @Override
    public void setOffhandCool(int oc) {

    }

    @Override
    public boolean isOffhandAttack() {
        return false;
    }

    @Override
    public void setOffhandAttack(boolean off) {

    }

    @Override
    public ItemStack getOffHand() {
        return null;
    }

    @Override
    public void setOffHand(ItemStack is) {

    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public void setProtected(boolean protect) {

    }

    @Override
    public int getDownTimer() {
        return 0;
    }

    @Override
    public void setDownTimer(int time) {

    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }
}
