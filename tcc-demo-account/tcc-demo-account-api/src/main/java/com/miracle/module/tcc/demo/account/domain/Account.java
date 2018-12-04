package com.miracle.module.tcc.demo.account.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account implements Serializable{

	private static final long serialVersionUID = 8559901603124753137L;
	
	private Integer userId;
	
	private Integer balance;
	
	private Timestamp createTime;
	
	private Timestamp updateTime;
}
