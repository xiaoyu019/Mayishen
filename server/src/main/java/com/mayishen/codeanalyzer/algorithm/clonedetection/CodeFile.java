package com.mayishen.codeanalyzer.algorithm.clonedetection;

import java.util.*;

public class CodeFile {
    private String fileName;
    private String filePath;
    private String fingerPrint;
    private String MD5;
    private Integer lineCount;
    private Integer type;

    public CodeFile(String fileName, String filePath, Set<Integer> fingerPrint, String MD5, Integer lineCount, Integer type) {
        this.fileName = fileName;
        this.filePath = filePath;
        if (fingerPrint != null) {
            this.setFingerPrint(fingerPrint);
        } else {
            this.fingerPrint = "";
        }
        this.MD5 = MD5;
        this.lineCount = lineCount;
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public Integer getLineCount() {
        return lineCount;
    }

    public void setLineCount(Integer lineCount) {
        this.lineCount = lineCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFingerPrint(Set<Integer> features) {
        List<Integer> featureIntegerList = new ArrayList<Integer>(features);
        List<String> featureStringList = new ArrayList<>();
        for (Integer feature : featureIntegerList) {
            featureStringList.add(String.valueOf(feature));
        }
        this.fingerPrint = String.join("-", featureStringList);
    }

    public Set<Integer> getFingerPrint() {
        List<String> features = Arrays.asList(this.fingerPrint.split("-"));
        Set<Integer> fp = new TreeSet<>();
        for (String feature : features) {
            if (feature != null && feature.length() > 0) {
                fp.add(Integer.valueOf(feature));
            }
        }
        return fp;
    }

    public String getFingerPrintString() {
        return fingerPrint;
    }

    public void setFingerPrint(String fingerPrint) {
        this.fingerPrint = fingerPrint;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "CodeFile{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fingerPrint='" + fingerPrint + '\'' +
                ", MD5='" + MD5 + '\'' +
                ", lineCount=" + lineCount +
                ", type=" + type +
                "}\n";
    }
}

