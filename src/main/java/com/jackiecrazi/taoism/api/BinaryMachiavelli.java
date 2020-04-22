package com.jackiecrazi.taoism.api;

import java.util.HashMap;

public class BinaryMachiavelli {

private static HashMap<String, HashMap<Integer, Integer>> cachedStartEndBitMaps = new HashMap();
	
	private static HashMap<Integer, Integer> getBitMap(int start, int end)
	{
		HashMap<Integer, Integer> bitMap = null;
		String cacheKey = start + "," + end;
		
		if (cachedStartEndBitMaps.containsKey(cacheKey))
		{
			bitMap = cachedStartEndBitMaps.get(cacheKey);
		}
		else
		{
			bitMap = new HashMap();
			
			for (int i = start; i <= end; i++)
			{
				bitMap.put(i, i - start);
			}
			
			cachedStartEndBitMaps.put(cacheKey, bitMap);
		}
		
		return bitMap;
	}

	public static int getInteger(int source, int start, int end)
	{
		return getInteger(source, getBitMap(start, end));
	}

	public static int getInteger(int source, HashMap<Integer, Integer> bitMap)
	{
		int output = 0;
		
		for (int bitLocation : bitMap.keySet())
		{
			boolean bit = (source & (1 << bitLocation)) != 0;
			
			if (bit)
				output += 1 << bitMap.get(bitLocation);
		}
		
		return output;
	}
	
	public static int setInteger(int source, int start, int end, int value)
	{
		return setInteger(source, getBitMap(start, end), value);
	}
	
	public static int setInteger(int source, HashMap<Integer, Integer> bitMap, int value)
	{
		int output = source;
		
		int limit = (1 << bitMap.size()) - 1;
		
		if (value > limit)
		{
			value = limit;
		}
		
		for (int bitLocation : bitMap.keySet())
		{
			int valueBitSize = 1 << bitMap.get(bitLocation);
			boolean valueBit = (value & valueBitSize) == valueBitSize;
			
			int sourceBitSize = 1 << bitLocation;
			
			if (valueBit)
				output |= sourceBitSize;
			else
				output &= ~sourceBitSize;
		}
		
		return output;
	}
	
	public static boolean getBoolean(int source, int bit)
	{
		int bitSize = 1 << bit;
		return (source & bitSize) == bitSize;
	}
	
	public static int setBoolean(int source, int bit, boolean value)
	{
		int output = source;
		int bitSize = 1 << bit;
		
		if (value)
			output |= bitSize;
		else
			output &= ~bitSize;
		
		return output;
	}

}
