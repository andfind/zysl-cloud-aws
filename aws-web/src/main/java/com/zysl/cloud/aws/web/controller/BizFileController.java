package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.req.BizFileShareRequest;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiUploadRequest;
import com.zysl.cloud.aws.api.req.SysFileRenameRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.aws.api.srv.BizFileSrv;
import com.zysl.cloud.aws.api.srv.SysFileSrv;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysDirManager;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.ShareFileRequestV;
import com.zysl.cloud.aws.web.validator.SysDirListRequestV;
import com.zysl.cloud.aws.web.validator.SysDirRequestV;
import com.zysl.cloud.aws.web.validator.SysFileMultiCompleteRequestV;
import com.zysl.cloud.aws.web.validator.SysFileMultiRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRenameRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.SpringContextUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import com.zysl.cloud.utils.validator.BeanValidator;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Slf4j
@RestController
public class BizFileController extends BaseController implements BizFileSrv {
	
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private ISysFileManager sysFileManager;
	@Autowired
	private ISysDirManager sysDirManager;
	@Autowired
	private IS3FileService s3FileService;
	@Autowired
	private BizConfig bizConfig;
	
	
	@Override
	public BaseResponse<Boolean> isExist(@RequestBody SysFileExistRequest request){
		return ServiceProvider.call(request, SysFileRequestV.class, Boolean.class, req -> {
			
			//增加默认path
			if(CollectionUtils.isEmpty(request.getPaths())){
				List<SysDirRequest> paths = new ArrayList<>();
				List<String> buckets = webConfig.getAnnouncementBuckets();
				if(CollectionUtils.isEmpty(buckets)){
					for(String key:buckets){
						SysDirRequest dirRequest = new SysDirRequest();
						setFileSystemDefault(dirRequest);
						dirRequest.setPath(key + ":/");
						paths.add(dirRequest);
					}
				}
			}
			
			if(!CollectionUtils.isEmpty(request.getPaths())){
				for(SysDirRequest path:request.getPaths()){
					SysFileRequest fileRequest = BeanCopyUtil.copy(path,SysFileRequest.class);
					fileRequest.setFileName(request.getFileName());
					fileRequest.setVersionId(request.getVersionId());
					if(sysFileManager.info(fileRequest) != null){
						return Boolean.TRUE;
					}
				}
			}
			
			return Boolean.FALSE;
		});
	}
	
