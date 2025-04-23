package com.mayishen.codeanalyzer.controller;


import com.mayishen.codeanalyzer.dto.OpenSourceFileDTO;
import com.mayishen.codeanalyzer.dto.OpenSourceProjectDTO;
import com.mayishen.codeanalyzer.service.OpenSourceFileService;
import com.mayishen.codeanalyzer.service.OpenSourceProjectService;
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
@RequestMapping("/open-source-file")
public class OpenSourceFileController {
    @Autowired
    private OpenSourceFileService openSourceFileService;
    @Autowired
    private OpenSourceProjectService openSourceProjectService;

    /***
     * 根据源码分析项目ID分页检索开源文件
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 源码分析项目ID
     * @param type 文件类型
     * @return 分页结果
     */
    @GetMapping("/getByProjectID")
    public Result<?> selectOpenSourceFilePageByProjectID(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "0") Integer type) {
        return Result.success(openSourceFileService.selectPageByProjectID(pageNum, pageSize, id, type));
    }

    /***
     * 根据开源项目ID分页检索开源文件
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 开源项目ID
     * @param type 文件类型
     * @return 分页结果
     */
    @GetMapping("/getByOpenSourceProjectID")
    public Result<?> selectOpenSourceFilePageByOpenSourceProjectID(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "0") Integer type) {
        return Result.success(openSourceFileService.selectPageByOpenSourceProjectID(pageNum, pageSize, id, type));
    }


    /***
     * 获取开源代码文件
     * @param id 开源代码文件ID
     * @return 结果
     * @throws IOException
     */
    @GetMapping("/getCodeFile/{id}")
    public Result getCodeFile(@PathVariable Integer id) throws IOException {
        OpenSourceFileDTO codeFile = openSourceFileService.selectById(id);
        OpenSourceProjectDTO codeProject = openSourceProjectService.selectById(codeFile.getOpenSourceProjectID());
        String targetPath = FileUtils.getUploadDir() + "/files/open-source-codes/" + codeProject.getPath().substring(0, codeProject.getPath().lastIndexOf(".")) + "/" + codeFile.getPath();
        Map<String, Object> res = new HashMap<>();
        res.put("type", codeFile.getType());
        res.put("codes", Files.readAllLines(Paths.get(targetPath)));
        return Result.success(res);
    }

}
