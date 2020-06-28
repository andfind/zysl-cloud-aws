package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.req.BizFileOfficeToPdfRequest;
import com.zysl.cloud.aws.api.req.BizFileShareRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.key.BizKeyOfficeToPdfRequest;
import com.zysl.cloud.aws.api.req.key.BizKeyShareRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.api.srv.BizFileSrv;
import com.zysl.cloud.aws.api.srv.BizKeySrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.biz.service.IPPTService;
import com.zysl.cloud.aws.biz.service.IWordService;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3KeyService;
import com.zysl.cloud.aws.config.BizConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.utils.DateUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.utils.ReqDefaultUtils;
import com.zysl.cloud.aws.web.validator.SysFileExistRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyOfficeRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyShareRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTML.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@RestController
public class BizKeyController extends BaseController implements BizKeySrv {
	
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private ISysKeyManager sysKeyManager;
	@Autowired
	private BizConfig bizConfig;
	@Autowired
	private IWordService wordService;
	@Autowired
	private IPDFService pdfService;
	@Autowired
	private IPPTService pptService;
	@Autowired
	private IS3KeyService s3KeyService;
	@Autowired
	private IS3FactoryService s3FactoryService;
	
	
	@Override
	public	BaseResponse<SysKeyDTO> shareFile(@RequestBody BizKeyShareRequest request){
		return ServiceProvider.call(request, SysKeyShareRequestV.class, SysKeyDTO.class, req -> {
			request.formatPathURI();
			//校验是否存在
			SysKeyDTO sysKeyDTO = sysKeyManager.info(request);
			if(sysKeyDTO == null){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}

			//获取文件内容
			byte[] bodys = sysKeyManager.getBody(request,null);
			//重新上传文件
			SysKeyUploadRequest uploadRequest = createSysKeyUploadRequest(request);
			sysKeyManager.upload(uploadRequest,bodys);
			
			//生成标签信息:查询本身标签+ 过期时间及下载次数
			S3Client s3 = s3FactoryService.getS3ClientByBucket(uploadRequest.getHost());
			S3KeyBO s3KeyBO = new S3KeyBO(uploadRequest.getHost(),uploadRequest.getKey());
			List<TagBO> tagBOList = s3KeyService.getTagList(s3,s3KeyBO);
			if(tagBOList == null || CollectionUtils.isEmpty(tagBOList)){
				tagBOList = new ArrayList<>();
			}
			tagBOList.addAll(createTagList(req));
			s3KeyService.setTagList(s3,s3KeyBO,tagBOList);
			
			return sysKeyManager.info(uploadRequest);
		},"shareFile");
	}
	
	
	private SysKeyUploadRequest createSysKeyUploadRequest(BizKeyShareRequest request){
		String targetKey = request.getKey();
		if(StringUtils.isNotEmpty(request.getShareName())){
			if(targetKey.indexOf(BizConstants.PATH_SEPARATOR) > -1){
				targetKey = targetKey.substring(0,targetKey.lastIndexOf(BizConstants.PATH_SEPARATOR)+1) + request.getShareName();
			}else {
				targetKey = request.getShareName();
			}
			
		}
		//此处已指定了s3的某个特殊bucket
		SysKeyUploadRequest uploadRequest = new SysKeyUploadRequest();
		uploadRequest.setScheme(request.getScheme());
		uploadRequest.setIsCover(Boolean.TRUE);
		uploadRequest.setHost(bizConfig.shareFileBucket);
		uploadRequest.setKey(targetKey);
		
		return uploadRequest;
	}
	
