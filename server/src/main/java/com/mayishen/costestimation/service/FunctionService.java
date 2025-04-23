package com.mayishen.costestimation.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mayishen.common.Constants;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.DTOUtils;
import com.mayishen.utils.TokenUtils;
import com.mayishen.costestimation.dto.FunctionDTO;
import com.mayishen.costestimation.entity.CostProject;
import com.mayishen.costestimation.entity.Function;
import com.mayishen.costestimation.mapper.CostProjectMapper;
import com.mayishen.costestimation.mapper.FunctionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FunctionService {
    @Resource
    private CostProjectMapper costProjectMapper;
    @Resource
    private FunctionMapper functionMapper;

    /***
     * 插入功能点
     * @param functionDTO 功能点的DTO实体
     */
    public void insert(FunctionDTO functionDTO) {
        Function function = new Function();
        BeanUtil.copyProperties(functionDTO, function);
        functionMapper.insert(function);
    }

    /***
     * 根据功能点ID检索功能点
     * @param id 功能点的ID
     * @return 功能点的DTO实体
     */
    public FunctionDTO selectByID(Integer id) {
        Function function = functionMapper.selectOne(Wrappers.<Function>lambdaQuery().eq(Function::getId, id));
        if (function == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        return DTOUtils.toDTO(function);
    }

    /***
     * 根据功能点的ID更新功能点
     * @param functionDTO 功能点的DTO实体
     * @return 功能点的DTO实体
     */
    public FunctionDTO updateById(FunctionDTO functionDTO) {
        Function res = functionMapper.selectOne(Wrappers.<Function>lambdaQuery().eq(Function::getId, functionDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        CostProject costProject = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, functionDTO.getProjectId()));
        if (TokenUtils.getUserId() == costProject.getUserID()) {
            Function function = new Function();
            BeanUtil.copyProperties(functionDTO, function);
            functionMapper.updateById(function);
            return functionDTO;
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }


    /***
     * 根据功能点的ID删除功能点
     * @param id 功能点的ID
     */
    public void deleteById(Integer id) {
        Function res = functionMapper.selectOne(Wrappers.<Function>lambdaQuery().eq(Function::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        CostProject costProject = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, res.getProjectId()));
        if (TokenUtils.getUserId() == costProject.getUserID()) {
            functionMapper.deleteById(id);
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }

    /***
     * 根据项目的ID删除功能点
     * @param id 项目的ID
     */
    public void deleteByProjectId(Integer id) {
        CostProject costProject = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, id));
        if (costProject == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        functionMapper.delete(Wrappers.<Function>lambdaQuery().eq(Function::getProjectId, id));
    }


    /***
     * 根据项目的ID检索功能点
     * @param id 项目的ID
     * @return 功能点的DTO实体列表
     */
    public List<FunctionDTO> selectByProjectID(Integer id) {
        CostProject costProject = costProjectMapper.selectOne(Wrappers.<CostProject>lambdaQuery().eq(CostProject::getId, id));
        if (costProject == null) {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
        List<Function> res = functionMapper.selectList(Wrappers.<Function>lambdaQuery().eq(Function::getProjectId, id));
        res.stream().forEach(function -> {
            function.setContent("");
        });
        return DTOUtils.toFunctionDTOList(res);
    }

    /***
     * 根据功能点ID获取功能点内容
     * @param id 功能点ID
     * @return 功能点内容
     */
    public String getFunctionContentById(Integer id) {
        FunctionDTO function = this.selectByID(id);
        return function.getContent();
    }

    /***
     * 获取某项目给定类型的功能点数量
     * @param pid 项目的ID
     * @param type 功能点类型
     * @return 功能点数量
     */
    public Integer getFunctionCounts(Integer pid, Integer type) {
        return functionMapper.selectCount(Wrappers.<Function>lambdaQuery().eq(Function::getProjectId, pid).eq(Function::getType, type)).intValue();
    }
}
