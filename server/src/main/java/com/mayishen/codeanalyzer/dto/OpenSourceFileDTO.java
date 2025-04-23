package com.mayishen.codeanalyzer.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mayishen.codeanalyzer.entity.ProjectCodeFile;
import lombok.Data;

import java.util.List;


@Data
public class OpenSourceFileDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String path;
    private Integer type;
    private String md5;

    private String fingerPrint;
    private Integer lineNumber;

    private Integer openSourceProjectID;

    private String openSourceProjectName;
    private String projectFileName;
    private String projectFilePath;
    private Integer projectFileId;

    private List<ProjectCodeFile> projectCodeFiles;
}
