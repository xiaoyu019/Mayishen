package com.mayishen.core.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.service.CodeProjectService;
import com.mayishen.codeanalyzer.service.ProjectCodeFileService;
import com.mayishen.common.Constants;
import com.mayishen.common.Result;
import com.mayishen.core.dto.FileDTO;
import com.mayishen.exception.ServiceException;
import com.mayishen.utils.FileUtils;
import com.mayishen.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    private CodeProjectService codeProjectService;
    @Autowired
    private ProjectCodeFileService projectCodeFileService;

    /***
     * 上传文件
     * @param file 文件
     * @return 文件网络地址
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result<?> upload(MultipartFile file) throws IOException {
        // 生成文件保存路径
        String originFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String rootFilePath = FileUtils.getUploadDir() + "/files/upload/" + flag + originFilename.substring(originFilename.lastIndexOf("."));
        // 保存文件服务器磁盘
        FileUtil.writeBytes(file.getBytes(), rootFilePath);
        // 判断上传文件类型
        if (originFilename.substring(originFilename.lastIndexOf(".")).contains("zip")) {
            // 如果上传文件后缀为zip
            // 检测压缩文件是否可解压
            if (FileUtils.testZipFile(rootFilePath)) {
                return Result.success(flag + originFilename.substring(originFilename.lastIndexOf(".")));
            } else {
                FileUtils.deleteFolder(rootFilePath);
                return Result.error(Constants.CODE_401, "压缩文件错误");
            }
        } else {
            // 如果上文件后缀为其他
            return Result.success(flag + originFilename.substring(originFilename.lastIndexOf(".")));
        }
    }

    /***
     * 获取文件
     * @param flag 文件标识
     * @param response 返回文件流的响应
     */
    @GetMapping("/get/{flag}")
    public void getFiles(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        String basePath = FileUtils.getUploadDir() + "/files/";
        // 按文件类型索引文件夹
        if (flag.contains(".zip")) {
            basePath += "zips/";
        } else if (flag.contains(".doc") || flag.contains(".docx")) {
            basePath += "documents/";
        } else {
            basePath += "upload/";
        }
        // 索引相应文件
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String avatar = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(avatar)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(avatar, "UTF-8"));
                response.setContentType("application/octet-stream");
                byte[] bytes = FileUtil.readBytes(basePath + avatar);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500, "文件下载失败");
        }
    }

    /***
     * 获取文件夹文件列表
     * @param pid 项目ID
     * @param dirPath 项目内文件路径
     * @return 文件列表
     */
    @GetMapping("/getDirFiles")
    public Result<?> getDirFiles(@RequestParam(defaultValue = "1") Integer pid, @RequestParam(defaultValue = "") String dirPath) {
        CodeProjectDTO res = codeProjectService.selectById(pid);
        // 判断权限
        if (TokenUtils.getUser().getRole() != 0 && res.getUserID() != TokenUtils.getUserId()) {
            return Result.error(Constants.CODE_401, "文件夹权限错误");
        }
        // 索引文件夹内子文件夹和文件
        String basePath = FileUtils.getUploadDir() + "/files/codes/" + res.getSourceCodePath().substring(0, res.getSourceCodePath().lastIndexOf("."));
        List<FileDTO> files = FileUtils.findFilesByDirPath(basePath, dirPath);
        for (FileDTO dto : files) {
            if (!dto.getIsDir()) {
                dto.setCodeFile(projectCodeFileService.selectByPath(pid, dto.getPath()));
            }
        }
        return Result.success(files);
    }
}
