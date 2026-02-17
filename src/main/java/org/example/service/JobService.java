package org.example.service;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.config.MongoConfig;
import org.example.model.Job;
import org.example.model.StatusJob;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
public class JobService {
    private static final String COLLECTION_NAME = "jobs";

    public ObjectId createJob(Job job) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = jobToDocument(job);
        InsertOneResult result = collection.insertOne(doc);
        ObjectId insertedId = result.getInsertedId() != null
                ? result.getInsertedId().asObjectId().getValue()
                : null;
        System.out.println("[JobService] Job baru dibuat → ID: " + insertedId);
        return insertedId;
    }
    public int createJobs(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) return 0;
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        List<Document> docs = new ArrayList<>();
        for (Job job : jobs) {
            docs.add(jobToDocument(job));
        }
        collection.insertMany(docs);
        System.out.println("[JobService] " + docs.size() + " job berhasil disimpan");
        return docs.size();
    }

    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find()) {
            jobs.add(documentToJob(doc));
        }
        System.out.println("[JobService] Total job ditemukan: " + jobs.size());
        return jobs;
    }
    public Job getJobById(ObjectId jobId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("_id", jobId)).first();
        if (doc == null) {
            System.out.println("[JobService] Job tidak ditemukan → ID: " + jobId);
            return null;
        }
        System.out.println("[JobService] Job ditemukan → ID: " + jobId);
        return documentToJob(doc);
    }
    public List<Job> getJobsByStatus(StatusJob status) {
        List<Job> jobs = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("status", status.name()))) {
            jobs.add(documentToJob(doc));
        }
        System.out.println("[JobService] Ditemukan " + jobs.size() + " job dengan status: " + status);
        return jobs;
    }
    public List<Job> getPendingJobs() {
        return getJobsByStatus(StatusJob.PENDING);
    }
    public List<Job> getJobsByActionType(String actionType) {
        List<Job> jobs = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("actionType", actionType))) {
            jobs.add(documentToJob(doc));
        }
        System.out.println("[JobService] Ditemukan " + jobs.size() + " job dengan actionType: " + actionType);
        return jobs;
    }
    public long countAllJobs() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments();
        System.out.println("[JobService] Total job: " + count);
        return count;
    }
    public long countJobsByStatus(StatusJob status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments(Filters.eq("status", status.name()));
        System.out.println("[JobService] Total job [" + status + "]: " + count);
        return count;
    }

    public boolean updateJob(Job job) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", job.getId()),
                Updates.combine(
                        Updates.set("status", job.getStatus().name())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[JobService] Update job " + job.getId() + " → " + (updated ? "berhasil" : "tidak ada perubahan"));
        return updated;
    }
    public boolean updateJobStatus(ObjectId jobId, StatusJob statusJob, String errorMessage) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result;
        if (errorMessage != null) {
            result = collection.updateOne(
                    Filters.eq("_id", jobId),
                    Updates.combine(
                            Updates.set("status", statusJob.toString()),
                            Updates.set("errorMessage", errorMessage)
                    )
            );
        } else {
            result = collection.updateOne(
                    Filters.eq("_id", jobId),
                    Updates.set("status", statusJob.toString())
            );
        }
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[JobService] Job " + jobId + " → status: " + statusJob.toString() + (updated ? "" : " (tidak berubah)"));
        return updated;
    }
    public boolean updateJobStatus(ObjectId jobId, StatusJob status) {
        return updateJobStatus(jobId, status, null);
    }
    public long bulkUpdateStatus(StatusJob fromStatus, StatusJob toStatus) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateMany(
                Filters.eq("status", fromStatus.name()),
                Updates.set("status", toStatus.name())
        );
        System.out.println("[JobService] Bulk update " + fromStatus + " → " + toStatus
                + " | diperbarui: " + result.getModifiedCount());
        return result.getModifiedCount();
    }

    public boolean deleteJob(ObjectId jobId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("_id", jobId));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[JobService] Hapus job " + jobId + " → " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public long deleteJobsByStatus(StatusJob status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(Filters.eq("status", status.name()));
        System.out.println("[JobService] Hapus semua job [" + status + "] → " + result.getDeletedCount() + " dihapus");
        return result.getDeletedCount();
    }
    public long deleteAllJobs() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(new Document());
        System.out.println("[JobService] Semua job dihapus → total: " + result.getDeletedCount());
        return result.getDeletedCount();
    }

    private Document jobToDocument(Job job) {
        Document doc = new Document();
        if (job.getId() != null) {
            doc.append("_id", job.getId());
        }
        doc.append("status", job.getStatus() != null ? job.getStatus().name() : StatusJob.PENDING.name());
        doc.append("createdAt", job.getCreatedAt());
        doc.append("deviceId", new ObjectId(job.getDeviceId()));
        doc.append("dataExecution", job.getDataExecution());
        return doc;
    }
    private Job documentToJob(Document doc) {
        StatusJob status = StatusJob.valueOf(doc.getString("status"));
        Job job = new Job();
        job.setId(doc.getObjectId("_id"));
        job.setStatus(status);
        job.setCreatedAt(LocalDate.ofInstant(doc.getDate("createdAt").toInstant(), ZoneId.systemDefault() ));
        job.setDeviceId(doc.getObjectId("deviceId"));
        job.setDataExecution(doc.getString("dataExecution"));
        return job;
    }
}