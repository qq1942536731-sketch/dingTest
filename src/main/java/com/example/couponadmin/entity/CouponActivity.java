package com.example.couponadmin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_activity")
public class CouponActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(length = 1000)
    private String description;
    @Enumerated(EnumType.STRING)
    private ActivityStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalStock;
    private Integer issuedStock;
    @Version
    private Long version;

    public boolean isWithinWindow(LocalDateTime now) {
        return (startTime == null || !now.isBefore(startTime)) && (endTime == null || !now.isAfter(endTime));
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }
    public Integer getIssuedStock() { return issuedStock; }
    public void setIssuedStock(Integer issuedStock) { this.issuedStock = issuedStock; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
