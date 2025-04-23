package com.mayishen.core.service;

import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.dto.OpenSourceFileDTO;
import com.mayishen.codeanalyzer.dto.OpenSourceProjectDTO;
import com.mayishen.codeanalyzer.dto.ProjectCodeFileDTO;
import com.mayishen.codeanalyzer.service.CodeProjectService;
import com.mayishen.codeanalyzer.service.OpenSourceFileService;
import com.mayishen.codeanalyzer.service.OpenSourceProjectService;
import com.mayishen.codeanalyzer.service.ProjectCodeFileService;
import com.mayishen.codeanalyzer.algorithm.clonedetection.CloneDetection;
import com.mayishen.codeanalyzer.algorithm.clonedetection.CodeFile;
import com.mayishen.codeanalyzer.dto.*;
import com.mayishen.codeanalyzer.service.*;
import com.mayishen.costestimation.algorithm.FunctionPointAnalyzer;
import com.mayishen.costestimation.dto.CostProjectDTO;
import com.mayishen.costestimation.service.CostProjectService;
import com.mayishen.costestimation.service.FunctionService;
import com.mayishen.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

@Service
public class AsyncService {
    @Autowired
    private CostProjectService costProjectService;
    @Autowired
    private FunctionService functionService;

    @Autowired
    private CodeProjectService codeProjectService;
    @Autowired
    private ProjectCodeFileService projectCodeFileService;
    @Autowired
    private OpenSourceProjectService openSourceProjectService;
    @Autowired
    private OpenSourceFileService openSourceFileService;
    @Autowired
    private SubTaskService subTaskService;

