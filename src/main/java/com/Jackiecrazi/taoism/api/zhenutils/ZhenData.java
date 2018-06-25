package com.jackiecrazi.taoism.api.zhenutils;

public class ZhenData {
private boolean shouldActivate;
private int lingpersend;
	public ZhenData(boolean s,int persend) {
		shouldActivate=s;
		setLingPerSend(persend);
	}
	public boolean shouldActivate() {
		return shouldActivate;
	}
	public void setShouldActivate(boolean shouldActivate) {
		this.shouldActivate = shouldActivate;
	}
	public boolean equals(Object obj) {
		if(!(obj instanceof ZhenData))
        return false;
		ZhenData tp=(ZhenData)obj;
		return(tp.shouldActivate==this.shouldActivate&&tp.lingpersend==this.lingpersend);
    }
	public int getLingPerSend() {
		return lingpersend;
	}
	public void setLingPerSend(int lingpersend) {
		this.lingpersend = lingpersend;
	}
	/**
	 * @return 0 for activate-maintain, -1 for conditional activation (flag), any positive integer for periodic (number is activation duration)
	 */
}
