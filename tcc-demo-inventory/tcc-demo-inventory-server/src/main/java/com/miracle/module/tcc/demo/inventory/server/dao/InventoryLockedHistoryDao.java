package com.miracle.module.tcc.demo.inventory.server.dao;


import com.miracle.module.tcc.demo.inventory.domain.InventoryLockedHistory;

public interface InventoryLockedHistoryDao {
	int createInventoryLockedHistory(InventoryLockedHistory newHistory);
	
	InventoryLockedHistory getInventoryLockedHistoryById(long transactionId);
	
	int delInventoryLockedHistoryById(long transactionId);
}
