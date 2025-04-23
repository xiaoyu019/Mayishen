package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mayishen.codeanalyzer.entity.OpenSourceFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenSourceFileMapper extends BaseMapper<OpenSourceFile> {
    void linkWithProjectCodeFile(@Param("pcf_id") Integer pcfID, @Param("osf_id") Integer osfID);

    void clearLinkWithProjectCodeFile(@Param("pcf_id") Integer pcfID);

    IPage<OpenSourceFile> selectPageByProjectID(IPage<OpenSourceFile> page, @Param("pid") Integer pid, @Param("type") Integer type);

    IPage<OpenSourceFile> selectPageByOpenSourceProjectID(IPage<OpenSourceFile> page, @Param("pid") Integer pid, @Param("type") Integer type);

    List<OpenSourceFile> selectByCodeFileID(@Param("id") Integer id);
}
