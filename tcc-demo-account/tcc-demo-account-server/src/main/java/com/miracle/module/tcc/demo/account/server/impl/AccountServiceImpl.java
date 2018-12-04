package com.miracle.module.tcc.demo.account.server.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.miracle.common.transaction.annotation.TccTransactional;
import com.miracle.common.transaction.api.TccTransactionContextLocal;
import com.miracle.module.rpc.annotation.RpcService;
import com.miracle.module.tcc.demo.account.api.AccountService;
import com.miracle.module.tcc.demo.account.domain.FreezedAccountHistory;
import com.miracle.module.tcc.demo.account.server.dao.AccountDao;
import com.miracle.module.tcc.demo.account.server.dao.FreezedAccountHistoryDao;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;


@Slf4j
@Component
@RpcService(interfaceName="com.miracle.module.tcc.demo.account.api.AccountService", version="1.0.0")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private FreezedAccountHistoryDao freezedAccountHistoryDao;
	
	private boolean lastConfirmSuccess = true;
	
	private boolean lastCancelSucess = true;
	
	private void mockConditionResult(String mockResult)
	{
		if(mockResult.equalsIgnoreCase("TIMEOUT"))
		{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(mockResult.equalsIgnoreCase("EXCEPTION"))
		{
			throw new RuntimeException("mocked exception occured");
		}
	}
	
	private void executeMockCondition(String method, String pos, String mockMethod, String mockPos, String mockResult)
	{
		List<String> methodList = Splitter.on('&').splitToList(mockMethod);
		List<String> posList = Splitter.on('&').splitToList(mockPos);
		List<String> resultList = Splitter.on('&').splitToList(mockResult);
		
		String hitMethod = "MOCK_METHOD";
		String hitPos = "MOCK_POS";
		String hitResult = "MOCK_RESULT";
		
		int i = 0;
		for(String m : methodList)
		{
			if(m.equalsIgnoreCase(method))
			{
				hitMethod = m;
				hitPos = posList.get(i);
				hitResult = resultList.get(i);
				break;
			}
			i++;
		}
		
		if(method.equalsIgnoreCase(hitMethod) && pos.equalsIgnoreCase(hitPos))
		{
			if(hitMethod.equalsIgnoreCase("BIZ"))
			{
				mockConditionResult(hitResult);
			}
			else if(hitMethod.equalsIgnoreCase("CONFIRM"))
			{
				
				if(lastConfirmSuccess)
				{
					try{
						mockConditionResult(hitResult);
					}
					finally{
						if(hitResult.equalsIgnoreCase("OK"))
						{
							lastConfirmSuccess = true;
						}
						else
						{
							lastConfirmSuccess = false;
						}
					}
				}
				else
				{
					lastConfirmSuccess = true;
				}
				
			}
			else if(hitMethod.equalsIgnoreCase("CANCEL"))
			{
				if(lastCancelSucess)
				{
					try{
						mockConditionResult(hitResult);
					}
					finally{
						if(hitResult.equalsIgnoreCase("OK"))
						{
							lastCancelSucess = true;
						}
						else
						{
							lastCancelSucess = false;
						}
					}
				}
				else
				{
					lastCancelSucess = true;
				}
			}
		}
	}
	
	@Override
	@TccTransactional(confirmMethod = "confirmPayment", cancelMethod = "cancelPayment")
	@Transactional(transactionManager="transactionManager_account", propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT, timeout=8000, rollbackFor=Exception.class)
	public boolean payment(Integer userId, Integer amount, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("BIZ", "BEFORE", mockMethod, mockPos, mockResult);
		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", userId);
		params.put("delta", amount);
		params.put("updateTime", new Timestamp(System.currentTimeMillis()));
		if(accountDao.decAccountBalance(params) > 0)
		{
			executeMockCondition("BIZ", "IN", mockMethod, mockPos, mockResult);
			Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
			FreezedAccountHistory newHistory = new FreezedAccountHistory();
			newHistory.setTransactionId(transactionId);
			newHistory.setUserId(userId);
			newHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
			newHistory.setFreezed(amount);
			
			if(freezedAccountHistoryDao.createFreezedHistory(newHistory) <= 0)
			{
				log.error("Failed to create account freezed history, userId=" + userId +
						"transactionId=" + transactionId);
				throw new RuntimeException("Failed to create account freezed history, userId=" + userId +
						"transactionId=" + transactionId);
			}
		}
		else {
			log.error("No enough money available now for userId=" + userId);
			throw new RuntimeException("No enough money available now for userId=" + userId);
		}
		log.info("Try account payment for userId: {} successfully.", userId);
		executeMockCondition("BIZ", "AFTER", mockMethod, mockPos, mockResult);
		return true;
	}

	public void confirmPayment(Integer userId, Integer amount, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("CONFIRM", "BEFORE", mockMethod, mockPos, mockResult);
		Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
		freezedAccountHistoryDao.delFreezedHistoryById(transactionId);
		executeMockCondition("CONFIRM", "IN", mockMethod, mockPos, mockResult);
		
		log.info("Confirm account payment for userId: " + userId);
		executeMockCondition("CONFIRM", "AFTER", mockMethod, mockPos, mockResult);
	}
	
	@Transactional(transactionManager="transactionManager_account", propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT, timeout=8000, rollbackFor=Exception.class)
	public void cancelPayment(Integer userId, Integer amount, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("CANCEL", "BEFORE", mockMethod, mockPos, mockResult);
		Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
		if(freezedAccountHistoryDao.delFreezedHistoryById(transactionId) > 0)
		{
			executeMockCondition("CANCEL", "IN", mockMethod, mockPos, mockResult);
			Map<String, Object> params = Maps.newHashMap();
			params.put("userId", userId);
			params.put("delta", amount);
			params.put("updateTime", new Timestamp(System.currentTimeMillis()));
			accountDao.incAccountBalance(params);
			log.info("Add freezed money back to account: " + userId);
		}
		log.info("Cancel account payment for userId: " + userId);
		executeMockCondition("CANCEL", "AFTER", mockMethod, mockPos, mockResult);
	}
}
