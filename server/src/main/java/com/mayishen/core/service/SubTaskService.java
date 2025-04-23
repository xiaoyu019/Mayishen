package com.mayishen.core.service;

import cn.hutool.core.bean.BeanUtil;
import com.mayishen.codeanalyzer.dto.*;
import com.mayishen.codeanalyzer.service.*;
import com.mayishen.codeanalyzer.algorithm.bugdetection.JsonToForm;
import com.mayishen.codeanalyzer.algorithm.bugdetection.OpenFrame;
import com.mayishen.codeanalyzer.algorithm.bugdetection.OpenFrameBug;
import com.mayishen.codeanalyzer.algorithm.clonedetection.CloneDetection;
import com.mayishen.codeanalyzer.dto.*;
import com.mayishen.codeanalyzer.service.*;
import com.mayishen.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Future;

@Async
@Service
public class SubTaskService {
    @Autowired
    private BugService bugService;
    @Autowired
    private OpenSourceLibraryService openSourceLibraryService;
    @Autowired
    private ProjectCodeFileService projectCodeFileService;
    @Autowired
    private OpenSourceFileService openSourceFileService;
    @Autowired
    private OpenSourceProjectService openSourceProjectService;

    /***
     * 异步子任务：依赖库漏洞检测
     * @param targetPath 目标路径
     * @param project 项目实体
     */
    @Async
    public void detectionOpenSourceLibraryBug(String targetPath, CodeProjectDTO project) {
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：分析源码分析项目【id=" + project.getId() + "]的开源库漏洞");

