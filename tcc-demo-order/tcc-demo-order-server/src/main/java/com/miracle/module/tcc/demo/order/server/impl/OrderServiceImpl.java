package com.miracle.module.tcc.demo.order.server.impl;


import java.sql.Timestamp;
import java.util.Map;

import com.miracle.common.transaction.annotation.TccTransactional;
import com.miracle.common.transaction.utils.SnowflakeIdGenerator;
import com.miracle.module.rpc.annotation.RpcConsumer;
import com.miracle.module.rpc.annotation.RpcService;
import com.miracle.module.tcc.demo.account.api.AccountService;
import com.miracle.module.tcc.demo.inventory.api.InventoryService;
import com.miracle.module.tcc.demo.order.api.OrderService;
import com.miracle.module.tcc.demo.order.api.OrderStatus;
import com.miracle.module.tcc.demo.order.domain.Order;
import com.miracle.module.tcc.demo.order.server.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;


@Slf4j
@Component
@RpcService(interfaceName="com.miracle.module.tcc.demo.order.api.OrderService", version="1.0.0")
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private SnowflakeIdGenerator idGenerator;
	
	@RpcConsumer(version="1.0.0", timeout=8000)
	private AccountService accountService;
	
	@RpcConsumer(version="1.0.0", timeout=8000)
	private InventoryService inventoryService;
	
	@Override
	@TccTransactional(confirmMethod = "confirmPayment", cancelMethod = "cancelPayment")
	public Integer makePayment(Order order) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", order.getOrderId());
		params.put("status", OrderStatus.PAYING.getStatus());
		orderDao.updateOrderStatus(params);
		
		accountService.payment(order.getUserId(), order.getTotalAmount(), "BIZ", "ANY", "OK");
		
		inventoryService.decreaseInventory(order.getProductId(), order.getCount(), "BIZ", "ANY", "OK");
		
		log.info("Trying make payment");
		return 0;
	}

	public void confirmPayment(Order order)
	{
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", order.getOrderId());
		params.put("status", OrderStatus.PAY_SUCCESS.getStatus());
		orderDao.updateOrderStatus(params);
		log.info("Confirm payment");
	}
	
	public void cancelPayment(Order order)
	{
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderId", order.getOrderId());
		params.put("status", OrderStatus.PAY_FAIL.getStatus());
		orderDao.updateOrderStatus(params);
		log.info("Cancel payment");
	}

	@Override
	public Order makeOrder(Integer userId, Integer productId, Integer count,
			Integer amount) {
		Order newOrder = new Order();
		newOrder.setOrderId(idGenerator.nextId());
		newOrder.setCount(count);
		newOrder.setCreateTime(new Timestamp(System.currentTimeMillis()));
		newOrder.setProductId(productId);
		newOrder.setTotalAmount(amount);
		newOrder.setUserId(userId);
		newOrder.setStatus(OrderStatus.NEW.getStatus());
		
		orderDao.createOrder(newOrder);
		
		return newOrder;
	}

}
