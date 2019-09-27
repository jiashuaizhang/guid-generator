package com.zhangjiashuai.guid.generator;

import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;

import com.zhangjiashuai.guid.consts.Const;

import lombok.Setter;

/**
 *     基于redis实现的guid生成器
 * @author jiash
 */
@Setter
public class RedisGuidGenerator implements GuidGenerator {
	
	private RedisTemplate<Object, Object> redisTemplate;
	
	public RedisGuidGenerator() {
		
	}
	
	public RedisGuidGenerator(RedisTemplate<Object, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	private static String keyPrefix;
	private static String defaultKey;
	
	static {
		keyPrefix = Const.ROOT + ":";
		defaultKey = keyPrefix + Const.DEFAULT_TABLE;
	}
	
	@Override
	public long generate() {
		return generate(defaultKey);
	}

	@Override
	public long generate(String table) {
		Objects.requireNonNull(redisTemplate);
		Objects.requireNonNull(table);
		String key = keyPrefix + table;
		Long value = redisTemplate.opsForValue().increment(key, 1);
		return value;
	}

}
