package com.mayishen.costestimation.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class CostProjectDTO {
    private Integer id;

    private String name;
    private String affiliation;

    private String createDate;
    private Integer status;
    private Integer type;
    private Integer progress;
    private BigDecimal totalAmount;

    private Float totalScale;
    private Integer totalUnadaptedScale;
    private Float totalEffort;
    private Float totalAdaptedEffort;
    private BigDecimal totalLabourCost;
    private BigDecimal totalNonlabourCost;
    private BigDecimal totalCost;
    private Float totalCloneRatio;

    private String requirementDocumentPath;

    private Integer scaleRadio;
    private Integer factorRadio;
    private Integer costRadio;
    private Integer rateRadio;
    private Float scaleFactor;
    private Float labourCostRate;
    private List<EffortDistributionDTO> effortDistribution;

    private Integer appFactorCate;
    private Integer integrityFactorCate;
    private Integer distributedFactorCate;
    private Integer performanceFactorCate;
    private Integer reliabilityFactorCate;
    private Integer multisiteFactorCate;
    private Integer platformFactorCate;
    private Integer backgroundFactorCate;

    private Float appFactor;
    private Float integrityFactor;
    private Float nonFunctionFactor;
    private Float platformFactor;
    private Float backgroundFactor;
    private Integer city;
    private Integer area;

    private Integer userID;
    private String comment;

    private List<Integer> functionCount;

}
