package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.netflix.discovery.converters.Auto;
import com.zysl.cloud.aws.api.dto.DownloadFileDTO;
import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.aws.api.req.DownloadFileRequest;
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
import com.zysl.cloud.aws.api.srv.SysFileSrv;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.MultipartUploadBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysDirManager;
import com.zysl.cloud.aws.rule.service.ISysFileManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.CompleteMultipartRequestV;
import com.zysl.cloud.aws.web.validator.CopyObjectsRequestV;
import com.zysl.cloud.aws.web.validator.CreateMultipartRequestV;
import com.zysl.cloud.aws.web.validator.DownloadFileRequestV;
import com.zysl.cloud.aws.web.validator.MultiDownloadFileRequestV;
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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

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
	
	@Override
	public BaseResponse<String> mkdir(SysDirRequest request) {
		return ServiceProvider.call(request, SysDirRequestV.class, String.class, req -> {
			setFileSystemDefault(request);
			sysDirManager.mkdir(request);
			
			return RespCodeEnum.SUCCESS.getName();
		});
	}
	
	@Override
	public BasePaginationResponse<SysFileDTO> list(SysDirListRequest request) {
		return ServiceProvider.callList(request, SysDirListRequestV.class, SysFileDTO.class, (req,myPage) -> {
			setFileSystemDefault(request);
			if(request.getPageSize() == null){
				myPage.setPageSize(1000);
			}
			if(request.getPageSize() == -1){
				myPage.setPageSize(999999999);
				myPage.setPageNo(1);
			}
			if(request.getPageIndex() == null){
				myPage.setPageNo(1);
			}
			return sysDirManager.list(request,myPage);
		});
	}
	
	@Override
	public BaseResponse<String> copy(SysFileRenameRequest request) {
		return ServiceProvider.call(request, SysFileRenameRequestV.class, String.class, req -> {
			
			setFileSystemDefault(request.getSource());
			setFileSystemDefault(request.getTarget());
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
		});
	}
	
	@Override
	public BaseResponse<String> move(SysFileRenameRequest request) {
		return ServiceProvider.call(request, SysFileRenameRequestV.class, String.class, req -> {
			setFileSystemDefault(request.getSource());
			setFileSystemDefault(request.getTarget());
			if(!StringUtils.isBlank(request.getSource().getFileName())){
				sysFileManager.move(request.getSource(),request.getTarget());
			}else{
				SysDirRequest source = BeanCopyUtil.copy(request.getSource(),SysDirRequest.class);
				SysDirRequest target = BeanCopyUtil.copy(request.getTarget(),SysDirRequest.class);
				sysDirManager.move(source, target);
			}
			
			return RespCodeEnum.SUCCESS.getName();
		});
		
	}
	
	@Override
	public BaseResponse<String> delete(SysFileRequest request) {
		return ServiceProvider.call(request, SysDirRequestV.class, String.class, req -> {
			setFileSystemDefault(request);
			if(!StringUtils.isBlank(request.getFileName())){
				sysFileManager.delete(request);
			}else{
				SysDirRequest source = BeanCopyUtil.copy(request,SysDirRequest.class);
				sysDirManager.delete(source);
			}
			return RespCodeEnum.SUCCESS.getName();
		});
	}
	
	@Override
	public BaseResponse<SysFileDTO> info(SysFileRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, SysFileDTO.class, req -> {
			setFileSystemDefault(request);
			return sysFileManager.info(request);
		});
	}
	
	@Override
	public BaseResponse<SysFileDTO> upload(HttpServletRequest httpServletRequest, SysFileUploadRequest request) {
		return ServiceProvider.call(request, null, SysFileDTO.class,req -> {
			setFileSystemDefault(request);
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			
			byte[] bytes = getBytesFromHttpRequest(httpServletRequest);
			
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1 ? Boolean.TRUE : Boolean.FALSE;
			sysFileManager.upload(fileRequest,bytes,isOverWrite);
			
			//设置返回参数
			SysFileDTO dto = sysFileManager.info(fileRequest);
			return dto;
		});
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> download(HttpServletRequest request, HttpServletResponse response, SysFileDownloadRequest downRequest) {
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		setFileSystemDefault(downRequest);
		
		try{
			validator(baseResponse,downRequest, SysFileRequestV.class);
			
			//临时权限校验
			if(!checkOwner(downRequest)){
				baseResponse.setCode(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
				return baseResponse;
			}
			
			
			//获取标签中的文件名称
			SysFileDTO fileDTO = sysFileManager.info(downRequest);
			if(fileDTO == null){
				baseResponse.setCode(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
				return baseResponse;
			}
			
			//从头信息取Range:bytes=0-1000
			String range = request.getHeader("Range");
			//对Range数值做校验
			Long[] byteLength = HttpUtils.checkRange(range);
			
			if(StringUtils.isBlank(range)){
				byteLength[1] = webConfig.getDownloadMaxFileSize() * 1024 * 1024L;
				if(fileDTO.getSize() > byteLength[1]){
					baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
					baseResponse.setMsg("文件大小超过" + webConfig.getDownloadMaxFileSize() + "m必须分片下载.");
					return baseResponse;
				}
			}
			
			range = StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]);
			//返回数据
			byte[] bodys = sysFileManager.getFileBodys(downRequest,range);
			
			//设置响应头：Content-Range: bytes 0-2000/4932
			byteLength[1] = byteLength[1] > fileDTO.getSize()-1 ? fileDTO.getSize()-1 : byteLength[1];
			String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1],"/",fileDTO.getSize());
			response.setHeader("Content-Range",rspRange);
			
			
			//下载数据
			HttpUtils.downloadFileByte(request,response,fileDTO.getFileName(),bodys);
			
			return null;
		}catch (AppLogicException e){
			log.error("multiDownloadFile.AppLogicException:",e);
			baseResponse.setMsg(e.getMessage());
			return baseResponse;
		}catch (Exception e){
			log.error("multiDownloadFile.Exception:",e);
			baseResponse.setMsg(e.getMessage());
			return baseResponse;
		}
	}
	
	@Override
	public BaseResponse<String> multiUploadStart(SysFileMultiStartRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, String.class , req -> {
			setFileSystemDefault(request);
			return sysFileManager.multiUploadStart(request);
		});
	}
	
	@Override
	public BaseResponse<String> multiUploadData(HttpServletRequest httpServletRequest, SysFileMultiUploadRequest request) {
		return ServiceProvider.call(request, SysFileMultiRequestV.class, String.class , req -> {
			byte[] bytes = getBytesFromHttpRequest(httpServletRequest);
			
			if(request.getPartNumber() == null){
				request.setPartNumber(1);
			}
			setFileSystemDefault(request);
			
			return sysFileManager.multiUploadBodys(request,bytes);
		});
		
		
	}
	
	@Override
	public BaseResponse<String> multiUploadComplete(SysFileMultiCompleteRequest request) {
		return ServiceProvider.call(request, SysFileMultiCompleteRequestV.class, String.class, req -> {
			setFileSystemDefault(request);
			sysFileManager.multiUploadComplete(request);
			return RespCodeEnum.SUCCESS.getName();
		});
	}
	
	@Override
	public BaseResponse<String> multiUploadAbort(SysFileMultiRequest request) {
		return ServiceProvider.call(request, SysFileMultiRequestV.class, String.class , req -> {
			setFileSystemDefault(request);
			sysFileManager.multiUploadAbort(request);
			return RespCodeEnum.SUCCESS.getName();
		});
	}
	
	@Override
	public BaseResponse<FilePartInfoDTO> multiUploadInfoQuery(SysFileMultiStartRequest request) {
		return ServiceProvider.call(request, SysFileRequestV.class, FilePartInfoDTO.class, req -> {
			setFileSystemDefault(request);
			return sysFileManager.multiUploadInfo(request);
		});
	}
	
	@Override
	public BasePaginationResponse<SysFileDTO> listVersions(@RequestBody SysFileListRequest request){
		return ServiceProvider.callList(request, SysFileRequestV.class, SysFileDTO.class, (req,myPage) -> {
			setFileSystemDefault(request);
			return sysFileManager.listVersions(request);
		});
	}
	
	private void setFileSystemDefault(SysFileListRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	
	private void setFileSystemDefault(SysFileMultiStartRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	private void setFileSystemDefault(SysFileRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	private void setFileSystemDefault(SysDirRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
	}
	private void setFileSystemDefault(SysDirListRequest request){
		if(StringUtils.isBlank(request.getType())){
			request.setType(webConfig.getFileSystemTypeDefault());
		}
		if(StringUtils.isBlank(request.getServerNo())){
			request.setServerNo(webConfig.getFileSystemServerNoDefault());
		}
		
	}
	
	private byte[] getBytesFromHttpRequest(HttpServletRequest httpServletRequest)throws AppLogicException{
		try {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)httpServletRequest;
			return multipartHttpServletRequest.getFile("file").getBytes();
		} catch (IOException e) {
			log.error("--uploadFile获取文件流异常--：{}", e);
			throw new AppLogicException("获取文件流异常");
		}
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
