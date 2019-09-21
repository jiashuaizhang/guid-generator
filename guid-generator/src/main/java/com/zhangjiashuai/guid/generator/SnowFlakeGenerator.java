package com.zhangjiashuai.guid.generator;

import java.util.Objects;

import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.util.SnowFlake;

/**
 *     基于snowFlake实现的guid生成器
 * @author jiash
 */
public class SnowFlakeGenerator implements GuidGenerator {

	@Override
	public long generate() {
		return generate(Const.DEFAULT_DATACENTER_ID, Const.DEFAULT_MACHINEID);
	}

	@Override
	public long generate(String table) {
		Objects.requireNonNull(table);
		long datacenterId = Math.abs(table.hashCode()) % SnowFlake.MAX_DATACENTER_NUM;
		return generate(datacenterId, Const.DEFAULT_MACHINEID);
	}

	@Override
	public String generateWithTablePrefix(String table) {
		long id = generate(table);
		return table + "-" + id;
	}
	
}
