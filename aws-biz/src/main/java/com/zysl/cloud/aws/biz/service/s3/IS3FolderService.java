package com.zysl.cloud.aws.biz.service.s3;

import com.zysl.cloud.aws.biz.service.IFolderService;
import com.zysl.cloud.aws.domain.bo.S3ObjectBO;
import com.zysl.cloud.utils.common.MyPage;

public interface IS3FolderService<T> extends IFolderService<T> {

    /**
     * 查询最新对象的版本号
     * @param t
     * @return
     */
    String getLastVersion(T t);

    /**
     * 分页查询
     * 非根目录
     * @description
     * @author miaomingming
     * @date 10:04 2020/4/7
     * @param t
     * @param myPage
     * @return com.zysl.cloud.aws.domain.bo.S3ObjectBO
     **/
    S3ObjectBO list(T t, MyPage myPage);
}
