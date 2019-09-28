### 简要描述
一个基于springboot设计的分布式ID生成器，提供snowflake、redis、zookeeper等实现方式

### 快速开始
**1. 添加依赖到maven**

- 项目没有发布到中央仓库，需手动编译
```
		<dependency>
		    <groupId>com.zhangjiashuai</groupId>
    		    <artifactId>guid-generator-spring-boot-starter</artifactId>
    		    <version>1.1.1</version>
		</dependency>
```

**2. 注入使用**

```java
	@Autowired
	private GuidGenerator guidGenerator;
	
	@Test
	public void testGuidGenerate() {
		for (int i = 0; i < 100; i++) {
			long id = guidGenerator.generate();
			//long id = guidGenerator.generate("tableName");
			System.out.printf("guid: [%d]", id);
		}
	}
```

### 配置:
**1. snowflake**

添加如下配置，或不做任何配置
```
guid:
  impl: snowflake
```
特别说明:snowflake方式下，若部署多个节点，只能保证单个节点生成的ID是有序的。而整个集群生成的ID无法保证有序，如果对有序性有执着要求，请使用zookeeper或redis方式。

##### 1.1.0版本更新:
1)支持通过zookeeper获取默认machineId和datacenterId,保证不重复

2)支持配置默认machineId和datacenterId，避免每次重启后machineId和datacenterId重新生成导致ID顺序混乱

3)如果什么也不配置，默认machineId和datacenterId将会在应用启动时随机生成，所以启动多个节点会有小概率的重复问题

- 注:当使用无参方法`guidGenerator.generate()`时，使用默认machineId和datacenterId;当使用`guidGenerator.generate("tableName")`时，使用默认machineId，datacenterId根据入参的哈希值计算.所以当部署节点数超过32，即machineId有重复时，`guidGenerator.generate("tableName")`方法将不可靠。

完整配置如下:
```
guid:
  impl: snowflake
  zookeeper:
    # zookeeper连接地址
    connectString: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
    # zookeeper会话超时时间，默认60000
    sessionTimeOut: 3000
  snowflake:
      # 是否开启zookeeper worker获取，默认false
      zkWorkerIdEnabled: true
      # /guid根节点下存储workId的父节点,默认 _workerId_. 当zookeeperEnabled为true时生效
      workerIdNode: _workerId_
      # 默认machineId，范围0~31.当zookeeperEnabled为false时生效
      machineId: 1
      # 默认datacenterId，范围0~31.当zookeeperEnabled为false时生效
      datacenterId: 31
```

#### 1.1.1版本更新
1)snowflake支持主从自动切换，并提供客户端监听主节点变换. 相同的一组machineId和datacenterId的节点，可以自动组成主从关系,只有主节点提供服务，从而保证ID的有序性和唯一性

- 服务端配置
```
		<dependency>
		    <groupId>com.zhangjiashuai</groupId>
    		    <artifactId>guid-generator-spring-boot-starter</artifactId>
    		    <version>1.1.1</version>
		</dependency>
```
```
guid:
  impl: snowflake
  zookeeper:
    connectString: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
    sessionTimeOut: 3000
  snowflake:
      machineId: 1
      datacenterId: 31
      # 是否开启leader选举,用于主从自动切换，默认false. 相同machineId和datacenterId的一组节点会组成主从关系，所以使用时需要配置machineId和datacenterId
      zkLeaderSelect: true
      # leader选举相关节点在zookeeper /guid根节点下的父节点
      leaderSelectorNode: _leaderSelector_
      # 对外提供服务的端口号
      port: ${server.port}
      # 是否开启zookeeper worker自动获取，当zkLeaderSelect开启时，请关闭zkWorkerIdEnabled
      zkWorkerIdEnabled: false
      # worker获取相关节点在zookeeper /guid根节点下的父节点
      workerIdNode: _workerId_
server:
  port: 8081
```
- 客户端配置
```
		<dependency>
		    <groupId>com.zhangjiashuai</groupId>
		    <artifactId>guid-generator-zookeeper-client-spring-boot-starter</artifactId>
		    <version>1.1.1</version>
		</dependency>
```
```
guid:
  zookeeper:
    connectString: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
    sessionTimeOut: 3000
  snowflake:
      machineId: 1
      datacenterId: 31
      # 是否开启leader选举,用于主从自动切换，默认false. 相同machineId和datacenterId的一组节点会组成主从关系，所以使用时需要配置machineId和datacenterId
      zkLeaderSelect: true
      # leader选举相关节点在zookeeper /guid根节点下的父节点
      leaderSelectorNode: _leaderSelector_
server:
  port: 8081
```
- 客户端使用

1. 客户端将snowflake主节点的ip和端口自动写入`import com.zhangjiashuai.guid.client.leader.LeaderAddress`类，通过调用`LeaderAddress.getIp()`和
`LeaderAddress.getPort()`即可获取主节点地址

2. 示例
```java
	@Test
	public void testLeaderSelect() {
		for (int i = 0; i < 100; i++) {
			String ip = LeaderAddress.getIp();
			int port = LeaderAddress.getPort();
			System.out.printf("leader ip:[%s],port:[%d]\n", ip, port);
			String url = "http://" + ip + ":" + port + "/generate";
			try {
				long guid = restTemplate.getForObject(url, Long.class);
				System.out.println("id : " + guid);
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
```

2)zookeeper生成器修改了实现方式，从顺序节点名称后缀改为了持久节点数据版本号

**2. redis**

添加如下配置
```
guid:
  impl: redis
```
此方式依赖bean: org.springframework.data.redis.core.RedisTemplate,需引入`spring-boot-starter-data-redis`,配置redis连接。

**3. zookeeper**

添加如下配置
```
guid:
  impl: zookeeper
  zookeeper:
    # zookeeper连接地址
    connectString: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
    # zookeeper会话超时时间，默认60000
    sessionTimeOut: 3000
```
### 性能对比
开发环境渣笔记本随便测了下，可以做参考
- i7 6500U, 64位win10
- zookeeper，redis均为本机部署
- 相同的代码，循环生成10000个ID

|生成方式|耗时(ms)
|:----    |:---|
|snowflake |467|
|redis     |4793  |
|zookeeper     |27536  |
