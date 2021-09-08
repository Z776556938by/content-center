package com.wistron.springboot.springbootlearn.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author K21064736
 * @Description TODO
 * @Date 11:25 2021/8/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer userId;

    private String wxId;

    private String wxNickname;

    private String roles;

    private String avatarUrl;

    private Date createTime;

    private Date updateTime;

    private Integer bonus;
}
