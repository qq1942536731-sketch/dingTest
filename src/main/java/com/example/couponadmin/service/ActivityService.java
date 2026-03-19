package com.example.couponadmin.service;

import com.example.couponadmin.dto.ActivityForm;
import com.example.couponadmin.entity.ActivityStatus;
import com.example.couponadmin.entity.CouponActivity;
import com.example.couponadmin.entity.CouponClaim;
import com.example.couponadmin.repository.CouponActivityRepository;
import com.example.couponadmin.repository.CouponClaimRepository;
import com.example.couponadmin.util.PaginationHelper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    public Page<CouponActivity> findPage(int page, int size) {
        return PaginationHelper.paginate(findAll(), page, size);
    }

    public CouponActivity findById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));
    }

    public List<CouponClaim> findRecentClaims(Long activityId) {
        return claimRepository.findTop5ByActivityIdOrderByClaimedAtDesc(activityId);
    }

    public Page<CouponClaim> findClaimPage(Long activityId, int page, int size) {
        return PaginationHelper.paginate(claimRepository.findByActivityIdOrderByClaimedAtDesc(activityId), page, size);
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
        CouponActivity activity = findById(id);
        if (target == ActivityStatus.RUNNING) {
            LocalDateTime now = LocalDateTime.now();
            if (!activity.isWithinWindow(now)) {
                throw new IllegalStateException("当前时间不在活动开始结束区间内，不能启动");
            }
        }
        activity.setStatus(target);
    }

    @Transactional
    public String issueCoupon(Long activityId, String claimant) {
        CouponActivity activity = findById(activityId);
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
        claim.setCouponCode(generateCouponCode());
        claim.setClaimedAt(LocalDateTime.now());
        claimRepository.save(claim);
        return claim.getCouponCode();
    }

    private String generateCouponCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } while (claimRepository.existsByCouponCode(code));
        return code;
    }

    private void validateWindow(ActivityForm form) {
        if (form.getEndTime().isBefore(form.getStartTime())) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
    }
}
