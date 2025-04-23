package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayishen.codeanalyzer.entity.Bug;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BugMapper extends BaseMapper<Bug> {
    List<Bug> selectByOSLId(@Param("id") Integer id);

    void linkWithOpenSourceLibrary(@Param("oid") Integer oid, @Param("bid") Integer bid);

    Integer countByProjectID(@Param("pid") Integer id);
}
