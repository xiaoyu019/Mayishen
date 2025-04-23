package com.mayishen.costestimation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayishen.core.dto.CountByDayDTO;
import com.mayishen.core.dto.CountByQueryDTO;
import com.mayishen.costestimation.entity.CostProject;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CostProjectMapper extends BaseMapper<CostProject> {

    List<CountByDayDTO> countByDay(@Param("uid") Integer uid);

    List<CountByQueryDTO> countByQuery(@Param("uid") Integer uid, @Param("query") String query);

    void setProgress(@Param("id") Integer id, @Param("progress") Integer progress);

    BigDecimal getTotalAmount(@Param("uid") Integer uid);

    Integer thisMonthCount(@Param("uid") Integer uid);
}