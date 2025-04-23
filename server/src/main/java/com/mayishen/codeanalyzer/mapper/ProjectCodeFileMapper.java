package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mayishen.codeanalyzer.entity.ProjectCodeFile;
import org.apache.ibatis.annotations.Param;

public interface ProjectCodeFileMapper extends BaseMapper<ProjectCodeFile> {
    IPage<ProjectCodeFile> selectPageByProjectID(IPage<ProjectCodeFile> page, @Param("pid") Integer pid, @Param("type") Integer type, @Param("status") Integer status);
}
