package com.zysl.cloud.aws.rule.service;

import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.common.MyPage;
import java.util.List;

public interface ISysDirManager {
	
	/**
	 * 创建目录
	 * @description
	 * @author miaomingming
	 * @date 9:09 2020/4/7
	 * @param request
	 * @return void
	 **/
	void mkdir(SysDirRequest request);
	
	/**
	 * 目录下列表信息查询
	 * @description
	 * @author miaomingming
	 * @date 9:52 2020/4/7
	 * @param request
	 * @param myPage
	 * @return java.util.List<com.zysl.cloud.aws.api.dto.SysFileDTO>
	 **/
	List<SysFileDTO> list(SysDirListRequest request, MyPage myPage);
	
	/**
	 * 复制
	 * @description
	 * @author miaomingming
	 * @date 11:32 2020/4/7
	 * @param source
	 * @param target
	 * @param isOverWrite
	 * @return void
	 **/
	void copy(SysDirRequest source,SysDirRequest target,Boolean isOverWrite);
	
	/**
	 * 删除
	 * @description
	 * @author miaomingming
	 * @date 11:21 2020/4/7
	 * @param request
	 * @return void
	 **/
	void delete(SysDirRequest request);
	
	/**
	 * 移动
	 * @description
	 * @author miaomingming
	 * @date 11:22 2020/4/7
	 * @param source
	 * @param target
	 * @return void
	 **/
	void move(SysDirRequest source,SysDirRequest target);
	
}
