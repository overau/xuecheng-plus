package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/03/04 10:19
 */
@Api(value = "课程发布接口", tags = "课程发布接口")
@Controller
public class CoursePublishController {

    @Resource
    private CoursePublishService coursePublishService;

    /**
     * 课程预览
     * @param courseId 课程id
     * @return ModelAndView
     */
    @ApiOperation("课程信息预览接口")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@ApiParam("课程id") @PathVariable("courseId") Long courseId){
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    /**
     * 提交课程审核
     * @param courseId 课程id
     */
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId){
        Long companyId = 1232141425L;
        coursePublishService.commitAudit(companyId, courseId);
    }

}
