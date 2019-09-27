package com.zhangjiashuai.guid.config;

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
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.zhangjiashuai.guid.generator.RedisGuidGenerator;
import com.zhangjiashuai.guid.generator.SnowFlakeGenerator;
import com.zhangjiashuai.guid.generator.ZooKeeperGuidGenerator;

@Configuration
public class GuidGeneratorConfiguration {
	
	@Bean
    @ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "guid.zookeeper")
	@ConditionalOnProperty(prefix = "guid.zookeeper", name = "connectString")
	public ZooKeeperConfig zooKeeperConfig() {
		return new ZooKeeperConfig();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(ZooKeeperConfig.class)
	public CuratorFramework curator(@Autowired ZooKeeperConfig zooKeeperConfig) {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString(zooKeeperConfig.getConnectString())
                .sessionTimeoutMs(zooKeeperConfig.getSessionTimeOut()).retryPolicy(retryPolicy)
                .build();
        curator.start();
        return curator;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "guid", name = "impl", havingValue = "zookeeper")
	public ZooKeeperGuidGenerator zooKeeperGuidGenerator(@Autowired CuratorFramework curator) {
		return new ZooKeeperGuidGenerator(curator);
	}
	
	
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnProperty(prefix = "guid", name = "impl", havingValue = "redis")
    public RedisTemplate<Object, Object> redisTemplate(@Autowired RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<Object, Object>();
        template.setConnectionFactory(connectionFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
	
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "guid", name = "impl", havingValue = "redis")
    public RedisGuidGenerator redisGuidGenerator(@Autowired RedisTemplate<Object, Object> redisTemplate) {
    	return new RedisGuidGenerator(redisTemplate);
    }
    
	@Bean(initMethod = "init")
	@ConfigurationProperties(prefix = "guid.snowflake")
    @ConditionalOnProperty(prefix = "guid", name = "impl", havingValue = "snowflake", matchIfMissing = true)
    public SnowFlakeWorkerId snowFlakeZookeeperMachineId(@Autowired CuratorFramework curator) {
    	return new SnowFlakeWorkerId(curator);
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "guid", name = "impl", havingValue = "snowflake", matchIfMissing = true)
    public SnowFlakeGenerator snowFlakeGenerator(@Autowired(required = false) SnowFlakeWorkerId snowFlakeZookeeperMachineId) {
    	return new SnowFlakeGenerator(snowFlakeZookeeperMachineId);
    }
    
}
