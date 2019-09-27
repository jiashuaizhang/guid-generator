package com.zhangjiashuai.guid.generator;

import java.util.Objects;

import com.zhangjiashuai.guid.config.SnowFlakeWorkerId;
import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.util.SnowFlake;

import lombok.extern.slf4j.Slf4j;

/**
 *     基于snowFlake实现的guid生成器
 * @author jiash
 */
@Slf4j
public class SnowFlakeGenerator implements GuidGenerator {
	
	private SnowFlakeWorkerId snowFlakeWorkerId;
	
	public SnowFlakeGenerator() {
		
	}
	
	public SnowFlakeGenerator(SnowFlakeWorkerId snowFlakeZookeeperWorkerId) {
		this.snowFlakeWorkerId = snowFlakeZookeeperWorkerId;
		String msg = "using default machineId:[{}], datacenterId:[{}]";
		if(snowFlakeZookeeperWorkerId != null) {
			log.info(msg, snowFlakeZookeeperWorkerId.getMachineId(), snowFlakeZookeeperWorkerId.getDatacenterId());
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
	
	private long getDefaultMachineId() {
		long machineId  = Const.DEFAULT_MACHINEID;
		if(snowFlakeWorkerId != null) {
			machineId = snowFlakeWorkerId.getMachineId();
		}
		return machineId;
	}
	
	private long getDefaultDataCenterId() {
		long dataCenterId  = Const.DEFAULT_DATACENTER_ID;
		if(snowFlakeWorkerId != null) {
			dataCenterId = snowFlakeWorkerId.getDatacenterId();
		}
		return dataCenterId;
	}
	
}
