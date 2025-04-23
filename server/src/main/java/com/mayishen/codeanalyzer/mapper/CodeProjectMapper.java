package com.mayishen.codeanalyzer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayishen.codeanalyzer.entity.CodeProject;
import com.mayishen.core.dto.CountByDayDTO;
import com.mayishen.core.dto.CountByQueryDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CodeProjectMapper extends BaseMapper<CodeProject> {
    void setProgress(@Param("id") Integer id, @Param("progress") Integer progress);

    List<CountByDayDTO> countByDay(@Param("uid") Integer uid);

    List<CountByQueryDTO> countByQuery(@Param("uid") Integer uid, @Param("query") String query);

    List<CountByQueryDTO> countFileByType(@Param("pid") Integer pid);

    List<CountByQueryDTO> countCloneFileByType(@Param("pid") Integer pid);

    Integer getTotalValidFileNumber(@Param("uid") Integer uid);

    Integer getTotalLineNumber(@Param("uid") Integer uid);

    Integer thisMonthCount(@Param("uid") Integer uid);


}
