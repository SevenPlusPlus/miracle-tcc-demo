package com.miracle.module.tcc.demo.inventory.test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan({"com.miracle.common.transaction", "com.miracle.module.tcc.demo.inventory.test.server"})
public class TestApp {
	
    public static void main(final String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApp.class, args);
        context.getBean(TestClient.class).testShipment();
    }
}
