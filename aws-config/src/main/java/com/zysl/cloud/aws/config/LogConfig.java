package com.zysl.cloud.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class LogConfig {
	
	@Value("${es.log.template}")
	private String logTemplate;
}
