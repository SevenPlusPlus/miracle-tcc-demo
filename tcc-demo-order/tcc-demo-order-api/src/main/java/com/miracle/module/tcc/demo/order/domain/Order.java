package com.miracle.module.tcc.demo.order.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order implements Serializable{

	private static final long serialVersionUID = 2450196137277995102L;

	private Long orderId;
	
	private Timestamp createTime;
	
	private Integer status;
	
	private Integer productId;
	
	private Integer count;
	
	private Integer totalAmount;
	
	private Integer userId;
}
