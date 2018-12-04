package com.miracle.module.tcc.demo.inventory.api;


import com.miracle.common.transaction.annotation.TccTransactional;

public interface InventoryService {
	@TccTransactional
	boolean decreaseInventory(Integer productId, Integer num, String mockMethod, String mockPos, String mockResult);
}
