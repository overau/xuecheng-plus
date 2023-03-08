package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.ucenter.constant.AuthConstants;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/03/08 13:23
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String loginJson) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(loginJson, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", loginJson);
            throw new RuntimeException("认证请求数据格式不对");
        }
        String authType = authParamsDto.getAuthType();
        // 根据认证类型(账号密码、验证码、微信登录)获取到具体执行认证逻辑的bean
        AuthService authService = applicationContext.getBean(
                authType + AuthConstants.AUTH_BEAN_SUFFIX, AuthService.class);
        // 执行认证
        XcUserExt user = authService.execute(authParamsDto);
        // 返回UserDetails
        return this.getUserPrincipal(user);
    }

    /**
     * 获取UserDetails对象
     * @param user XcUserExt
     * @return UserDetails
     */
    private UserDetails getUserPrincipal(XcUserExt user){
        user.setPassword(null);
        String userJson = JSON.toJSONString(user);
        return User.withUsername(userJson)
                .password("")
                .authorities("test").build();
    }

}
