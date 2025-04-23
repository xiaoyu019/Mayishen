package com.mayishen.codeanalyzer.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mayishen.codeanalyzer.entity.OpenSourceFile;
import lombok.Data;

import java.util.List;

@Data
public class OpenSourceProjectDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String version;
    private Integer license;
    private String path;
    private String url;

    private String createDate;

    private Integer totalLineNumber;
    private Integer validFileNumber;


    private Integer cloneLineNumber;
    private Integer cloneFileNumber;

    private List<OpenSourceFile> openSourceFiles;
}
