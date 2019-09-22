package com.zhangjiashuai.guid.generator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import com.zhangjiashuai.guid.consts.Const;
import com.zhangjiashuai.guid.exception.GuidGenerateException;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 *     基于zookeeper实现的guid生成器
 * @author jiash
 */
@Slf4j
@Setter
public class ZooKeeperGuidGenerator implements GuidGenerator{
	
	private CuratorFramework curator;
	private ExecutorService cleanExecutor;
	private int cleanUnit;
	
	public ZooKeeperGuidGenerator() {
		
	}
	
	public ZooKeeperGuidGenerator(CuratorFramework curator, ExecutorService cleanExecutor, int cleanUnit) {
		this.curator = curator;
		this.cleanExecutor = cleanExecutor;
		this.cleanUnit = cleanUnit;
	}
	
	private static String root;
	private static String nodePrefix ;
	private static String tableNodeTemplate;
	static {
		root = "/" + Const.ROOT;
		nodePrefix = root + "/";
		tableNodeTemplate = nodePrefix + "%s/%s-";
	}
	
	@Override
	public long generate() {
		return generate(Const.DEFAULT_TABLE);
	}

	@Override
	public long generate(String table) {
		String idWithPrefix = generateWithTablePrefix(table);
		return extractId(idWithPrefix, table);
	}

	@Override
	public String generateWithTablePrefix(String table) {
		Objects.requireNonNull(table);
		String nodePath = formatNodePath(table);
		try {
			String idWithPrefix = curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(nodePath);
			cleanIdNodes(idWithPrefix, table);
			return idWithPrefix;
		} catch (Exception e) {
			String msg = String.format("error generate id with tableName [%s]", table);
			throw new GuidGenerateException(msg, e);
		}
	}
	
	private String formatNodePath(String table) {
		return String.format(tableNodeTemplate, table, table);
	}
	/**
	 * 从带前缀的id字符串截取数字id
	 * @param idWithPrefix
	 * @param table
	 * @return
	 */
	private long extractId(String idWithPrefix, String table) {
		int beginIndex = formatNodePath(table).length();
		String idStr = idWithPrefix.substring(beginIndex);
		return Long.parseLong(idStr);
	}
	
	/**
	 * 清理id节点，已生成的id不需要保存在zookeeper中
	 * @param idWithPrefix
	 * @param table
	 */
	private void cleanIdNodes(String idWithPrefix, String table) {
		if(cleanExecutor == null) {
			return;
		}
		cleanExecutor.submit(() -> {
			try {
				long id = extractId(idWithPrefix, table);
				if((id + 1) % cleanUnit == 0) {
					String parentPath = nodePrefix + table;
					List<String> childen = curator.getChildren().forPath(parentPath);
					for (String path : childen) {
						curator.delete().quietly().forPath(parentPath + path);
					}
				}
			} catch (Exception e) {
				log.error("error while clean id nodes", e);
			}
		});
	}
}
