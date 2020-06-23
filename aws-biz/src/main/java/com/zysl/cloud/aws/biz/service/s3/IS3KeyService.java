package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.common.MyPage;
import java.util.List;
import software.amazon.awssdk.services.s3.S3Client;

public interface IS3KeyService<T> {
	
	/**
	 * 新增文件
	 * @description
	 * @author miaomingming
	 * @date 11:10 2020/6/17
	 * @param s3Client
	 * @param t
	 * @return T
	 **/
	T create(S3Client s3Client,T t);
	
	/**
	 * 删除文件
	 * 必须指定key
	 * @description
	 * @author miaomingming
	 * @date 10:52 2020/6/17
	 * @param s3Client
	 * @param t
	 * @return void
	 **/
	void delete(S3Client s3Client,T t);
	
	/**
	 * 删除多个对象
	 * @description
	 * @author miaomingming
	 * @date 11:08 2020/6/17
	 * @param s3Client
	 * @param tList
	 * @return void
	 **/
	void deleteList(S3Client s3Client,String bucket,List<T> tList);
	
	/**
	 * 删除bucket下所有文件
	 * @description
	 * @author miaomingming
	 * @date 15:16 2020/6/23
	 * @param s3Client
	 * @param bucket
	 * @return void
	 **/
	void deleteAllKey(S3Client s3Client,String bucket);
	
	/**
	 * 复制文件
	 * @description
	 * @author miaomingming
	 * @date 13:10 2020/3/26
	 * @param s3Client
	 * @param src
	 * @param dest
	 * @return void
	 **/
	 void copy(S3Client s3Client,T src, T dest);
	
	/**
	 * 查询文件基础信息
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param s3Client
	 * @param t
	 * @return T
	 **/
	T getBaseInfo(S3Client s3Client,T t);
	
	/**
	 * 查询文件信息及内容
	 * @description
	 * @author miaomingming
	 * @date 11:52 2020/3/26
	 * @param s3Client
	 * @param t
	 * @return T
	 **/
	T getInfoAndBody(S3Client s3Client,T t);
	
	/**
	 * 查询版本列表信息
	 * @description
	 * @author miaomingming
	 * @date 13:35 2020/3/26
	 * @param s3Client
	 * @param t
	 * @return java.util.List<T>
	 **/
	List<T> getVersions(S3Client s3Client,T t, MyPage myPage);
	
	/**
	 * 查询文件列表信息
	 * @description
	 * @author miaomingming
	 * @date 11:12 2020/6/17
	 * @param s3Client
	 * @param t
	 * @return java.util.List<T>
	 **/
	List<T> list(S3Client s3Client,T t, MyPage myPage);
	
	/**
	 * 查询标签信息
	 * @description
	 * @author miaomingming
	 * @date 16:20 2020/6/16
	 * @param s3Client
	 * @param t
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> getTagList(S3Client s3Client,T t);
	
	/**
	 * 设置标签
	 * @description
	 * @author miaomingming
	 * @date 19:35 2020/6/22
	 * @param s3Client
	 * @param t
	 * @param tagBOList
	 * @return void
	 **/
	void setTagList(S3Client s3Client,T t,List<TagBO> tagBOList);
}
