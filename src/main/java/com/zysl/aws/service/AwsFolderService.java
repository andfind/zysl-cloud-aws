package com.zysl.aws.service;

import com.zysl.aws.model.*;
import software.amazon.awssdk.services.s3.model.CopyObjectResponse;

import java.util.List;

public interface AwsFolderService {

    /**
     * 创建目录folder
     */
    boolean createFolder(CreateFolderRequest request);

    /**
     * 删除目录（文件夹）
     */
    boolean deleteFolder(DelObjectRequest request);

    /**
     * 调用s3接口查询文件夹下的对象
     * @param request
     * @return
     */
    List<FileInfo> getS3FileList(QueryObjectsRequest request);

    /**
     * 目录复制
     * @param request
     * @return
     */
    CopyObjectResponse copyFolder(CopyFileRequest request);
}