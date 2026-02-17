package org.example.api.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.example.api.util.JsonUtil;
import org.example.model.Job;
import org.example.model.StatusJob;
import org.example.model.dto.JodAddDto;
import org.example.service.JobService;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import static spark.Spark.*;
public class JobController {
    private final JobService jobService;
    private final ObjectMapper mapper = new ObjectMapper();
    public JobController(JobService jobService) {
        this.jobService = jobService;
        registerRoutes();
    }
    private void registerRoutes() {
        
        get("/api/jobs", (req, res) -> {
            res.type("application/json");
            return JsonUtil.success(jobService.getAllJobs());
        });
        
        get("/api/jobs/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id  = new ObjectId(req.params(":id"));
                Job      job = jobService.getJobById(id);
                if (job == null) {
                    res.status(404);
                    return JsonUtil.error(404, "Job tidak ditemukan");
                }
                return JsonUtil.success(job);
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
        
        get("/api/jobs/status/:status", (req, res) -> {
            res.type("application/json");
            try {
                StatusJob status = StatusJob.valueOf(req.params(":status").toUpperCase());
                return JsonUtil.success(jobService.getJobsByStatus(status));
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "Status tidak valid");
            }
        });
        
        post("/api/jobs", (req, res) -> {
            res.type("application/json");
            try {
                String body = req.body();
                JodAddDto jodAddDto = mapper.readValue(body, JodAddDto.class);
                String flowString = mapper.writeValueAsString(jodAddDto.getAppFlow());
                Job job = new Job();
                job.setStatus(jodAddDto.getStatus());
                job.setDeviceId(jodAddDto.getDeviceId());
                job.setDataExecution(flowString);
                ObjectId id  = jobService.createJob(job);
                res.status(201);
                return JsonUtil.success(id.toHexString());
            } catch (Exception e) {
                res.status(400);
                return JsonUtil.error(400, "Request tidak valid: " + e.getMessage());
            }
        });
        
        put("/api/jobs/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id  = new ObjectId(req.params(":id"));
                Job      job = JsonUtil.fromJson(req.body(), Job.class);
                job.setId(id);
                boolean updated = jobService.updateJob(job);
                if (!updated) {
                    res.status(404);
                    return JsonUtil.error(404, "Job tidak ditemukan");
                }
                return JsonUtil.success("Job berhasil diupdate");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
        
        patch("/api/jobs/:id/status", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id     = new ObjectId(req.params(":id"));
                Job      body   = JsonUtil.fromJson(req.body(), Job.class);
                boolean  updated = jobService.updateJobStatus(
                        id,
                        StatusJob.PENDING,
                        null
                );
                if (!updated) {
                    res.status(404);
                    return JsonUtil.error(404, "Job tidak ditemukan");
                }
                return JsonUtil.success("Status berhasil diupdate");
            } catch (Exception e) {
                res.status(400);
                return JsonUtil.error(400, "Request tidak valid: " + e.getMessage());
            }
        });
        
        delete("/api/jobs/:id", (req, res) -> {
            res.type("application/json");
            try {
                ObjectId id      = new ObjectId(req.params(":id"));
                boolean  deleted = jobService.deleteJob(id);
                if (!deleted) {
                    res.status(404);
                    return JsonUtil.error(404, "Job tidak ditemukan");
                }
                return JsonUtil.success("Job berhasil dihapus");
            } catch (IllegalArgumentException e) {
                res.status(400);
                return JsonUtil.error(400, "ID tidak valid");
            }
        });
    }
}