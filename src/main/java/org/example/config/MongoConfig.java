package org.example.config;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
public class MongoConfig {
    // Ganti dengan connection string Atlas kamu
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    private static final String DATABASE_NAME = "automation_db";
    private static MongoClient mongoClient;
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            System.out.println("[MongoDB] Connected to Atlas");
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("[MongoDB] Connection closed");
        }
    }
}