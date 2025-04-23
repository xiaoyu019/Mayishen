package com.mayishen.codeanalyzer.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class ProjectCodeFileDTO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String path;
    private Integer type;
    private String md5;

    private String fingerPrint;
    private Integer lineNumber;
    private Integer projectId;

    private List<OpenSourceFileDTO> openSourceFiles;
}