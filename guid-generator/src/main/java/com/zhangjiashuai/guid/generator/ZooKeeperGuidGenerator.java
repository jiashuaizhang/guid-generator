package com.zhangjiashuai.guid.generator;

import java.util.Objects;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.exception.GuidGenerateException;

import lombok.Setter;

/**
 * 基于zookeeper实现的guid生成器
 * @author jiash
 */
@Setter
public class ZooKeeperGuidGenerator implements GuidGenerator{
	
	private CuratorFramework curator;
	
	public ZooKeeperGuidGenerator() {
		
	}
	
	public ZooKeeperGuidGenerator(CuratorFramework curator) {
		this.curator = curator;
	}
	
	private static String root;
	private static String nodePrefix ;
	static {
		root = "/" + Const.ROOT;
		nodePrefix = root + "/";
	}
	
	@Override
	public long generate() {
		return generate(Const.DEFAULT_TABLE);
	}

	@Override
	public long generate(String table) {
		Objects.requireNonNull(table);
		String nodePath = nodePrefix + table;
		try {
			return nextVersion(nodePath, table.getBytes());
		} catch (Exception e) {
			String msg = String.format("error generate id with tableName [%s]", table);
			throw new GuidGenerateException(msg, e);
		}
	}
	
	private int nextVersion(String path, byte[] data) throws Exception {
		Stat stat = null;
		try {
			stat = curator.setData().forPath(path, data);
		} catch (NoNodeException e) {
			try {
				curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
				stat = new Stat();
			} catch (NodeExistsException e1) {
				stat = curator.setData().forPath(path, data);
			} 
		}
		return stat.getVersion();
	}
}
