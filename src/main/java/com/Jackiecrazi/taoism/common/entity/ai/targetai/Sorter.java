package com.Jackiecrazi.taoism.common.entity.ai.targetai;

import java.util.Comparator;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;


public class Sorter implements Comparator {
        private final Entity theEntity;
        private static final String __OBFID = "CL_00001622";

        public Sorter(Entity p_i1662_1_)
        {
            this.theEntity = p_i1662_1_;
        }

        public int compare(Entity p_compare_1_, Entity p_compare_2_)
        {
            double d0 = this.theEntity.getDistanceSqToEntity(p_compare_1_);
            double d1 = this.theEntity.getDistanceSqToEntity(p_compare_2_);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
        /**
         * 
         * @param one
         * @param two
         * @return -1 if the first is less than the second, 1 if vice versa, 0 if neither.
         */
        public int compare(ChunkCoordinates one, ChunkCoordinates two)
        {
        	ChunkCoordinates orig=new ChunkCoordinates((int)this.theEntity.posX,(int)this.theEntity.posY,(int)this.theEntity.posZ);
            double d0 = orig.getDistanceSquaredToChunkCoordinates(one);
            double d1 = orig.getDistanceSquaredToChunkCoordinates(two);
            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }

        public int compare(Object p_compare_1_, Object p_compare_2_)
        {
            return this.compare((Entity)p_compare_1_, (Entity)p_compare_2_);
        }
}
