package com.zhangjiashuai.guid.generator;

import java.util.Objects;

import com.zhangjiashuai.guid.config.SnowFlakeZookeeperWorkerId;
import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.util.SnowFlake;

import lombok.extern.slf4j.Slf4j;

/**
 *     基于snowFlake实现的guid生成器
 * @author jiash
 */
@Slf4j
public class SnowFlakeGenerator implements GuidGenerator {
	
	private SnowFlakeZookeeperWorkerId snowFlakeZookeeperWorkerId;
	
	public SnowFlakeGenerator() {
		
	}
	
	public SnowFlakeGenerator(SnowFlakeZookeeperWorkerId snowFlakeZookeeperWorkerId) {
		this.snowFlakeZookeeperWorkerId = snowFlakeZookeeperWorkerId;
		String msg = "using default machineId:[{}], dataCenterId:[{}]";
		if(snowFlakeZookeeperWorkerId != null) {
			log.info(msg, snowFlakeZookeeperWorkerId.getMachineId(), snowFlakeZookeeperWorkerId.getDataCenterId());
		} else {
			log.info(msg, Const.DEFAULT_MACHINEID, Const.DEFAULT_DATACENTER_ID);
		}
	}
	
	@Override
	public long generate() {
		return generate(getDefaultDataCenterId(), getDefaultMachineId());
	}

	@Override
	public long generate(String table) {
		Objects.requireNonNull(table);
		long datacenterId = Math.abs(table.hashCode()) % SnowFlake.MAX_DATACENTER_NUM;
		return generate(datacenterId, getDefaultMachineId());
	}

	@Override
	public String generateWithTablePrefix(String table) {
		long id = generate(table);
		return table + "-" + id;
	}
	
	private long getDefaultMachineId() {
		long machineId  = Const.DEFAULT_MACHINEID;
		if(snowFlakeZookeeperWorkerId != null && snowFlakeZookeeperWorkerId.isEnabled()) {
			machineId = snowFlakeZookeeperWorkerId.getMachineId();
		}
		return machineId;
	}
	
	private long getDefaultDataCenterId() {
		long dataCenterId  = Const.DEFAULT_DATACENTER_ID;
		if(snowFlakeZookeeperWorkerId != null && snowFlakeZookeeperWorkerId.isEnabled()) {
			dataCenterId = snowFlakeZookeeperWorkerId.getDataCenterId();
		}
		return dataCenterId;
	}
	
}
