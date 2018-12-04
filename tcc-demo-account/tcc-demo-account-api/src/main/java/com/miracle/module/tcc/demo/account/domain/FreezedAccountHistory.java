package com.miracle.module.tcc.demo.account.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreezedAccountHistory implements Serializable{

	private static final long serialVersionUID = 4854843246254376489L;

	private Long transactionId;
	
	private Integer userId;
	
	private Integer freezed;
	
	private Timestamp createTime;
}
