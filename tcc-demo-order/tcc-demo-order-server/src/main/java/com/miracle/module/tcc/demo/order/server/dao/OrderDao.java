package com.miracle.module.tcc.demo.order.server.dao;

import com.miracle.module.tcc.demo.order.domain.Order;

import java.util.Map;


public interface OrderDao {
	int createOrder(Order newOrder);
	
	int updateOrderStatus(Map<String, Object> params);
	
	Order getOrderById(Long orderId);
}
