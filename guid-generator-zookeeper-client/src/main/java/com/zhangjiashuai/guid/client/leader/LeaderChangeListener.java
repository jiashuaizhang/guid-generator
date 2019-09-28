package com.zhangjiashuai.guid.client.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhangjiashuai.guid.client.config.SnowFlakeConfig;
import com.zhangjiashuai.guid.client.consts.Const;
import com.zhangjiashuai.guid.client.exception.GuidGenerateException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeaderChangeListener {
	
	private CuratorFramework curator;
	private SnowFlakeConfig snowFlakeConfig;
	
	public LeaderChangeListener(CuratorFramework curator, SnowFlakeConfig snowFlakeConfig) {
		this.curator = curator;
		this.snowFlakeConfig = snowFlakeConfig;
	}

	public LeaderChangeListener() {}
	
	@SuppressWarnings({ "resource", "deprecation" })
	public void init() {
		String node = snowFlakeConfig.getMachineId() + "-" + snowFlakeConfig.getDatacenterId();
		String nodePath = "/" + Const.ROOT + "/" + snowFlakeConfig.getLeaderSelectorNode() + "/" + Const.LEADER_NODE + "/" + node;
        try {
			final NodeCache nodeCache = new NodeCache(curator, nodePath);
			nodeCache.start(true);
			if(nodeCache.getCurrentData() != null) {
				String data = new String(nodeCache.getCurrentData().getData());
				log.info("get leader node, data: {}", data);
				setIpAndPort(data);
			}
			nodeCache.getListenable().addListener(new NodeCacheListener() {
			    @Override
			    public void nodeChanged() throws Exception {
			    	if(nodeCache.getCurrentData() == null) {
				        log.info("leader lost, ip:[{}], port:[{}]", LeaderAddress.ip, LeaderAddress.port);
				        LeaderAddress.ip = null;
				        LeaderAddress.port = -1;
			    	} else {
			    		String newData = new String(nodeCache.getCurrentData().getData());
			    		log.info("leader node changed,new data: {}", newData);
			    		setIpAndPort(newData);
			    	}
			    }
			});
		} catch (Exception e) {
			throw new GuidGenerateException("failed to get snowflake leader", e);
		}
	}
	
    private void setIpAndPort(String data) {
        JSONObject json = JSON.parseObject(data);
        LeaderAddress.ip = json.getString("ip");
        LeaderAddress.port = json.getIntValue("port");
    }
}
