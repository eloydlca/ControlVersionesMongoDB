package org.example.model;

import org.bson.types.Binary;

public class Version {
    private float version;
    private Binary data;

    public Version(float version, byte[] data) {
        this.version = version;
        this.data = new Binary(data);
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    public Binary getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = new Binary(data);
    }
}
