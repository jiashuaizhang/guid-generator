package com.zhangjiashuai.guid.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.zhangjiashuai.guid.generator.GuidGenerator;

@RestController
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Autowired
	private GuidGenerator generator;
	
	@RequestMapping("/generate")
	public Long generate() {
		return generator.generate();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
