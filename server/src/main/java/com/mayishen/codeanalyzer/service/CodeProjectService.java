package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.entity.CodeProject;
import com.mayishen.codeanalyzer.mapper.CodeProjectMapper;
import com.mayishen.common.Constants;
import com.mayishen.core.dto.CountByDayDTO;
import com.mayishen.core.dto.CountByQueryDTO;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.DTOUtils;
import com.mayishen.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CodeProjectService {
    @Resource
    private CodeProjectMapper codeProjectMapper;
    @Autowired
    private ProjectCodeFileService projectCodeFileService;
    @Autowired
    private OpenSourceLibraryService openSourceLibraryService;
    @Autowired
    private BugService bugService;

    /***
     * 分页检索项目
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param nameSearch 项目名称的模糊搜索词
     * @param affiliationSearch 送审机构名的模糊搜索词
     * @return 当前页的对象
     */
    public Page<CodeProjectDTO> selectPage(Integer pageNum, Integer pageSize, String nameSearch, String affiliationSearch) {
        LambdaQueryWrapper<CodeProject> wrapper = Wrappers.<CodeProject>lambdaQuery().like(CodeProject::getName, nameSearch).like(CodeProject::getAffiliation, affiliationSearch).last("order by create_date desc");
        if (TokenUtils.getUser().getRole() != 0) {
            wrapper.eq(CodeProject::getUserID, TokenUtils.getUserId());
        }
        Page<CodeProject> page = codeProjectMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<CodeProjectDTO> DTOPage = new Page<>();
        BeanUtil.copyProperties(page, DTOPage);
        List<CodeProjectDTO> newRecords = DTOUtils.toCodeProjectDTOList(page.getRecords());
        newRecords.stream().forEach(codeProjectDTO -> {
            codeProjectDTO.setDependencyNum(openSourceLibraryService.countByProjectID(codeProjectDTO.getId()));
            codeProjectDTO.setBugNum(bugService.countByProjectID(codeProjectDTO.getId()));
        });
        DTOPage.setRecords(newRecords);
        return DTOPage;
    }

    /****
     * 根据项目的ID检索项目
     * @param id 项目的ID
     * @return 项目的DTO实体
     */
    public CodeProjectDTO selectById(Integer id) {
        CodeProjectDTO codeProjectDTO = DTOUtils.toDTO(codeProjectMapper.selectById(id));
        if (codeProjectDTO == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        List<Integer> codeFileCount = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            codeFileCount.add(projectCodeFileService.getCodeFilesCounts(codeProjectDTO.getId(), i));
        }
        codeProjectDTO.setCodeFileCount(codeFileCount);
        codeProjectDTO.setDependencyNum(openSourceLibraryService.countByProjectID(id));
        codeProjectDTO.setBugNum(bugService.countByProjectID(id));
        return codeProjectDTO;
    }

    /***
     * 根据用户的ID检索项目
     * @param id 用户的ID
     * @return 项目的DTO实体列表
     */
    public List<CodeProjectDTO> selectByUserID(Integer id) {
        return DTOUtils.toCodeProjectDTOList(codeProjectMapper.selectList(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getUserID, id)));
    }


    /***
     * 新增源码分析项目
     * @param codeProjectDTO 项目的DTO实体
     */
    public void insert(CodeProjectDTO codeProjectDTO) {
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getName, codeProjectDTO.getName()));
        if (res == null) {
            CodeProject codeProject = new CodeProject();
            BeanUtil.copyProperties(codeProjectDTO, codeProject);
            codeProjectMapper.insert(codeProject);
            codeProjectDTO.setId(codeProject.getId());
        } else {
            throw new ServiceException(Constants.CODE_401, "项目名重复");
        }
    }

    /***
     * 根据项目的ID更新项目
     * @param codeProjectDTO 项目的DTO实体
     * @return 项目的DTO实体
     */
    public CodeProjectDTO updateById(CodeProjectDTO codeProjectDTO) {
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, codeProjectDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        if (TokenUtils.getUserId() == res.getUserID()) {
            if (res.getStatus() == 2) {
                throw new ServiceException(Constants.CODE_400, "权限错误，该项目已完结");
            }
            CodeProject codeProject = new CodeProject();
            BeanUtil.copyProperties(codeProjectDTO, codeProject);
            codeProjectMapper.updateById(codeProject);
            return codeProjectDTO;
        } else {
            throw new ServiceException(Constants.CODE_400, "权限错误");
        }
    }

    /***
     * 根据项目的ID更新项目，不检测项目权限
     * @param codeProjectDTO 项目的DTO实体
     * @return 项目的DTO实体
     */
    public CodeProjectDTO safelyUpdateById(CodeProjectDTO codeProjectDTO) {
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, codeProjectDTO.getId()));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        CodeProject codeProject = new CodeProject();
        BeanUtil.copyProperties(codeProjectDTO, codeProject);
        codeProjectMapper.updateById(codeProject);
        return codeProjectDTO;
    }


    /***
     * 根据ID删除项目
     * @param id 项目ID
     */
    public void deleteById(Integer id) {
        CodeProject res = codeProjectMapper.selectOne(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getId, id));
        if (res == null) {
            throw new ServiceException(Constants.CODE_401, "参数错误");
        }
        codeProjectMapper.deleteById(id);
    }

    /***
     * 设置项目进度
     * @param id 项目ID
     * @param progress 进度，0~100
     */
    public void setProgress(Integer id, Integer progress) {
        codeProjectMapper.setProgress(id, progress);
    }

    /***
     * 按照日期统计项目数量
     * @param uid 用户ID
     * @return 结果
     */
    public List<CountByDayDTO> countByDay(Integer uid) {
        List<CountByDayDTO> res = codeProjectMapper.countByDay(uid);
        Collections.reverse(res);
        return res;
    }

    /***
     * 按照查询词统计项目数量
     * @param uid 用户ID
     * @param query 查询词
     * @return 结果
     */
    public List<CountByQueryDTO> countByQuery(Integer uid, String query) {
        return codeProjectMapper.countByQuery(uid, query);
    }

    /***
     * 按照文件类型统计文件数量
     * @param pid 项目ID
     * @return 结果
     */
    public List<CountByQueryDTO> countFileByType(Integer pid) {
        return codeProjectMapper.countFileByType(pid);
    }

    /***
     * 按照文件类型统计相似文件数量
     * @param pid 项目ID
     * @return 结果
     */
    public List<CountByQueryDTO> countCloneFileByType(Integer pid) {
        return codeProjectMapper.countCloneFileByType(pid);
    }

    /***
     * 根据用户ID统计项目数量
     * @param uid 用户ID
     * @return 结果
     */
    public Integer count(Integer uid) {
        if (uid > 0) {
            return codeProjectMapper.selectCount(Wrappers.<CodeProject>lambdaQuery().eq(CodeProject::getUserID, uid)).intValue();
        } else {
            return codeProjectMapper.selectCount(null).intValue();
        }
    }

    /***
     * 统计总的代码文件数量
     * @param uid 用户ID
     * @return 总的代码文件数量
     */
    public Integer getTotalValidFileNumber(Integer uid) {
        return codeProjectMapper.getTotalValidFileNumber(uid);
    }

    /***
     * 统计总的文件行数
     * @param uid 用户ID
     * @return 总的文件行数
     */
    public Integer getTotalLineNumber(Integer uid) {
        return codeProjectMapper.getTotalLineNumber(uid);
    }

    /***
     * 统计本月项目数量
     * @param uid 用户ID
     * @return 本月项目数量
     */
    public Integer thisMonthCount(Integer uid) {
        return codeProjectMapper.thisMonthCount(uid);
    }
}
