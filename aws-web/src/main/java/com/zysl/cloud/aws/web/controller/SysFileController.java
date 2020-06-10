package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileArrayRequest;
import com.zysl.cloud.aws.api.req.SysFileDownloadRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiCompleteRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiUploadRequest;
import com.zysl.cloud.aws.api.req.SysFileRenameRequest;
import com.zysl.cloud.aws.api.req.SysFileRequest;
import com.zysl.cloud.aws.api.req.SysFileUploadRequest;
import com.zysl.cloud.aws.api.srv.SysFileSrv;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysDirManager;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.utils.ReqDefaultUtils;
import com.zysl.cloud.aws.web.validator.SysDirListRequestV;
import com.zysl.cloud.aws.web.validator.SysDirRequestV;
import com.zysl.cloud.aws.web.validator.SysFileArrayRequestV;
import com.zysl.cloud.aws.web.validator.SysFileMultiCompleteRequestV;
import com.zysl.cloud.aws.web.validator.SysFileMultiRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRenameRequestV;
import com.zysl.cloud.aws.web.validator.SysFileRequestV;
import com.zysl.cloud.aws.web.validator.SysFileUploadRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SysFileController extends BaseController implements SysFileSrv {
	
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private ISysFileManager sysFileManager;
	@Autowired
	private ISysDirManager sysDirManager;
	@Autowired
	private IS3FileService s3FileService;
	@Autowired
	private ReqDefaultUtils reqDefaultUtils;
	
	@Override
	public BaseResponse<String> mkdir(SysDirRequest request) {
		return ServiceProvider.call(request, SysDirRequestV.class, String.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			sysDirManager.mkdir(request);
			
			return RespCodeEnum.SUCCESS.getName();
		},"mkdir");
	}
	
	@Override
	public BasePaginationResponse<SysFileDTO> list(SysDirListRequest request) {
		return ServiceProvider.callList(request, SysDirListRequestV.class, SysFileDTO.class, (req,myPage) -> {
			reqDefaultUtils.setFileSystemDefault(request);
			if(request.getPageSize() == null || request.getPageSize() < -1 || request.getPageSize() == 0){
				myPage.setPageSize(1000);
			}else if(request.getPageSize().intValue() == -1){
				myPage.setPageSize(999999999);
				myPage.setPageNo(1);
			}
			if(request.getPageIndex() == null){
				myPage.setPageNo(1);
			}
			return sysDirManager.list(request,myPage);
		},"listDir");
	}
	
	@Override
	public BaseResponse<String> copy(SysFileRenameRequest request) {
		return ServiceProvider.call(request, SysFileRenameRequestV.class, String.class, req -> {
			
			reqDefaultUtils.setFileSystemDefault(request.getSource());
			reqDefaultUtils.setFileSystemDefault(request.getTarget());
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1? Boolean.TRUE : Boolean.FALSE;
			if(!StringUtils.isBlank(request.getSource().getFileName())){
				if(StringUtils.isBlank(request.getTarget().getFileName())){
					request.getTarget().setFileName(request.getSource().getFileName());
				}
				sysFileManager.copy(request.getSource(),request.getTarget(),isOverWrite);
			}else{
				SysDirRequest source = BeanCopyUtil.copy(request.getSource(),SysDirRequest.class);
				SysDirRequest target = BeanCopyUtil.copy(request.getTarget(),SysDirRequest.class);
				sysDirManager.copy(source, target,isOverWrite);
			}
			
			return RespCodeEnum.SUCCESS.getName();
		},"copy");
	}
	
	@Override
	public BaseResponse<String> move(SysFileRenameRequest request) {
		return ServiceProvider.call(request, SysFileRenameRequestV.class, String.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request.getSource());
			reqDefaultUtils.setFileSystemDefault(request.getTarget());
			if(!StringUtils.isBlank(request.getSource().getFileName())){
				sysFileManager.move(request.getSource(),request.getTarget());
			}else{
				SysDirRequest source = BeanCopyUtil.copy(request.getSource(),SysDirRequest.class);
				SysDirRequest target = BeanCopyUtil.copy(request.getTarget(),SysDirRequest.class);
				sysDirManager.move(source, target);
			}
			
			return RespCodeEnum.SUCCESS.getName();
		},"move");
		
	}
	
	@Override
	public BaseResponse<String> delete(SysFileRequest request) {
		return ServiceProvider.call(request, SysDirRequestV.class, String.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			if(!StringUtils.isBlank(request.getFileName())){
				sysFileManager.delete(request);
			}else{
				SysDirRequest source = BeanCopyUtil.copy(request,SysDirRequest.class);
				sysDirManager.delete(source);
			}
			return RespCodeEnum.SUCCESS.getName();
		},"delete");
	}
	
	
	@Override
	public BaseResponse<String> deleteList(@RequestBody SysFileArrayRequest request){
		return ServiceProvider.call(request, SysFileArrayRequestV.class, String.class, req -> {
			
			for(SysFileRequest sysFileRequest:request.getSysFileList()){
				reqDefaultUtils.setFileSystemDefault(sysFileRequest);
				if(!StringUtils.isBlank(sysFileRequest.getFileName())){
					sysFileManager.delete(sysFileRequest);
				}else{
					SysDirRequest source = BeanCopyUtil.copy(sysFileRequest,SysDirRequest.class);
					sysDirManager.delete(source);
				}
			}
			
			return RespCodeEnum.SUCCESS.getName();
		},"deleteList");
	}
	
	@Override
	public BaseResponse<SysFileDTO> info(SysFileRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, SysFileDTO.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			return sysFileManager.info(request);
		},"info");
	}
	
	@Override
	public BaseResponse<SysFileDTO> upload(HttpServletRequest httpServletRequest, SysFileUploadRequest request) {
		return ServiceProvider.call(request, SysFileUploadRequestV.class, SysFileDTO.class,req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			
			byte[] bytes = HttpUtils.getBytesFromHttpRequest(httpServletRequest,request);
			
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1 ? Boolean.TRUE : Boolean.FALSE;
			
			sysFileManager.upload(fileRequest,bytes,isOverWrite);
			
			//设置返回参数
			SysFileDTO dto = sysFileManager.info(fileRequest);
			return dto;
		},"upload");
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> download(HttpServletRequest request, HttpServletResponse response, SysFileDownloadRequest downRequest) {
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		reqDefaultUtils.setFileSystemDefault(downRequest);

		try{
			if(!validator(baseResponse,downRequest, SysFileRequestV.class)){
				return baseResponse;
			}
			log.info("download {} [ES_LOG_START]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));

			//临时权限校验
			if(!checkOwner(downRequest)){
				baseResponse.setCode(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
				return baseResponse;
			}


			//获取标签中的文件名称
			SysFileDTO fileDTO = sysFileManager.info(downRequest);
			if(fileDTO == null){
				baseResponse.setCode(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
				response.setStatus(RespCodeEnum.NOT_EXISTED.getCode());
				return baseResponse;
			}

			//从头信息取Range:bytes=0-1000
			String range = request.getHeader("Range");
			log.info("download {} Range={}",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),range);
			//对Range数值做校验
			Long[] byteLength = HttpUtils.checkRange(range);

			if(StringUtils.isBlank(range)){
				log.info("{}--range is null",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
				byteLength[1] = webConfig.getDownloadMaxFileSize() * 1024 * 1024L;
				if(fileDTO.getSize() > byteLength[1]){
					log.info("fileSize:{},range:{},file:{}",fileDTO.getSize(),range,StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
					baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
					baseResponse.setMsg("文件大小超过" + webConfig.getDownloadMaxFileSize() + "m只能分片下载.");
					return baseResponse;
				}
				range = StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]);
			}

			//返回数据
			byte[] bodys = sysFileManager.getFileBodys(downRequest,range);


			if(!StringUtils.isBlank(range)){
				//设置响应头：Content-Range: bytes 0-2000/4932
				byteLength[1] = byteLength[1] > fileDTO.getSize()-1 ? fileDTO.getSize()-1 : byteLength[1];
				String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1],"/",fileDTO.getSize());
				response.setHeader("Content-Range",rspRange);
			}


			//下载数据
			HttpUtils.downloadFileByte(request,response,fileDTO.getFileName(),bodys);
			log.info("baseResponse:{}", JSON.toJSONString(baseResponse));
			log.info("download {} [ES_LOG_SUCCESS]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()));
			return null;
		}catch (AppLogicException e){
			log.error("download.AppLogicException:",e);
			log.error("download {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
			if(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode().equals(e.getExceptionCode())){
				response.setStatus(RespCodeEnum.NOT_EXISTED.getCode());
			}else{
				response.setStatus(RespCodeEnum.FAILED.getCode());
			}
			return baseResponse;
		}catch (Exception e){
			log.error("download.Exception:",e);
			log.error("download {} {} [ES_LOG_EXCEPTION]",StringUtils.join(downRequest.getPath(),downRequest.getFileName()),e.getMessage());
			baseResponse.setMsg(e.getMessage());
			response.setStatus(RespCodeEnum.FAILED.getCode());
			return baseResponse;
		}
	}
	
	@Override
	public BaseResponse<String> multiUploadStart(SysFileMultiStartRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, String.class , req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			return sysFileManager.multiUploadStart(request);
		},"multiUploadStart");
	}
	
	@Override
	public BaseResponse<String> multiUploadData(HttpServletRequest httpServletRequest, SysFileMultiUploadRequest request) {
		return ServiceProvider.call(request, SysFileMultiRequestV.class, String.class , req -> {
			byte[] bytes = HttpUtils.getBytesFromHttpRequest(httpServletRequest);
			
			if(request.getPartNumber() == null){
				request.setPartNumber(1);
			}
			reqDefaultUtils.setFileSystemDefault(request);
			
			return sysFileManager.multiUploadBodys(request,bytes);
		},"multiUploadData");
		
		
	}
	
	@Override
	public BaseResponse<SysFileDTO> multiUploadComplete(SysFileMultiCompleteRequest request) {
		return ServiceProvider.call(request, SysFileMultiCompleteRequestV.class, SysFileDTO.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			sysFileManager.multiUploadComplete(request);
			
			return sysFileManager.info(BeanCopyUtil.copy(request,SysFileRequest.class));
		},"multiUploadComplete");
	}
	
	@Override
	public BaseResponse<String> multiUploadAbort(SysFileMultiRequest request) {
		return ServiceProvider.call(request, SysFileMultiRequestV.class, String.class , req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			sysFileManager.multiUploadAbort(request);
			return RespCodeEnum.SUCCESS.getName();
		},"multiUploadAbort");
	}
	
	@Override
	public BaseResponse<FilePartInfoDTO> multiUploadInfoQuery(SysFileMultiStartRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, FilePartInfoDTO.class, req -> {
			reqDefaultUtils.setFileSystemDefault(request);
			return sysFileManager.multiUploadInfo(request);
		},"multiUploadInfoQuery");
	}
	
	@Override
	public BasePaginationResponse<SysFileDTO> listVersions(@RequestBody SysFileListRequest request){
		return ServiceProvider.callList(request, SysFileRequestV.class, SysFileDTO.class, (req,myPage) -> {
			reqDefaultUtils.setFileSystemDefault(request);
			return sysFileManager.listVersions(request);
		},"listVersions");
	}
	
	
	

	
	//临时数据校验，是否对象拥有者
	private boolean checkOwner(SysFileDownloadRequest request){
		if(!StringUtils.isEmpty(request.getUserId())){
			S3ObjectBO s3ObjectBO = ObjectFormatUtils.createS3ObjectBO(request);
			//查询标签
			List<TagBO> list = s3FileService.getTags(s3ObjectBO);
			//需要校验权限
			for (TagBO tag : list) {
				//判断标签可以是否是owner
				if(S3TagKeyEnum.OWNER.getCode().equals(tag.getKey()) &&
					request.getUserId().equals(tag.getValue())){
					return Boolean.TRUE;
				}
			}
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
