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
import org.example.model.Device;
import org.example.model.StatusDevice;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class DeviceService {
    private static final String COLLECTION_NAME = "devices";

    public ObjectId createDevice(Device device) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = deviceToDocument(device);
        InsertOneResult result = collection.insertOne(doc);
        ObjectId insertedId = result.getInsertedId() != null
                ? result.getInsertedId().asObjectId().getValue()
                : null;
        System.out.println("[DeviceService] Device baru dibuat → ID: " + insertedId);
        return insertedId;
    }
    public int createDevices(List<Device> devices) {
        if (devices == null || devices.isEmpty()) return 0;
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        List<Document> docs = new ArrayList<>();
        for (Device device : devices) {
            docs.add(deviceToDocument(device));
        }
        collection.insertMany(docs);
        System.out.println("[DeviceService] " + docs.size() + " device berhasil disimpan");
        return docs.size();
    }

    public List<Device> getAllDevices() {
        List<Device> devices = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find()) {
            devices.add(documentToDevice(doc));
        }
        System.out.println("[DeviceService] Total device ditemukan: " + devices.size());
        return devices;
    }
    public Device getDeviceById(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            System.out.println("[DeviceService] Device tidak ditemukan → ID: " + id);
            return null;
        }
        System.out.println("[DeviceService] Device ditemukan → ID: " + id);
        return documentToDevice(doc);
    }
    public Device getDeviceByDeviceId(String deviceId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("deviceId", deviceId)).first();
        if (doc == null) {
            System.out.println("[DeviceService] Device tidak ditemukan → deviceId: " + deviceId);
            return null;
        }
        System.out.println("[DeviceService] Device ditemukan → deviceId: " + deviceId);
        return documentToDevice(doc);
    }
    public List<Device> getDevicesByStatus(StatusDevice statusDevice) {
        List<Device> devices = new ArrayList<>();
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        for (Document doc : collection.find(Filters.eq("statusDevice", statusDevice.name()))) {
            devices.add(documentToDevice(doc));
        }
        System.out.println("[DeviceService] Ditemukan " + devices.size() + " device dengan status: " + statusDevice);
        return devices;
    }
    public List<Device> getConnectedDevices() {
        return getDevicesByStatus(StatusDevice.CONNECTED);
    }
    public List<Device> getNotConnectedDevices() {
        return getDevicesByStatus(StatusDevice.NOT_CONNECTED);
    }
    public Device getDeviceByCurrentJobId(ObjectId jobId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        Document doc = collection.find(Filters.eq("currentJobId", jobId)).first();
        if (doc == null) {
            System.out.println("[DeviceService] Tidak ada device yang mengerjakan job → jobId: " + jobId);
            return null;
        }
        System.out.println("[DeviceService] Device ditemukan untuk job → jobId: " + jobId);
        return documentToDevice(doc);
    }
    public long countAllDevices() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments();
        System.out.println("[DeviceService] Total device: " + count);
        return count;
    }
    public long countDevicesByStatus(StatusDevice statusDevice) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        long count = collection.countDocuments(Filters.eq("statusDevice", statusDevice.name()));
        System.out.println("[DeviceService] Total device [" + statusDevice + "]: " + count);
        return count;
    }

    public boolean updateDevice(Device device) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", device.getId()),
                Updates.combine(
                        Updates.set("deviceId", device.getDeviceId()),
                        Updates.set("deviceName", device.getDeviceName()),
                        Updates.set("model", device.getModel()),
                        Updates.set("androidVersion", device.getAndroidVersion()),
                        Updates.set("currentJobId", device.getCurrentJobId()),
                        Updates.set("statusDevice", device.getStatusDevice().name()),
                        Updates.set("lastSeen", device.getLastSeen().toString())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[DeviceService] Update device " + device.getId() + " → " + (updated ? "berhasil" : "tidak ada perubahan"));
        return updated;
    }
    public boolean updateDeviceStatus(ObjectId id, StatusDevice statusDevice) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("statusDevice", statusDevice.name())
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[DeviceService] Device " + id + " → status: " + statusDevice + (updated ? "" : " (tidak berubah)"));
        return updated;
    }
    public boolean updateDeviceJob(ObjectId id, ObjectId jobId, StatusDevice statusDevice) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("currentJobId", jobId),
                        Updates.set("statusDevice", statusDevice.name())
                )
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[DeviceService] Device " + id + " → jobId: " + jobId + " | status: " + statusDevice);
        return updated;
    }
    public boolean updateLastSeen(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("lastSeen", LocalDate.now().toString())
        );
        boolean updated = result.getModifiedCount() > 0;
        System.out.println("[DeviceService] lastSeen device " + id + " → diperbarui ke: " + LocalDate.now());
        return updated;
    }
    public long bulkUpdateStatus(StatusDevice fromStatus, StatusDevice toStatus) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        UpdateResult result = collection.updateMany(
                Filters.eq("statusDevice", fromStatus.name()),
                Updates.set("statusDevice", toStatus.name())
        );
        System.out.println("[DeviceService] Bulk update " + fromStatus + " → " + toStatus
                + " | diperbarui: " + result.getModifiedCount());
        return result.getModifiedCount();
    }

    public boolean deleteDevice(ObjectId id) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[DeviceService] Hapus device " + id + " → " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public boolean deleteDeviceByDeviceId(String deviceId) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteOne(Filters.eq("deviceId", deviceId));
        boolean deleted = result.getDeletedCount() > 0;
        System.out.println("[DeviceService] Hapus device → deviceId: " + deviceId + " | " + (deleted ? "berhasil" : "tidak ditemukan"));
        return deleted;
    }
    public long deleteDevicesByStatus(StatusDevice statusDevice) {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(Filters.eq("statusDevice", statusDevice.name()));
        System.out.println("[DeviceService] Hapus semua device [" + statusDevice + "] → " + result.getDeletedCount() + " dihapus");
        return result.getDeletedCount();
    }
    public long deleteAllDevices() {
        MongoDatabase db = MongoConfig.getDatabase();
        MongoCollection<Document> collection = db.getCollection(COLLECTION_NAME);
        DeleteResult result = collection.deleteMany(new Document());
        System.out.println("[DeviceService] Semua device dihapus → total: " + result.getDeletedCount());
        return result.getDeletedCount();
    }

    private Document deviceToDocument(Device device) {
        Document doc = new Document();
        if (device.getId() != null) {
            doc.append("_id", device.getId());
        }
        doc.append("deviceId", device.getDeviceId());
        doc.append("deviceName", device.getDeviceName());
        doc.append("internal", device.isInternal());
        doc.append("model", device.getModel());
        doc.append("androidVersion", device.getAndroidVersion());
        doc.append("currentJobId", device.getCurrentJobId());
        doc.append("statusDevice", device.getStatusDevice() != null
                ? device.getStatusDevice().name()
                : StatusDevice.NOT_CONNECTED.name());
        doc.append("lastSeen", device.getLastSeen() != null
                ? device.getLastSeen().toString()
                : LocalDate.now().toString());
        doc.append("createdAt", device.getCreatedAt() != null
                ? device.getCreatedAt().toString()
                : LocalDate.now().toString());
        return doc;
    }
    private Device documentToDevice(Document doc) {
        Device device = new Device();
        device.setId(doc.getObjectId("_id"));
        device.setDeviceId(doc.getString("deviceId"));
        device.setDeviceName(doc.getString("deviceName"));
        device.setModel(doc.getString("model"));
        device.setAndroidVersion(doc.getString("androidVersion"));
        ObjectId currentJobId = doc.getObjectId("currentJobId");
        device.setCurrentJobId(currentJobId != null ? currentJobId : null);
        boolean internal = doc.getBoolean("internal");
        device.setInternal(internal && internal);
        String statusStr = doc.getString("statusDevice");
        device.setStatusDevice(statusStr != null
                ? StatusDevice.valueOf(statusStr)
                : StatusDevice.NOT_CONNECTED);
        String lastSeenStr = doc.getString("lastSeen");
        device.setLastSeen(lastSeenStr != null
                ? LocalDate.parse(lastSeenStr)
                : LocalDate.now());
        String createdAtStr = doc.getString("createdAt");
        device.setCreatedAt(createdAtStr != null
                ? LocalDate.parse(createdAtStr)
                : LocalDate.now());
        return device;
    }
}