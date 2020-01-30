package com.jackiecrazi.taoism.api;

public class PartDefinition {
    public final String name;
    public final MaterialType[] accepted;
    public final boolean necessary;
    public PartDefinition(String id, boolean mandatory, MaterialType... acceptable){
        name=id;
        accepted=acceptable;
        necessary=mandatory;
    }
    public PartDefinition(String id, MaterialType... acceptable){
        this(id,true,acceptable);
    }
}
