package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.constant.AuthConstants;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/03/08 15:13
 */
@Service(AuthConstants.PASSWORD + AuthConstants.AUTH_BEAN_SUFFIX)
public class PasswordAuthServiceImpl implements AuthService {

    @Resource
    private XcUserMapper xcUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 认证方法
     *
     * @param authParamsDto 认证信息
     * @return 用户扩展信息: 用户基本信息和权限信息
     */
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 查询数据库
        String username = authParamsDto.getUsername();
        LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcUser::getUsername, username);
        XcUser user = xcUserMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("账号不存在!");
        }
        // 比对密码
        String rawPassword = authParamsDto.getPassword();
        String passwordDb = user.getPassword();
        if (!passwordEncoder.matches(rawPassword, passwordDb)) {
            throw new RuntimeException("账号或者密码错误!");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);
        return xcUserExt;
    }
}
