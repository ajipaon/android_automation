package org.example.model;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
public class Account {
    private ObjectId id;
    private String email;
    private String password;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt;
    private String status = "active";
    private String recoveryPhone = "";
    private AccountMetadata metadata;
    private ObjectId currentTaskId;

    public Account() {}
    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRecoveryPhone() {
        return recoveryPhone;
    }
    public void setRecoveryPhone(String recoveryPhone) {
        this.recoveryPhone = recoveryPhone;
    }
    public AccountMetadata getMetadata() {
        return metadata;
    }
    public void setMetadata(AccountMetadata metadata) {
        this.metadata = metadata;
    }
    public ObjectId getCurrentTaskId() {
        return currentTaskId;
    }
    public void setCurrentTaskId(ObjectId currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", status='" + status + '\'' +
                ", recoveryPhone='" + recoveryPhone + '\'' +
                ", metadata=" + metadata +
                ", currentTaskId=" + currentTaskId +
                '}';
    }

    public static class AccountMetadata {
        private String createdBy;
        private String createdVia;
        private String browser;
        public AccountMetadata() {}
        public AccountMetadata(String createdBy, String createdVia, String browser) {
            this.createdBy = createdBy;
            this.createdVia = createdVia;
            this.browser = browser;
        }
        public String getCreatedBy() {
            return createdBy;
        }
        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }
        public String getCreatedVia() {
            return createdVia;
        }
        public void setCreatedVia(String createdVia) {
            this.createdVia = createdVia;
        }
        public String getBrowser() {
            return browser;
        }
        public void setBrowser(String browser) {
            this.browser = browser;
        }
        @Override
        public String toString() {
            return "AccountMetadata{" +
                    "createdBy='" + createdBy + '\'' +
                    ", createdVia='" + createdVia + '\'' +
                    ", browser='" + browser + '\'' +
                    '}';
        }
    }
}