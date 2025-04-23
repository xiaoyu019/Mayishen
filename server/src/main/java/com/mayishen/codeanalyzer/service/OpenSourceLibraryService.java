package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.entity.CodeProject;
import com.mayishen.codeanalyzer.mapper.CodeProjectMapper;
import com.mayishen.common.Constants;
import com.mayishen.codeanalyzer.dto.OpenSourceLibraryDTO;
import com.mayishen.codeanalyzer.entity.OpenSourceLibrary;
import com.mayishen.core.dto.CountByQueryDTO;
import com.mayishen.exception.ServiceException;
import com.mayishen.codeanalyzer.mapper.OpenSourceLibraryMapper;
import com.mayishen.utils.DTOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OpenSourceLibraryService {
    @Resource
    private OpenSourceLibraryMapper openSourceLibraryMapper;
    @Resource
    private CodeProjectMapper codeProjectMapper;
    @Resource
    private BugService bugService;

    /**
     * 插入依赖库
     * @param openSourceLibraryDTO 依赖库的DTO实体
     */
    public void insert(OpenSourceLibraryDTO openSourceLibraryDTO) {
        OpenSourceLibrary res = openSourceLibraryMapper.selectOne(Wrappers.<OpenSourceLibrary>lambdaQuery().eq(OpenSourceLibrary::getName, openSourceLibraryDTO.getName()).eq(OpenSourceLibrary::getVersion, openSourceLibraryDTO.getVersion()).eq(OpenSourceLibrary::getProjectId, openSourceLibraryDTO.getProjectId()));
        if (res != null) {
            throw new ServiceException(Constants.CODE_401, "项目重复");
        }
        OpenSourceLibrary openSourceLibrary = new OpenSourceLibrary();
        BeanUtil.copyProperties(openSourceLibraryDTO, openSourceLibrary);
        openSourceLibraryMapper.insert(openSourceLibrary);
        openSourceLibraryDTO.setId(openSourceLibrary.getId());
    }


    /***
     * 根据项目的ID删除依赖库
     * @param id 项目的ID
     */
    public void deleteByProjectId(Integer id) {
        CodeProject codeProject = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (codeProject == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        openSourceLibraryMapper.delete(Wrappers.<OpenSourceLibrary>lambdaQuery().eq(OpenSourceLibrary::getProjectId, id));
    }

    /***
     * 根据项目的ID检索依赖库
     * @param id 项目的ID
     * @return 依赖库的DTO实体列表
     */
    public Page<OpenSourceLibraryDTO> selectPageByProjectID(Integer pageNum, Integer pageSize, Integer id) {
        CodeProject codeProject = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (codeProject == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        Page<OpenSourceLibrary> page = openSourceLibraryMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<OpenSourceLibrary>lambdaQuery().eq(OpenSourceLibrary::getProjectId, id).last(" order by size desc"));
        Page<OpenSourceLibraryDTO> newPage = new Page<>();
        BeanUtil.copyProperties(page, newPage);
        List<OpenSourceLibraryDTO> list = DTOUtils.toOpenSourceLibraryDTOList(page.getRecords());
        list.stream().forEach(openSourceLibraryDTO -> {
            openSourceLibraryDTO.setBugs(bugService.selectByOSLId(openSourceLibraryDTO.getId()));
        });
        newPage.setRecords(list);
        return newPage;
    }

    /***
     * 根据漏洞等级统计漏洞数量
     * @param id 项目ID
     * @return 统计结果
     */
    public List<CountByQueryDTO> countBugByLevel(Integer id) {
        CodeProject codeProject = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (codeProject == null) {
            throw new ServiceException(Constants.CODE_400, "参数错误");
        }
        return openSourceLibraryMapper.countBugByLevel(id);
    }

    /***
     * 根据项目ID统计依赖库数量
     * @param id 项目iD
     * @return 依赖库数量
     */
    public Integer countByProjectID(Integer id) {
        return openSourceLibraryMapper.selectCount(Wrappers.<OpenSourceLibrary>lambdaQuery().eq(OpenSourceLibrary::getProjectId, id)).intValue();
    }
}
