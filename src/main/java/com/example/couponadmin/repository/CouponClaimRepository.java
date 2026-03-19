package com.example.couponadmin.repository;

import com.example.couponadmin.entity.CouponClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponClaimRepository extends JpaRepository<CouponClaim, Long> {
}
