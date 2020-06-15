package com.zysl.cloud.aws.prop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3ServerProp implements Serializable {

	//服务器编号
	private String serverNo;

	//站点
	private String endpoint;

	//访问key
	private String accessKey;

	//安全key
	private String secretKey;

	//空间是否已满，满了只能做查询操作
	private Boolean noSpace;
	
	private Map<String,String> bucketMap = new HashMap();

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("{\"S3ServerProp\":{");
		sb.append("serverNo='").append(serverNo).append('\'');
		sb.append(", endpoint='").append(endpoint).append('\'');
		sb.append(", accessKey='").append(accessKey).append('\'');
		sb.append(", secretKey='").append(secretKey).append('\'');
		sb.append(", noSpace=").append(noSpace).append('\'');
		sb.append(", bucketMap.size=").append(bucketMap.isEmpty() ? 0 : bucketMap.values().size());
		sb.append("}}");
		return sb.toString();
	}
}
