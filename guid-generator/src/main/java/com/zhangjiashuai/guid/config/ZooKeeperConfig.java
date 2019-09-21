package com.zhangjiashuai.guid.config;

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
	private int sessionTimeOut = 3000;
	/**
	 * 清理id任务线程池大小，为0则不清理
	 */
	private int cleanExecutorSize = 1;
	/**
	 * 每隔多少个id触发一次清理
	 */
	private int cleanUnit = 100;
}