    /***
     * 异步任务：分析需求文档的功能点
     * @param project 项目的DTO实体
     */
    @Async
    public void analyzeFunctionPoint(CostProjectDTO project) {
        try {
            System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：分析软件成本分析项目【id=" + project.getId() + "]的功能点");

            // 初始化任务
            project.setProgress(0);
            project.setStatus(0);
            costProjectService.safelyUpdateById(project);

            // 清空功能点
            functionService.deleteByProjectId(project.getId());

            // 分析功能点
            String path = FileUtils.getUploadDir() + "/files/documents/" + project.getRequirementDocumentPath();
            FunctionPointAnalyzer.insertfunctionPoints(costProjectService, functionService, project.getId(), path);

            // 任务结束
            project.setProgress(100);
            project.setStatus(1);
            costProjectService.safelyUpdateById(project);
            System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
        } catch (Exception e) {
            e.printStackTrace();
            project.setStatus(-1);
            costProjectService.safelyUpdateById(project);
            Thread.currentThread().interrupt();
        }
    }

    private final Integer MAX_PROCESSING_NUM = 12;

    @Async
    public void analyzeSourceCode(CodeProjectDTO project) {
        try {
            System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：分析源码分析项目【id=" + project.getId() + "]的源码");

            // 初始化任务
            project.setProgress(0);
            project.setStatus(0);
            codeProjectService.safelyUpdateById(project);

            String flag = project.getSourceCodePath().substring(0, project.getSourceCodePath().lastIndexOf("."));
            String sourcePath = FileUtils.getUploadDir() + "/files/zips/" + project.getSourceCodePath(), targetPath = FileUtils.getUploadDir() + "/files/codes/" + flag + "/";

            // 解压缩源代码
            FileUtils.unzip(sourcePath, targetPath);
            // 分析代码文件
            projectCodeFileService.deleteByProjectId(project.getId());
            List<CodeFile> codeFiles = CloneDetection.getProjectCodeFiles(targetPath);
            codeProjectService.setProgress(project.getId(), 10);
            Integer validFileNumber = 0, totalLineNumber = 0, cnt = 0;
            for (CodeFile code : codeFiles) {
                ProjectCodeFileDTO dto = new ProjectCodeFileDTO();
                dto.setName(code.getFileName());
                dto.setType(code.getType());
                dto.setPath(code.getFilePath());
                dto.setMd5(code.getMD5());
                dto.setFingerPrint(code.getFingerPrintString());
                dto.setLineNumber(code.getLineCount());
                dto.setProjectId(project.getId());
                projectCodeFileService.insert(dto);
                if (code.getType() > 0) {
                    validFileNumber += 1;
                    totalLineNumber += code.getLineCount();
                }
                cnt++;
                codeProjectService.setProgress(project.getId(), (int) (10 + 10.0 * cnt / codeFiles.size()));
            }
            project.setTotalFileNumber(codeFiles.size());
            project.setValidFileNumber(validFileNumber);
            project.setTotalLineNumber(totalLineNumber);
            project.setProgress(20);
            codeProjectService.safelyUpdateById(project);
            subTaskService.detectionOpenSourceLibraryBug(targetPath, project);

            // 克隆检测
            openSourceProjectService.clearLinkWithCodeProject(project.getId());
            List<ProjectCodeFileDTO> codes = projectCodeFileService.selectByProjectId(project.getId());
            for (ProjectCodeFileDTO code : codes) {
                openSourceFileService.clearLinkWithProjectCodeFile(code.getId());
            }
            Integer openSourceProjectNumber = openSourceProjectService.count();
            Map<Integer, Integer> cloneProjectCodeFiles = new HashMap<>();
            Integer process_num = 1, finished_num = 0;
            List<OpenSourceProjectDTO> database = new ArrayList<>();
            database.addAll(openSourceProjectService.selectPage(process_num, 1, "").getRecords());
            List<Future<Set>> list = new ArrayList<>();
            while (database.size() > 0 || list.size() > 0) {
                while (database.size() < MAX_PROCESSING_NUM && process_num < openSourceProjectNumber) {
                    process_num++;
                    database.addAll(openSourceProjectService.selectPage(process_num, 1, "").getRecords());
                }
                while (database.size() > 0 && list.size() < MAX_PROCESSING_NUM)
                    list.add(subTaskService.analyzeByProject(project.getId(), database.remove(0).getId()));

                Iterator<Future<Set>> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Future<Set> future = iterator.next();
                    if (future.isDone()) {
                        Set<ProjectCodeFileDTO> set = future.get();
                        for (ProjectCodeFileDTO dto : set)
                            if (!cloneProjectCodeFiles.containsKey(dto.getId()))
                                cloneProjectCodeFiles.put(dto.getId(), dto.getLineNumber());
                        iterator.remove();
                        finished_num++;
                        codeProjectService.setProgress(project.getId(), (int) (20 + 75.0 * finished_num / openSourceProjectNumber));
                    }
                }
                System.out.println("进程池中尚有" + list.size() + "进程");
                Thread.sleep(1000);
            }

            // 任务结束
            System.out.println("统计克隆情况");
            Integer totalCloneFileCount = cloneProjectCodeFiles.values().size(), totalCloneLineCount = cloneProjectCodeFiles.values().stream().reduce(0, Integer::sum);
            System.out.println("克隆文件" + totalCloneFileCount);
            System.out.println("克隆行数" + totalCloneLineCount);
            project.setCloneFileNumber(totalCloneFileCount);
            project.setCloneLineNumber(totalCloneLineCount);
            project.setStatus(1);
            project.setProgress(100);
            codeProjectService.safelyUpdateById(project);
            System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
        } catch (Exception e) {
            e.printStackTrace();
            project.setStatus(-1);
            codeProjectService.safelyUpdateById(project);
            Thread.currentThread().interrupt();
        }
    }


    /***
     * 增加开源代码的异步任务
     * @param projectDTO 开源代码的DTO实体
     */
    @Async
    public void addOpenSourceCode(OpenSourceProjectDTO projectDTO) {
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：分析开源项目代码" + projectDTO.toString());
        String flag = projectDTO.getPath().substring(0, projectDTO.getPath().lastIndexOf("."));
        String sourcePath = FileUtils.getUploadDir() + "/files/zips/" + projectDTO.getPath(), targetPath = FileUtils.getUploadDir() + "/files/open-source-codes/" + flag + "/";
        try {
            FileUtils.deleteFolder(targetPath);
            FileUtils.unzip(sourcePath, targetPath);
            List<CodeFile> codeFiles = CloneDetection.getProjectCodeFiles(targetPath);
            Integer validFileNumber = 0, totalLineNumber = 0;
            for (CodeFile code : codeFiles) {
                if (code.getType() > 0) {
                    validFileNumber += 1;
                    totalLineNumber += code.getLineCount();
                    OpenSourceFileDTO dto = new OpenSourceFileDTO();
                    dto.setName(code.getFileName());
                    dto.setType(code.getType());
                    dto.setPath(code.getFilePath());
                    dto.setMd5(code.getMD5());
                    dto.setFingerPrint(code.getFingerPrintString());
                    dto.setLineNumber(code.getLineCount());
                    dto.setOpenSourceProjectID(projectDTO.getId());
                    openSourceFileService.insert(dto);
                }
            }
            projectDTO.setValidFileNumber(validFileNumber);
            projectDTO.setTotalLineNumber(totalLineNumber);
            openSourceProjectService.updateById(projectDTO);
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
    }
}