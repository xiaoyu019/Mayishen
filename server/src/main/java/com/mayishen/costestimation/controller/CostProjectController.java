package com.mayishen.costestimation.controller;

import com.mayishen.common.Constants;
import com.mayishen.common.Result;
import com.mayishen.core.service.AsyncService;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.FileUtils;
import com.mayishen.utils.TokenUtils;
import com.mayishen.costestimation.dto.CostProjectDTO;
import com.mayishen.costestimation.service.CostProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/cost-project")
public class CostProjectController {
    @Autowired
    private CostProjectService costProjectService;
    @Autowired
    private AsyncService asyncService;

    /***
     * 获取成本分析项目信息
     * @param id 成本分析项目
     * @return 项目信息
     */
    @GetMapping("/get/{id}")
    public Result<?> selectOne(@PathVariable Integer id) {
        return Result.success(costProjectService.selectById(id));
    }

    /***
     * 根据用户ID获取成本分析项目
     * @param id 用户ID
     * @return 成本分析项目列表
     */
    @GetMapping("/getByUserID/{id}")
    public Result<?> selectByUserID(@PathVariable Integer id) {
        return Result.success(costProjectService.selectByUserID(id));
    }

    /***
     * 重启成本分析任务
     * @param id 成本分析项目ID
     * @return 结果
     */
    @PostMapping("/restartTask/{id}")
    public Result<?> restartTask(@PathVariable Integer id) {
        CostProjectDTO dto = costProjectService.selectById(id);
        if (dto.getUserID() != TokenUtils.getUserId()) {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
        asyncService.analyzeFunctionPoint(dto);
        return Result.success();
    }

    /***
     * 分页检索成本分析项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 名称的模糊搜索词
     * @param affiliationSearch 机构名称的模糊搜索词
     * @param status 状态
     * @return 分页结果
     */
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "") String nameSearch, @RequestParam(defaultValue = "") String affiliationSearch, @RequestParam(defaultValue = "0") Integer status) {
        return Result.success(costProjectService.selectPage(pageNum, pageSize, nameSearch, affiliationSearch, status));
    }

    /***
     * 新增成本分析项目
     * @param costProjectDTO 成本分析项目信息
     * @return 结果
     * @throws IOException
     */
    @PostMapping
    public Result<?> save(@RequestBody CostProjectDTO costProjectDTO) throws IOException {
        costProjectDTO.setUserID(TokenUtils.getUserId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        costProjectDTO.setCreateDate(dateFormat.format(new Date()));
        costProjectDTO.setTotalNonlabourCost(new BigDecimal(0));
        costProjectDTO.setTotalLabourCost(new BigDecimal(0));
        costProjectDTO.setTotalCost(new BigDecimal(0));
        costProjectService.insert(costProjectDTO);
        FileUtils.moveDocument(costProjectDTO);
        asyncService.analyzeFunctionPoint(costProjectDTO);
        return Result.success(costProjectDTO.getId());
    }

    /***
     * 更新成本分析项目
     * @param costProjectDTO 成本分析项目信息
     * @return 结果
     */
    @PutMapping
    public Result<?> update(@RequestBody CostProjectDTO costProjectDTO) {
        costProjectService.updateById(costProjectDTO);
        return Result.success();
    }

    /***
     * 删除成本分析项目
     * @param id 成本分析项目ID
     * @return 结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> update(@PathVariable Integer id) {
        costProjectService.deleteById(id);
        return Result.success();
    }

    /***
     * 按照日期统计成本分析项目数量
     * @return 统计结果
     */
    @GetMapping("/countByDay")
    public Result<?> countByDay() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.countByDay(0));
        } else {
            return Result.success(costProjectService.countByDay(TokenUtils.getUserId()));
        }
    }

    /***
     * 按照查询词统计成本分析项目数量
     * @param query 查询词
     * @return 统计结果
     */
    @GetMapping("/countByQuery/{query}")
    public Result<?> countByQuery(@PathVariable String query) {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.countByQuery(0, query));
        } else {
            return Result.success(costProjectService.countByQuery(TokenUtils.getUserId(), query));
        }
    }

    /***
     * 统计成本分析项目数量
     * @return 成本分析项目数量
     */
    @GetMapping("/count")
    public Result<?> count() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.count(0));
        } else {
            return Result.success(costProjectService.count(TokenUtils.getUserId()));
        }
    }

    /***
     * 按照状态统计成本分析项目数量
     * @param status 状态
     * @return 成本分析项目数量
     */
    @GetMapping("/countByStatus/{status}")
    public Result<?> countByStatus(@PathVariable Integer status) {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.count(0, status));
        } else {
            return Result.success(costProjectService.count(TokenUtils.getUserId(), status));
        }
    }

    /***
     * 统计总的送审金额
     * @return 总的送审金额
     */
    @GetMapping("/getTotalAmount")
    public Result<?> getTotalAmount() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.getTotalAmount(0));
        } else {
            return Result.success(costProjectService.getTotalAmount(TokenUtils.getUserId()));
        }
    }

    /***
     * 统计本月项目数量
     * @return 本月项目数量
     */
    @GetMapping("/thisMonthCount")
    public Result<?> thisMonthCount() {
        if (TokenUtils.getUser().getRole() == 0) {
            return Result.success(costProjectService.thisMonthCount(0));
        } else {
            return Result.success(costProjectService.thisMonthCount(TokenUtils.getUserId()));
        }
    }
}
