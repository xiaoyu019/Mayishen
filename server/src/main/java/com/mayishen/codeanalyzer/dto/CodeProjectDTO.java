package com.mayishen.codeanalyzer.dto;

import lombok.Data;

import java.util.List;


@Data
public class CodeProjectDTO {
    private Integer id;

    private String name;
    private String affiliation;
    private String createDate;

    private Integer status;
    private Integer progress;
    private String sourceCodePath;

    private Integer totalFileNumber;
    private Integer validFileNumber;
    private Integer cloneFileNumber;

    private Integer totalLineNumber;
    private Integer cloneLineNumber;

    private String comment;
    private Integer userID;

    private List<Integer> codeFileCount;
    private List<OpenSourceLibraryDTO> openSourceLibraryList;
    private Integer dependencyNum;
    private Integer bugNum;
}
