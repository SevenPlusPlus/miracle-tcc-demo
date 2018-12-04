package com.miracle.module.tcc.demo.inventory.server.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.miracle.common.transaction.annotation.TccTransactional;
import com.miracle.common.transaction.api.TccTransactionContextLocal;
import com.miracle.module.rpc.annotation.RpcService;
import com.miracle.module.tcc.demo.inventory.api.InventoryService;
import com.miracle.module.tcc.demo.inventory.domain.InventoryLockedHistory;
import com.miracle.module.tcc.demo.inventory.server.dao.InventoryDao;
import com.miracle.module.tcc.demo.inventory.server.dao.InventoryLockedHistoryDao;
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
@RpcService(interfaceName="com.miracle.module.tcc.demo.inventory.api.InventoryService", version="1.0.0")
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryDao inventoryDao;
	
	@Autowired
	private InventoryLockedHistoryDao lockedHistoryDao;
	
	
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
	@TccTransactional(confirmMethod = "confirmDecreaseInventory", cancelMethod = "cancelDecreaseInventory")
	@Transactional(transactionManager="transactionManager_inventory", propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT, timeout=8000, rollbackFor=Exception.class)
	public boolean decreaseInventory(Integer productId, Integer num, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("BIZ", "BEFORE", mockMethod, mockPos, mockResult);
		Map<String, Object> params = Maps.newHashMap();
		params.put("productId", productId);
		params.put("delta", num);
		params.put("updateTime", new Timestamp(System.currentTimeMillis()));
		if(inventoryDao.decInventoryAvailableNum(params) > 0)
		{
			executeMockCondition("BIZ", "IN", mockMethod, mockPos, mockResult);
			Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
			InventoryLockedHistory newHistory = new InventoryLockedHistory();
			newHistory.setTransactionId(transactionId);
			newHistory.setProductId(productId);
			newHistory.setCreateTime(new Timestamp(System.currentTimeMillis()));
			newHistory.setLockedNum(num);
			
			if(lockedHistoryDao.createInventoryLockedHistory(newHistory) <= 0)
			{
				log.error("Failed to create inventory locked history, productId=" + productId +
						"transactionId=" + transactionId);
				throw new RuntimeException("Failed to create account freezed history, productId=" + productId +
						"transactionId=" + transactionId);
			}
		}
		else {
			log.error("No enough available inventory now for productId=" + productId);
			throw new RuntimeException("No enough available inventory now for productId=" + productId);
		}
		
		log.info("Try decrease inventory productId: {} successfully.", productId);
		executeMockCondition("BIZ", "AFTER", mockMethod, mockPos, mockResult);
		return true;
	}

	public void confirmDecreaseInventory(Integer productId, Integer num, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("CONFIRM", "BEFORE", mockMethod, mockPos, mockResult);
		Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
		lockedHistoryDao.delInventoryLockedHistoryById(transactionId);
		executeMockCondition("CONFIRM", "IN", mockMethod, mockPos, mockResult);
		log.info("Confirm decrease inventory for productId: " + productId);
		executeMockCondition("CONFIRM", "AFTER", mockMethod, mockPos, mockResult);
	}
	
	@Transactional(transactionManager="transactionManager_inventory", propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT, timeout=8000, rollbackFor=Exception.class)
	public void cancelDecreaseInventory(Integer productId, Integer num, String mockMethod, String mockPos, String mockResult) {
		executeMockCondition("CANCEL", "BEFORE", mockMethod, mockPos, mockResult);
		Long transactionId = TccTransactionContextLocal.getInstance().get().getTransactionId();
		if(lockedHistoryDao.delInventoryLockedHistoryById(transactionId) > 0)
		{
			executeMockCondition("CANCEL", "IN", mockMethod, mockPos, mockResult);
			Map<String, Object> params = Maps.newHashMap();
			params.put("productId", productId);
			params.put("delta", num);
			params.put("updateTime", new Timestamp(System.currentTimeMillis()));
			inventoryDao.incInventoryAvailableNum(params);
			log.info("Add locked inventory back to productId: " + productId);
		}
		
		log.info("Cancel decrease inventory for productId: " + productId);
		executeMockCondition("CANCEL", "AFTER", mockMethod, mockPos, mockResult);
	}
}
