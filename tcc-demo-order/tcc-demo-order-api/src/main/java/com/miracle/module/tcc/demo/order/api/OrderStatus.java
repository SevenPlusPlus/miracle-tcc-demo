package com.miracle.module.tcc.demo.order.api;

public enum OrderStatus {

	NEW(0), PAYING(1), PAY_SUCCESS(2), PAY_FAIL(3);
	
	private int status;
	
	private OrderStatus(int status)
	{
		this.status = status;
	}
	
	public int getStatus()
	{
		return this.status;
	}
}

