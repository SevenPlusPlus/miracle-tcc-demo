package com.miracle.module.tcc.demo.order.test.server;

import com.miracle.module.rpc.annotation.RpcConsumer;
import com.miracle.module.tcc.demo.order.api.OrderService;
import com.miracle.module.tcc.demo.order.domain.Order;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TestClient {
	
	@RpcConsumer(version="1.0.0", timeout=800000)
	private OrderService orderService;
	
	public void makeOrder()
	{
		Order newOrder = orderService.makeOrder(100001, 10001, 5, 200);
		log.info("Echo from server: " + orderService.makePayment(newOrder));
	}
}
