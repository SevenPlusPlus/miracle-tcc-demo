package com.miracle.module.tcc.demo.account.test.server;

import com.miracle.module.rpc.annotation.RpcConsumer;
import com.miracle.module.tcc.demo.account.api.AccountService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TestClient {
	
	@RpcConsumer(version="1.0.0")
	private AccountService demo;
	
	public void testPayment()
	{
		log.info("Echo from server: " + demo.payment(100001, 100, "BIZ", "ANY", "OK"));
	}
}
