package com.zysl.cloud.aws.api.srv;

import com.zysl.cloud.aws.api.dto.FolderDTO;
import com.zysl.cloud.aws.api.dto.ObjectInfoDTO;
import com.zysl.cloud.aws.api.req.*;
import com.zysl.cloud.utils.common.BasePaginationResponse;
import com.zysl.cloud.utils.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 目录处理类
 */
@RequestMapping("/aws/folder")
public interface FolderSrv {

    /**
     * 创建目标
     * @param request
     * @return
     */
    @PostMapping("/add")
    BaseResponse<String> createFolder(@RequestBody CreateFolderRequest request);

    /**
     * 删除目录
     * @param request
     * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
     **/
    @PostMapping("/delete")
    BaseResponse<String> deleteFolder(@RequestBody DelObjectRequest request);

    /**
     * 查询目录对象列表
     * @param request
     * @return com.zysl.cloud.utils.common.BasePaginationResponse<com.zysl.cloud.aws.api.dto.ObjectInfoDTO>
     **/
    @PostMapping("/objects")
    BasePaginationResponse<ObjectInfoDTO> getS3Objects(@RequestBody QueryObjectsRequest request);

    /**
     * 目录复制
     * @param request
     * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
     **/
    @PostMapping("/copy")
    BaseResponse<String> copyFolder(@RequestBody CopyObjectsRequest request);

    /**
     * 目录移动
     * @param request
     * @return com.zysl.cloud.utils.common.BaseResponse<java.lang.String>
     **/
    @PostMapping("/move")
    BaseResponse<String> moveFolder(@RequestBody CopyObjectsRequest request);

    /**
     * 目录重命名
     * @param request
     * @return com.zysl.cloud.utils.common.BaseResponse<com.zysl.cloud.aws.api.dto.FolderDTO>
     **/
    @PostMapping("/rename")
    BaseResponse<FolderDTO> folderRename(@RequestBody ObjectRenameRequest request);

}
