### 简要描述
一个基于springboot设计的分布式ID生成器，提供snowflake、redis、zookeeper等实现方式

### 快速开始
**1. 添加依赖到maven**

- 项目没有发布到中央仓库，需手动编译
```
		<dependency>
		    <groupId>com.zhangjiashuai</groupId>
    		    <artifactId>guid-generator-spring-boot-starter</artifactId>
    		    <version>1.1.0</version>
		</dependency>
```

**2. 注入使用**

```
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
      zookeeperEnabled: true
      # /guid根节点下存储workId的父节点,默认 _workerId_. 当zookeeperEnabled为true时生效
      node: _workerId_
      # 默认machineId，范围0~31.当zookeeperEnabled为false时生效
      machineId: 1
      # 默认datacenterId，范围0~31.当zookeeperEnabled为false时生效
      datacenterId: 31
```
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
    # 历史节点清理任务线程池大小，为0则不清理，默认1
    cleanExecutorSize: 2
    # 每隔多少个id触发一次清理，默认100
    cleanUnit: 100
```
