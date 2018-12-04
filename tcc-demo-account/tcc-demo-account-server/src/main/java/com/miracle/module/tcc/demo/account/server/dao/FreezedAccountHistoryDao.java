package com.miracle.module.tcc.demo.account.server.dao;


import com.miracle.module.tcc.demo.account.domain.FreezedAccountHistory;

public interface FreezedAccountHistoryDao {
	int createFreezedHistory(FreezedAccountHistory newHistory);
	
	FreezedAccountHistory getAccountFreezedHistoryById(long transactionId);
	
	int delFreezedHistoryById(long transactionId);
}
