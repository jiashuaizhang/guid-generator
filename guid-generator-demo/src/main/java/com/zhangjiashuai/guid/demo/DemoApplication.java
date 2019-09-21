package com.zhangjiashuai.guid.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.zhangjiashuai.guid.annotation.EnableGuidGenerator;

@SpringBootApplication
@EnableGuidGenerator
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
