package com.zhangjiashuai.guid.util;

import java.util.List;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * snowFlake实例缓存
 * @author jiash
 */
public final class SnowFlakeInstanceCache {
	
	private SnowFlakeInstanceCache() {
		
	}
	
    private static final LoadingCache<List<Long>, SnowFlake> SNOW_FLAKE_CACHE = CacheBuilder.newBuilder()
    		.build(new CacheLoader<List<Long>, SnowFlake>() {
                @Override
                public SnowFlake load(List<Long> key) {
                    return new SnowFlake(key.get(0), key.get(1));
                }
            });
    
    public static SnowFlake get(long datacenterId, long machineId) {
    	List<Long> key = Lists.newArrayList(datacenterId, machineId);
    	return SNOW_FLAKE_CACHE.getUnchecked(key);
    }
}
