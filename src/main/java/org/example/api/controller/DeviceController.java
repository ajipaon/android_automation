package org.example.api.controller;
import org.bson.types.ObjectId;
import org.example.api.util.JsonUtil;
import org.example.model.Device;
import org.example.model.StatusDevice;
import org.example.service.DeviceService;
import static spark.Spark.*;
public class DeviceController {
    private final DeviceService deviceService;
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
        registerRoutes();
    }
    private void registerRoutes() {
        
        get("/api/devices", (req, res) -> {
            res.type("application/json");
            return JsonUtil.success(deviceService.getAllDevices());
        });
        
        get("/api/devices/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id     = new ObjectId(req.params(":id"));
                Device   device = deviceService.getDeviceById(id);
                if (device == null) {
                    res.status(404);
                    return JsonUtil.error(404, "Device tidak ditemukan");
                }
                return JsonUtil.success(device);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
        
        get("/api/devices/status/:status", (req, res) -> {
            res.type("application/json");
            try {
                StatusDevice status = StatusDevice.valueOf(req.params(":status").toUpperCase());
                return JsonUtil.success(deviceService.getDevicesByStatus(status));
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "Status tidak valid");
            }
        });
        
        get("/api/devices/connected", (req, res) -> {
            res.type("application/json");
            return JsonUtil.success(deviceService.getConnectedDevices());
        });
        
        post("/api/devices", (req, res) -> {
            res.type("application/json");
            try {
                Device   device = JsonUtil.fromJson(req.body(), Device.class);
                ObjectId id     = deviceService.createDevice(device);
                res.status(201);
                return JsonUtil.success(id.toHexString());
            } catch (Exception e) {
                res.status(400);
                return JsonUtil.error(400, "Request tidak valid: " + e.getMessage());
            }
        });
        
        put("/api/devices/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id     = new ObjectId(req.params(":id"));
                Device   device = JsonUtil.fromJson(req.body(), Device.class);
                device.setId(id);
                boolean updated = deviceService.updateDevice(device);
                if (!updated) {
                    res.status(404);
                    return JsonUtil.error(404, "Device tidak ditemukan");
                }
                return JsonUtil.success("Device berhasil diupdate");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
        
        patch("/api/devices/:id/status", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId     id     = new ObjectId(req.params(":id"));
                Device       body   = JsonUtil.fromJson(req.body(), Device.class);
                boolean      updated = deviceService.updateDeviceStatus(id, body.getStatusDevice());
                if (!updated) {
                    res.status(404);
                    return JsonUtil.error(404, "Device tidak ditemukan");
                }
                return JsonUtil.success("Status berhasil diupdate");
            } catch (Exception e) {
                res.status(400);
                return JsonUtil.error(400, "Request tidak valid: " + e.getMessage());
            }
        });
        
        delete("/api/devices/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id      = new ObjectId(req.params(":id"));
                boolean  deleted = deviceService.deleteDevice(id);
                if (!deleted) {
                    res.status(404);
                    return JsonUtil.error(404, "Device tidak ditemukan");
                }
                return JsonUtil.success("Device berhasil dihapus");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
    }
}