package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * freemarker模板controller
 * @author HeJin
 * @version 1.0
 * @since 2023/02/28 10:51
 */
@Controller
public class FreeMarkerController {

    @GetMapping("/test")
    public ModelAndView test() {
        String name = "张三";
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        modelAndView.addObject("name", name);
        return modelAndView;
    }
}
