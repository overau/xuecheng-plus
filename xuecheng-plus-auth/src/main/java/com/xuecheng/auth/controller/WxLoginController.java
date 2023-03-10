package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.impl.WxAuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 微信登录接口
 *
 * @author HeJin
 * @version 1.0
 * @since 2023/03/10 17:08
 */
@Controller
public class WxLoginController {

    private static final Logger log = LoggerFactory.getLogger(WxLoginController.class);

    @Resource
    WxAuthServiceImpl wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) {
        log.debug("微信扫码回调,code:{},state:{}", code, state);
        // TODO 远程调用微信申请令牌、拿到令牌查询用户信息、用户信息写入项目数据库

        XcUser xcUser = new XcUser();
        xcUser.setUsername("t1");
        if (xcUser == null) {
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.51xuecheng.cn/sign.html?username=" + username + "&authType=wx";
    }

}
