package com.zysl.cloud.aws.web.controller;

import com.zysl.cloud.aws.api.dto.WordToPDFDTO;
import com.zysl.cloud.aws.api.req.WordToPDFRequest;
import com.zysl.cloud.aws.api.srv.WordSrv;
import com.zysl.cloud.aws.biz.enums.ErrCodeEnum;
import com.zysl.cloud.aws.biz.service.IFileService;
import com.zysl.cloud.aws.biz.service.IPDFService;
import com.zysl.cloud.aws.biz.service.IWordService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.utils.BizUtil;
import com.zysl.cloud.aws.web.validator.KeyRequestV;
import com.zysl.cloud.aws.web.validator.WordToPDFRequestV;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.AppLogicException;
import com.zysl.cloud.utils.common.BaseResponse;
import com.zysl.cloud.utils.service.provider.ServiceProvider;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class WordController extends BaseController implements WordSrv {

	@Autowired
	private IWordService wordService;
	@Autowired
	private IPDFService pdfService;
	@Resource(name="s3FileService")
	private IFileService fileService;


	@Override
	public BaseResponse<WordToPDFDTO> changeWordToPdf(@RequestBody WordToPDFRequest request){
		return ServiceProvider.call(request, WordToPDFRequestV.class,WordToPDFDTO.class,req->{
			//step 1.读取源文件--
			//调用s3接口下载文件内容
			S3ObjectBO queryBO = new S3ObjectBO();
			queryBO.setBucketName(request.getBucketName());
			setPathAndFileName(queryBO,request.getFileName());
			queryBO.setVersionId(request.getVersionId());
			S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getInfoAndBody(queryBO);

			if(s3ObjectBO == null || s3ObjectBO.getBodys() == null || s3ObjectBO.getBodys().length == 0){
				log.info("===未查询到数据文件:{}===",request.getFileName());
				throw new AppLogicException(ErrCodeEnum.WORD_FILE_NOT_EXIST.getCode());
			}

			//step 2.word转pdf、加水印 300,300
			String fileName = BizUtil.getTmpFileNameWithoutSuffix(request.getFileName());
			byte[] outBuff = wordService.changeWordToPDF(s3ObjectBO.getBodys(),false, request.getTextMark());
			log.info("===changeToPDF===outBuff,fileName:{},outBuff.length:{}", request.getFileName(),outBuff == null ? 0 : outBuff.length);
			if(outBuff == null || outBuff.length == 0){
				throw new AppLogicException(ErrCodeEnum.WORD_FILE_TO_PDF_SIZE_ZERO.getCode());
			}

			//step 3.实现加密
			if(!StringUtils.isBlank(request.getUserPwd()) && !StringUtils.isBlank(request.getOwnerPwd())){
				byte[] addPwdOutBuff = pdfService.addPwd(outBuff,request.getUserPwd(),request.getOwnerPwd());
				log.info("===addPwd===file add pwd success.fileName:{},addPwdOutBuff.length:{}",request.getFileName(),addPwdOutBuff == null ? 0 : addPwdOutBuff.length);
				if(addPwdOutBuff == null || addPwdOutBuff.length == 0){
					throw new AppLogicException(ErrCodeEnum.WORD_FILE_TO_PDF_ENCRYPTION_SIZE_ZERO.getCode());
				}
				outBuff = addPwdOutBuff;
			}

			//step 4.上传到temp-001
			S3ObjectBO addRequestBO = new S3ObjectBO();
			addRequestBO.setBucketName(request.getBucketName());
			addRequestBO.setPath("");
			addRequestBO.setFileName(fileName + ".pdf");
			addRequestBO.setBodys(outBuff);

			S3ObjectBO addFileRst = (S3ObjectBO)fileService.create(addRequestBO);

			WordToPDFDTO dto = new WordToPDFDTO();
			//step 7.设置返回参数
			dto.setBucketName(request.getBucketName());
			dto.setFileName(fileName + ".pdf");
			dto.setVersionId(addFileRst.getVersionId());

			return dto;
		});
	}
}
