package com.zhangjiashuai.guid.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.zhangjiashuai.guid.config.SnowFlakeWorkerId;
import com.zhangjiashuai.guid.generator.GuidGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {
	
	@Autowired
	private GuidGenerator guidGenerator;
	@Autowired(required = false)
	private SnowFlakeWorkerId snowFlakeZookeeperMachineId;

	@Test
	public void testGuidGenerate() {
		for (int i = 0; i < 100; i++) {
			long id = guidGenerator.generate();
			System.out.println("id : " + id);
		}
//		while (true) {
//			System.out.println("运行中.....");
//			try {
//				TimeUnit.SECONDS.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	@Test
	public void testConfig() {
		System.out.println(snowFlakeZookeeperMachineId);
	}

}
