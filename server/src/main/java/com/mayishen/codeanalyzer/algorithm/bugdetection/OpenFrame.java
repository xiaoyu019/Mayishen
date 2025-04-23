package com.mayishen.codeanalyzer.algorithm.bugdetection;

import java.util.Arrays;

public class OpenFrame {
    private String name;
    private String version;

    private int size;
    private OpenFrameBug[] openFrameBug;

    public void OpenFrame() {

    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public OpenFrameBug[] getOpenFrameBug() {
        return openFrameBug;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setOpenFrameBug(OpenFrameBug[] openFrameBug) {
        this.openFrameBug = openFrameBug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "OpenFrame{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", size=" + size +
                ", bug=" + Arrays.toString(openFrameBug) +
                '}';
    }
}
