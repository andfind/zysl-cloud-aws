package com.zysl.cloud.aws.biz.service;

import java.util.List;

/**
 * 对象处理接口
 * @author miaomingming
 **/
public interface IKeyService<T> {
	/**
	 * 新增
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return T
	 **/
	T create(T t);
	
	/**
	 * 删除
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return T
	 **/
	void delete(T t);
	
	/**
	 * 复制
	 * @description
	 * @author miaomingming
	 * @param src
	 * @param dest
	 * @return void
	 **/
	boolean copy(T src,T dest);
	
	/**
	 * 查询基础信息
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return T
	 **/
	T getBaseInfo(T t);
	
	/**
	 * 查询详细信息
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return T
	 **/
	T getInfoAndBody(T t);
	
	/**
	 * 查询版本列表信息
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return java.util.List<T>
	 **/
	List<T> getVersions(T t);
	
	/**
	 * 查询对象列表信息
	 * @description
	 * @author miaomingming
	 * @param t
	 * @return java.util.List<T>
	 **/
	List<T> list(T t);
}
