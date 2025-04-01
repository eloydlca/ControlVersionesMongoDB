package org.example.ui;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDAO implements IMongoDAO {
    private static final String CONNECTION_STRING = "mongodb://172.16.0.91:27017";
    private static final String DATABASE_NAME = "controlVersion";
    private static final String COLLECTION_NAME = "files";

    private MongoDatabase database;

    public MongoDAO() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
    }
}
