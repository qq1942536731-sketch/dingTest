package com.example.couponadmin.repository;

import com.example.couponadmin.entity.CouponActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponActivityRepository extends JpaRepository<CouponActivity, Long> {
    @Modifying
    @Query("update CouponActivity c set c.issuedStock = c.issuedStock + 1 where c.id = :id and c.status = com.example.couponadmin.entity.ActivityStatus.RUNNING and c.issuedStock < c.totalStock")
    int tryIssue(@Param("id") Long id);
}
