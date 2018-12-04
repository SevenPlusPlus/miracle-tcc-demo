package com.miracle.module.tcc.demo.inventory.server.dao;

import java.util.Map;

import com.miracle.module.tcc.demo.inventory.domain.Inventory;

public interface InventoryDao {
	int createInventory(Inventory newInventory);
	
	Inventory getInventoryById(Integer productId);
	
	int decInventoryAvailableNum(Map<String, Object> params);
	
	int incInventoryAvailableNum(Map<String, Object> params);
}
