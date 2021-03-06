package com.zysl.aws.mapper;

import com.zysl.aws.model.db.S3Service;
import com.zysl.aws.model.db.S3ServiceCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface S3ServiceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int countByExample(S3ServiceCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int deleteByExample(S3ServiceCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int insert(S3Service record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int insertSelective(S3Service record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    List<S3Service> selectByExample(S3ServiceCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    S3Service selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int updateByExampleSelective(@Param("record") S3Service record, @Param("example") S3ServiceCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int updateByExample(@Param("record") S3Service record, @Param("example") S3ServiceCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int updateByPrimaryKeySelective(S3Service record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_service
     *
     * @mbggenerated Fri Feb 14 12:02:46 CST 2020
     */
    int updateByPrimaryKey(S3Service record);
}