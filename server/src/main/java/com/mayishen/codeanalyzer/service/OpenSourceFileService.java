package com.mayishen.codeanalyzer.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.dto.OpenSourceFileDTO;
import com.mayishen.codeanalyzer.entity.OpenSourceFile;
import com.mayishen.codeanalyzer.mapper.OpenSourceFileMapper;
import com.mayishen.utils.DTOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OpenSourceFileService {
    @Resource
    private OpenSourceFileMapper openSourceFileMapper;

    /***
     * 插入开源项目文件
     * @param openSourceFileDTO 开源项目文件的DTO实体
     */
    public void insert(OpenSourceFileDTO openSourceFileDTO) {
        OpenSourceFile openSourceFile = new OpenSourceFile();
        BeanUtil.copyProperties(openSourceFileDTO, openSourceFile);
        openSourceFileMapper.insert(openSourceFile);
        openSourceFileDTO.setId(openSourceFileDTO.getId());
    }


    /***
     * 根据类型检索开源项目文件
     * @param id 开源项目ID
     * @param type 类型
     * @return 源项目文件的DTO列表
     */
    public List<OpenSourceFileDTO> selectByType(Integer id, Integer type) {
        return DTOUtils.toOpenSourceFileDTOList(openSourceFileMapper.selectList(Wrappers.<OpenSourceFile>lambdaQuery().eq(OpenSourceFile::getOpenSourceProjectID, id).eq(OpenSourceFile::getType, type)));
    }

    /***
     * 将项目文件与相似的开源代码文件连接
     * @param pcfID 项目文件ID
     * @param osfID 开源代码文件ID
     */
    public void linkWithProjectCodeFile(Integer pcfID, Integer osfID) {
        openSourceFileMapper.linkWithProjectCodeFile(pcfID, osfID);
    }

    /***
     * 清除项目文件的开源代码文件连接
     * @param pcfID 项目文件ID
     */
    public void clearLinkWithProjectCodeFile(Integer pcfID) {
        openSourceFileMapper.clearLinkWithProjectCodeFile(pcfID);
    }


    /***
     * 根据源码分析项目ID获取开源文件列表分页结果
     * @param pageNum 页码
     * @param pageSize 分页结果
     * @param pid 项源码分析项目ID
     * @param type 文件类型
     * @return 分页结果
     */
    public Page<OpenSourceFileDTO> selectPageByProjectID(Integer pageNum, Integer pageSize, Integer pid, Integer type) {
        Page<OpenSourceFile> page = new Page<>(pageNum, pageSize);
        IPage<OpenSourceFile> mapIPage = openSourceFileMapper.selectPageByProjectID(page, pid, type);
        Page<OpenSourceFileDTO> newPage = new Page<>();
        BeanUtil.copyProperties(mapIPage, newPage);
        List<OpenSourceFile> records = mapIPage.getRecords();
        records.stream().forEach(openSourceFile -> {
            openSourceFile.setFingerPrint("");
            openSourceFile.setMd5("");
        });
        newPage.setRecords(DTOUtils.toOpenSourceFileDTOList(records));
        return newPage;
    }


    /***
     * 根据开源项目ID获取开源文件列表分页结果
     * @param pageNum 页码
     * @param pageSize 分页大小
     * @param id 开源项目ID
     * @param type 文件类型
     * @return 分页结果
     */
    public Page<OpenSourceFileDTO> selectPageByOpenSourceProjectID(Integer pageNum, Integer pageSize, Integer id, Integer type) {
        Page<OpenSourceFile> page = new Page<>(pageNum, pageSize);
        IPage<OpenSourceFile> mapIPage = openSourceFileMapper.selectPageByOpenSourceProjectID(page, id, type);
        Page<OpenSourceFileDTO> newPage = new Page<>();
        BeanUtil.copyProperties(mapIPage, newPage);
        List<OpenSourceFile> records = mapIPage.getRecords();
        records.stream().forEach(openSourceFile -> {
            openSourceFile.setFingerPrint("");
            openSourceFile.setMd5("");
            openSourceFile.getProjectCodeFiles().stream().forEach(codeFile -> {
                codeFile.setFingerPrint("");
                codeFile.setMd5("");
            });
        });
        newPage.setRecords(DTOUtils.toOpenSourceFileDTOList(records));
        return newPage;
    }

    /***
     * 根据ID获取开源代码文件DTO实体
     * @param id 开源代码文件ID
     * @return 开源代码文件DTO实体
     */
    public OpenSourceFileDTO selectById(Integer id) {
        return DTOUtils.toDTO(openSourceFileMapper.selectOne(Wrappers.<OpenSourceFile>lambdaQuery().eq(OpenSourceFile::getId, id)));
    }

}
