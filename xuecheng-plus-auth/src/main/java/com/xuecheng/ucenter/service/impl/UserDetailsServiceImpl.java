package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
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
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private XcUserMapper xcUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<XcUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(XcUser::getUsername, username);
        XcUser user = xcUserMapper.selectOne(queryWrapper);
        if (user == null){
            throw new UsernameNotFoundException("账号不存在!");
        }
        String password = user.getPassword();
        user.setPassword(null);
        String userJson = JSON.toJSONString(user);
        return User.withUsername(userJson)
                .password(password)
                .authorities("test").build();
    }
}
