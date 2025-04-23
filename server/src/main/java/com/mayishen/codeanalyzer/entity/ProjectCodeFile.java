package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@TableName("project_code_file")
@Data
public class ProjectCodeFile {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String path;
    private Integer type;
    private String md5;

    @TableField("finger_print")
    private String fingerPrint;
    @TableField("line_number")
    private Integer lineNumber;
    @TableField("project_id")
    private Integer projectId;

    @TableField(exist = false)
    private List<OpenSourceFile> openSourceFiles;
}