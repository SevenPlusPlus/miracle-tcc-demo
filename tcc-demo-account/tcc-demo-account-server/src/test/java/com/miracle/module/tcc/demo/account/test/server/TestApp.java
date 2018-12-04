package com.miracle.module.tcc.demo.account.test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
public class TestApp {
	
    public static void main(final String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApp.class, args);
        context.getBean(TestClient.class).testPayment();
    }
}
