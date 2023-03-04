package com.xuecheng.content.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/03/04 10:19
 */
@Api(value = "课程发布接口", tags = "课程发布接口")
@Controller
public class CoursePublishController {

    /**
     * 课程预览
     * @param courseId 课程id
     * @return ModelAndView
     */
    @ApiOperation("课程信息预览接口")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(
            @ApiParam("课程id") @PathVariable("courseId") Long courseId){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", null);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

}
