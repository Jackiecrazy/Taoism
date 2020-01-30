package com.jackiecrazi.taoism.api;

import com.jackiecrazi.taoism.common.entity.EntityMove;

public class MoveMap {
    public enum KEYCOMBO{
        LEFTNORMAL,
        LEFTJUMP,
        RIGHTNORMAL,
        RIGHTJUMP,
        PARRY
    }
    private EntityMove[] left0=new EntityMove[0], left1=new EntityMove[0], right0=new EntityMove[0], right1=new EntityMove[0], parry=new EntityMove[0];
    public MoveMap put(KEYCOMBO key, EntityMove... moves){
        switch(key){
            case LEFTNORMAL:
                left0=moves;
                break;
            case LEFTJUMP:
                left1=moves;
                break;
            case RIGHTNORMAL:
                right0=moves;
                break;
            case RIGHTJUMP:
                right1=moves;
                break;
            case PARRY:
                parry=moves;
                break;
        }
        return this;
    }
    public EntityMove[] get(KEYCOMBO key){
        switch(key){
            case LEFTJUMP:
                return left1;
            case RIGHTNORMAL:
                return right0;
            case RIGHTJUMP:
                return right1;
            case PARRY:
                return parry;
            default:
                return left0;
        }
    }
}
