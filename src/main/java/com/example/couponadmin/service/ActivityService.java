package com.example.couponadmin.service;

import com.example.couponadmin.dto.ActivityForm;
import com.example.couponadmin.entity.ActivityStatus;
import com.example.couponadmin.entity.CouponActivity;
import com.example.couponadmin.entity.CouponClaim;
import com.example.couponadmin.repository.CouponActivityRepository;
import com.example.couponadmin.repository.CouponClaimRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {
    private final CouponActivityRepository activityRepository;
    private final CouponClaimRepository claimRepository;

    public ActivityService(CouponActivityRepository activityRepository, CouponClaimRepository claimRepository) {
        this.activityRepository = activityRepository;
        this.claimRepository = claimRepository;
    }

    public List<CouponActivity> findAll() {
        return activityRepository.findAll();
    }

    public CouponActivity create(ActivityForm form) {
        validateWindow(form);
        CouponActivity activity = new CouponActivity();
        activity.setName(form.getName());
        activity.setDescription(form.getDescription());
        activity.setStartTime(form.getStartTime());
        activity.setEndTime(form.getEndTime());
        activity.setTotalStock(form.getTotalStock());
        activity.setIssuedStock(0);
        activity.setStatus(ActivityStatus.DRAFT);
        return activityRepository.save(activity);
    }

    @Transactional
    public void updateStatus(Long id, ActivityStatus target) {
        CouponActivity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));
        if (target == ActivityStatus.RUNNING) {
            LocalDateTime now = LocalDateTime.now();
            if (!activity.isWithinWindow(now)) {
                throw new IllegalStateException("当前时间不在活动开始结束区间内，不能启动");
            }
        }
        activity.setStatus(target);
    }

    @Transactional
    public void issueCoupon(Long activityId, String claimant) {
        CouponActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));
        if (!activity.isWithinWindow(LocalDateTime.now())) {
            throw new IllegalStateException("活动不在可发券时间窗口内");
        }
        int updated = activityRepository.tryIssue(activityId);
        if (updated == 0) {
            throw new IllegalStateException("券库存不足或活动未启动，已阻止超发");
        }
        CouponClaim claim = new CouponClaim();
        claim.setActivityId(activityId);
        claim.setClaimant(claimant);
        claim.setClaimedAt(LocalDateTime.now());
        claimRepository.save(claim);
    }

    private void validateWindow(ActivityForm form) {
        if (form.getEndTime().isBefore(form.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
    }
}
