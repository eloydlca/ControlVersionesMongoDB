package org.example.model;

import org.bson.types.Binary;
import org.bson.types.ObjectId;

public class Version {

    private float version;
    private String versionDescription;
    private Binary data;

    public Version(float version, String versionDescription, Binary data) {
        this.version = version;
        this.versionDescription = versionDescription;
        this.data = data;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public void setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
    }

    public Binary getData() {
        return data;
    }

    public void setData(Binary data) {
        this.data = data;
    }
}
