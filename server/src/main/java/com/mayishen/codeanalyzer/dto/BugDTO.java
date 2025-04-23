package com.mayishen.codeanalyzer.dto;

import lombok.Data;

@Data
public class BugDTO {
    private Integer id;
    private String name;
    private String libraryName;
    private String version;
    private Integer level;
    private String description;

    private String publishedTime;
    private String modifiedTime;
}
