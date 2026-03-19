package com.example.couponadmin.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_claim")
public class CouponClaim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;
    private String claimant;
    private LocalDateTime claimedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getClaimant() { return claimant; }
    public void setClaimant(String claimant) { this.claimant = claimant; }
    public LocalDateTime getClaimedAt() { return claimedAt; }
    public void setClaimedAt(LocalDateTime claimedAt) { this.claimedAt = claimedAt; }
}
