package com.zhangjiashuai.guid.client.leader;

import com.zhangjiashuai.guid.client.exception.GuidGenerateException;

public final class LeaderAddress {
	
	static volatile String ip;
	static volatile int port = -1;
	
	private LeaderAddress() {}
	
	public static String getIp() {
		if(ip == null) {
			throw new GuidGenerateException("no leader ip found");
		}
		return ip;
	}
	
	public static int getPort() {
		if(port == -1) {
			throw new GuidGenerateException("no leader port found");
		}
		return port;
	}
	
	public static String getIpAndPort() {
		return getIp() + ":" + getPort();
	}
	
	public static String getHttpAddress() {
		return "http://" + getIpAndPort() + "/";
	}
	
	public static String getHttpsAddress() {
		return "https://" + getIpAndPort() + "/";
	}
	
}
