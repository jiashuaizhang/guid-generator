### 简要描述
一个基于springboot设计的分布式ID生成器，提供snowflake、redis、zookeeper等实现方式

### 快速开始
*1. 添加依赖到maven*

- 项目没有发布到中央仓库，需手动编译
```
		<dependency>
		    <groupId>com.zhangjiashuai</groupId>
    		    <artifactId>guid-generator</artifactId>
    		    <version>1.0.0</version>
		</dependency>
```

*2. 添加启动注解到配置类*

	 `@EnableGuidGenerator`

*3. 注入使用*

```
	@Autowired
	private GuidGenerator guidGenerator;
	
	@Test
	public void testGuidGenerate() {
		for (int i = 0; i < 100; i++) {
			long id = guidGenerator.generate("table_0");
			System.out.printf("guid: [%d]", id);
		}
	}
```

### 配置:
*1. snowflake*

添加如下配置，或不做任何配置
```
guid:
  impl: snowflake
```
特别说明:snowflake方式下，若部署多个节点，需外部分别指定machineId和datacenterId，调用`long generate(long datacenterId, long machineId);`,避免可能的ID重复。

*2. redis*

添加如下配置
```
guid:
  impl: redis
```
此方式依赖bean: org.springframework.data.redis.core.RedisTemplate,需引入`spring-boot-starter-data-redis`,配置redis连接。

*3. zookeeper*

添加如下配置
```
guid:
  impl: zookeeper
  zookeeper:
    # zookeeper连接地址
    connectString: 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
    # zookeeper会话超时时间，默认3000
    sessionTimeOut: 3000
    # 历史节点清理任务线程池大小，为0则不清理，默认1
    cleanExecutorSize: 2
    # 每隔多少个id触发一次清理，默认100
    cleanUnit: 100
```
