package com.zysl.cloud.aws.biz.utils;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.domain.bo.TagBO;
import java.util.List;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

public class S3Utils {
	/**
	 * 从标签列表中读取指定key的value值
	 * @description
	 * @author miaomingming
	 * @date 9:58 2020/6/16
	 * @param tagList
	 * @param key
	 * @return java.lang.String
	 **/
	public static String getTagValue(List<TagBO> tagList, String key) {
		if(!CollectionUtils.isEmpty(tagList)){
			for (TagBO tag :tagList) {
				if(key.equals(tag.getKey())){
					return tag.getValue();
				}
			}
		}
		return null;
	}
	
	/**
	 * 生成Tagging
	 * @description
	 * @author miaomingming
	 * @date 9:59 2020/6/16
	 * @param tagList
	 * @return software.amazon.awssdk.services.s3.model.Tagging
	 **/
	public static Tagging creatTagging(List<TagBO> tagList){
		if(!CollectionUtils.isEmpty(tagList)){
			List<Tag> tagSet = Lists.newArrayList();
			tagList.forEach(obj -> {
				tagSet.add(Tag.builder().key(obj.getKey()).value(obj.getValue()).build());
			});
			//设置标签信息
			return CollectionUtils.isEmpty(tagSet) ? null : Tagging.builder().tagSet(tagSet).build();
		}
		return null;
	}
}
