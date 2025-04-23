package com.mayishen.costestimation.dto;

import lombok.Data;

@Data
public class FunctionDTO {
    private Integer id;
    private String name;
    private Integer type;
    private String content;
    private Integer projectId;
}
