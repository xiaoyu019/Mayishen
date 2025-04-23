package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("bug")
@Data
public class Bug {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    @TableField("library_name")
    private String libraryName;
    private String version;
    private Integer level;
    private String description;

    @TableField("published_time")
    private String publishedTime;

    @TableField("modified_time")
    private String modifiedTime;
}
