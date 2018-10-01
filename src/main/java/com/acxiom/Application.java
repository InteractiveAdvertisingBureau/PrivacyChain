package com.acxiom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(scanBasePackages = "com.acxiom.service")
@EnableAutoConfiguration
@ComponentScan
@Configuration
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
