package com.miracle.module.tcc.demo.account.server.dao;

import com.miracle.module.tcc.demo.account.domain.Account;

import java.util.Map;


public interface AccountDao {
	int createAccount(Account newAccount);
	
	Account getAccountById(Integer userId);
	
	int decAccountBalance(Map<String, Object> params);
	
	int incAccountBalance(Map<String, Object> params);
}
