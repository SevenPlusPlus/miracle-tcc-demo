package com.miracle.module.tcc.demo.inventory.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory implements Serializable{
	
	private static final long serialVersionUID = 4700079043491860993L;
	
	private Integer productId;
	
	private Integer availableNum;	
	
	private Timestamp createTime;
	
	private Timestamp updateTime;
}
