package com.zysl.cloud.aws.biz.service.s3.impl;

import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("s3KeyService")
public class S3KeyServiceImpl implements IS3KeyService<S3KeyBO> {
	
	@Override
	public S3KeyBO create(S3KeyBO s3KeyBO) {
		return null;
	}
	
	@Override
	public void delete(S3KeyBO s3KeyBO) {
	
	}
	
	@Override
	public void rename(S3KeyBO src, S3KeyBO dest) {
	
	}
	
	@Override
	public S3KeyBO copy(S3KeyBO src, S3KeyBO dest) {
		return null;
	}
	
	@Override
	public void move(S3KeyBO src, S3KeyBO dest) {
	
	}
	
	@Override
	public void modify(S3KeyBO s3KeyBO) {
	
	}
	
	@Override
	public S3KeyBO getBaseInfo(S3KeyBO s3KeyBO) {
		return null;
	}
	
	@Override
	public S3KeyBO getDetailInfo(S3KeyBO s3KeyBO) {
		return null;
	}
	
	@Override
	public S3KeyBO getInfoAndBody(S3KeyBO s3KeyBO) {
		return null;
	}
	
	@Override
	public List<S3KeyBO> getVersions(S3KeyBO s3KeyBO) {
		return null;
	}
	
	@Override
	public S3KeyBO rename(S3KeyBO s3KeyBO) {
		return null;
	}
}
