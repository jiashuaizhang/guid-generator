package com.zhangjiashuai.guid.client.config;

import lombok.Data;

@Data
public class SnowFlakeConfig {
	
	private boolean zkLeaderSelect;
	private String leaderSelectorNode = "_leaderSelector_";
	private long machineId;
	private long datacenterId;
	private int port;
	
}
