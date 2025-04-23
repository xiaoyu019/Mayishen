package com.mayishen.utils;

import com.mayishen.codeanalyzer.dto.CodeProjectDTO;
import com.mayishen.codeanalyzer.dto.OpenSourceProjectDTO;
import com.mayishen.core.dto.FileDTO;
import com.mayishen.exception.ServiceException;
import com.mayishen.common.Constants;
import com.mayishen.costestimation.dto.CostProjectDTO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/***
 * 文件工具
 */
public class FileUtils {
    private static int BUFFER = 1024;

    // 本地路径
    private final static String uploadDir = "D:/Mayishen";

    // 远程服务器路径
    //    private final static String uploadDir = "/home/Mayishen";
    public static String getUploadDir() {
        return uploadDir;
    }

    /***
     * 测试.ZIP压缩文件是否能解压
     * @param filePath 文件地址
     * @return 是否能解压
     */
    public static Boolean testZipFile(String filePath) {
        List<String> charsets = Arrays.asList("GBK", "UTF-8");
        for (String charset : charsets) {
            try {
                ZipFile zipfile = new ZipFile(filePath, Charset.forName(charset));
                zipfile.close();
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    /***
     * 解压目标压缩文件到对应目录
     * @param filePath 目标压缩文件路径
     * @param zipDir 解压缩目录
     * @return
     */
    public static String unzip(String filePath, String zipDir) {
        File file = new File(zipDir);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        } else {
            deleteFolder(zipDir);
            file.mkdirs();
        }
        String name = "";
        List<String> charsets = Arrays.asList("GBK", "UTF-8");
        for (String charset : charsets) {
            try {
                BufferedOutputStream dest = null;
                BufferedInputStream is = null;
                ZipEntry entry;
                ZipFile zipfile = new ZipFile(filePath, Charset.forName(charset));

                Enumeration dir = zipfile.entries();
                while (dir.hasMoreElements()) {
                    entry = (ZipEntry) dir.nextElement();

                    if (entry.isDirectory()) {
                        name = entry.getName();
                        name = name.substring(0, name.length() - 1);
                        File fileObject = new File(zipDir + name);
                        fileObject.mkdir();
                    }
                }

                Enumeration e = zipfile.entries();
                while (e.hasMoreElements()) {
                    entry = (ZipEntry) e.nextElement();
                    if (entry.isDirectory()) {
                        continue;
                    } else {
                        is = new BufferedInputStream(zipfile.getInputStream(entry));
                        int count;
                        byte[] dataByte = new byte[BUFFER];
                        FileOutputStream fos = new FileOutputStream(zipDir + entry.getName());
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = is.read(dataByte, 0, BUFFER)) != -1) {
                            dest.write(dataByte, 0, count);
                        }
                        dest.flush();
                        dest.close();
                        is.close();
                    }
                }
                return name;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new ServiceException(Constants.CODE_500, "解压缩失败");
    }

    /***
     * 删除目标文件夹
     * @param folderPath 文件夹地址
     */
    public static void deleteFolder(String folderPath) {
        try {
            File baseFolder = new File(folderPath);
            if (baseFolder.exists()) {
                deleteAllFileInFolder(folderPath);
                baseFolder.delete();
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /***
     * 删除目标路径所有文件夹
     * @param path 文件夹地址
     * @return 是否删除
     */
    public static boolean deleteAllFileInFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFileInFolder(path + File.separator + tempList[i]);
                deleteFolder(path + File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }


    /***
     * 根据文件名检索文件路径
     * @param dirPath 目标文件夹路径
     * @param fName 文件名
     * @return 文件路径
     */
    public static String findFilePathByFileName(String dirPath, String fName) {
        List<String> fileNames = new LinkedList<>();
        fileNames.add(dirPath);
        while (fileNames.size() > 0) {
            String fileName = fileNames.remove(0);
            File file = new File(fileName);
            if (file.isDirectory()) {
                String[] subFolderFileNames = file.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return new File(dir, name).isDirectory() || name.toLowerCase().contains(fName.toLowerCase());
                    }
                });
                for (String subFolderFileName : subFolderFileNames) {
                    if (subFolderFileName.contains(fName))
                        return fileName + "/" + subFolderFileName;
                    fileNames.add(fileName + "/" + subFolderFileName);
                }
            }

        }
        return "";
    }

    /***
     * 根据文件夹路径检索文件
     * @param basePath 主路径
     * @param dirPath 主路径下的文件夹路径
     * @return 文件列表
     */
    public static List<FileDTO> findFilesByDirPath(String basePath, String dirPath) {
        List<FileDTO> fileNames = new ArrayList<>();
        File file = new File(basePath + "/" + dirPath);
        if (file.isDirectory()) {
            String[] subFolderFileNames = file.list();
            for (String subFolderFileName : subFolderFileNames) {
                FileDTO dto = new FileDTO();
                dto.setName(subFolderFileName);
                String path = dirPath + "/" + subFolderFileName;
                path.replace("//", "/");
                dto.setPath(path);
                File subFile = new File(basePath + path);
                if (subFile.isDirectory()) {
                    dto.setIsDir(true);
                    dto.setFileCount(subFile.list().length);
                } else {
                    dto.setIsDir(false);
                    dto.setSize(FormetFileSize(subFile.length()));
                }
                fileNames.add(dto);
            }
        }
        return fileNames;
    }

    /***
     * 获取文件大小
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /***
     * 移动文档
     * @param project 项目的DTO实体
     * @throws IOException
     */
    public static void moveDocument(CostProjectDTO project) throws IOException {
        Path source = Paths.get(uploadDir + "/files/upload/" + project.getRequirementDocumentPath());
        Path dest = Paths.get(uploadDir + "/files/documents/" + project.getRequirementDocumentPath());
        Files.move(source, dest);
    }

    /***
     * 移动源码
     * @param project 项目的DTO实体
     * @throws IOException
     */
    public static void moveSourceCode(CodeProjectDTO project) throws IOException {
        Path source = Paths.get(uploadDir + "/files/upload/" + project.getSourceCodePath());
        Path dest = Paths.get(uploadDir + "/files/zips/" + project.getSourceCodePath());
        Files.move(source, dest);
    }

    /***
     * 移动源码
     * @param project 项目的DTO实体
     * @throws IOException
     */
    public static void moveSourceCode(OpenSourceProjectDTO project) throws IOException {
        Path source = Paths.get(uploadDir + "/files/upload/" + project.getPath());
        Path dest = Paths.get(uploadDir + "/files/zips/" + project.getPath());
        Files.move(source, dest);
    }
}
