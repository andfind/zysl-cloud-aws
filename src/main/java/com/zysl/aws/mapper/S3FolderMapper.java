package com.zysl.aws.mapper;

import com.zysl.aws.model.db.S3Folder;
import com.zysl.aws.model.db.S3FolderCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface S3FolderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int countByExample(S3FolderCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int deleteByExample(S3FolderCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int insert(S3Folder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int insertSelective(S3Folder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    List<S3Folder> selectByExample(S3FolderCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    S3Folder selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int updateByExampleSelective(@Param("record") S3Folder record, @Param("example") S3FolderCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int updateByExample(@Param("record") S3Folder record, @Param("example") S3FolderCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int updateByPrimaryKeySelective(S3Folder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    int updateByPrimaryKey(S3Folder record);
}