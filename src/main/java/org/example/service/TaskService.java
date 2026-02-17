package org.example.service;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.config.MongoConfig;
import org.example.model.Task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class TaskService {
    private static final String COLLECTION_NAME = "tasks";

    public ObjectId createTask(Task task) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = taskToDocument(task);
        InsertOneResult result = collection.insertOne(doc);
        ObjectId insertedId = result.getInsertedId() != null
                ? result.getInsertedId().asObjectId().getValue()
                : null;
        System.out.println("[TaskService] Task baru dibuat → ID: " + insertedId);
        return insertedId;
    }
    public int createTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) return 0;
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        List<Document> docs = new ArrayList<>();
        for (Task task : tasks) {
            docs.add(taskToDocument(task));
        }
        collection.insertMany(docs);
        System.out.println("[TaskService] " + docs.size() + " task berhasil disimpan");
        return docs.size();
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find()) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Total task ditemukan: " + tasks.size());
        return tasks;
    }
    public Task getTaskById(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            System.out.println("[TaskService] Task tidak ditemukan → ID: " + id);
            return null;
        }
        System.out.println("[TaskService] Task ditemukan → ID: " + id);
        return documentToTask(doc);
    }
    public List<Task> getTasksByStatus(String status) {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("status", status))) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Ditemukan " + tasks.size() + " task dengan status: " + status);
        return tasks;
    }
    public List<Task> getPendingTasksByPriority() {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection
                .find(Filters.eq("status", "pending"))
                .sort(Sorts.descending("priority"))) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Ditemukan " + tasks.size() + " task pending");
        return tasks;
    }
    public List<Task> getTasksByType(String taskType) {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("task_type", taskType))) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Ditemukan " + tasks.size() + " task dengan type: " + taskType);
        return tasks;
    }
    public List<Task> getTasksByAssignedDevice(ObjectId deviceId) {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("assigned_device", deviceId))) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Ditemukan " + tasks.size() + " task untuk device: " + deviceId);
        return tasks;
    }
    public List<Task> getTasksByAssignedAccount(ObjectId accountId) {
        List<Task> tasks = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("assigned_account", accountId))) {
            tasks.add(documentToTask(doc));
        }
        System.out.println("[TaskService] Ditemukan " + tasks.size() + " task untuk account: " + accountId);
        return tasks;
    }
    public long countAllTasks() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments();
        System.out.println("[TaskService] Total task: " + count);
        return count;
    }
    public long countTasksByStatus(String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments(Filters.eq("status", status));
        System.out.println("[TaskService] Total task [" + status + "]: " + count);
        return count;
    }

    public boolean updateTask(Task task) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", task.getId()),
                Updates.combine(
                        Updates.set("task_type", task.getTaskType()),
                        Updates.set("app_name", task.getAppName()),
                        Updates.set("app_package", task.getAppPackage()),
                        Updates.set("app_url", task.getAppUrl()),
                        Updates.set("testing_url", task.getTestingUrl()),
                        Updates.set("activities", task.getActivities()),
                        Updates.set("testers_emails", task.getTestersEmails()),
                        Updates.set("priority", task.getPriority()),
                        Updates.set("status", task.getStatus()),
                        Updates.set("retry_count", task.getRetryCount()),
                        Updates.set("error_message", task.getErrorMessage()),
                        Updates.set("failed_at", task.getFailedAt() != null ? task.getFailedAt().toString() : null),
                        Updates.set("assigned_account", task.getAssignedAccount()),
                        Updates.set("assigned_device", task.getAssignedDevice())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[TaskService] Update task " + task.getId() + " → " + (updated ? "berhasil" : "tidak ada perubahan"));
        return updated;
    }
    public boolean updateTaskStatus(ObjectId id, String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("status", status)
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[TaskService] Task " + id + " → status: " + status + (updated ? "" : " (tidak berubah)"));
        return updated;
    }
    public boolean markTaskAsFailed(ObjectId id, String errorMessage) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("status", "failed"),
                        Updates.set("error_message", errorMessage),
                        Updates.set("failed_at", LocalDateTime.now().toString())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[TaskService] Task " + id + " → failed | error: " + errorMessage);
        return updated;
    }
    public boolean assignTask(ObjectId id, ObjectId deviceId, ObjectId accountId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("assigned_device", deviceId),
                        Updates.set("assigned_account", accountId),
                        Updates.set("status", "running")
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[TaskService] Task " + id + " → assigned | device: " + deviceId + " | account: " + accountId);
        return updated;
    }
    public boolean retryTask(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.inc("retry_count", 1),
                        Updates.set("status", "pending"),
                        Updates.set("assigned_device", null),
                        Updates.set("assigned_account", null),
                        Updates.set("error_message", null),
                        Updates.set("failed_at", null)
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[TaskService] Task " + id + " → di-retry");
        return updated;
    }
    public long bulkUpdateStatus(String fromStatus, String toStatus) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateMany(
                Filters.eq("status", fromStatus),
                Updates.set("status", toStatus)
        );
        System.out.println("[TaskService] Bulk update " + fromStatus + " → " + toStatus
                + " | diperbarui: " + result.getModifiedCount());
        return result.getModifiedCount();
    }

    public boolean deleteTask(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[TaskService] Hapus task " + id + " → " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public long deleteTasksByStatus(String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(Filters.eq("status", status));
        System.out.println("[TaskService] Hapus semua task [" + status + "] → " + result.getDeletedCount() + " dihapus");
        return result.getDeletedCount();
    }
    public long deleteAllTasks() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(new Document());
        System.out.println("[TaskService] Semua task dihapus → total: " + result.getDeletedCount());
        return result.getDeletedCount();
    }

    private Document taskToDocument(Task task) {
        Document doc = new Document();
        if (task.getId() != null) {
            doc.append("_id", task.getId());
        }
        doc.append("task_type", task.getTaskType());
        doc.append("app_name", task.getAppName());
        doc.append("app_package", task.getAppPackage());
        doc.append("app_url", task.getAppUrl());
        doc.append("testing_url", task.getTestingUrl());
        doc.append("activities", task.getActivities() != null ? task.getActivities() : new ArrayList<>());
        doc.append("testers_emails", task.getTestersEmails() != null ? task.getTestersEmails() : new ArrayList<>());
        doc.append("priority", task.getPriority());
        doc.append("status", task.getStatus() != null ? task.getStatus() : "pending");
        doc.append("created_at", task.getCreatedAt() != null ? task.getCreatedAt().toString() : LocalDateTime.now().toString());
        doc.append("retry_count", task.getRetryCount());
        doc.append("error_message", task.getErrorMessage());
        doc.append("failed_at", task.getFailedAt() != null ? task.getFailedAt().toString() : null);
        doc.append("assigned_account", task.getAssignedAccount());
        doc.append("assigned_device", task.getAssignedDevice());
        return doc;
    }
    private Task documentToTask(Document doc) {
        Task task = new Task();
        task.setId(doc.getObjectId("_id"));
        task.setTaskType(doc.getString("task_type"));
        task.setAppName(doc.getString("app_name"));
        task.setAppPackage(doc.getString("app_package"));
        task.setAppUrl(doc.getString("app_url"));
        task.setTestingUrl(doc.getString("testing_url"));
        List<String> activities = doc.getList("activities", String.class);
        task.setActivities(activities != null ? activities : new ArrayList<>());
        List<String> testersEmails = doc.getList("testers_emails", String.class);
        task.setTestersEmails(testersEmails != null ? testersEmails : new ArrayList<>());
        task.setPriority(doc.getInteger("priority", 0));
        task.setStatus(doc.getString("status"));
        task.setRetryCount(doc.getInteger("retry_count", 0));
        task.setErrorMessage(doc.getString("error_message"));
        task.setAssignedAccount(doc.getObjectId("assigned_account"));
        task.setAssignedDevice(doc.getObjectId("assigned_device"));
        String createdAtStr = doc.getString("created_at");
        task.setCreatedAt(createdAtStr != null ? LocalDateTime.parse(createdAtStr) : LocalDateTime.now());
        String failedAtStr = doc.getString("failed_at");
        task.setFailedAt(failedAtStr != null ? LocalDateTime.parse(failedAtStr) : null);
        return task;
    }
}