package com.jackiecrazi.taoism.client.stupidmodels;

/**
 * KEK.
 * 8 shades of alpha and 16 colors every other channel
 * bits 0~3 are blue
 * bits 4~7 are green
 * bits 8~11 are red
 * bits 12~15
 * why only 8 shades of alpha? because minecraft's bad damage handling ._.
 */
public class WeirdColor {
    short color;

    public WeirdColor(int r, int g, int b, int a) {
        r /= 32;
        g /= 32;
        b /= 32;
        a /= 32;
        //int col = BinaryMachiavelli.setInteger(0, )
        color =0;
    }

    public int getRed() {
        return (getRGB() >> 0) & 0xFF;
    }

    public int getGreen() {
        return (getRGB() >> 3) & 0xFF;
    }

    public int getBlue() {
        return (getRGB() >> 0) & 0xFF;
    }

    public int getAlpha() {
        return (getRGB() >> 0) & 0xFF;
    }

    public int getRGB() {
        return color;
    }
}
