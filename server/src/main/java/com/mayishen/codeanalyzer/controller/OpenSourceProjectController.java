package com.mayishen.codeanalyzer.controller;

import com.mayishen.codeanalyzer.dto.OpenSourceProjectDTO;
import com.mayishen.codeanalyzer.service.OpenSourceProjectService;
import com.mayishen.common.Constants;
import com.mayishen.common.Result;
import com.mayishen.core.service.AsyncService;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/open-source-project")
public class OpenSourceProjectController {
    @Autowired
    private OpenSourceProjectService openSourceProjectService;
    @Autowired
    private AsyncService asyncService;

    /***
     * 根据ID获取开源项目信息
     * @param id 开源项目ID
     * @return 开源项目信息
     */
    @GetMapping("/get/{id}")
    public Result<?> selectOne(@PathVariable Integer id) {
        return Result.success(openSourceProjectService.selectById(id));
    }

    /***
     * 重启开源项目分析任务
     * @param id 开源项目ID
     * @return 结果
     */
    @PostMapping("/restartTask/{id}")
    public Result<?> restartTask(@PathVariable Integer id) {
        OpenSourceProjectDTO dto = openSourceProjectService.selectById(id);
        if (dto == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        asyncService.addOpenSourceCode(dto);
        return Result.success();
    }

    /***
     * 分页检索开源项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 名称模糊搜索词
     * @return 分页结果
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "") String nameSearch) {
        return Result.success(openSourceProjectService.selectPage(pageNum, pageSize, nameSearch));
    }

    /***
     * 分页检索开源代码文件
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 源码项目ID
     * @param license 开源许可证类型
     * @param order 排序方式
     * @return 分页结果
     */
    @GetMapping("/getByProjectID")
    public Result<?> selectOpenSourceFilePageByProjectID(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "0") Integer id, @RequestParam(defaultValue = "-1") Integer license, @RequestParam(defaultValue = "0") Integer order) {
        return Result.success(openSourceProjectService.selectPageByProjectID(pageNum, pageSize, id, license, order));
    }

    /***
     * 检查开源项目是否存在
     * @param name 开源项目名
     * @param version 开源项目版本号
     * @return 结果
     */
    @GetMapping("/check")
    public Result<?> check(@RequestParam(required = true) String name, @RequestParam(required = true) String version) {
        return Result.success(openSourceProjectService.check(name, version));
    }

    /***
     * 保存开源项目
     * @param openSourceProjectDTO 开源项目信息
     * @return 开源项目ID
     * @throws IOException
     */
    @PostMapping
    public Result<?> save(@RequestBody OpenSourceProjectDTO openSourceProjectDTO) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        openSourceProjectDTO.setCreateDate(dateFormat.format(new Date()));
        openSourceProjectService.insert(openSourceProjectDTO);
        FileUtils.moveSourceCode(openSourceProjectDTO);
        asyncService.addOpenSourceCode(openSourceProjectDTO);
        return Result.success(openSourceProjectDTO.getId());
    }

    /***
     * 删除开源项目
     * @param id 开源项目ID
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Integer id) {
        OpenSourceProjectDTO project = openSourceProjectService.selectById(id);
        String sourcePath = FileUtils.getUploadDir() + "/files/zips/" + project.getPath(), targetPath = FileUtils.getUploadDir() + "/files/codes/" + project.getPath().substring(0, project.getPath().lastIndexOf(".")) + "/";
        FileUtils.deleteFolder(sourcePath);
        FileUtils.deleteFolder(targetPath);
        openSourceProjectService.deleteById(id);
        return Result.success();
    }

}
