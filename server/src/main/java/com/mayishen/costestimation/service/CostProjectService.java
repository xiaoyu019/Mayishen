package com.mayishen.costestimation.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.common.Constants;
import com.mayishen.core.dto.CountByDayDTO;
import com.mayishen.core.dto.CountByQueryDTO;
import com.mayishen.costestimation.mapper.CostProjectMapper;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.DTOUtils;
import com.mayishen.utils.FileUtils;
import com.mayishen.utils.TokenUtils;
import com.mayishen.costestimation.dto.CostProjectDTO;
import com.mayishen.costestimation.entity.CostProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CostProjectService {
    @Resource
    private CostProjectMapper costProjectMapper;
    @Autowired
    private FunctionService functionService;

    /***
     * 分页检索项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 项目名称的模糊搜索字段
     * @param affiliationSearch 送审机构名的模糊搜索字段
     * @param status 项目状态
     * @return 当前页的对象
     */
    public Page<CostProjectDTO> selectPage(Integer pageNum, Integer pageSize, String nameSearch, String affiliationSearch, Integer status) {
        LambdaQueryWrapper<CostProject> wrapper = Wrappers.<CostProject>lambdaQuery().like(CostProject::getName, nameSearch).like(CostProject::getAffiliation, affiliationSearch);
        if (status > 0) {
            wrapper.eq(CostProject::getStatus, status);
        } else {
            wrapper.le(CostProject::getStatus, 1);
        }
        if (TokenUtils.getUser().getRole() == 1) {
            wrapper.eq(CostProject::getUserID, TokenUtils.getUserId());
        }
        wrapper.last("order by create_date desc");
        Page<CostProject> page = costProjectMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<CostProjectDTO> DTOPage = new Page<>();
        BeanUtil.copyProperties(page, DTOPage);
        DTOPage.setRecords(DTOUtils.toCostProjectDTOList(page.getRecords()));
        return DTOPage;
    }

    /****
     * 根据项目的ID检索项目
     * @param id 项目的ID
     * @return 项目的DTO实体
     */
    public CostProjectDTO selectById(Integer id) {
        CostProjectDTO costProjectDTO = DTOUtils.toDTO(costProjectMapper.selectById(id));
        if (costProjectDTO == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        costProjectDTO.setFunctionCount(Arrays.asList(functionService.getFunctionCounts(id, 0), functionService.getFunctionCounts(id, 1), functionService.getFunctionCounts(id, 2), functionService.getFunctionCounts(id, 3), functionService.getFunctionCounts(id, 4)));
        return costProjectDTO;
    }

    /***
     * 插入项目
     * @param costProjectDTO 项目的DTO实体
     */
    public void insert(CostProjectDTO costProjectDTO) {
        CostProject res = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getName, costProjectDTO.getName()).eq(CostProject::getUserID, TokenUtils.getUserId()));
        if (res != null) {
            throw new ServiceException(Constants.CODE_401, "项目名称重复");
        }
        CostProject costProject = new CostProject();
        BeanUtil.copyProperties(costProjectDTO, costProject);
        costProjectMapper.insert(costProject);
        costProjectDTO.setId(costProject.getId());
    }

    /***
     * 根据项目的ID更新项目
     * @param costProjectDTO 项目的DTO实体
     * @return 项目的DTO实体
     */
    public CostProjectDTO updateById(CostProjectDTO costProjectDTO) {
        CostProject res = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, costProjectDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        if (TokenUtils.getUser().getRole() == 0 || TokenUtils.getUserId() == res.getUserID()) {
            if (res.getStatus() == 2 && TokenUtils.getUser().getRole() == 1) {
                throw new ServiceException(Constants.CODE_400, "该项目已完结");
            }
            CostProject costProject = new CostProject();
            BeanUtil.copyProperties(costProjectDTO, costProject);
            costProjectMapper.updateById(costProject);
            return costProjectDTO;
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }

    /***
     * 根据项目的ID更新项目，不检测项目权限
     * @param costProjectDTO 项目的DTO实体
     * @return 项目的DTO实体
     */
    public CostProjectDTO safelyUpdateById(CostProjectDTO costProjectDTO) {
        CostProject res = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, costProjectDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        CostProject costProject = new CostProject();
        BeanUtil.copyProperties(costProjectDTO, costProject);
        costProjectMapper.updateById(costProject);
        return costProjectDTO;
    }

    /***
     * 根据项目的ID删除项目
     * @param id 项目的ID
     */
    public void deleteById(Integer id) {
        CostProject res = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        if (TokenUtils.getUser().getRole() == 0 || TokenUtils.getUserId() == res.getUserID()) {
            FileUtils.deleteFolder(res.getRequirementDocumentPath());
            costProjectMapper.deleteById(id);
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }


    /***
     * 根据用户的ID检索项目
     * @param id 用户的ID
     * @return 项目的DTO实体列表
     */
    public List<CostProjectDTO> selectByUserID(Integer id) {
        return DTOUtils.toCostProjectDTOList(costProjectMapper.selectList(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getUserID, id)));
    }

    /***
     * 根据项目ID设置处理进度
     * @param id 项目的ID
     * @param progress 处理进度，范围为0-100
     */
    public void setProgress(Integer id, Integer progress) {
        if (progress < 0) {
            progress = 0;
        } else if (progress > 100) {
            progress = 100;
        }
        costProjectMapper.setProgress(id, progress);
    }

    /***
     * 按照日期统计项目数量
     * @param uid 用户ID
     * @return 统计结果
     */
    public List<CountByDayDTO> countByDay(Integer uid) {
        List<CountByDayDTO> res = costProjectMapper.countByDay(uid);
        Collections.reverse(res);
        return res;
    }

    /***
     * 按照查询词统计项目数量
     * @param uid 用户ID
     * @param query 查询词
     * @return 统计结果
     */
    public List<CountByQueryDTO> countByQuery(Integer uid, String query) {
        return costProjectMapper.countByQuery(uid, query);
    }

    /***
     * 统计成本分析项目数量
     * @param uid 用户ID
     * @return 统计结果
     */
    public Integer count(Integer uid) {
        if (uid > 0) {
            return costProjectMapper.selectCount(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getUserID, uid)).intValue();
        } else {
            return costProjectMapper.selectCount(null).intValue();
        }
    }

    /***
     * 按照状态统计成本分析项目数量
     * @param uid 用户ID
     * @param status 项目状态
     * @return 统计结果
     */
    public Integer count(Integer uid, Integer status) {
        if (uid > 0) {
            return costProjectMapper.selectCount(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getUserID, uid).eq(CostProject::getStatus, status)).intValue();
        } else {
            return costProjectMapper.selectCount(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getStatus, status)).intValue();
        }
    }

    /***
     * 统计总的送审金额
     * @param uid 用户ID
     * @return 总的送审金额
     */
    public BigDecimal getTotalAmount(Integer uid) {
        return costProjectMapper.getTotalAmount(uid);
    }

    /***
     * 统计本月项目数量
     * @param uid 用户ID
     * @return 本月项目数量
     */
    public Integer thisMonthCount(Integer uid) {
        return costProjectMapper.thisMonthCount(uid);
    }

}
