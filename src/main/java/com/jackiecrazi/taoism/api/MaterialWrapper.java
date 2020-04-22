package com.jackiecrazi.taoism.api;


public class MaterialWrapper {
	public final MaterialStatWrapper msw;
	public final int amount;

	public MaterialWrapper(MaterialStatWrapper m, int amnt) {
		msw = m;
		amount = amnt;
	}
	
	public boolean isAnvilWorked(){
		return msw.name.startsWith("ingot");
	}
}