package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mayishen.codeanalyzer.entity.OpenSourceProject;
import org.apache.ibatis.annotations.Param;

public interface OpenSourceProjectMapper extends BaseMapper<OpenSourceProject> {
    void linkWithCodeProject(@Param("pid") Integer pid, @Param("oid") Integer oid, @Param("cloneFileNumber") Integer cloneFileNumber, @Param("cloneLineNumber") Integer cloneLineNumber);

    void clearLinkWithCodeProject(@Param("pid") Integer pid);

    IPage<OpenSourceProject> selectPageByProjectID(Page<OpenSourceProject> page, @Param("pid") Integer pid, @Param("license") Integer license, @Param("order") Integer order);
}
