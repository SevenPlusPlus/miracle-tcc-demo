package com.miracle.module.tcc.demo.inventory.test.server;

import com.miracle.module.rpc.annotation.RpcConsumer;
import com.miracle.module.tcc.demo.inventory.api.InventoryService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TestClient {
	
	@RpcConsumer(version="1.0.0")
	private InventoryService inventoryService;
	
	public void testShipment()
	{
		log.info("Echo from server: " + inventoryService.decreaseInventory(10001, 10, "BIZ", "ANY", "OK"));
	}
}
