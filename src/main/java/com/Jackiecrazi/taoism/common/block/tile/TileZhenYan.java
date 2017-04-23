package com.Jackiecrazi.taoism.common.block.tile;

import java.util.HashMap;
import java.util.Map.Entry;

import com.Jackiecrazi.taoism.api.TaoistPosition;
import com.Jackiecrazi.taoism.api.zhenutils.ZhenData;


public class TileZhenYan extends AbstractZhenComponent {
	private HashMap<TaoistPosition,ZhenData> priority=new HashMap<TaoistPosition,ZhenData>();
	private int maxPerTick,maxSize;
	
	public TileZhenYan(int meta) {
		super(meta);
		maxPerTick=(meta)*200;
		maxSize=(meta+1)*2;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!priority.isEmpty()){
			for(Entry<TaoistPosition,ZhenData> e:priority.entrySet()){
				if(e.getValue().shouldActivate()){
					this.sendLingTo(e.getKey().getX(), e.getKey().getY(), e.getKey().getZ(), e.getValue().getLingPerSend());
				}
			}
		}
	}
	//and then activate every update, sending ling over. The legs activate when they get enough ling, so all control's here
	//PRIORITY IS NOT NEEDED
	@Override
	protected boolean receiveLingRequest(int x, int y, int z, int amnt) {
		if(priority.keySet().size()==maxSize)return false;
		if(!priority.containsKey(new TaoistPosition(x,y,z,worldObj)))priority.put(new TaoistPosition(x,y,z,worldObj), new ZhenData(true,amnt));
		if(amnt>lingAmount&&maxPerTick<amnt)return false;
		if(!(worldObj.getTileEntity(yanx, yany, yanz) instanceof AbstractZhenComponent))return false;
		((AbstractZhenComponent)worldObj.getTileEntity(yanx, yany, yanz)).addLing(Math.min(amnt,maxPerTick));
		return this.removeLing(amnt);
	}
}
