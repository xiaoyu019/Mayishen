package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("open_source_library")
@Data
public class OpenSourceLibrary {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String version;
    private Integer size;

    @TableField("project_id")
    private Integer projectId;
}
