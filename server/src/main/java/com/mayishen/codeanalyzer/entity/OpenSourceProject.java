package com.mayishen.codeanalyzer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@TableName("open_source_project")
@Data
public class OpenSourceProject {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String version;
    private Integer license;
    private String path;
    private String url;

    @TableField("create_date")
    private String createDate;

    @TableField(value="total_line_number")
    private Integer totalLineNumber;
    @TableField(value="valid_file_number")
    private Integer validFileNumber;

    @TableField(value="clone_line_number",exist =  false)
    private Integer cloneLineNumber;
    @TableField(value="clone_file_number",exist =  false)
    private Integer cloneFileNumber;
}
