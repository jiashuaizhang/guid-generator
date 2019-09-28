package com.zhangjiashuai.guid.zookeeper.listener;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import com.zhangjiashuai.guid.config.SnowFlakeConfig;
import com.zhangjiashuai.guid.consts.Const;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class LeaderListener extends LeaderSelectorListenerAdapter {
	
	private SnowFlakeConfig snowFlakeConfig;
	
	private LeaderSelector leaderSelector;
	
	private static final String DATA_TEMPLATE = "{\"ip\":\"%s\", \"port\":%d}";

	
	public LeaderListener(SnowFlakeConfig snowFlakeConfig) {
		this.snowFlakeConfig = snowFlakeConfig;
	}
	
	public LeaderListener() {
		
	}
	

	@Override
	public void takeLeadership(CuratorFramework client) throws Exception {
		int port = snowFlakeConfig.getPort();
		String ip = leaderSelector.getLeader().getId();
		log.info("leader changed, ip:[{}], port:[{}]", ip, port);
		String node = snowFlakeConfig.getMachineId() + "-" + snowFlakeConfig.getDatacenterId();
		String nodePath = "/" + Const.ROOT + "/" + snowFlakeConfig.getLeaderSelectorNode() + "/" + Const.LEADER_NODE + "/" + node;
		String data = String.format(DATA_TEMPLATE, ip, port);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, data.getBytes());
		} catch (NodeExistsException e) {
			client.setData().forPath(nodePath, data.getBytes());
		}
		while (true) {
			TimeUnit.SECONDS.sleep(60);
			log.info("leader running, ip:[{}], port:[{}]", ip, port);
		}
	}

}
