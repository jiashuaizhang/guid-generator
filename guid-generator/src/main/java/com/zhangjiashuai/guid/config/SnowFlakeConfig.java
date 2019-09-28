package com.zhangjiashuai.guid.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.exception.GuidGenerateException;
import com.zhangjiashuai.guid.util.SnowFlake;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SnowFlakeConfig {
	
	private boolean zkWorkerIdEnabled;
	private boolean zkLeaderSelect;
	private String workerIdNode = "_workerId_";
	private String leaderSelectorNode = "_leaderSelector_";
	private int port;
	private CuratorFramework curator;
	private long machineId = Const.DEFAULT_MACHINEID;
	private long datacenterId = Const.DEFAULT_DATACENTER_ID;
	
	public SnowFlakeConfig() {
		
	}
	
	public SnowFlakeConfig(CuratorFramework curator) {
		this.curator = curator;
	}
	
	public void init() {
		if(this.zkWorkerIdEnabled) {
			zkWorkerId();
		}
	}
	
	private void zkWorkerId() {
		String machineIdNode = "_machineId_";
		String dataCenterIdNode = "_dataCenterId_";
		long machineId = nextWorkId(machineIdNode, SnowFlake.MAX_MACHINE_NUM, this.machineId);
		long dataCenterId = nextWorkId(dataCenterIdNode, SnowFlake.MAX_DATACENTER_NUM, this.datacenterId);
		long firstDataCenterId = dataCenterId;
		long firstMachineId = machineId;
		while (!checkValid(machineId, dataCenterId)) {
			 machineId = nextWorkId(machineIdNode, SnowFlake.MAX_MACHINE_NUM, this.machineId);
			 if(machineId == firstMachineId) {
				 log.info("datacenterId: [{}] is full", dataCenterId);
				 dataCenterId = nextWorkId(dataCenterIdNode, SnowFlake.MAX_DATACENTER_NUM, this.datacenterId);
				 if(dataCenterId == firstDataCenterId) {
					 throw new GuidGenerateException("worker exhausted");
				 }
			 }
		}
		this.machineId = machineId;
		this.datacenterId = dataCenterId;
	}
	
	private boolean checkValid(long machineId, long datacenterId) {
		String key = machineId + "-" + datacenterId;
		String nodePath = "/" + Const.ROOT + "/_existedWorker_/" + key;
		try {
			Stat stat = curator.checkExists().creatingParentsIfNeeded().forPath(nodePath);
			if(stat == null) {
				curator.create().withMode(CreateMode.EPHEMERAL).forPath(nodePath);
	            log.info("machineId:[{}] and datacenterId:[{}] is valid,lets use them", machineId, datacenterId);
			    return true;
			}
		} catch (NodeExistsException e) {
			log.info("machineId:[{}] and datacenterId:[{}] is in use", machineId, datacenterId);
		} catch (Exception e) {
			log.error("check worker valid error", e);
		}
		return false;
	}
	
	private long nextWorkId(String type, long maxNum, long defaultValue) {
		String nodePath = "/" + Const.ROOT + "/" + workerIdNode + "/" + type + "/" + type;
		try {
			String idWithPrefix = curator.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(nodePath);
			int beginIndex = nodePath.length();
			String idStr = idWithPrefix.substring(beginIndex);
			long id = Long.parseLong(idStr);
			long workerId = id % maxNum;
			log.info("got {}:[{}] by zookeeper", type, workerId);
			return workerId;
		}  catch (Exception e) {
			log.error("calculate {} error", type, e);
			return defaultValue;
		}
	}
}
