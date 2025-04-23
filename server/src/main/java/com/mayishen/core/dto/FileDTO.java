package com.mayishen.core.dto;

import com.mayishen.codeanalyzer.dto.ProjectCodeFileDTO;
import lombok.Data;

@Data
public class FileDTO {
    private String name;
    private String path;

    private Boolean isDir;
    private Integer fileCount;
    private String size;

    private ProjectCodeFileDTO codeFile;
}