        //检索pom.xml文件路径
        String xml_path = FileUtils.findFilePathByFileName(targetPath, "pom.xml");
        if (xml_path.length() > 0) {
            // 获取开源库和开源库漏洞
            openSourceLibraryService.deleteByProjectId(project.getId());
            List<OpenFrame> openFrames = Arrays.asList(JsonToForm.getResult(xml_path));

            // 插入数据库
            for (OpenFrame frame : openFrames) {
                OpenSourceLibraryDTO o = new OpenSourceLibraryDTO();
                BeanUtil.copyProperties(frame, o);
                o.setProjectId(project.getId());
                openSourceLibraryService.insert(o);
                if (frame.getOpenFrameBug() != null)
                    for (OpenFrameBug openFrameBug : frame.getOpenFrameBug()) {
                        BugDTO bug = new BugDTO();
                        bug.setName(openFrameBug.getBugName());
                        bug.setLibraryName(openFrameBug.getFrameName());
                        bug.setVersion(openFrameBug.getVersions());
                        bug.setDescription(openFrameBug.getSummary());
                        bug.setLevel(openFrameBug.getBugLevel());
                        bug.setPublishedTime(openFrameBug.getPublished());
                        bug.setModifiedTime(openFrameBug.getModified());
                        bugService.insert(bug);
                        bugService.linkWithOpenSourceLibrary(o.getId(), bug.getId());
                    }
            }
        }
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
    }

    /***
     *  根据文件类型分析相似文件
     * @param pid 项目ID
     * @param oid 开源项目ID
     * @param t 类型
     * @return 相似文件分析结果
     * @throws NoSuchAlgorithmException
     */
    @Async
    public Future<List<Set>> analyzeByType(Integer pid, Integer oid, Integer t) throws NoSuchAlgorithmException {
        System.out.println(" 【" + Thread.currentThread().getName() + " 代码文件分析】当前类型为" + t);
        Set<ProjectCodeFileDTO> cloneProjectCodeFiles = new HashSet<>();
        Set<OpenSourceFileDTO> cloneCodeFiles = new HashSet<>();
        List<ProjectCodeFileDTO> codesA = projectCodeFileService.selectByType(pid, t);
        List<OpenSourceFileDTO> codesB = openSourceFileService.selectByType(oid, t);
        for (ProjectCodeFileDTO codeA : codesA) {
            for (OpenSourceFileDTO codeB : codesB) {
                // 根据MD5值或代码指纹判断相似度
                if (codeA.getMd5().equals(codeB.getMd5())) {
                    cloneProjectCodeFiles.add(codeA);
                    cloneCodeFiles.add(codeB);
                    openSourceFileService.linkWithProjectCodeFile(codeA.getId(), codeB.getId());
                } else {
                    if (codeB.getLineNumber() >= 0.5 * codeA.getLineNumber() && codeB.getLineNumber() <= 1.5 * codeA.getLineNumber()) {
                        float sim = CloneDetection.getSimilarity(CloneDetection.getCodeFingerPrint(codeA.getFingerPrint()), CloneDetection.getCodeFingerPrint(codeB.getFingerPrint()));
                        // 相似度大于80%认定相似
                        if (sim >= 0.80f) {
                            cloneProjectCodeFiles.add(codeA);
                            cloneCodeFiles.add(codeB);
                            openSourceFileService.linkWithProjectCodeFile(codeA.getId(), codeB.getId());
                        }
                        ;
                    }
                }
            }
        }
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
        return new AsyncResult<List<Set>>(Arrays.asList(cloneProjectCodeFiles, cloneCodeFiles));
    }

    /***
     * 根据项目分析相似文件
     * @param pid 项目ID
     * @param oid 开源项目ID
     * @return 相似文件分析结果
     * @throws NoSuchAlgorithmException
     */
    @Async
    public Future<Set> analyzeByProject(Integer pid, Integer oid) throws NoSuchAlgorithmException {
        System.out.println(" 【" + Thread.currentThread().getName() + " 代码文件分析】当前分析开源项目ID为" + oid);
        Set<ProjectCodeFileDTO> cloneProjectCodeFiles = new HashSet<>();
        Set<OpenSourceFileDTO> cloneCodeFiles = new HashSet<>();
        for (Integer t = 1; t <= 11; t++) {
            System.out.println(" 【" + Thread.currentThread().getName() + " 代码文件分析】当前类型为" + t);
            List<ProjectCodeFileDTO> codesA = projectCodeFileService.selectByType(pid, t);
            List<OpenSourceFileDTO> codesB = openSourceFileService.selectByType(oid, t);
            for (ProjectCodeFileDTO codeA : codesA) {
                for (OpenSourceFileDTO codeB : codesB) {
                    // 根据MD5值或者代码指纹判断相似结果
                    if (codeA.getMd5().equals(codeB.getMd5())) {
                        cloneProjectCodeFiles.add(codeA);
                        cloneCodeFiles.add(codeB);
                        openSourceFileService.linkWithProjectCodeFile(codeA.getId(), codeB.getId());
                    } else {
                        if (codeB.getLineNumber() >= 0.5 * codeA.getLineNumber() && codeB.getLineNumber() <= 1.5 * codeA.getLineNumber()) {
                            float sim = CloneDetection.getSimilarity(CloneDetection.getCodeFingerPrint(codeA.getFingerPrint()), CloneDetection.getCodeFingerPrint(codeB.getFingerPrint()));
                            // 相似度大于80%认定相似
                            if (sim >= 0.80f) {
                                cloneProjectCodeFiles.add(codeA);
                                cloneCodeFiles.add(codeB);
                                openSourceFileService.linkWithProjectCodeFile(codeA.getId(), codeB.getId());
                            }
                            ;
                        }
                    }
                }
            }

        }
        Integer cloneFileCount = cloneCodeFiles.size(), cloneLineCount = cloneCodeFiles.stream().map(OpenSourceFileDTO::getLineNumber).reduce(0, Integer::sum);
        if (cloneFileCount > 0) {
            System.out.println("项目ID：" + pid + " 克隆开源项目ID：" + oid + " 克隆文件数：" + cloneFileCount + " 克隆行数：" + cloneLineCount);
            openSourceProjectService.linkWithCodeProject(pid, oid, cloneFileCount, cloneLineCount);
        }
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务完成");
        return new AsyncResult<Set>(cloneProjectCodeFiles);
    }

}
