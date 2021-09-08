package com.wistron.springboot.springbootlearn.domain.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author K21064736
 * @Description TODO
 * @Date 11:29 2021/8/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareDTO {


    /**
     * id
     */

    private Integer id;

    /**
     * 发布人id
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面
     */
    private String cover;

    /**
     * 概要信息
     */
    private String summary;

    /**
     * 价格（所需积分）
     */
    private Integer price;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 下载数
     */
    private Integer buyCount;

    /**
     * 是否显示 0:否 1:是
     */
    private Boolean showFlag;

    /**
     * 审核状况 NOT_YES  PASSED
     */
    private String auditStatus;

    /**
     * 审核不通过原因
     */
    private String reason;

    private String wxNickname;
}
