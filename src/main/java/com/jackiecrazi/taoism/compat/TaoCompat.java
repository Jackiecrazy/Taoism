package com.jackiecrazi.taoism.compat;

import net.minecraftforge.fml.common.Loader;

public class TaoCompat {
    public static boolean isMovePlusLoaded;
    public static void checkCompatStatus(){
        isMovePlusLoaded= Loader.isModLoaded("moveplus");
    }
}
