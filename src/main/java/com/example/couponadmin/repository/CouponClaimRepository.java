package com.example.couponadmin.repository;

import com.example.couponadmin.entity.CouponClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponClaimRepository extends JpaRepository<CouponClaim, Long> {
    List<CouponClaim> findTop5ByActivityIdOrderByClaimedAtDesc(Long activityId);

    boolean existsByCouponCode(String couponCode);
}
