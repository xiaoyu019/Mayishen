package com.mayishen.common.handler;

import com.alibaba.fastjson2.TypeReference;
import com.mayishen.costestimation.dto.EffortDistributionDTO;

import java.util.List;

public class EffortDistributionListTypeHandler extends ListTypeHandler<EffortDistributionDTO> {
    @Override
    protected TypeReference<List<EffortDistributionDTO>> specificType() {
        return new TypeReference<List<EffortDistributionDTO>>() {
        };
    }
}