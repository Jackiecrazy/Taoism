package com.jackiecrazi.taoism.api;

public class MoveCode {
    /*
    a movement code is a byte that represents the series of inputs of the player.
    Toggle, forward, back, left, right, jump, sneak, mouse button
    A byte is 8 bits so this fits perfectly.
     */
    private boolean isValid, isForwardPressed, isBackPressed, isLeftPressed, isRightPressed, isJumpPressed, isSneakPressed, isLeftClick;

    public MoveCode(boolean valid, boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sneak, boolean isLeft) {
        isValid = valid;
        isForwardPressed = forward;
        isBackPressed = back;
        isLeftPressed = left;
        isRightPressed = right;
        isJumpPressed = jump;
        isSneakPressed = sneak;
        isLeftClick = isLeft;
    }

    public MoveCode(byte data) {
        isValid = BinaryMachiavelli.getBoolean(data, 0);
        isForwardPressed = BinaryMachiavelli.getBoolean(data, 1);
        isBackPressed = BinaryMachiavelli.getBoolean(data, 2);
        isLeftPressed = BinaryMachiavelli.getBoolean(data, 3);
        isRightPressed = BinaryMachiavelli.getBoolean(data, 4);
        isJumpPressed = BinaryMachiavelli.getBoolean(data, 5);
        isSneakPressed = BinaryMachiavelli.getBoolean(data, 6);
        isLeftClick = BinaryMachiavelli.getBoolean(data, 7);
    }

    public static enum CriteriaType{
        ON,
        OFF,
        IGNORE
    }
    private static CriteriaType[] quickLookup={
            CriteriaType.OFF,
            CriteriaType.IGNORE,
            CriteriaType.ON
    };

    public static class MoveCriteria{
        public final CriteriaType isForwardPressed, isBackPressed, isLeftPressed, isRightPressed, isJumpPressed, isSneakPressed, isLeftClick;
        public MoveCriteria(CriteriaType forward, CriteriaType back, CriteriaType left, CriteriaType right, CriteriaType jump, CriteriaType sneak, CriteriaType isLeft){
            isForwardPressed = forward;
            isBackPressed = back;
            isLeftPressed = left;
            isRightPressed = right;
            isJumpPressed = jump;
            isSneakPressed = sneak;
            isLeftClick = isLeft;
        }

        /**
         * -1 for off, 0 for ignore, 1 for on
         */
        public MoveCriteria(int forward, int back, int left, int right, int jump, int sneak, int leftClick){
            isForwardPressed = quickLookup[forward+1];
            isBackPressed = quickLookup[back+1];
            isLeftPressed = quickLookup[left+1];
            isRightPressed = quickLookup[right+1];
            isJumpPressed = quickLookup[jump+1];
            isSneakPressed = quickLookup[sneak+1];
            isLeftClick = quickLookup[leftClick+1];
        }
    }

    public byte toByte() {
        int out = 0;
        out = BinaryMachiavelli.setBoolean(out, 0, isValid);
        out = BinaryMachiavelli.setBoolean(out, 1, isForwardPressed);
        out = BinaryMachiavelli.setBoolean(out, 2, isBackPressed);
        out = BinaryMachiavelli.setBoolean(out, 3, isLeftPressed);
        out = BinaryMachiavelli.setBoolean(out, 4, isRightPressed);
        out = BinaryMachiavelli.setBoolean(out, 5, isJumpPressed);
        out = BinaryMachiavelli.setBoolean(out, 6, isSneakPressed);
        out = BinaryMachiavelli.setBoolean(out, 7, isLeftClick);
        return (byte) out;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof MoveCode))return false;
        MoveCode mc= (MoveCode)obj;
        return (this.toByte() == mc.toByte());
    }

    public boolean compareTo(MoveCriteria mc){
        if(!isValid)return false;
        if(mc.isForwardPressed!=CriteriaType.IGNORE&&(isForwardPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isForwardPressed)return false;
        if(mc.isBackPressed!=CriteriaType.IGNORE&&(isBackPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isBackPressed)return false;
        if(mc.isLeftPressed!=CriteriaType.IGNORE&&(isLeftPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isLeftPressed)return false;
        if(mc.isRightPressed!=CriteriaType.IGNORE&&(isRightPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isRightPressed)return false;
        if(mc.isJumpPressed!=CriteriaType.IGNORE&&(isJumpPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isJumpPressed)return false;
        if(mc.isSneakPressed!=CriteriaType.IGNORE&&(isSneakPressed?CriteriaType.ON:CriteriaType.OFF)!=mc.isSneakPressed)return false;
        if(mc.isLeftClick!=CriteriaType.IGNORE&&(isLeftClick?CriteriaType.ON:CriteriaType.OFF)!=mc.isLeftClick)return false;
        return true;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isForwardPressed() {
        return isForwardPressed;
    }

    public boolean isBackPressed() {
        return isBackPressed;
    }

    public boolean isLeftPressed() {
        return isLeftPressed;
    }

    public boolean isRightPressed() {
        return isRightPressed;
    }

    public boolean isJumpPressed() {
        return isJumpPressed;
    }

    public boolean isSneakPressed() {
        return isSneakPressed;
    }

    public boolean isLeftClick() {
        return isLeftClick;
    }
}
