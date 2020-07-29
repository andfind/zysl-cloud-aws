package com.zysl.cloud.aws.biz.service.s3.impl;

import com.google.common.collect.Lists;
import com.zysl.cloud.aws.api.enums.DeleteStoreEnum;
import com.zysl.cloud.aws.biz.constant.BizConstants;
import com.zysl.cloud.aws.biz.constant.S3Method;
import com.zysl.cloud.aws.biz.service.s3.IS3FactoryService;
import com.zysl.cloud.aws.biz.service.s3.IS3FileService;
import com.zysl.cloud.aws.biz.service.s3.IS3FolderService;
import com.zysl.cloud.aws.biz.utils.S3Utils;
import com.zysl.cloud.aws.config.LogConfig;
import com.zysl.cloud.aws.domain.bo.ObjectInfoBO;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.aws.domain.bo.TagBO;
import com.zysl.cloud.utils.LogHelper;
import com.zysl.cloud.utils.StringUtils;
import com.zysl.cloud.utils.common.MyPage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tagging;

@Slf4j
@Service("s3FolderService")
public class S3FolderServiceImpl implements IS3FolderService<S3ObjectBO> {

	@Autowired
	private IS3FactoryService s3FactoryService;
	@Autowired
	private IS3FileService fileService;
	@Autowired
	private LogConfig logConfig;


	@Override
	public S3ObjectBO create(S3ObjectBO t){
		LogHelper.info(getClass(),"createFolder.param",t.bucketKey(),t.toString());
		
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName(),Boolean.TRUE);

		PutObjectRequest.Builder request = PutObjectRequest.builder()
												.bucket(t.getBucketName())
												.key(t.getPath());
		
		//设置标签信息
		Tagging tagging = S3Utils.creatTagging(t.getTagList());
		if(null != tagging){
			request.tagging(tagging);
		}

		RequestBody requestBody = RequestBody.empty();
		PutObjectResponse response = s3FactoryService.callS3MethodWithBody(request.build(), requestBody, s3, S3Method.PUT_OBJECT);
		LogHelper.info(getClass(),"createFolder.response",t.bucketKey(),response);
		
		t.setVersionId(this.getLastVersion(t));

