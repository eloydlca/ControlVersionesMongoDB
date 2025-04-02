package org.example.dao;

import org.bson.types.ObjectId;
import org.example.model.UserFile;
import org.example.model.Version;

import java.util.List;

public interface IMongoDAO {
    void saveFile(UserFile file);
    List<UserFile> getAllFiles();
    UserFile getFileById(ObjectId id);
    void updateFile(UserFile file);
    long deleteFile(ObjectId id);
    void addVersionToFile(ObjectId fileId, Version newVersion);
    void removeVersionFromFile(ObjectId fileId, float versionNumber);
}
