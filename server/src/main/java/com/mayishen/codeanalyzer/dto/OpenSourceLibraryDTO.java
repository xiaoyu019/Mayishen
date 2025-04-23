package com.mayishen.codeanalyzer.dto;

import lombok.Data;

import java.util.List;


@Data
public class OpenSourceLibraryDTO {

    private Integer id;

    private String name;
    private String version;
    private Integer size;

    private Integer projectId;

    private List<BugDTO> bugs;
}
