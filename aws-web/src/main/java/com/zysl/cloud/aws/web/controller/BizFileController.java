package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.BizFileOfficeToPdfRequest;
import com.zysl.cloud.aws.api.req.BizFileShareRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.srv.BizFileSrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.biz.service.IPPTService;
import com.zysl.cloud.aws.biz.service.IWordService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.utils.ReqDefaultUtils;
import com.zysl.cloud.aws.web.validator.SysFileExistRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BizFileController extends BaseController implements BizFileSrv {
	
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private ISysFileManager sysFileManager;
	@Autowired
	private IS3FileService s3FileService;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private ReqDefaultUtils reqDefaultUtils;
	@Autowired
	private IWordService wordService;
	@Autowired
	private IPDFService pdfService;
	@Autowired
	private IPPTService pptService;
	
	
	@Override
	public BaseResponse<Boolean> isExist(@RequestBody SysFileExistRequest request){
		return ServiceProvider.call(request, SysFileExistRequestV.class, Boolean.class, req -> {
			
			List<SysDirRequest> paths = new ArrayList<>();
			//增加默认path
			if(CollectionUtils.isEmpty(request.getPaths())){
				List<String> buckets = webConfig.getAnnouncementBuckets();
				if(!CollectionUtils.isEmpty(buckets)){
					for(String key:buckets){
						SysDirRequest dirRequest = new SysDirRequest();
						dirRequest.setPath(key + ":/");
						paths.add(dirRequest);
					}
				}
			}else{
				paths = request.getPaths();
			}
			
			if(!CollectionUtils.isEmpty(paths)){
				for(SysDirRequest path:paths){
					if(StringUtils.isEmpty(path.getPath())){
						continue;
					}
					SysFileRequest fileRequest = BeanCopyUtil.copy(path,SysFileRequest.class);
					reqDefaultUtils.setFileSystemDefault(fileRequest);
					fileRequest.setFileName(request.getFileName());
					fileRequest.setVersionId(request.getVersionId());
					if(sysFileManager.info(fileRequest) != null){
						return Boolean.TRUE;
					}
				}
			}
			
			
			return Boolean.FALSE;
		},"isExist");
	}
	
	@Override
	public	BaseResponse<SysFileDTO> shareFile(@RequestBody BizFileShareRequest request){
		return ServiceProvider.call(request, SysFileRequestV.class, SysFileDTO.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			//TODO--调用复制接口，分享后key可以指定名称，不指定则返回UUID作为名称
			//复制源文件信息
			S3ObjectBO src = ObjectFormatUtils.createS3ObjectBO(request);
			
			//获取文件内容
			S3ObjectBO s3ObjectBO = (S3ObjectBO)s3FileService.getInfoAndBody(src);
			
			S3ObjectBO target = ObjectFormatUtils.createS3ObjectBO(request);
			target.setBucketName(bizConfig.shareFileBucket);
			target.setBodys(s3ObjectBO.getBodys());
			
			//生成标签信息
			target.setTagList(createTagList(req));
			//重新上传文件
			S3ObjectBO rst = (S3ObjectBO)s3FileService.create(target);
			
			SysFileRequest fileRequest = new SysFileRequest();
			fileRequest.setPath(rst.getBucketName() + ":/" + rst.getPath());
			fileRequest.setFileName(rst.getFileName());
			fileRequest.setVersionId(rst.getVersionId());
			reqDefaultUtils.setFileSystemDefault(fileRequest);
			
			return sysFileManager.info(fileRequest);
		},"shareFile");
	}
	
	/**
	 * 生成标签信息
	 * @description
	 * @author miaomingming
	 * @param req
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	private List<TagBO> createTagList(BizFileShareRequest req){
		List<TagBO> tagList = Lists.newArrayList();
		if(req.getMaxDownloadAmout() != null){
			TagBO tag = new TagBO();
			tag.setKey(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode());
			tag.setValue(String.valueOf(req.getMaxDownloadAmout()));
			tagList.add(tag);
		}
		if(req.getMaxHours() != null){
			TagBO tag = new TagBO();
			tag.setKey(S3TagKeyEnum.TAG_VALIDITY.getCode());
			String date = DateUtils.getDateToString(DateUtils.addDateHour(new Date(), req.getMaxHours()));
			tag.setValue(date);
			tagList.add(tag);
		}
		return tagList;
	}
	
	@Override
	@ResponseBody
	public BaseResponse<String> shareFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downRequest){
		log.error("shareFileDownload.param:{}:", JSON.toJSONString(downRequest));
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		try{
			if(!validator(baseResponse,downRequest, SysFileRequestV.class)){
				return baseResponse;
			}
			
			log.info("shareFileDownload {} [ES_LOG_START]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
			
			S3ObjectBO src = ObjectFormatUtils.createS3ObjectBO(downRequest);
			S3ObjectBO s3ObjectBO = (S3ObjectBO) s3FileService.getInfoAndBody(src);
			List<TagBO> tagBOList = checkAndSetShareDownload(s3ObjectBO);
			//在重新设置文件标签
			s3ObjectBO.setTagList(tagBOList);
			s3FileService.modify(s3ObjectBO);
			
			//执行下载
			HttpUtils.downloadFileByte(request,response,downRequest.getFileName(),s3ObjectBO.getBodys());
			log.info("shareFileDownload {} [ES_LOG_SUCCESS]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
			return null;
		}catch (AppLogicException e){
			log.error("shareFileDownload.AppLogicException:{}:", JSON.toJSONString(downRequest),e);
			log.error("shareFileDownload {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
		}catch (Exception e){
			log.error("shareFileDownload.Exception:{}:", JSON.toJSONString(downRequest),e);
			log.error("shareFileDownload {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
		}
		return baseResponse;
	}
	
	@Override
	@ResponseBody
	public BaseResponse<String> videoFileDownload(HttpServletRequest request,HttpServletResponse response, SysFileDownloadRequest downRequest){
		log.error("vedioFileDownload.param:{}:", JSON.toJSONString(downRequest));
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		try{
			if(!validator(baseResponse,downRequest, SysFileRequestV.class)){
				return baseResponse;
			}
			log.info("videoFileDownload {} [ES_LOG_START]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
			
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
			log.info("videoFileDownload {} [ES_LOG_SUCCESS]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
			return null;
		}catch (AppLogicException e){
			log.error("vedioFileDownload.AppLogicException:{}:", JSON.toJSONString(downRequest),e);
			log.error("videoFileDownload {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
		}catch (Exception e){
			log.error("vedioFileDownload.Exception:{}:", JSON.toJSONString(downRequest),e);
			log.error("videoFileDownload {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
		}
		return baseResponse;
	}
	
	@Override
	public BaseResponse<SysFileDTO> officeToPdf(@RequestBody BizFileOfficeToPdfRequest request){
		return ServiceProvider.call(request, SysFileRequestV.class, SysFileDTO.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			//step 0.校验
			String fileName = request.getFileName().toLowerCase();
			if(!(fileName.endsWith("doc") || fileName.endsWith("docx")
				|| fileName.endsWith("ppt") || fileName.endsWith("pptx"))){
				throw new AppLogicException(ErrCodeEnum.FILE_TO_PDF_TYPE_LIMIT.getCode());
			}
			//step 1.数据读取
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			S3ObjectBO rst = (S3ObjectBO)s3FileService.getInfoAndBody(s3ObjectBO);
			byte[] bodys = rst.getBodys();
			if(bodys == null || bodys.length == 0){
				log.warn("officeToPdf.noSuchKey:{}",request);
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			
			//step 2.转换
			if(fileName.endsWith("doc") || fileName.endsWith("docx")){
				bodys = wordService.changeWordToPDF(bodys);
			}else if(fileName.endsWith("ppt") || fileName.endsWith("pptx")){
				bodys = pptService.changePPTToPDF(bodys);
			}
			if(bodys == null || bodys.length == 0){
				log.warn("officeToPdf.toPdf.bodys.is.null:{}",request);
				throw new AppLogicException("step2",ErrCodeEnum.FILE_TO_PDF_BODY_NULL.getCode());
			}
			//step 3.水印
			if(!StringUtils.isBlank(request.getTextMark())){
				bodys = pdfService.addPdfTextMark(bodys,request.getTextMark());
				if(bodys == null || bodys.length == 0){
					log.warn("officeToPdf.addMark.bodys.is.null:{}",request);
					throw new AppLogicException("step3",ErrCodeEnum.FILE_TO_PDF_BODY_NULL.getCode());
				}
			}
			
			//step 4.加密
			if(!StringUtils.isBlank(request.getUserPwd()) && !StringUtils.isBlank(request.getOwnerPwd())){
				bodys = pdfService.addPwd(bodys,request.getUserPwd(),request.getOwnerPwd());
				if(bodys == null || bodys.length == 0){
					log.warn("officeToPdf.addPwd.bodys.is.null:{}",request);
					throw new AppLogicException("step4",ErrCodeEnum.FILE_TO_PDF_BODY_NULL.getCode());
				}
			}
			
			//step 5.上传新文件
			//修改bucket及文件名
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			if(fileRequest.getPath().indexOf(BizConstants.DISK_SEPARATOR) > -1){
				String path = request.getPath();
				path = bizConfig.getPdfDefaultRootPath() + path.substring(path.indexOf(":")+1);
				fileRequest.setPath(path);
			}
			
			if(fileName.indexOf(BizConstants.DISK_SEPARATOR) > -1){
				fileName = fileName.substring(0,fileName.lastIndexOf("."));
				fileName += StringUtils.join("_",System.currentTimeMillis(),".pdf");
				fileRequest.setFileName(fileName);
			}
			
			sysFileManager.upload(fileRequest,bodys,Boolean.TRUE);
			
			
			//step 6.查询并返回
			return sysFileManager.info(fileRequest);
		},"officeToPdf");
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
				DateUtils.doCompareDate(new Date(), DateUtils.createDate(tag.getValue())) > 0){
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
	
	
	
	
}
