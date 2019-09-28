package com.zhangjiashuai.guid.demo;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.zhangjiashuai.guid.client.leader.LeaderAddress;
import com.zhangjiashuai.guid.config.SnowFlakeConfig;
import com.zhangjiashuai.guid.generator.GuidGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {
	
	@Autowired
	private GuidGenerator guidGenerator;
	@Autowired(required = false)
	private SnowFlakeConfig snowFlakeZookeeperMachineId;
	@Autowired
	private RestTemplate restTemplate;

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

}
