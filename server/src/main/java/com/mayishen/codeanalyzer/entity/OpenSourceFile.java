package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;


@TableName("open_source_file")
@Data
public class OpenSourceFile {
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

    @TableField("osp_id")
    private Integer openSourceProjectID;


    @TableField(value = "open_source_project_name",exist = false)
    private String openSourceProjectName;
    @TableField(value = "project_file_name",exist = false)
    private String projectFileName;
    @TableField(value = "project_file_path",exist = false)
    private String projectFilePath;
    @TableField(value = "project_file_id",exist = false)
    private Integer projectFileId;

    @TableField(exist = false)
    private List<ProjectCodeFile> projectCodeFiles;

}
