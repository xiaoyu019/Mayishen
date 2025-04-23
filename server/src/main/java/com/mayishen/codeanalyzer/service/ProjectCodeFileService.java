package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.dto.ProjectCodeFileDTO;
import com.mayishen.codeanalyzer.entity.CodeProject;
import com.mayishen.codeanalyzer.entity.ProjectCodeFile;
import com.mayishen.codeanalyzer.mapper.CodeProjectMapper;
import com.mayishen.codeanalyzer.mapper.OpenSourceFileMapper;
import com.mayishen.codeanalyzer.mapper.ProjectCodeFileMapper;
import com.mayishen.common.Constants;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.DTOUtils;
import com.mayishen.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectCodeFileService {
    @Resource
    private ProjectCodeFileMapper projectCodeFileMapper;
    @Resource
    private CodeProjectMapper codeProjectMapper;
    @Resource
    private OpenSourceFileMapper openSourceFileMapper;

    /***
     * 分页检索项目文件
     * @param pageNum 页码
     * @param pageSize 分页数量
     * @param id 源码分析项目ID
     * @param type 文件类型
     * @param status 状态
     * @return 分页结果
     */
    public Page<ProjectCodeFileDTO> selectPage(Integer pageNum, Integer pageSize, Integer id, Integer type, Integer status) {
        if (id == -1) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (TokenUtils.getUser().getRole() != 0 && res.getUserID() != TokenUtils.getUserId()) {
            throw new ServiceException(Constants.CODE_402, "权限错误");
        }
        Page<ProjectCodeFile> page = new Page<>(pageNum, pageSize);
        IPage<ProjectCodeFile> mapIPage = projectCodeFileMapper.selectPageByProjectID(page, id, type, status);
        Page<ProjectCodeFileDTO> DTOPage = new Page<>();
        BeanUtil.copyProperties(mapIPage, DTOPage);
        List<ProjectCodeFile> records = mapIPage.getRecords();
        List<ProjectCodeFileDTO> newRecords = new ArrayList<>();
        for (ProjectCodeFile codeFile : records) {
            codeFile.setFingerPrint("");
            codeFile.setMd5("");
            ProjectCodeFileDTO dto = DTOUtils.toDTO(codeFile);
            dto.setOpenSourceFiles(DTOUtils.toOpenSourceFileDTOList(openSourceFileMapper.selectByCodeFileID(dto.getId())));
            newRecords.add(dto);
        }
        DTOPage.setRecords(newRecords);
        return DTOPage;
    }

    /***
     * 根据路径检索项目文件
     * @param pid 源码分析项目ID
     * @param path 项目文件路径
     * @return 项目文件的DTO实体
     */
    public ProjectCodeFileDTO selectByPath(Integer pid, String path) {
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, pid));
        if (TokenUtils.getUser().getRole() != 0 && res.getUserID() != TokenUtils.getUserId()) {
            throw new ServiceException(Constants.CODE_402, "权限错误");
        }
        System.out.println(path);
        ProjectCodeFile codeFile = projectCodeFileMapper.selectOne(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getProjectId, pid).eq(ProjectCodeFile::getPath, path));
        if (codeFile == null) {
            ProjectCodeFileDTO dto = new ProjectCodeFileDTO();
            dto.setName(path.substring(path.lastIndexOf("/") + 1));
            dto.setPath(path);
            return dto;
        }
        ProjectCodeFileDTO dto = DTOUtils.toDTO(codeFile);
        dto.setFingerPrint("");
        dto.setMd5("");
        dto.setOpenSourceFiles(DTOUtils.toOpenSourceFileDTOList(openSourceFileMapper.selectByCodeFileID(dto.getId())));
        return dto;
    }

    /***
     * 新增项目文件
     * @param projectCodeFileDTO 项目文件的DTO实体
     */
    public void insert(ProjectCodeFileDTO projectCodeFileDTO) {
        ProjectCodeFile projectCodeFile = new ProjectCodeFile();
        BeanUtil.copyProperties(projectCodeFileDTO, projectCodeFile);
        projectCodeFileMapper.insert(projectCodeFile);
        projectCodeFileDTO.setId(projectCodeFile.getId());
    }

    /***
     * 更新项目文件
     * @param ProjectCodeFileDTO 项目文件的DTO实体
     * @return 更新后的项目文件的DTO实体
     */
    public ProjectCodeFileDTO updateById(ProjectCodeFileDTO ProjectCodeFileDTO) {
        ProjectCodeFile res = projectCodeFileMapper.selectOne(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getId, ProjectCodeFileDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        ProjectCodeFile ProjectCodeFile = new ProjectCodeFile();
        BeanUtil.copyProperties(ProjectCodeFileDTO, ProjectCodeFile);
        projectCodeFileMapper.updateById(ProjectCodeFile);
        return ProjectCodeFileDTO;
    }

    /***
     * 删除项目文件
     * @param id 项目文件的ID
     */
    public void deleteById(Integer id) {
        ProjectCodeFile res = projectCodeFileMapper.selectOne(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        projectCodeFileMapper.deleteById(id);
    }

    /***
     * 根据源码分析项目ID删除源码分析文件
     * @param id 源码分析项目ID
     */
    public void deleteByProjectId(Integer id) {
        CodeProject codeProject = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (codeProject == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        projectCodeFileMapper.delete(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getProjectId, id));
    }


    /***
     * 根据项目文件ID获取项目文件
     * @param id 项目文件ID
     * @return 项目文件的DTO实体
     */
    public ProjectCodeFileDTO selectById(Integer id) {
        ProjectCodeFile codeFile = projectCodeFileMapper.selectOne(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getId, id));
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, codeFile.getProjectId()));
        return DTOUtils.toDTO(codeFile);
    }

    /***
     * 统计项目文件数量
     * @param pid 源码分析项目的ID
     * @param type 文件类型
     * @return 文件数量
     */
    public Integer getCodeFilesCounts(Integer pid, Integer type) {
        return projectCodeFileMapper.selectCount(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getProjectId, pid).eq(ProjectCodeFile::getType, type)).intValue();

    }

    /***
     * 根据项目ID检索项目文件列表
     * @param id 源码分析项目的ID
     * @return 项目文件的DTO实体列表
     */
    public List<ProjectCodeFileDTO> selectByProjectId(Integer id) {
        return DTOUtils.toProjectCodeFileDTOList(projectCodeFileMapper.selectList(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getProjectId, id)));
    }

    /***
     * 根据文件类型检索项目文件列表
     * @param id 源码分析项目的ID
     * @param type 文件类型
     * @return 项目文件的DTO实体列表
     */
    public List<ProjectCodeFileDTO> selectByType(Integer id, Integer type) {
        return DTOUtils.toProjectCodeFileDTOList(projectCodeFileMapper.selectList(Wrappers.<ProjectCodeFile>lambdaQuery().eq(ProjectCodeFile::getProjectId, id).eq(ProjectCodeFile::getType, type)));
    }
}
