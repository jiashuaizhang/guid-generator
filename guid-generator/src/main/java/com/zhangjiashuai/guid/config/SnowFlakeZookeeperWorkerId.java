package com.zhangjiashuai.guid.config;

import java.nio.charset.StandardCharsets;

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
public class SnowFlakeZookeeperWorkerId {
	
	private boolean enabled;
	private String node;
	private final String defaultNode = "_workerId_";
	private CuratorFramework curator;
	private long machineId = Const.DEFAULT_MACHINEID;
	private long dataCenterId = Const.DEFAULT_DATACENTER_ID;
	
	public SnowFlakeZookeeperWorkerId() {
		
	}
	
	public SnowFlakeZookeeperWorkerId(CuratorFramework curator) {
		this.curator = curator;
	}
	
	public void init() {
		if(!this.enabled) {
			return;
		}
		if(node == null) {
			node = defaultNode;
		}
		String machineIdNode = "_machineId_";
		String dataCenterIdNode = "_dataCenterId_";
		long machineId = nextWorkId(machineIdNode, SnowFlake.MAX_MACHINE_NUM, this.machineId);
		long dataCenterId = nextWorkId(dataCenterIdNode, SnowFlake.MAX_DATACENTER_NUM, this.dataCenterId);
		long firstDataCenterId = dataCenterId;
		long firstMachineId = machineId;
		while (!checkValid(machineId, dataCenterId)) {
			 machineId = nextWorkId(machineIdNode, SnowFlake.MAX_MACHINE_NUM, this.machineId);
			 if(machineId == firstMachineId) {
				 log.info("dataCenterId: [{}] is full", dataCenterId);
				 dataCenterId = nextWorkId(dataCenterIdNode, SnowFlake.MAX_DATACENTER_NUM, this.dataCenterId);
				 if(dataCenterId == firstDataCenterId) {
					 throw new GuidGenerateException("worker exhausted");
				 }
			 }
		}
		this.machineId = machineId;
		this.dataCenterId = dataCenterId;
	}
	
	private boolean checkValid(long machineId, long dataCenterId) {
		String key = machineId + "-" + dataCenterId;
		String nodePath = "/" + Const.ROOT + "/_existedWorker_/" + key;
		try {
			Stat stat = curator.checkExists().creatingParentsIfNeeded().forPath(nodePath);
			if(stat == null) {
				curator.create().storingStatIn(stat).withMode(CreateMode.EPHEMERAL).forPath(nodePath, defaultNode.getBytes(StandardCharsets.UTF_8));
	                        log.info("machineId:[{}] and dataCenterId:[{}] is valid,lets use them", machineId, dataCenterId);
			        return true;
			}
		} catch (NodeExistsException e) {
			log.info("machineId:[{}] and dataCenterId:[{}] is in use", machineId, dataCenterId);
		} catch (Exception e) {
			log.error("check worker valid error", e);
		}
		return false;
	}
	
	private long nextWorkId(String type, long maxNum, long defaultValue) {
		String nodePath = "/" + Const.ROOT + "/" + node + "/" + type + "/" + type;
		try {
			String idWithPrefix = curator.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(nodePath, defaultNode.getBytes(StandardCharsets.UTF_8));
			int beginIndex = nodePath.length();
			String idStr = idWithPrefix.substring(beginIndex);
			log.info("idWithPrefix {} : {}", type, idWithPrefix);
			long id = Long.parseLong(idStr);
			long workerId = Math.abs(id) % maxNum;
			log.info("got {}:[{}] by zookeeper", type, workerId);
			return workerId;
		}  catch (Exception e) {
			log.error("calculate {} error", node, e);
			return defaultValue;
		}
	}
}
