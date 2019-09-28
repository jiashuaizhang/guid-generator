package com.zhangjiashuai.guid.client.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zhangjiashuai.guid.client.leader.LeaderChangeListener;

@Configuration
public class ZooKeeperClientConfiguration {
	
	@Bean
    @ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "guid.zookeeper")
	@ConditionalOnProperty(prefix = "guid.zookeeper", name = "connectString")
	public ZooKeeperConfig zooKeeperConfigClient() {
		return new ZooKeeperConfig();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(ZooKeeperConfig.class)
	public CuratorFramework curator(@Autowired ZooKeeperConfig zooKeeperConfigClient) {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperConfigClient.getConnectString())
                .sessionTimeoutMs(zooKeeperConfigClient.getSessionTimeOut()).retryPolicy(retryPolicy)
                .build();
        curator.start();
        return curator;
	}
	
	@Bean
	@ConfigurationProperties(prefix = "guid.snowflake")
	@ConditionalOnMissingBean
    public SnowFlakeConfig snowFlakeConfigClient() {
    	return new SnowFlakeConfig();
    }
	
	@Bean(initMethod = "init")
	@ConditionalOnMissingBean
    public LeaderChangeListener leaderChangeListener(@Autowired CuratorFramework curator, @Autowired SnowFlakeConfig snowFlakeConfigClient) {
		if(!snowFlakeConfigClient.isZkLeaderSelect()) {
			return null;
		}
		return new LeaderChangeListener(curator, snowFlakeConfigClient);
    }
}
