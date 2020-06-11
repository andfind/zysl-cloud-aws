package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;

/**
 * 文件完整信息
 * @description
 * @author miaomingming
 **/
public class FileDetailBO extends BaseFileBO implements Serializable {
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"FileDetailBO\":{");
		sb.append("},\"super-FileDetailBO\":")
			.append(super.toString()).append("}");
		return sb.toString();
	}
}
