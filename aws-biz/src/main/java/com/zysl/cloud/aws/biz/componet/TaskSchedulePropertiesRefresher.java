//package com.zysl.cloud.aws.biz.componet;
//
//import com.ctrip.framework.apollo.model.ConfigChangeEvent;
//import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
//import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class TaskSchedulePropertiesRefresher implements ApplicationContextAware {
//	private ApplicationContext applicationContext;
//
//	@Autowired
//	private IS3FactoryService s3FactoryService;
//
//	@ApolloConfigChangeListener
//	public void onChange(ConfigChangeEvent changeEvent) {
//		refreshTaskScheduleProperties(changeEvent);
//	}
//
//	private void refreshTaskScheduleProperties(ConfigChangeEvent changeEvent) {
//		log.info("Refreshing TaskSchedule properties!");
//
//		// 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
//		this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
//
//		s3FactoryService.amazonS3ClientInit();
//		log.info("TaskSchedule properties refreshed!");
//	}
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
//	}
//}
