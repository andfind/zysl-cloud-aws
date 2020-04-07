package com.zysl.cloud.aws.web.controller;

import com.google.common.collect.Lists;
import com.netflix.discovery.converters.Auto;
import com.zysl.cloud.aws.api.dto.DownloadFileDTO;
import com.zysl.cloud.aws.api.dto.FilePartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysFileDTO;
import com.zysl.cloud.aws.api.dto.UploadFieDTO;
import com.zysl.cloud.aws.api.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.enums.OPAuthTypeEnum;
import com.zysl.cloud.aws.api.req.SysDirListRequest;
import com.zysl.cloud.aws.api.req.SysDirRequest;
import com.zysl.cloud.aws.api.req.SysFileExistRequest;
import com.zysl.cloud.aws.api.req.SysFileListRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiRequest;
import com.zysl.cloud.aws.api.req.SysFileMultiStartRequest;
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
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.CompleteMultipartRequestV;
import com.zysl.cloud.aws.web.validator.CopyObjectsRequestV;
import com.zysl.cloud.aws.web.validator.DownloadFileRequestV;
import com.zysl.cloud.aws.web.validator.MultiDownloadFileRequestV;
import com.zysl.cloud.aws.web.validator.SysDirListRequestV;
import com.zysl.cloud.aws.web.validator.SysDirRequestV;
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
			SysFileRequest fileRequest = BeanCopyUtil.copy(request,SysFileRequest.class);
			setFileSystemDefault(request);
			
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
			byte[] bytes = null;
			try {
				bytes = multipartHttpServletRequest.getFile("file").getBytes();
			} catch (IOException e) {
				log.error("--uploadFile获取文件流异常--：{}", e);
				throw new AppLogicException("获取文件流异常");
			}
			
			boolean isOverWrite = request.getIsOverWrite() == null || request.getIsOverWrite() == 1 ? Boolean.TRUE : Boolean.FALSE;
			sysFileManager.upload(fileRequest,bytes,isOverWrite);
			
			//设置返回参数
			SysFileDTO dto = sysFileManager.info(fileRequest);
			return dto;
		});
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> download(HttpServletRequest request, HttpServletResponse response, SysFileRequest downRequest) {
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		setFileSystemDefault(downRequest);
		
		List<String> validate = new ArrayList<>();
		try{
			SysFileRequestV validator = BeanCopyUtil.copy(downRequest, SysFileRequestV.class);
			BeanValidator beanValidator = SpringContextUtil.getBean("beanValidator", BeanValidator.class);
			validate = beanValidator.validate(validator, BeanValidator.CASE_DEFAULT);
			
			if(!CollectionUtils.isEmpty(validate)){
				baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
				baseResponse.setMsg(RespCodeEnum.ILLEGAL_PARAMETER.getName());
				baseResponse.setValidations(validate);
				return baseResponse;
			}
			
			
			//获取标签中的文件名称
			Object obj = sysFileManager.info(downRequest);
			if(obj == null){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			S3ObjectBO s3ObjectBO = (S3ObjectBO)obj;
			
			//从头信息取Range:bytes=0-1000
			String range = request.getHeader("Range");
			//对Range数值做校验
			Long[] byteLength = HttpUtils.checkRange(range);
			
			if(StringUtils.isBlank(range)){
				byteLength[1] = webConfig.getDownloadMaxFileSize() * 1024 * 1024L;
			}
			
			range = StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]);
			//返回数据
			byte[] bodys = sysFileManager.getFileBodys(downRequest,range);
			
			//设置响应头：Content-Range: bytes 0-2000/4932
			byteLength[1] = byteLength[1] > s3ObjectBO.getContentLength()-1 ? s3ObjectBO.getContentLength()-1 : byteLength[1];
			String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1],"/",s3ObjectBO.getContentLength());
			response.setHeader("Content-Range",rspRange);
			
			
			//下载数据
			HttpUtils.downloadFileByte(request,response,s3ObjectBO.getFileName(),bodys);
			
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
		return null;
	}
	
	@Override
	public BaseResponse<String> multiUploadData(HttpServletRequest req, SysFileMultiRequest request) {
		return null;
	}
	
	@Override
	public BaseResponse<String> multiUploadComplete(SysFileMultiRequest request) {
		return null;
	}
	
	@Override
	public BaseResponse<String> multiUploadAbort(SysFileMultiRequest request) {
		return null;
	}
	
	@Override
	public BaseResponse<FilePartInfoDTO> multiUploadInfoQuery(SysFileMultiRequest request) {
		return null;
	}
	
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
		if(request.getPageSize() == null){
			request.setPageSize(1000);
		}
		if(request.getPageSize() == -1){
			request.setPageSize(999999999);
			request.setPageIndex(1);
		}
		if(request.getPageIndex() == null){
			request.setPageIndex(1);
		}
	}
}
