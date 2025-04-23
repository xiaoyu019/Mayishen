package com.mayishen.codeanalyzer.controller;

import com.mayishen.codeanalyzer.service.OpenSourceLibraryService;
import com.mayishen.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open-source-library")
public class OpenSourceLibraryController {
    @Autowired
    private OpenSourceLibraryService openSourceLibraryService;

    /***
     * 分页检索依赖库
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 项目ID
     * @return 分页结果
     */
    @GetMapping("/getByProjectID")
    public Result<?> selectByProjectID(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer id) {
        return Result.success(openSourceLibraryService.selectPageByProjectID(pageNum, pageSize, id));
    }

    /***
     * 根据漏洞等级统计漏洞数量
     * @param id 项目ID
     * @return 漏洞数量
     */
    @GetMapping("/countBugByLevel/{id}")
    public Result<?> countBugByLevel(@PathVariable Integer id) {
        return Result.success(openSourceLibraryService.countBugByLevel(id));
    }
}
