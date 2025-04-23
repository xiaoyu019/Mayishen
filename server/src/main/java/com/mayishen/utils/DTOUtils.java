package com.mayishen.utils;

import com.mayishen.codeanalyzer.dto.*;
import com.mayishen.codeanalyzer.entity.*;
import com.mayishen.core.dto.UserDTO;
import com.mayishen.core.entity.User;
import com.mayishen.codeanalyzer.dto.*;
import com.mayishen.codeanalyzer.entity.*;
import com.mayishen.costestimation.dto.CostProjectDTO;
import com.mayishen.costestimation.dto.FunctionDTO;
import com.mayishen.costestimation.entity.CostProject;
import com.mayishen.costestimation.entity.Function;
import com.mayishen.core.entity.*;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/***
 * 实体转换DTO工具
 */
public class DTOUtils {

    public static List<CostProjectDTO> toCostProjectDTOList(List<CostProject> list) {
        List<CostProjectDTO> res = new ArrayList<>();
        for (CostProject p : list) {
            CostProjectDTO dto = new CostProjectDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static CostProjectDTO toDTO(CostProject costProject) {
        CostProjectDTO dto = new CostProjectDTO();
        BeanUtils.copyProperties(costProject, dto);
        return dto;
    }

    public static List<CodeProjectDTO> toCodeProjectDTOList(List<CodeProject> list) {
        List<CodeProjectDTO> res = new ArrayList<>();
        for (CodeProject p : list) {
            CodeProjectDTO dto = new CodeProjectDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static CodeProjectDTO toDTO(CodeProject codeProject) {
        CodeProjectDTO dto = new CodeProjectDTO();
        BeanUtils.copyProperties(codeProject, dto);
        return dto;
    }


    public static OpenSourceLibraryDTO toDTO(OpenSourceLibrary osl) {
        OpenSourceLibraryDTO dto = new OpenSourceLibraryDTO();
        BeanUtils.copyProperties(osl, dto);
        return dto;
    }

    public static List<OpenSourceLibraryDTO> toOpenSourceLibraryDTOList(List<OpenSourceLibrary> list) {
        List<OpenSourceLibraryDTO> res = new ArrayList<>();
        for (OpenSourceLibrary p : list) {
            OpenSourceLibraryDTO dto = new OpenSourceLibraryDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }


    public static List<FunctionDTO> toFunctionDTOList(List<Function> list) {
        List<FunctionDTO> res = new ArrayList<>();
        for (Function p : list) {
            FunctionDTO dto = new FunctionDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static FunctionDTO toDTO(Function function) {
        FunctionDTO dto = new FunctionDTO();
        BeanUtils.copyProperties(function, dto);
        return dto;
    }

    public static List<BugDTO> toBugDTOList(List<Bug> list) {
        List<BugDTO> res = new ArrayList<>();
        for (Bug p : list) {
            BugDTO dto = new BugDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static BugDTO toDTO(Bug bug) {
        BugDTO dto = new BugDTO();
        BeanUtils.copyProperties(bug, dto);
        return dto;
    }

    public static List<UserDTO> toUserDTOList(List<User> list) {
        List<UserDTO> res = new ArrayList<>();
        for (User p : list) {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public static List<ProjectCodeFileDTO> toProjectCodeFileDTOList(List<ProjectCodeFile> list) {
        List<ProjectCodeFileDTO> res = new ArrayList<>();
        for (ProjectCodeFile p : list) {
            ProjectCodeFileDTO dto = new ProjectCodeFileDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static ProjectCodeFileDTO toDTO(ProjectCodeFile projectCodeFile) {
        ProjectCodeFileDTO dto = new ProjectCodeFileDTO();
        BeanUtils.copyProperties(projectCodeFile, dto);
        return dto;
    }

    public static List<OpenSourceProjectDTO> toOpenSourceProjectDTOList(List<OpenSourceProject> list) {
        List<OpenSourceProjectDTO> res = new ArrayList<>();
        for (OpenSourceProject p : list) {
            OpenSourceProjectDTO dto = new OpenSourceProjectDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static OpenSourceProjectDTO toDTO(OpenSourceProject openSourceProject) {
        OpenSourceProjectDTO dto = new OpenSourceProjectDTO();
        BeanUtils.copyProperties(openSourceProject, dto);
        return dto;
    }

    public static List<OpenSourceFileDTO> toOpenSourceFileDTOList(List<OpenSourceFile> list) {
        List<OpenSourceFileDTO> res = new ArrayList<>();
        for (OpenSourceFile p : list) {
            OpenSourceFileDTO dto = new OpenSourceFileDTO();
            BeanUtils.copyProperties(p, dto);
            res.add(dto);
        }
        return res;
    }

    public static OpenSourceFileDTO toDTO(OpenSourceFile openSourceFile) {
        OpenSourceFileDTO dto = new OpenSourceFileDTO();
        BeanUtils.copyProperties(openSourceFile, dto);
        return dto;
    }
}
