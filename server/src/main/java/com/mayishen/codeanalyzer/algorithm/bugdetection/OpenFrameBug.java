package com.mayishen.codeanalyzer.algorithm.bugdetection;

public class OpenFrameBug {
    private String id;
    private String frameName;
    private String bugName;
    private String summary;
    private String modified;
    private String published;
    private String level;
    private String versions;

    public void Bug() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrameName() {
        return frameName;
    }

    public void setFrameName(String frameName) {
        this.frameName = frameName;
    }

    public void setBugName(String bugName) {
        this.bugName = bugName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getLevel() {
        return level;
    }

    public Integer getBugLevel() {
        switch (level) {
            case "INFO":
                return 0;
            case "LOW":
                return 1;
            case "MODERATE":
                return 2;
            case "HIGH":
                return 3;
            case "CRITICAL":
                return 4;
            default:
                return -1;
        }
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getBugName() {
        return bugName;
    }
}