	@Override
	public	BaseResponse<SysFileDTO> shareFile(@RequestBody BizFileShareRequest request){
		return ServiceProvider.call(request, SysFileRequestV.class, SysFileDTO.class, req -> {
			
			//复制源文件信息
			S3ObjectBO src = ObjectFormatUtils.createS3ObjectBO(request);
			
			//获取文件内容
			S3ObjectBO s3ObjectBO = (S3ObjectBO)s3FileService.getInfoAndBody(src);
			
			S3ObjectBO target = ObjectFormatUtils.createS3ObjectBO(request);
			target.setBucketName(bizConfig.shareFileBucket);
			target.setBodys(s3ObjectBO.getBodys());
			
			//获取标签信息
			List<TagBO> tagList = Lists.newArrayList();
			if(!StringUtils.isEmpty(req.getMaxDownloadAmout()+"")){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode());
				tag.setValue(String.valueOf(req.getMaxDownloadAmout()));
				tagList.add(tag);
			}
			if(!StringUtils.isEmpty(req.getMaxHours()+"")){
				TagBO tag = new TagBO();
				tag.setKey(S3TagKeyEnum.TAG_VALIDITY.getCode());
				String date = DateUtils.getDateToString(DateUtils.addDateHour(new Date(), req.getMaxHours()));
				tag.setValue(date);
				tagList.add(tag);
			}
			target.setTagList(tagList);
			//重新上传文件
			S3ObjectBO rst = (S3ObjectBO)s3FileService.create(target);
			
			
			SysFileRequest fileRequest = new SysFileRequest();
			fileRequest.setPath(rst.getBucketName() + ":/" + rst.getPath());
			fileRequest.setFileName(rst.getFileName());
			fileRequest.setVersionId(rst.getVersionId());
			if(StringUtils.isBlank(fileRequest.getType())){
				fileRequest.setType(webConfig.getFileSystemTypeDefault());
			}
			if(StringUtils.isBlank(fileRequest.getServerNo())){
				fileRequest.setServerNo(webConfig.getFileSystemServerNoDefault());
			}
			return sysFileManager.info(fileRequest);
		});
	}
	
	@Override
	@ResponseBody
	public BaseResponse<String> shareFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downRequest){
		log.error("shareFileDownload.param:{}:", JSON.toJSONString(downRequest));
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		try{
			validator(baseResponse,downRequest, SysFileRequestV.class);
			
			S3ObjectBO src = ObjectFormatUtils.createS3ObjectBO(downRequest);
			S3ObjectBO s3ObjectBO = (S3ObjectBO) s3FileService.getInfoAndBody(src);
			List<TagBO> tagBOList = checkAndSetShareDownload(s3ObjectBO);
			//在重新设置文件标签
			s3ObjectBO.setTagList(tagBOList);
			s3FileService.modify(s3ObjectBO);
			
			//执行下载
			HttpUtils.downloadFileByte(request,response,downRequest.getFileName(),s3ObjectBO.getBodys());
			return null;
		}catch (AppLogicException e){
			log.error("shareFileDownload.AppLogicException:{}:", JSON.toJSONString(downRequest),e);
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
		}catch (Exception e){
			log.error("shareFileDownload.Exception:{}:", JSON.toJSONString(downRequest),e);
			baseResponse.setMsg(e.getMessage());
		}
		return baseResponse;
	}
	
	@Override
	@ResponseBody
	public BaseResponse<String> vedioFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downRequest){
		log.error("vedioFileDownload.param:{}:", JSON.toJSONString(downRequest));
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		try{
			validator(baseResponse,downRequest, SysFileRequestV.class);
			S3ObjectBO src = ObjectFormatUtils.createS3ObjectBO(downRequest);
			S3ObjectBO s3ObjectBO = (S3ObjectBO) s3FileService.getInfoAndBody(src);
			
			response.reset();
			//设置头部类型
			response.setContentType("video/mp4;charset=UTF-8");
			//执行下载
			ServletOutputStream out = null;
			try {
				out = response.getOutputStream();
				out.write(s3ObjectBO.getBodys());
				out.flush();
			} catch (IOException e) {
				log.error("--文件流转换异常：--", e);
			}finally {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}
			return null;
		}catch (AppLogicException e){
			log.error("vedioFileDownload.AppLogicException:{}:", JSON.toJSONString(downRequest),e);
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
		}catch (Exception e){
			log.error("vedioFileDownload.Exception:{}:", JSON.toJSONString(downRequest),e);
			baseResponse.setMsg(e.getMessage());
		}
		return baseResponse;
	}
	
	/**
	 * 检查是否分享文件，并更新分享次数等
	 * @description
	 * @author miaomingming
	 * @date 10:00 2020/4/9
	 * @param s3ObjectBO
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	private List<TagBO> checkAndSetShareDownload(S3ObjectBO s3ObjectBO){
		if(s3ObjectBO == null || CollectionUtils.isEmpty(s3ObjectBO.getTagList())){
			throw new AppLogicException(ErrCodeEnum.FILE_IS_NOT_SHARED.getCode());
		}
		boolean isExistShareTag = Boolean.FALSE;
		List<TagBO> tagList = s3ObjectBO.getTagList();
		List<TagBO> newTagList = Lists.newArrayList();
		for (TagBO tag : tagList) {
			//判断下载次数
			if(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode().equals(tag.getKey()) &&
				Integer.parseInt(tag.getValue()) < 1){
				//下载次数已下完
				log.info("--shareDownloadFile.times.is.max:{}--",s3ObjectBO);
				throw new AppLogicException(ErrCodeEnum.FILE_SHARED_DOWNLOAD_MAX_TIMES.getCode());
			}
			//判断是否在有效期内
			if(S3TagKeyEnum.TAG_VALIDITY.getCode().equals(tag.getKey()) &&
				DateUtils.doCompareDate(new Date(), DateUtils.getStringToDate(tag.getValue())) > 0){
				//已过有效期
				log.info("--shareDownloadFile.times.is.timeout:{}--",s3ObjectBO);
				throw new AppLogicException(ErrCodeEnum.FILE_SHARED_DOWNLOAD_TIMEOUT.getCode());
			}
			if(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode().equals(tag.getKey())){
				isExistShareTag = Boolean.TRUE;
				int amout = Integer.parseInt(tag.getValue()) - 1;
				tag.setValue(String.valueOf(amout));
				newTagList.add(tag);
			}else{
				newTagList.add(tag);
			}
		}
		if(!isExistShareTag){
			throw new AppLogicException(ErrCodeEnum.FILE_IS_NOT_SHARED.getCode());
		}
		return newTagList;
	}
	
	
	private void setFileSystemDefault(SysDirRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
}
