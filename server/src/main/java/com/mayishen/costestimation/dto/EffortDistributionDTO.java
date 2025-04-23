package com.mayishen.costestimation.dto;

import lombok.Data;

@Data
public class EffortDistributionDTO {
    private String name;
    private Integer ratio;
    private Float labourCostRate;
}
