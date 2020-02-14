package com.zysl.aws.model.db;

import java.util.Date;

public class S3Folder {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_folder.id
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_folder.service_no
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    private String serviceNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_folder.folder_name
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    private String folderName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column s3_folder.create_time
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    private Date createTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_folder.id
     *
     * @return the value of s3_folder.id
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_folder.id
     *
     * @param id the value for s3_folder.id
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_folder.service_no
     *
     * @return the value of s3_folder.service_no
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public String getServiceNo() {
        return serviceNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_folder.service_no
     *
     * @param serviceNo the value for s3_folder.service_no
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public void setServiceNo(String serviceNo) {
        this.serviceNo = serviceNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_folder.folder_name
     *
     * @return the value of s3_folder.folder_name
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_folder.folder_name
     *
     * @param folderName the value for s3_folder.folder_name
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column s3_folder.create_time
     *
     * @return the value of s3_folder.create_time
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column s3_folder.create_time
     *
     * @param createTime the value for s3_folder.create_time
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s3_folder
     *
     * @mbggenerated Fri Feb 14 10:44:02 CST 2020
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", serviceNo=").append(serviceNo);
        sb.append(", folderName=").append(folderName);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}