package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("code_project")
@Data
public class CodeProject {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String affiliation;
    @TableField("create_date")
    private String createDate;

    private Integer status;
    private Integer progress;
    @TableField("source_code_path")
    private String sourceCodePath;

    @TableField("total_file_number")
    private Integer totalFileNumber;
    @TableField("valid_file_number")
    private Integer validFileNumber;
    @TableField("clone_file_number")
    private Integer cloneFileNumber;

    @TableField("total_line_number")
    private Integer totalLineNumber;
    @TableField("clone_line_number")
    private Integer cloneLineNumber;

    private String comment;
    @TableField("user_id")
    private Integer userID;
}
