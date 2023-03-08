package com.xuecheng.checkcode.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha图片验证码配置类
 * @author 攀博课堂(www.pbteach.com)
 * @version 1.0
 **/
@Configuration
public class KaptchaConfig {

    //图片验证码生成器，使用开源的kaptcha
    @Bean
    public DefaultKaptcha producer() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 图片边框
        properties.setProperty("kaptcha.border", "yes");
        // 边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "red");
        // 验证码整体宽度
        properties.setProperty("kaptcha.image.width", "138");
        // 验证码整体高度
        properties.setProperty("kaptcha.image.height", "34");
        // 文字个数
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 文字大小
        properties.setProperty("kaptcha.textproducer.font.size","28");
        // 文字随机字体
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        // 文字距离
        properties.setProperty("kaptcha.textproducer.char.space","10");
        // 干扰线颜色
        properties.setProperty("kaptcha.noise.color","blue");
        // 自定义验证码背景
        Config config=new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
