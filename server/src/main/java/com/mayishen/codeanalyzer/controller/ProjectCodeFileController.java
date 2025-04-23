package com.mayishen.codeanalyzer.controller;


import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.dto.ProjectCodeFileDTO;
import com.mayishen.codeanalyzer.service.CodeProjectService;
import com.mayishen.codeanalyzer.service.ProjectCodeFileService;
import com.mayishen.common.Result;
import com.mayishen.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/project-code-file")
public class ProjectCodeFileController {
    @Autowired
    private ProjectCodeFileService projectCodeFileService;
    @Autowired
    private CodeProjectService codeProjectService;

    /***
     * 分页检索项目源码文件
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 项目ID
     * @param type 文件类型
     * @param status 状态
     * @return 分页结果
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "-1") Integer id, @RequestParam(defaultValue = "-1") Integer type, @RequestParam(defaultValue = "0") Integer status) {
        return Result.success(projectCodeFileService.selectPage(pageNum, pageSize, id, type, status));
    }

    /***
     * 获取代码文件
     * @param id 代码文件ID
     * @return 结果
     * @throws IOException
     */
    @GetMapping("/getCodeFile/{id}")
    public Result getCodeFile(@PathVariable Integer id) throws IOException {
        ProjectCodeFileDTO codeFile = projectCodeFileService.selectById(id);
        CodeProjectDTO codeProject = codeProjectService.selectById(codeFile.getProjectId());
        String targetPath = FileUtils.getUploadDir() + "/files/codes/" + codeProject.getSourceCodePath().substring(0, codeProject.getSourceCodePath().lastIndexOf(".")) + "/" + codeFile.getPath();
        Map<String, Object> res = new HashMap<>();
        res.put("type", codeFile.getType());
        res.put("codes", Files.readAllLines(Paths.get(targetPath)));
        return Result.success(res);
    }
}
