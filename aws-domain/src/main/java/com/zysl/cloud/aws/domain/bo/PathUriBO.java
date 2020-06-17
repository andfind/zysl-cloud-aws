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
	
	
	private String scheme;
	private String bucket;
	private String key;
	private String versionId;
	private String serverNo;
	
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"PathUriBO\":{");
		if (scheme != null) {
			sb.append("scheme='").append(scheme).append('\'');
		}
		if (bucket != null) {
			sb.append(", bucket='").append(bucket).append('\'');
		}
		if (key != null) {
			sb.append(", key='").append(key).append('\'');
		}
		if (versionId != null) {
			sb.append(", versionId='").append(versionId).append('\'');
		}
		if (serverNo != null) {
			sb.append(", serverNo='").append(serverNo).append('\'');
		}
		sb.append("}}");
		return sb.toString();
	}
}