	/**
	 * 生成标签信息
	 * @description
	 * @author miaomingming
	 * @param req
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	private List<TagBO> createTagList(BizKeyShareRequest req){
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
	public BaseResponse<String> shareFileDownload(HttpServletRequest request,HttpServletResponse response, SysKeyDownloadRequest downRequest){
		log.error("downloadShareFile {} ES_LOG.param", JSON.toJSONString(downRequest));
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		try{
			if(!validator(baseResponse,downRequest, SysKeyRequestV.class)){
				return baseResponse;
			}
			downRequest.formatPathURI();
			log.info("downloadShareFile {} [ES_LOG_START]",downRequest.getPath());
			
			//step 1.校验是否存在
			SysKeyDTO sysKeyDTO = sysKeyManager.info(downRequest);
			if(sysKeyDTO == null){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			
			//step 2.查询标签并检查次数和有效期
			List<TagBO> tagBOList = sysKeyManager.tagList(downRequest);
			tagBOList = checkAndSetShareDownload(tagBOList,downRequest.getPath());
			
			//step 3.在重新设置文件标签
			S3Client s3 = s3FactoryService.getS3ClientByBucket(downRequest.getHost());
			S3KeyBO s3KeyBO = new S3KeyBO(downRequest.getHost(),downRequest.getKey());
			s3KeyService.setTagList(s3,s3KeyBO,tagBOList);
			
			//step 4.执行下载
			byte[] bodys = sysKeyManager.getBody(downRequest,null);
			HttpUtils.downloadFileByte(request,response,getFileName(downRequest),bodys);
			
			log.info("downloadShareFile {} [ES_LOG_SUCCESS]",downRequest.getPath());
			return null;
		}catch (AppLogicException e){
			log.error("shareFileDownload.AppLogicException:{}:", JSON.toJSONString(downRequest),e);
			log.error("downloadShareFile {} {} [ES_LOG_EXCEPTION]",downRequest.getPath(),ExceptionUtil.getMessage(e));
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
		}catch (Exception e){
			log.error("shareFileDownload.Exception:{}:", JSON.toJSONString(downRequest),e);
			log.error("downloadShareFile {} {} [ES_LOG_EXCEPTION]",downRequest.getPath(), ExceptionUtil.getMessage(e));
			baseResponse.setMsg(e.getMessage());
		}
		return baseResponse;
	}
	
	
	@Override
	public BaseResponse<SysKeyDTO> officeToPdf(@RequestBody BizKeyOfficeToPdfRequest request){
		return ServiceProvider.call(request, SysKeyOfficeRequestV.class, SysKeyDTO.class, req -> {
			request.formatPathURI();
			//step 0.校验
			String fileName = request.getKey().toLowerCase();
			if(!(fileName.endsWith("doc") || fileName.endsWith("docx")
				|| fileName.endsWith("ppt") || fileName.endsWith("pptx"))){
				throw new AppLogicException(ErrCodeEnum.FILE_TO_PDF_TYPE_LIMIT.getCode());
			}
			//step 1.数据读取
			byte[] bodys = sysKeyManager.getBody(request,null);
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
			String targetKey = request.getKey().substring(0,fileName.lastIndexOf("."));
			targetKey += StringUtils.join("_",System.currentTimeMillis(),".pdf");
			
			SysKeyUploadRequest uploadRequest = BeanCopyUtil.copy(request,SysKeyUploadRequest.class);
			uploadRequest.setHost(bizConfig.pdfFileBucket);
			uploadRequest.setKey(targetKey);
			uploadRequest.setIsCover(Boolean.TRUE);
			
			sysKeyManager.upload(uploadRequest,bodys);
			
			//step 6.查询并返回
			return sysKeyManager.info(uploadRequest);
		},"officeToPdf");
	}
	
	/**
	 * 检查是否分享文件，并更新分享次数等
	 * @description
	 * @author miaomingming
	 * @date 17:28 2020/6/23
	 * @param tagList
	 * @return java.util.List<com.zysl.cloud.aws.domain.bo.TagBO>
	 **/
	private List<TagBO> checkAndSetShareDownload(List<TagBO> tagList,String path){
		if(CollectionUtils.isEmpty(tagList)){
			throw new AppLogicException(ErrCodeEnum.FILE_IS_NOT_SHARED.getCode());
		}
		boolean isExistShareTag = Boolean.FALSE;
		List<TagBO> newTagList = Lists.newArrayList();
		for (TagBO tag : tagList) {
			//判断下载次数
			if(S3TagKeyEnum.TAG_DOWNLOAD_AMOUT.getCode().equals(tag.getKey()) &&
				Integer.parseInt(tag.getValue()) < 1){
				//下载次数已下完
				log.info("ES_LOG {} downloadShareFile.times.is.max",path);
				throw new AppLogicException(ErrCodeEnum.FILE_SHARED_DOWNLOAD_MAX_TIMES.getCode());
			}
			//判断是否在有效期内
			if(S3TagKeyEnum.TAG_VALIDITY.getCode().equals(tag.getKey()) &&
				DateUtils.doCompareDate(new Date(), DateUtils.createDate(tag.getValue())) > 0){
				//已过有效期
				log.info("ES_LOG {} downloadShareFile.timeout",path);
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
	
	
	private String getFileName(SysKeyRequest request){
		String key = request.getKey();
		if(StringUtils.isNotEmpty(key)){
			if(!key.endsWith(BizConstants.PATH_SEPARATOR)){
				return key.substring(key.lastIndexOf(BizConstants.PATH_SEPARATOR) + 1);
			}
		}
		return null;
	}
	
}
