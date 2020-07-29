package com.zysl.cloud.aws.web.controller;

import com.alibaba.fastjson.JSON;
import com.zysl.cloud.aws.api.dto.PartInfoDTO;
import com.zysl.cloud.aws.api.dto.SysKeyDTO;
import com.zysl.cloud.aws.api.dto.SysKeyFileDTO;
import com.zysl.cloud.aws.api.enums.DownTypeEnum;
import com.zysl.cloud.aws.api.req.key.SysKeyCopyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyCreateRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteListRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDeleteRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyDownloadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyExistRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyMultiUploadRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyPageRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyRequest;
import com.zysl.cloud.aws.api.req.key.SysKeyUploadRequest;
import com.zysl.cloud.aws.api.srv.SysKeySrv;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.enums.S3TagKeyEnum;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.config.WebConfig;
import com.zysl.cloud.aws.domain.bo.S3KeyBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.aws.rule.service.ISysKeyManager;
import com.zysl.cloud.aws.rule.utils.ObjectFormatUtils;
import com.zysl.cloud.aws.web.utils.HttpUtils;
import com.zysl.cloud.aws.web.validator.SysKeyCopyRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyDeleteListRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyExistRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyMultiUploadRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyPageRequestV;
import com.zysl.cloud.aws.web.validator.SysKeyRequestV;
import com.zysl.cloud.utils.BeanCopyUtil;
import com.zysl.cloud.utils.ExceptionUtil;
import com.zysl.cloud.utils.LogHelper;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.enums.RespCodeEnum;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysKeyController extends BaseController implements SysKeySrv {
	
	@Autowired
	private ISysKeyManager sysKeyManager;
	@Autowired
	private WebConfig webConfig;
	@Autowired
	private LogConfig logConfig;
	@Autowired
	private HttpUtils httpUtils;
	
	@Override
	public BaseResponse<SysKeyDTO> create(SysKeyCreateRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			sysKeyManager.create(request);
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"create");
	}
	
	
	@Override
	public BaseResponse<SysKeyDTO> upload(HttpServletRequest httpServletRequest, SysKeyUploadRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			Boolean isCover = request.getIsCover() != null ? request.getIsCover() : Boolean.TRUE;
			request.setIsCover(isCover);
			
			byte[] bodys = httpUtils.getBytesFromHttpRequest(httpServletRequest,request);
			
			sysKeyManager.upload(request,bodys);
			
			//设置返回参数
			return sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
		},"upload");
	}
	
	@ResponseBody
	@Override
	public BaseResponse<String> download(HttpServletRequest request, HttpServletResponse response, SysKeyDownloadRequest downRequest) {
		BaseResponse<String> baseResponse = new BaseResponse<>();
		baseResponse.setSuccess(Boolean.FALSE);
		downRequest.formatPathURI();

		try{
			if(!validator(baseResponse,downRequest, SysKeyRequestV.class)){
				return baseResponse;
			}
			LogHelper.info(getClass(),"download",downRequest.getPath(), JSON.toJSONString(downRequest));
			
			SysKeyRequest sysKeyRequest = BeanCopyUtil.copy(downRequest,SysKeyRequest.class);
			List<TagBO> tagBOList = sysKeyManager.tagList(sysKeyRequest);
			SysKeyDTO sysKeyDTO = sysKeyManager.info(sysKeyRequest);
			
			//临时权限校验
			if(!checkOwner(downRequest,tagBOList)){
				baseResponse.setCode(ErrCodeEnum.OBJECT_OP_AUTH_CHECK_FAILED.getCode());
				return baseResponse;
			}


			//获取标签中的文件名称,没有则按照上传path截取
			String fileName = S3Utils.getTagValue(tagBOList, S3TagKeyEnum.FILE_NAME.getCode());
			if(StringUtils.isEmpty(fileName)){
				fileName = downRequest.getPath().substring(downRequest.getPath().lastIndexOf(BizConstants.PATH_SEPARATOR) + 1);
				if(fileName.indexOf("#") > -1){
					fileName = fileName.substring(0,fileName.indexOf("#"));
				}
			}

			//从头信息取Range:bytes=0-1000
			String range = request.getHeader("Range");
			LogHelper.info(getClass(),"download.range",downRequest.getPath(), range);
			//对Range数值做校验
			Long[] byteLength = httpUtils.checkRange(range);

			if(StringUtils.isBlank(range)){
				LogHelper.info(getClass(),"download",downRequest.getPath(), "range is null");
				byteLength[1] = webConfig.getDownloadMaxFileSize() * 1024 * 1024L;
				if(sysKeyDTO.getSize() > byteLength[1]){
					LogHelper.info(getClass(),"download",downRequest.getPath(), String.format("fileSize:%s",sysKeyDTO.getSize()));
					baseResponse.setCode(RespCodeEnum.ILLEGAL_PARAMETER.getCode());
					baseResponse.setMsg("文件大小超过" + webConfig.getDownloadMaxFileSize() + "m只能分片下载.");
					return baseResponse;
				}
				range = StringUtils.join("bytes=",byteLength[0],"-",byteLength[1]);
			}

			//返回数据
			byte[] bodys = sysKeyManager.getBody(downRequest,range);

			if(!StringUtils.isBlank(range)){
				//设置响应头：Content-Range: bytes 0-2000/4932
				byteLength[1] = byteLength[1] > sysKeyDTO.getSize()-1 ? sysKeyDTO.getSize()-1 : byteLength[1];
				String rspRange = StringUtils.join("bytes ",byteLength[0],"-",byteLength[1], BizConstants.PATH_SEPARATOR,sysKeyDTO.getSize());
				response.setHeader("Content-Range",rspRange);
			}

			//设置返回content-type
			String contentType = DownTypeEnum.FILE.getContentType();
    		if (downRequest.getType() != null && downRequest.getType().intValue() == DownTypeEnum.VIDEO.getCode()) {
				contentType = DownTypeEnum.VIDEO.getContentType();
			}
			//下载数据
			httpUtils.downloadFileByte(request,response,fileName,bodys,contentType);
			LogHelper.info(getClass(),"download",downRequest.getPath(), "SUCCESS");
			return null;
		}catch (AppLogicException e){
			LogHelper.warn(getClass(),"download",downRequest.getPath(), ExceptionUtil.getMessage(e),e);
			baseResponse.setMsg(e.getMessage());
			baseResponse.setCode(e.getExceptionCode());
			if(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode().equals(e.getExceptionCode())){
				response.setStatus(RespCodeEnum.NOT_EXISTED.getCode());
			}else{
				response.setStatus(RespCodeEnum.FAILED.getCode());
			}
			return baseResponse;
		}catch (Exception e){
			LogHelper.error(getClass(),"download",downRequest.getPath(), ExceptionUtil.getMessage(e),e);
			baseResponse.setMsg(e.getMessage());
			response.setStatus(RespCodeEnum.FAILED.getCode());
			return baseResponse;
		}
		
	}
	
	@Override
	public BaseResponse<SysKeyDTO> info(SysKeyRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			SysKeyDTO dto = sysKeyManager.info(BeanCopyUtil.copy(request, SysKeyRequest.class));
			if(dto == null){
				throw new AppLogicException(ErrCodeEnum.S3_SERVER_CALL_METHOD_NO_SUCH_KEY.getCode());
			}
			//设置返回参数
			return dto;
		},"info");
	}
	
	@Override
	public BaseResponse<String> delete(@RequestBody SysKeyDeleteRequest request){
		return ServiceProvider.call(request, SysKeyRequestV.class, String.class,req -> {
			request.formatPathURI();
			Boolean isPhy = request.getIsPhy() != null ? request.getIsPhy() : Boolean.FALSE;
			request.setIsPhy(isPhy);
			
			sysKeyManager.delete(request);
			return RespCodeEnum.SUCCESS.getCode();
		},"delete");
	}
	
	@Override
	public BaseResponse<String> deleteList(SysKeyDeleteListRequest request) {
		return ServiceProvider.call(request, SysKeyDeleteListRequestV.class, String.class,req -> {
			request.formatPathURI();
			
			sysKeyManager.deleteList(request);
			return RespCodeEnum.SUCCESS.getCode();
		},"deleteList");
	}
	
	@Override
	public BasePaginationResponse<SysKeyFileDTO> infoList(SysKeyPageRequest request) {
		return ServiceProvider.callList(request, SysKeyPageRequestV.class, SysKeyFileDTO.class, (req,myPage) -> {
			request.formatPathURI();
			if(myPage.getPageNo() == -1){
				myPage.setPageNo(1);
				myPage.setPageSize(BizConstants.MAX_PAGE_SIE);
			}
			if(request.getPageSize() == null){
				myPage.setPageSize(BizConstants.MAX_PAGE_SIE);
			}
			
			return sysKeyManager.infoList(BeanCopyUtil.copy(request,SysKeyRequest.class),myPage);
		},"infoList");
	}
	
	@Override
	public BasePaginationResponse<SysKeyDTO> versionList(SysKeyPageRequest request) {
		return ServiceProvider.callList(request, SysKeyPageRequestV.class, SysKeyDTO.class, (req,myPage) -> {
			request.formatPathURI();
			
			if(request.getPageSize() == null){
				myPage.setPageSize(BizConstants.MAX_PAGE_SIE);
			}
			return sysKeyManager.versionList(BeanCopyUtil.copy(request,SysKeyRequest.class),myPage);
		},"versionList");
	}
	
	@Override
	public BaseResponse<String> copy(SysKeyCopyRequest request) {
		return ServiceProvider.call(request, SysKeyCopyRequestV.class, String.class,req -> {
			
			SysKeyRequest src = new SysKeyRequest(request.getSrcPath());
			src.formatPathURI();
			
			SysKeyRequest dest = new SysKeyRequest(request.getDestPath());
			dest.formatPathURI();
			
			sysKeyManager.copy(src,dest,request.getIsCover() == null ? Boolean.TRUE : request.getIsCover());
			return RespCodeEnum.SUCCESS.getCode();
		},"copy");
	}
	
	@Override
	public BaseResponse<Boolean> isExist(@RequestBody SysKeyExistRequest request){
		return ServiceProvider.call(request, SysKeyExistRequestV.class, Boolean.class, req -> {
			List<String> paths = request.getPaths();
			//增加默认path
			if(CollectionUtils.isEmpty(paths)){
				paths = new ArrayList<>();
				List<String> buckets = webConfig.getAnnouncementBuckets();
				if(!CollectionUtils.isEmpty(buckets)){
					for(String bucket:buckets){
						paths.add(ObjectFormatUtils.getUriString(new S3KeyBO(bucket)));
					}
				}
			}
			
			if(!CollectionUtils.isEmpty(paths)){
				for(String path:paths){
					SysKeyRequest sysKeyRequest = new SysKeyRequest();
					sysKeyRequest.setPath(StringUtils.join(path,req.getFileName()));
					sysKeyRequest.formatPathURI();
					if(sysKeyManager.info(sysKeyRequest) != null){
						return Boolean.TRUE;
					}
				}
			}
			
			
			return Boolean.FALSE;
		},"isExist");
	}
	
	@Override
	public BaseResponse<SysKeyDTO> multiUpload(HttpServletRequest httpServletRequest, SysKeyMultiUploadRequest request){
		return ServiceProvider.call(request, SysKeyMultiUploadRequestV.class, SysKeyDTO.class,req -> {
			request.formatPathURI();
			Boolean isCover = request.getIsCover() != null ? request.getIsCover() : Boolean.TRUE;
			request.setIsCover(isCover);
			
			byte[] bodys = httpUtils.getBytesFromHttpRequest(httpServletRequest,request);
			
			//格式：bytes=开始位置/总大小，例如  bytes=0/1200
			String range = httpServletRequest.getHeader("Range");
			long totalSize = 0;
			if(StringUtils.isNotEmpty(range)){
				totalSize = Long.parseLong(range.substring(range.indexOf("/") + 1));
			}
			LogHelper.info(getClass(),"multiUpload",request.getEsLogMsg(), range);
			
			//是否覆盖
			if(request.getIsCover() != null && !request.getIsCover()
				&& sysKeyManager.info(request) != null){
				LogHelper.info(getClass(),"multiUpload",request.getPath(),"can.not.cover.but.exist");
				throw new AppLogicException(ErrCodeEnum.S3_BUCKET_OBJECT_EXIST.getCode());
			}
			
			Boolean isComplite = sysKeyManager.multiUpload(request,bodys,totalSize);
			
			//设置返回参数
			if(isComplite){
				return sysKeyManager.info(request);
			}else{
				SysKeyDTO dto = new SysKeyDTO();
				dto.setPath(request.getPath());
				
				return dto;
			}
			
		},"multiUpload");
	}
	
	@Override
	public BasePaginationResponse<PartInfoDTO> multiList(SysKeyPageRequest request) {
		return ServiceProvider.callList(request, SysKeyPageRequestV.class, PartInfoDTO.class, (req,myPage) -> {
			request.formatPathURI();
			
			return sysKeyManager.multiList(BeanCopyUtil.copy(request,SysKeyRequest.class));
		},"multiList");
	}
	
	@Override
	public BaseResponse<Boolean> multiAbort(SysKeyRequest request) {
		return ServiceProvider.call(request, SysKeyRequestV.class, Boolean.class,req -> {
			request.formatPathURI();
			
			sysKeyManager.multiAbort(request);
			
			//设置返回参数
			return Boolean.TRUE;
		},"multiAbort");
	}
	
	
	/**
	 * 临时数据校验，是否对象拥有者
	 * 有传参数就必须要校验
	 * @description
	 * @author miaomingming
	 * @date 10:09 2020/6/22
	 * @return
	 **/
	private boolean checkOwner(SysKeyDownloadRequest req, List<TagBO> tagBOList){
		if(!StringUtils.isEmpty(req.getUserId())){
			//需要校验权限
			String owner = S3Utils.getTagValue(tagBOList, S3TagKeyEnum.OWNER.getCode());
			if(req.getUserId().equals(owner)){
				return Boolean.TRUE;
			}
			LogHelper.warn(getClass(),"checkOwner",req.getPath(),req.getUserId());
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
}
