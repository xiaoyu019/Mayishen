package com.mayishen.costestimation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mayishen.common.handler.EffortDistributionListTypeHandler;
import com.mayishen.costestimation.dto.EffortDistributionDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@TableName(value = "cost_project",autoResultMap = true)
@Data
public class CostProject {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String affiliation;

    @TableField("create_date")
    private String createDate;
    private Integer status;
    private Integer progress;
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("total_scale")
    private Float totalScale;
    @TableField("total_unadapted_scale")
    private Integer totalUnadaptedScale;
    @TableField("total_effort")
    private Float totalEffort;
    @TableField("total_adapted_effort")
    private Float totalAdaptedEffort;

    @TableField("total_labour_cost")
    private BigDecimal totalLabourCost;
    @TableField("total_nonlabour_cost")
    private BigDecimal totalNonlabourCost;
    @TableField("total_cost")
    private BigDecimal totalCost;

    @TableField("requirement_document_path")
    private String requirementDocumentPath;

    @TableField("radio_scale")
    private Integer scaleRadio;
    @TableField("radio_factor")
    private Integer factorRadio;
    @TableField("radio_cost")
    private Integer costRadio;
    @TableField("radio_rate")
    private Integer rateRadio;
    @TableField("factor_scale")
    private Float scaleFactor;
    @TableField("labour_cost_rate")
    private Float labourCostRate;

    @TableField(value="effort_distribution",typeHandler = EffortDistributionListTypeHandler.class)
    private List<EffortDistributionDTO> effortDistribution;


    @TableField("factor_cate_app")
    private Integer appFactorCate;
    @TableField("factor_cate_integrity")
    private Integer integrityFactorCate;
    @TableField("factor_cate_distributed")
    private Integer distributedFactorCate;
    @TableField("factor_cate_performance")
    private Integer performanceFactorCate;
    @TableField("factor_cate_reliability")
    private Integer reliabilityFactorCate;
    @TableField("factor_cate_multisite")
    private Integer multisiteFactorCate;
    @TableField("factor_cate_platform")
    private Integer platformFactorCate;
    @TableField("factor_cate_background")
    private Integer backgroundFactorCate;

    @TableField("factor_app")
    private Float appFactor;
    @TableField("factor_integrity")
    private Float integrityFactor;
    @TableField("factor_nonfunction")
    private Float nonFunctionFactor;
    @TableField("factor_platform")
    private Float platformFactor;
    @TableField("factor_background")
    private Float backgroundFactor;


    private Integer city;
    private Integer area;
    private String comment;

    @TableField("user_id")
    private Integer userID;

}
