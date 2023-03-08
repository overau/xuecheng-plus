package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * 认证接口
 * @author HeJin
 * @version 1.0
 * @since 2023/03/08 15:11
 */
public interface AuthService {

    /**
     * 认证方法
     * @param authParamsDto 认证信息
     * @return 用户扩展信息: 用户基本信息和权限信息
     */
    XcUserExt execute(AuthParamsDto authParamsDto);

}