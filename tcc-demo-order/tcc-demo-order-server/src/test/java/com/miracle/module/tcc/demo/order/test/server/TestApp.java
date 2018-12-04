package com.miracle.module.tcc.demo.order.test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan({"com.miracle.common.transaction", "com.miracle.module.tcc.demo.order.test.server"})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TestApp {
	
    public static void main(final String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApp.class, args);
        context.getBean(TestClient.class).makeOrder();
    }
}
