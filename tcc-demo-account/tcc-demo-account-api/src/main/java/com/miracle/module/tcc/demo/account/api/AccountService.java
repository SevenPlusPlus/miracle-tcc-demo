package com.miracle.module.tcc.demo.account.api;


import com.miracle.common.transaction.annotation.TccTransactional;

public interface AccountService {
	
	@TccTransactional
	boolean payment(Integer userId, Integer amount, String mockMethod, String mockPos, String mockResult);
}
