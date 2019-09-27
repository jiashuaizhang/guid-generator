package com.zhangjiashuai.guid.generator;

import com.zhangjiashuai.guid.util.SnowFlake;
import com.zhangjiashuai.guid.util.SnowFlakeInstanceCache;

/**
 * guid生成器
 * @author jiash
 */
public interface GuidGenerator {
	
	/**
	  * 生成全局ID
	 * @return guid
	 */
	long generate();
	
	/**
	 *     生成ID,按照表名分组单独计数
	 * @param table 表名
	 * @return
	 */
	long generate(String table);
	
	/**
	 * 生成ID,按照表名分组单独计数，并拼接 "table + '-'"作为前缀
	 * @param table
	 * @return
	 */
	default String generateWithTablePrefix(String table) {
		long id = generate(table);
		return table + "-" + id;
	}
	
	/**
	 * 使用snowFlake生成ID
	 * @param datacenterId 数据中心id
	 * @param machineId 机器id
	 * @return
	 */
	default long generate(long datacenterId, long machineId) {
		SnowFlake snowFlake = SnowFlakeInstanceCache.get(datacenterId, machineId);
		return snowFlake.nextId();
	}
	
}
