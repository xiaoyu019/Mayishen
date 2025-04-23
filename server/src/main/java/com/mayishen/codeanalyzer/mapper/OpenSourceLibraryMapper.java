package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayishen.codeanalyzer.entity.OpenSourceLibrary;
import com.mayishen.core.dto.CountByQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OpenSourceLibraryMapper extends BaseMapper<OpenSourceLibrary> {
    List<CountByQueryDTO> countBugByLevel(@Param("pid") Integer id);
}
