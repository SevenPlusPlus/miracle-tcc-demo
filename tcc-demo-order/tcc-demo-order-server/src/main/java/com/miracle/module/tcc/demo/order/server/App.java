package com.miracle.module.tcc.demo.order.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@ComponentScan({"com.miracle.common.transaction", "com.miracle.module.tcc.demo"})
@MapperScan(basePackages = {"com.miracle.module.tcc.demo.order.server.dao"}
, sqlSessionFactoryRef = "sqlSessionFactory_order"
)
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class App {
	// CHECKSTYLE:OFF
    public static void main(final String[] args) {
        SpringApplication.run(App.class, args);
    }
}
