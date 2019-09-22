package com.zhangjiashuai.guid.consts;

import java.util.Random;

import com.zhangjiashuai.guid.util.SnowFlake;

public class Const {
	
	public static final String ROOT = "guid";
	public static final String DEFAULT_TABLE = "_default_";
	
	private static Random idRandom = new Random();
	
	public static final long DEFAULT_DATACENTER_ID = idRandom.nextInt((int) SnowFlake.MAX_DATACENTER_NUM);
	public static final long DEFAULT_MACHINEID = idRandom.nextInt((int) SnowFlake.MAX_MACHINE_NUM);
	
}
