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
