package com.jackiecrazi.taoism.moves;

import com.jackiecrazi.taoism.common.entity.EntityMove;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MovesOverlord {
    private static final HashMap<Item, EntityMove[][]> moveList=new HashMap<>();
    public static void init(){

    }
    public static void add(Item item, int qiLevel, EntityMove... moves){
        EntityMove[][] newList=moveList.computeIfAbsent(item, k -> new EntityMove[10][]);
        newList[qiLevel]=moves;
        moveList.put(item,newList);
    }
    @Nullable
    public static EntityMove[] getMoves(@Nullable Item item, int qiLevel){
        if(item==null)return moveList.get(Items.AIR)[qiLevel];
        return moveList.get(item)[qiLevel];
    }
}
