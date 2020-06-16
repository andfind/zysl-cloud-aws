package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.domain.bo.TagBO;
import java.util.List;

public interface IS3KeyService<T> extends IFileService<T> {
	
	/**
	 * 查询标签信息
	 * @description
	 * @author miaomingming
	 * @date 16:20 2020/6/16
	 * @param t
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	List<TagBO> getTagList(T t);
}
