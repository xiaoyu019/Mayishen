package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.dto.OpenSourceProjectDTO;
import com.mayishen.codeanalyzer.entity.OpenSourceProject;
import com.mayishen.codeanalyzer.mapper.OpenSourceProjectMapper;
import com.mayishen.common.Constants;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.DTOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OpenSourceProjectService {
    @Resource
    private OpenSourceProjectMapper openSourceProjectMapper;

    /***
     * 分页检索项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 项目名称的模糊搜索字段
     * @return 当前页的对象
     */
    public Page<OpenSourceProjectDTO> selectPage(Integer pageNum, Integer pageSize, String nameSearch) {
        Page<OpenSourceProject> page = openSourceProjectMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<OpenSourceProject>lambdaQuery().like(OpenSourceProject::getName, nameSearch).last("order by id desc"));
        Page<OpenSourceProjectDTO> DTOPage = new Page<>();
        BeanUtil.copyProperties(page, DTOPage);
        DTOPage.setRecords(DTOUtils.toOpenSourceProjectDTOList(page.getRecords()));
        return DTOPage;
    }

    /****
     * 根据项目的ID检索项目
     * @param id 项目的ID
     * @return 项目的DTO实体
     */
    public OpenSourceProjectDTO selectById(Integer id) {
        OpenSourceProjectDTO openSourceProjectDTO = DTOUtils.toDTO(openSourceProjectMapper.selectById(id));
        if (openSourceProjectDTO == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        return openSourceProjectDTO;
    }

    /***
     * 根据项目ID分页检索开源项目
     * @param pageNum
     * @param pageSize
     * @param pid
     * @param license
     * @param order
     * @return 分页结果
     */
    public Page<OpenSourceProjectDTO> selectPageByProjectID(Integer pageNum, Integer pageSize, Integer pid, Integer license, Integer order) {
        Page<OpenSourceProject> page = new Page<>(pageNum, pageSize);
        IPage<OpenSourceProject> mapIPage = openSourceProjectMapper.selectPageByProjectID(page, pid, license, order);
        Page<OpenSourceProjectDTO> newPage = new Page<>();
        BeanUtil.copyProperties(mapIPage, newPage);
        List<OpenSourceProject> records = mapIPage.getRecords();
        newPage.setRecords(DTOUtils.toOpenSourceProjectDTOList(records));
        return newPage;
    }

    /***
     * 新增开源项目
     * @param openSourceProjectDTO 开源项目的DTO文件
     */
    public void insert(OpenSourceProjectDTO openSourceProjectDTO) {
        OpenSourceProject res = openSourceProjectMapper.selectOne(Wrappers.<OpenSourceProject>lambdaQuery().eq(OpenSourceProject::getName, openSourceProjectDTO.getName()));
        if (res == null) {
            OpenSourceProject openSourceProject = new OpenSourceProject();
            BeanUtil.copyProperties(openSourceProjectDTO, openSourceProject);
            openSourceProjectMapper.insert(openSourceProject);
            openSourceProjectDTO.setId(openSourceProject.getId());
        } else {
            openSourceProjectDTO.setId(res.getId());
        }
    }


    /***
     * 根据ID删除项目
     * @param id 项目的ID
     */
    public void deleteById(Integer id) {
        OpenSourceProject res = openSourceProjectMapper.selectOne(Wrappers.<OpenSourceProject>lambdaQuery().eq(OpenSourceProject::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        openSourceProjectMapper.deleteById(id);
    }

    /***
     * 更新开源项目信息
     * @param openSourceProjectDTO 开源项目的DTO实体
     */
    public void updateById(OpenSourceProjectDTO openSourceProjectDTO) {
        OpenSourceProject openSourceProject = new OpenSourceProject();
        BeanUtil.copyProperties(openSourceProjectDTO, openSourceProject);
        openSourceProjectMapper.updateById(openSourceProject);
    }

    /***
     * 统计开源项目数量
     * @return 开源项目数量
     */
    public Integer count() {
        return openSourceProjectMapper.selectCount(null).intValue();
    }

    /***
     * 清除源码分析项目与相似的开源项目的连接
     * @param pid 源码分析项目
     */
    public void clearLinkWithCodeProject(Integer pid) {
        openSourceProjectMapper.clearLinkWithCodeProject(pid);
    }

    /***
     * 将源码分析项目与相似的开源项目连接
     * @param pid 源码分析项目
     * @param oid 开源项目
     * @param cloneFileNumber 相似文件数量
     * @param cloneLineNumber 相似代码行数
     */
    public void linkWithCodeProject(Integer pid, Integer oid, Integer cloneFileNumber, Integer cloneLineNumber) {
        openSourceProjectMapper.linkWithCodeProject(pid, oid, cloneFileNumber, cloneLineNumber);
    }

    /***
     * 检查开源项目是否存在
     * @param name 开源项目名称
     * @param version 版本
     * @return 是否存在
     */
    public Boolean check(String name, String version) {
        OpenSourceProject res = openSourceProjectMapper.selectOne(Wrappers.<OpenSourceProject>lambdaQuery().eq(OpenSourceProject::getName, name).eq(OpenSourceProject::getVersion, version));
        return res != null;
    }
}
