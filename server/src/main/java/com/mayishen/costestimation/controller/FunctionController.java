package com.mayishen.costestimation.controller;

import com.mayishen.common.Result;
import com.mayishen.costestimation.dto.FunctionDTO;
import com.mayishen.costestimation.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/function")
public class FunctionController {
    @Autowired
    private FunctionService functionService;

    /***
     * 根据项目Id获取功能点列表
     * @param id 项目ID
     * @return 功能点列表
     */
    @GetMapping("/getByProjectID/{id}")
    public Result<?> selectByProjectID(@PathVariable Integer id) {
        return Result.success(functionService.selectByProjectID(id));
    }

    /***
     * 根据功能点ID获取功能点内容
     * @param id 功能点ID
     * @return 功能点内容
     */
    @GetMapping("/getFunctionContentById/{id}")
    public Result<?> getFunctionContentById(@PathVariable Integer id) {
        return Result.success(functionService.getFunctionContentById(id));
    }

    /***
     * 新增功能点
     * @param functionDTO 功能点信息
     * @return 结果
     */
    @PostMapping
    public Result<?> save(@RequestBody FunctionDTO functionDTO) {
        functionService.insert(functionDTO);
        return Result.success();
    }

    /***
     * 更新功能点
     * @param functionDTO 功能点信息
     * @return 结果
     */
    @PutMapping
    public Result<?> update(@RequestBody FunctionDTO functionDTO) {
        functionService.updateById(functionDTO);
        return Result.success();
    }

    /***
     * 删除动能点
     * @param id 功能点ID
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Integer id) {
        functionService.deleteById(id);
        return Result.success();
    }

}
