package com.mayishen.codeanalyzer.controller;

import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.service.CodeProjectService;
import com.mayishen.common.Constants;
import com.mayishen.common.Result;
import com.mayishen.core.service.AsyncService;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.FileUtils;
import com.mayishen.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/code-project")
public class CodeProjectController {
    @Autowired
    private CodeProjectService codeProjectService;
    @Autowired
    private AsyncService asyncService;

    /***
     * 获取项目信息
     * @param id 项目ID
     * @return 项目信息
     */
    @GetMapping("/get/{id}")
    public Result<?> selectOne(@PathVariable Integer id) {
        return Result.success(codeProjectService.selectById(id));
    }

    /***
     * 根据用户Id获取项目列表
     * @param id 用户ID
     * @return 项目信息
     */
    @GetMapping("/getByUserID/{id}")
    public Result<?> selectByMemberID(@PathVariable Integer id) {
        return Result.success(codeProjectService.selectByUserID(id));
    }

    /***
     * 重启源码分析任务
     * @param id 项目ID
     * @return 结果
     */
    @PostMapping("/restartTask/{id}")
    public Result<?> restartTask(@PathVariable Integer id) {
        CodeProjectDTO dto = codeProjectService.selectById(id);
        System.out.println(dto.getUserID());
        if (dto.getUserID() != TokenUtils.getUserId()) {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
        asyncService.analyzeSourceCode(dto);
        return Result.success();
    }

    /***
     * 分页检索源码分析项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 名称模糊搜索词
     * @param affiliationSearch 机构模糊搜索词
     * @return 分页结果
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "") String nameSearch, @RequestParam(defaultValue = "") String affiliationSearch) {
        return Result.success(codeProjectService.selectPage(pageNum, pageSize, nameSearch, affiliationSearch));
    }

    /***
     * 保存项目
     * @param codeProjectDTO 项目信息
     * @return 项目ID
     * @throws IOException
     */
    @PostMapping
    public Result<?> save(@RequestBody CodeProjectDTO codeProjectDTO) throws IOException {
        codeProjectDTO.setUserID(TokenUtils.getUserId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        codeProjectDTO.setCreateDate(dateFormat.format(new Date()));
        codeProjectService.insert(codeProjectDTO);
        FileUtils.moveSourceCode(codeProjectDTO);
        asyncService.analyzeSourceCode(codeProjectDTO);
        return Result.success(codeProjectDTO.getId());
    }

    /***
     * 更新项目信息
     * @param codeProjectDTO 项目信息
     * @return 结果
     */
    @PutMapping
    public Result<?> update(@RequestBody CodeProjectDTO codeProjectDTO) {
        codeProjectService.updateById(codeProjectDTO);
        return Result.success();
    }

    /***
     * 删除项目
     * @param id 项目ID
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Integer id) {
        codeProjectService.deleteById(id);
        return Result.success();
    }

    /***
     * 按照日期统计数量
     * @return 统计结果
     */
    @GetMapping("/countByDay")
    public Result<?> countByDay() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.countByDay(0));
        } else {
            return Result.success(codeProjectService.countByDay(TokenUtils.getUserId()));
        }
    }

    /***
     * 按照查询词统计数量
     * @param query 查询词
     * @return 统计结果
     */
    @GetMapping("/countByQuery/{query}")
    public Result<?> countByQuery(@PathVariable String query) {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.countByQuery(0, query));
        } else {
            return Result.success(codeProjectService.countByQuery(TokenUtils.getUserId(), query));
        }
    }

    /***
     * 按照文件类型统计项目文件数量
     * @param id 项目ID
     * @return 统计结果
     */
    @GetMapping("/countFileByType/{id}")
    public Result<?> countFileByType(@PathVariable Integer id) {
        return Result.success(codeProjectService.countFileByType(id));
    }

    /***
     * 按照文件类型统计项目相似文件数量
     * @param id 项目ID
     * @return 统计结果
     */
    @GetMapping("/countCloneFileByType/{id}")
    public Result<?> countCloneFileByType(@PathVariable Integer id) {
        return Result.success(codeProjectService.countCloneFileByType(id));
    }

    /***
     * 统计用户项目数量
     * @return 项目数量
     */
    @GetMapping("/count")
    public Result<?> count() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.count(0));
        } else {
            return Result.success(codeProjectService.count(TokenUtils.getUserId()));
        }
    }

    /***
     * 获取总的有效代码文件数量
     * @return 总的有效代码文件数量
     */
    @GetMapping("/getTotalValidFileNumber")
    public Result<?> getTotalValidFileNumber() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.getTotalValidFileNumber(0));
        } else {
            return Result.success(codeProjectService.getTotalValidFileNumber(TokenUtils.getUserId()));
        }
    }

    /***
     * 获取总的文件代码行数
     * @return 总的文件代码行数
     */
    @GetMapping("/getTotalLineNumber")
    public Result<?> getTotalLineNumber() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.getTotalLineNumber(0));
        } else {
            return Result.success(codeProjectService.getTotalLineNumber(TokenUtils.getUserId()));
        }
    }


    /***
     * 统计本月项目数量
     * @return 本月项目数量
     */
    @GetMapping("/thisMonthCount")
    public Result<?> thisMonthCount() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(codeProjectService.thisMonthCount(0));
        } else {
            return Result.success(codeProjectService.thisMonthCount(TokenUtils.getUserId()));
        }
    }
}

