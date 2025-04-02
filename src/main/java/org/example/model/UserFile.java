package org.example.model;

import org.bson.types.ObjectId;

public class UserFile {
    private ObjectId id;
    private String fileName;
    private String fileExtension;
    private Version[] versions;

    public UserFile(String fileName, String fileExtension, Version[] versions) {
        this.id = new ObjectId();
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.versions = versions;
    }

    public UserFile(ObjectId id, String fileName, String fileExtension, Version[] versions) {
        this.id = id;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.versions = versions;
    }

    public ObjectId getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Version[] getVersions() {
        return versions;
    }

    public void setVersions(Version[] versions) {
        this.versions = versions;
    }
}
