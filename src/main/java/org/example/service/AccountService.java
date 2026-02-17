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
import org.example.model.Account;
import org.example.model.Account.AccountMetadata;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class AccountService {
    private static final String COLLECTION_NAME = "accounts";

    public ObjectId createAccount(Account account) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = accountToDocument(account);
        InsertOneResult result = collection.insertOne(doc);
        ObjectId insertedId = result.getInsertedId() != null
                ? result.getInsertedId().asObjectId().getValue()
                : null;
        System.out.println("[AccountService] Account baru dibuat → ID: " + insertedId);
        return insertedId;
    }
    public int createAccounts(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return 0;
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        List<Document> docs = new ArrayList<>();
        for (Account account : accounts) {
            docs.add(accountToDocument(account));
        }
        collection.insertMany(docs);
        System.out.println("[AccountService] " + docs.size() + " account berhasil disimpan");
        return docs.size();
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find()) {
            accounts.add(documentToAccount(doc));
        }
        System.out.println("[AccountService] Total account ditemukan: " + accounts.size());
        return accounts;
    }
    public Account getAccountById(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            System.out.println("[AccountService] Account tidak ditemukan → ID: " + id);
            return null;
        }
        System.out.println("[AccountService] Account ditemukan → ID: " + id);
        return documentToAccount(doc);
    }
    public Account getAccountByEmail(String email) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("email", email)).first();
        if (doc == null) {
            System.out.println("[AccountService] Account tidak ditemukan → email: " + email);
            return null;
        }
        System.out.println("[AccountService] Account ditemukan → email: " + email);
        return documentToAccount(doc);
    }
    public List<Account> getAccountsByStatus(String status) {
        List<Account> accounts = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("status", status))) {
            accounts.add(documentToAccount(doc));
        }
        System.out.println("[AccountService] Ditemukan " + accounts.size() + " account dengan status: " + status);
        return accounts;
    }
    public List<Account> getAvailableAccounts() {
        List<Account> accounts = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(
                Filters.and(
                        Filters.eq("status", "active"),
                        Filters.eq("current_task_id", null)
                )
        )) {
            accounts.add(documentToAccount(doc));
        }
        System.out.println("[AccountService] Ditemukan " + accounts.size() + " account tersedia");
        return accounts;
    }
    public Account getAccountByCurrentTaskId(ObjectId taskId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("current_task_id", taskId)).first();
        if (doc == null) {
            System.out.println("[AccountService] Tidak ada account untuk task → taskId: " + taskId);
            return null;
        }
        System.out.println("[AccountService] Account ditemukan untuk task → taskId: " + taskId);
        return documentToAccount(doc);
    }
    public List<Account> getExpiredAccounts() {
        List<Account> accounts = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        String now = LocalDateTime.now().toString();
        for (Document doc : collection.find(Filters.lt("expires_at", now))) {
            accounts.add(documentToAccount(doc));
        }
        System.out.println("[AccountService] Ditemukan " + accounts.size() + " account expired");
        return accounts;
    }
    public long countAllAccounts() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments();
        System.out.println("[AccountService] Total account: " + count);
        return count;
    }
    public long countAccountsByStatus(String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments(Filters.eq("status", status));
        System.out.println("[AccountService] Total account [" + status + "]: " + count);
        return count;
    }

    public boolean updateAccount(Account account) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", account.getId()),
                Updates.combine(
                        Updates.set("email", account.getEmail()),
                        Updates.set("password", account.getPassword()),
                        Updates.set("expires_at", account.getExpiresAt() != null ? account.getExpiresAt().toString() : null),
                        Updates.set("status", account.getStatus()),
                        Updates.set("recovery_phone", account.getRecoveryPhone()),
                        Updates.set("metadata", metadataToDocument(account.getMetadata())),
                        Updates.set("current_task_id", account.getCurrentTaskId())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[AccountService] Update account " + account.getId() + " → " + (updated ? "berhasil" : "tidak ada perubahan"));
        return updated;
    }
    public boolean updateAccountStatus(ObjectId id, String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("status", status)
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[AccountService] Account " + id + " → status: " + status + (updated ? "" : " (tidak berubah)"));
        return updated;
    }
    public boolean assignTask(ObjectId id, ObjectId taskId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("current_task_id", taskId),
                        Updates.set("status", "busy")
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[AccountService] Account " + id + " → assigned task: " + taskId);
        return updated;
    }
    public boolean releaseTask(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("current_task_id", null),
                        Updates.set("status", "active")
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[AccountService] Account " + id + " → task dilepas, kembali active");
        return updated;
    }
    public boolean updatePassword(ObjectId id, String password) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("password", password)
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[AccountService] Password account " + id + " → " + (updated ? "diperbarui" : "tidak berubah"));
        return updated;
    }
    public long bulkUpdateStatus(String fromStatus, String toStatus) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateMany(
                Filters.eq("status", fromStatus),
                Updates.set("status", toStatus)
        );
        System.out.println("[AccountService] Bulk update " + fromStatus + " → " + toStatus
                + " | diperbarui: " + result.getModifiedCount());
        return result.getModifiedCount();
    }

    public boolean deleteAccount(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[AccountService] Hapus account " + id + " → " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public boolean deleteAccountByEmail(String email) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("email", email));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[AccountService] Hapus account → email: " + email + " | " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public long deleteAccountsByStatus(String status) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(Filters.eq("status", status));
        System.out.println("[AccountService] Hapus semua account [" + status + "] → " + result.getDeletedCount() + " dihapus");
        return result.getDeletedCount();
    }
    public long deleteExpiredAccounts() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        String now = LocalDateTime.now().toString();
        DeleteResult result = collection.deleteMany(Filters.lt("expires_at", now));
        System.out.println("[AccountService] Hapus account expired → total: " + result.getDeletedCount());
        return result.getDeletedCount();
    }
    public long deleteAllAccounts() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(new Document());
        System.out.println("[AccountService] Semua account dihapus → total: " + result.getDeletedCount());
        return result.getDeletedCount();
    }

    private Document accountToDocument(Account account) {
        Document doc = new Document();
        if (account.getId() != null) {
            doc.append("_id", account.getId());
        }
        doc.append("email", account.getEmail());
        doc.append("password", account.getPassword());
        doc.append("created_at", account.getCreatedAt() != null
                ? account.getCreatedAt().toString()
                : LocalDateTime.now().toString());
        doc.append("expires_at", account.getExpiresAt() != null
                ? account.getExpiresAt().toString()
                : null);
        doc.append("status", account.getStatus() != null ? account.getStatus() : "active");
        doc.append("recovery_phone", account.getRecoveryPhone() != null ? account.getRecoveryPhone() : "");
        doc.append("metadata", metadataToDocument(account.getMetadata()));
        doc.append("current_task_id", account.getCurrentTaskId());
        return doc;
    }
    private Document metadataToDocument(AccountMetadata metadata) {
        if (metadata == null) return new Document();
        return new Document()
                .append("created_by", metadata.getCreatedBy())
                .append("created_via", metadata.getCreatedVia())
                .append("browser", metadata.getBrowser());
    }
    private Account documentToAccount(Document doc) {
        Account account = new Account();
        account.setId(doc.getObjectId("_id"));
        account.setEmail(doc.getString("email"));
        account.setPassword(doc.getString("password"));
        account.setStatus(doc.getString("status"));
        account.setRecoveryPhone(doc.getString("recovery_phone"));
        account.setCurrentTaskId(doc.getObjectId("current_task_id"));
        String createdAtStr = doc.getString("created_at");
        account.setCreatedAt(createdAtStr != null
                ? LocalDateTime.parse(createdAtStr)
                : LocalDateTime.now());
        String expiresAtStr = doc.getString("expires_at");
        account.setExpiresAt(expiresAtStr != null
                ? LocalDateTime.parse(expiresAtStr)
                : null);
        Document metaDoc = doc.get("metadata", Document.class);
        if (metaDoc != null) {
            AccountMetadata metadata = new AccountMetadata(
                    metaDoc.getString("created_by"),
                    metaDoc.getString("created_via"),
                    metaDoc.getString("browser")
            );
            account.setMetadata(metadata);
        }
        return account;
    }
}