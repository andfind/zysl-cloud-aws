package com.zysl.cloud.aws.domain.bo;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * 路径解析出来的对象
 * @description
 * @author miaomingming
 **/
@Getter
@Setter
public class PathUriBO implements Serializable {
	
	private static final long serialVersionUID = -5088955709475471751L;
	
	//协议：s3，sharePoint、ftp
	private String scheme;
	//bucket或者IP
	private String host;
	//完整路径
	private String key;
	private String versionId;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"PathUriBO\":{");
		if (scheme != null) {
			sb.append("scheme='").append(scheme).append('\'');
		}
		if (host != null) {
			sb.append(", host='").append(host).append('\'');
		}
		if (key != null) {
			sb.append(", key='").append(key).append('\'');
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		sb.append("}}");
		return sb.toString();
	}
}
