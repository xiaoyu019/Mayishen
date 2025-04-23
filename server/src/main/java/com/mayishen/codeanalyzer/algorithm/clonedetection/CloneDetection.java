package com.mayishen.codeanalyzer.algorithm.clonedetection;

import com.mayishen.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CloneDetection {
    public static Set<Integer> getCodeFingerPrint(String code) throws NoSuchAlgorithmException {
        return new Winnowing().getHashFingerPrint(code, "<|sep|>");
    }

    public static float getSimilarity(Set<Integer> code1, Set<Integer> code2) {
        if (code1.size() == 0 || code2.size() == 0)
            return 0.0f;
        Set<Integer> resSet = new HashSet<>();
        resSet.addAll(code1);
        resSet.retainAll(code2);
        Set<Integer> allSet = new HashSet<>();
        allSet.addAll(code1);
        allSet.addAll(code2);
        return 2.0f * resSet.size() / (code1.size() + code2.size());
        // return (float) resSet.size()/allSet.size();
    }

    public static List<CodeFile> getProjectCodeFiles(String projectPath) throws IOException, NoSuchAlgorithmException {
        List<CodeFile> list = new ArrayList<>();
        List<String> fileNames = new LinkedList<>();
        fileNames.add(projectPath);
        while (fileNames.size() > 0) {
            String fileName = fileNames.remove(0);
            File file = new File(fileName);
            if (file.isDirectory()) {
                String[] subFolderFileNames = file.list();
                for (String subFolderFileName : subFolderFileNames)
                    fileNames.add(fileName + "/" + subFolderFileName);
            } else {
                if (fileName.toLowerCase().matches("^.+(java|jsp|css|js|ts|xml|vue|properties|php|py)$")) {
                    List<String> code = null;
                    try {
                        code = Files.readAllLines(Paths.get(fileName));
                    } catch (MalformedInputException e) {
                        try {
                            Charset charset = Charset.forName("GBK");
                            code = Files.readAllLines(Paths.get(fileName), charset);
                        } catch (MalformedInputException | UnmappableCharacterException ee) {
                            System.out.println(fileName + "读取失败");
                            continue;
                        }
                    }
                    code.removeIf(String::isEmpty);
                    String text = null;
                    text = String.join("<|sep|>", code);
                    list.add(new CodeFile(fileName.substring(fileName.lastIndexOf("/") + 1), fileName.substring(fileName.lastIndexOf("//") + 1), getCodeFingerPrint(text), getMD5(file), code.size(), getType(fileName)));
                } else {
                    list.add(new CodeFile(fileName.substring(fileName.lastIndexOf("/") + 1), fileName.substring(fileName.lastIndexOf("//") + 1), null, getMD5(file), 0, 0));
                    FileUtils.deleteFolder(fileName);
                }
            }

        }
        return list;
    }

    public static String getMD5(File f) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }


    public static Integer getType(String filename) {
        if (filename.matches("^.+(java)$"))
            return 1;
        if (filename.matches("^.+(css)$"))
            return 2;
        if (filename.matches("^.+(js)$"))
            return 3;
        if (filename.matches("^.+(ts)$"))
            return 4;
        if (filename.matches("^.+(xml)$"))
            return 5;
//        if(filename.matches("^.+(html)$"))
//            return 6;
        if (filename.matches("^.+(vue)$"))
            return 6;
        if (filename.matches("^.+(jsp)$"))
            return 7;
        if (filename.matches("^.+(properties)$"))
            return 8;
        if (filename.matches("^.+(php)$"))
            return 9;
        if (filename.matches("^.+(py)$"))
            return 10;
        return 0;
    }
}
