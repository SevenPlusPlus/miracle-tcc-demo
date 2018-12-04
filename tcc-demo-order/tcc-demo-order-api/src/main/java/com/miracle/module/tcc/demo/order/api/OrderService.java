package com.miracle.module.tcc.demo.order.api;


import com.miracle.common.transaction.annotation.TccTransactional;
import com.miracle.module.tcc.demo.order.domain.Order;

public interface OrderService {
	
	Order makeOrder(Integer userId, Integer productId, Integer count, Integer amount);
	
	@TccTransactional
	Integer makePayment(Order order);
}
