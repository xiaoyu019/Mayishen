package com.mayishen.costestimation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("function")
@Data
public class Function {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer type;
    private String content;

    @TableField("project_id")
    private Integer projectId;
}
