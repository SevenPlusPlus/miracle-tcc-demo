package com.miracle.module.tcc.demo.inventory.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryLockedHistory implements Serializable{
	
	private static final long serialVersionUID = -3171685001794809551L;

	private Long transactionId;
	
	private Integer productId;
	
	private Integer lockedNum;	
	
	private Timestamp createTime;
}