		return t;
	}


	@Override
	public void delete(S3ObjectBO t){
		LogHelper.info(getClass(),"deleteFolder.response",t.bucketKey(),t.toString());

		//查询目录下的文件信息
		S3ObjectBO s3ObjectBO = getDetailInfo(t);
		//删除子目录及文件
		if(delFileList(t.getBucketName(), s3ObjectBO.getFolderList())
			&& delSubFolderList(t.getBucketName(), s3ObjectBO.getFileList())){
			//删除当前目录
			t.setDeleteStore(DeleteStoreEnum.COVER.getCode());
			fileService.delete(t);
		}
	}

	//删除文件
	private boolean delFileList(String bucket, List<ObjectInfoBO> fileList){
		//删除文件信息
		if(!CollectionUtils.isEmpty(fileList)){
			fileList.forEach(obj -> {
				deleteFile(bucket,obj.getKey());
			});
		}
		return true;
	}
	//删除子目录及其文件
	private boolean delSubFolderList(String bucket, List<ObjectInfoBO> folderList){
		if(!CollectionUtils.isEmpty(folderList)){
			S3ObjectBO t = new S3ObjectBO();
			for (ObjectInfoBO object : folderList) {
				t.setBucketName(bucket);
				setPathAndFileName(t, object.getKey());
				S3ObjectBO s3ObjectBO = getDetailInfo(t);
				//删除文件信息
				delFileList(bucket,s3ObjectBO.getFileList());
				
				//子文件夹
				List<ObjectInfoBO> folders = s3ObjectBO.getFolderList();
				if(!CollectionUtils.isEmpty(folders)){
					delSubFolderList(bucket, folders);
				}else{
					//删除当前目录
					s3ObjectBO.setDeleteStore(DeleteStoreEnum.COVER.getCode());
					fileService.delete(s3ObjectBO);
				}
			}
		}
		
		return true;
	}

	@Override
	public void modify(S3ObjectBO t){

	}

	@Override
	public void rename(S3ObjectBO src,S3ObjectBO dest){

	}

	@Override
	public boolean copy(S3ObjectBO src,S3ObjectBO dest){
		LogHelper.info(getClass(),"copyFolder.param",src.bucketKey(),dest.bucketKey());
		
		//在查询顶层目录下的对象信息
		S3ObjectBO detailInfo = getDetailInfo(src);
		
		/**
		 * 同时复制目录下的所有对象
		 * 判断两个bucket是否在同一台服务器，
		 * 不在一台服务器则下载上传，在则用原生复制接口
		 */
		if(s3FactoryService.judgeBucket(src.getBucketName(), dest.getBucketName())){
			LogHelper.info(getClass(),"copyFolder",src.bucketKey(),"same.bucket");
			//先复制顶层目录
			fileService.copy(src, dest);

			return copyObject(detailInfo, src, dest);
		}else{
			LogHelper.info(getClass(),"copyFolder",src.getPath(),"not.same.bucket");
			//上传顶层目录
			String destKey = getDestKey(src.getPath(), dest.getPath());
			S3ObjectBO destBO = createS3ObjectBO(dest.getBucketName(),destKey);
			//设置标签
			destBO.setTagList(fileService.addTags(src, Lists.newArrayList()));
			this.create(destBO);
			
			return uploadObject(detailInfo, src, dest);
		}
	}

	/**
	 * 复制子文件及子目录
	 * @description
	 * @author miaomingming
	 * @date 15:18 2020/6/10
	 * @param detailInfo
	 * @param src
	 * @param dest
	 * @return boolean
	 **/
	private boolean copyObject(S3ObjectBO detailInfo, S3ObjectBO src,S3ObjectBO dest){
		LogHelper.info(getClass(),"copyObject.param",src.bucketKey(),dest.bucketKey());
		//子文件
		List<ObjectInfoBO> fileList = detailInfo.getFileList();
		//文件直接复制
		if(!CollectionUtils.isEmpty(fileList)){
			fileList.forEach(file ->{
				S3ObjectBO srcBO = createS3ObjectBO(src.getBucketName(),file.getKey());
				S3ObjectBO destBO = createS3ObjectBO(dest.getBucketName(),getDestKey(file.getKey(), dest.getPath()));
				fileService.copy(srcBO, destBO);
			});
		}

		//子目录
		List<ObjectInfoBO> folderList = detailInfo.getFolderList();
		if(!CollectionUtils.isEmpty(folderList)){
			folderList.forEach(folder -> {
				//先复制
				S3ObjectBO srcBO = createS3ObjectBO(src.getBucketName(),folder.getKey());
				S3ObjectBO destBO = createS3ObjectBO(dest.getBucketName(),getDestKey(folder.getKey(), dest.getPath()));
				
				fileService.copy(srcBO, destBO);

				//查询
				S3ObjectBO objectDetail = getDetailInfo(srcBO);

				copyObject(objectDetail, src, dest);
			});
		}

		return true;
	}

	/**
	 * 上传子目录及文件
	 * @description
	 * @author miaomingming
	 * @date 15:38 2020/6/10
	 * @param detailInfo
	 * @param src
	 * @param dest
	 * @return boolean
	 **/
	public boolean uploadObject(S3ObjectBO detailInfo, S3ObjectBO src,S3ObjectBO dest){
		LogHelper.info(getClass(),"uploadObject.param",src.bucketKey(),dest.bucketKey());
		//子文件
		List<ObjectInfoBO> fileList = detailInfo.getFileList();
		//文件直接上传
		if(!CollectionUtils.isEmpty(fileList)){
			fileList.forEach(file ->{
				S3ObjectBO srcBO = createS3ObjectBO(src.getBucketName(),file.getKey());
				S3ObjectBO s3ObjectBO = (S3ObjectBO)fileService.getInfoAndBody(srcBO);
				
				S3ObjectBO destBO = createS3ObjectBO(dest.getBucketName(),getDestKey(file.getKey(), dest.getPath()));
				destBO.setBodys(s3ObjectBO.getBodys());
				
				//设置标签信息
				destBO.setTagList(fileService.mergeTags(s3ObjectBO.getTagList(), Lists.newArrayList()));

				fileService.create(destBO);
			});
		}


		//子目录
		List<ObjectInfoBO> folderList = detailInfo.getFolderList();
		if(!CollectionUtils.isEmpty(folderList)){
			folderList.forEach(folder -> {
				S3ObjectBO srcBO = createS3ObjectBO(src.getBucketName(),folder.getKey());
				S3ObjectBO destBO = createS3ObjectBO(dest.getBucketName(),getDestKey(folder.getKey(), dest.getPath()));
				
				//设置标签
				destBO.setTagList(fileService.addTags(srcBO, Lists.newArrayList()));
				this.create(destBO);

				//查询
				S3ObjectBO objects = getDetailInfo(srcBO);

				uploadObject(objects, src, dest);
			});
		}

		return true;
	}

	

	@Override
	public void move(S3ObjectBO src,S3ObjectBO dest){
		//先复制
		copy(src, dest);
		//删除
		delete(src);
	}
	@Override
	public S3ObjectBO getBaseInfo(S3ObjectBO t){
		return null;
	}

	@Override
	public S3ObjectBO getDetailInfo(S3ObjectBO t){
		LogHelper.info(getClass(),"getDetailInfoFolder.param",t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());

		//获取查询对象列表入参
		ListObjectsRequest request = null;
		if(StringUtils.isEmpty(t.getPath())){
			request = ListObjectsRequest.builder()
					.bucket(t.getBucketName())
					.delimiter(BizConstants.PATH_SEPARATOR)
					.build();
		}else{
			request = ListObjectsRequest.builder()
					.bucket(t.getBucketName())
					.prefix(t.getPath())
					.delimiter(BizConstants.PATH_SEPARATOR)
					.build();
		}
		//查询目录下的对象信息
		ListObjectsResponse response = s3FactoryService.callS3Method(request,s3, S3Method.LIST_OBJECTS);
		LogHelper.info(getClass(),"getDetailInfoFolder.response",t.bucketKey(),response);
		//目录及文件列表
		setFileListAndFolderList(t,response.commonPrefixes(),response.contents());

		//查询目录的标签信息
		List<TagBO> tagList = fileService.getTags(t);
		t.setTagList(tagList);
		
		LogHelper.info(getClass(),"getDetailInfoFolder.rst",t.bucketKey(),t.toString());
		return t;
	}


	@Override
	public List<S3ObjectBO> getVersions(S3ObjectBO t){
		LogHelper.info(getClass(),"getVersions.param",t.bucketKey(),t.toString());
		return fileService.getVersions(t);
	}

	@Override
	public S3ObjectBO rename(S3ObjectBO t) {
		LogHelper.info(getClass(),"rename.param",t.bucketKey(),t.toString());
		//重新上传目录，同时修改标签
		S3ObjectBO s3ObjectBO = this.create(t);
		s3ObjectBO.setTagFilename(t.getTagFilename());
		return s3ObjectBO;
	}


	

	@Override
	public String getLastVersion(S3ObjectBO t) {
		LogHelper.info(getClass(),"getLastVersion.param",t.bucketKey(),t.toString());
		
		return fileService.getLastVersion(t);
	}
	
	@Override
	public S3ObjectBO list(S3ObjectBO t, MyPage myPage){
		LogHelper.info(getClass(),"listFolder.param",t.bucketKey(),t.toString());
		//获取s3初始化对象
		S3Client s3 = s3FactoryService.getS3ClientByBucket(t.getBucketName());
		
		//查询结果
		List<CommonPrefix> commonPrefixes = new ArrayList<>();
		List<S3Object> contents = new ArrayList<>();
		//获取查询对象列表入参
		int totalRecords = 0,rspCount = 1;
		String nextMarker = null;
		ListObjectsResponse response = null;
		ListObjectsRequest.Builder request = ListObjectsRequest.builder()
											.bucket(t.getBucketName())
											.prefix(t.getPath())
											.delimiter(BizConstants.PATH_SEPARATOR);
		//查询目录下的对象信息
		while (response == null || response.isTruncated()){
			request.marker(nextMarker);
			response = s3FactoryService.callS3Method(request.build(),s3, S3Method.LIST_OBJECTS);
			LogHelper.info(getClass(),"listFolder.response",t.bucketKey(),response);
			
			nextMarker = response.nextMarker();
			if(!CollectionUtils.isEmpty(response.contents())){
				totalRecords += response.contents().size();
			}
			if(!CollectionUtils.isEmpty(response.commonPrefixes())){
				totalRecords += response.commonPrefixes().size();
			}
			//根据当前记录数及传入的页码+每页数据读取读取
			setFilesAndFolders(commonPrefixes,contents,response,myPage,rspCount++);
		}
		//目录及文件列表
		if(setFileListAndFolderList(t,commonPrefixes,contents)){
			totalRecords--;
		}
		myPage.setTotalRecords(totalRecords);
		
		return t;
	}
	
	public boolean setFileListAndFolderList(S3ObjectBO t,List<CommonPrefix> prefixes,List<S3Object> objectList){
		LogHelper.info(getClass(),"setFileListAndFolderList.param",t.bucketKey(),t.toString());
		//objectList是否存在本身路径对象
		boolean isExistLocalPath = Boolean.FALSE;
		List<ObjectInfoBO> folderList = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(prefixes)){
			prefixes.forEach(obj -> {
				ObjectInfoBO object = new ObjectInfoBO();
				object.setBucket(t.getBucketName());
				object.setKey(obj.prefix());
				folderList.add(object);
			});
		}
		
		//文件列表
		List<ObjectInfoBO> fileList = Lists.newArrayList();
		if(!CollectionUtils.isEmpty(objectList)){
			for(S3Object obj:objectList){
				if(obj.key().equals(StringUtils.join(t.getPath() ,t.getFileName()))){
					isExistLocalPath = Boolean.TRUE;
					continue;
				}
				ObjectInfoBO object = new ObjectInfoBO();
				object.setBucket(t.getBucketName());
				object.setKey(obj.key());
				object.setFileSize(obj.size());
				object.setUploadTime(obj.lastModified());
				fileList.add(object);
			}
		}
		
		t.setFolderList(folderList);
		t.setFileList(fileList);
		
		return isExistLocalPath;
	}
	/**
	 * 设置文件和目录的内容及数量，实现翻页效果
	 * @description
	 * @author miaomingming
	 * @date 10:44 2020/4/7
	 * @param commonPrefixes
	 * @param contents
	 * @param response
	 * @param myPage
	 * @return void
	 **/
	private void setFilesAndFolders(List<CommonPrefix> commonPrefixes,List<S3Object> contents,ListObjectsResponse response,MyPage myPage,Integer responseCount){
		int myPageStart = (myPage.getPageNo()-1) * myPage.getPageSize();
		int myPageEnd =  myPage.getPageNo() * myPage.getPageSize()-1;
		int needRecords = myPage.getPageSize() - commonPrefixes.size() - contents.size();
		
		int rspStartIndex = (responseCount - 1) * 1000;
		
		for(int i=0;i<response.commonPrefixes().size() && needRecords > 0;i++,rspStartIndex++){
			if(rspStartIndex >= myPageStart && rspStartIndex <= myPageEnd){
				commonPrefixes.add(response.commonPrefixes().get(i));
				needRecords--;
			}
			
		}
		
		for(int i=0;i<response.contents().size() && needRecords > 0 ;i++,rspStartIndex++){
			if(rspStartIndex >= myPageStart && rspStartIndex <= myPageEnd){
				contents.add(response.contents().get(i));
				needRecords--;
			}
		}
	}
	
	/**
	 * 删除文件
	 * @description
	 * @author miaomingming
	 * @date 15:14 2020/6/10
	 * @param bucket
	 * @param key
	 * @return void
	 **/
	private void deleteFile(String bucket,String key){
		S3ObjectBO s3ObjectBO = createS3ObjectBO(bucket,key);
		s3ObjectBO.setDeleteStore(DeleteStoreEnum.COVER.getCode());
		
		fileService.delete(s3ObjectBO);
	}
	/**
	 * 创建S3ObjectBO
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/6/10
	 * @param bucket
	 * @param key
	 * @return com.zysl.cloud.aws.domain.bo.S3ObjectBO
	 **/
	private S3ObjectBO createS3ObjectBO(String bucket,String key){
		S3ObjectBO s3ObjectBO = new S3ObjectBO();
		s3ObjectBO.setBucketName(bucket);
		setPathAndFileName(s3ObjectBO, key);
		return s3ObjectBO;
	}
	/**
	 * 设置路径和文件名
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/6/10
	 * @param s3ObjectBO
	 * @param s3Key
	 * @return void
	 **/
	private void setPathAndFileName(S3ObjectBO s3ObjectBO,String s3Key){
		if(StringUtils.isBlank(s3Key)){
			return;
		}
		if(s3ObjectBO == null){
			s3ObjectBO = new S3ObjectBO();
		}
		if(s3Key.startsWith(BizConstants.PATH_SEPARATOR)){
			s3Key = s3Key.substring(1);
		}
		if(s3Key.endsWith(BizConstants.PATH_SEPARATOR)){
			s3ObjectBO.setPath(s3Key);
			s3ObjectBO.setFileName("");
		}else{
			s3ObjectBO.setPath(s3Key.substring(0,s3Key.lastIndexOf(BizConstants.PATH_SEPARATOR)+1));
			s3ObjectBO.setFileName(s3Key.substring(s3Key.lastIndexOf(BizConstants.PATH_SEPARATOR)+1));
		}
	}
	/**
	 * 字符串替换TODO
	 * @description
	 * @author miaomingming
	 * @date 15:15 2020/6/10
	 * @param srcPath  格式: aa/bb/cc/
	 * @param destPath 格式: aa/bb/cc/
	 * @return java.lang.String
	 **/
	private String getDestKey(String srcPath, String destPath){
		//只能子目录复制，不能根目录复制
		if(StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(destPath)
			|| srcPath.indexOf(BizConstants.PATH_SEPARATOR) <= 0 ){
			return  null;
		}
		
		String str = srcPath.split(BizConstants.PATH_SEPARATOR)[0];
		String destStr = destPath + srcPath.substring(str.length() + 1);
		return destStr;
	}
}
