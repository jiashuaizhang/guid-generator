package com.zhangjiashuai.guid.client.config;

import lombok.Data;

@Data
public class ZooKeeperConfig {
	/**
	 * zookeeper连接地址
	 */
	private String connectString;
	/**
	 * zookeeper会话超时时间
	 */
	private int sessionTimeOut = 60000;
	
}
