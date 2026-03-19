package com.example.couponadmin.controller;

import com.example.couponadmin.dto.ActivityForm;
import com.example.couponadmin.entity.ActivityStatus;
import com.example.couponadmin.entity.CouponActivity;
import com.example.couponadmin.service.ActivityService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class ActivityController {
    private static final int PAGE_SIZE = 8;
    private static final int CLAIM_PAGE_SIZE = 8;

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('activity:view')")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        if (!model.containsAttribute("activityForm")) {
            model.addAttribute("activityForm", new ActivityForm());
        }
        Page<CouponActivity> activityPage = activityService.findPage(page, PAGE_SIZE);
        model.addAttribute("activities", activityPage.getContent());
        model.addAttribute("activityPage", activityPage);
        model.addAttribute("allActivities", activityService.findAll());
        model.addAttribute("statuses", ActivityStatus.values());
        return "activities";
    }

    @GetMapping("/activities/{id}/edit")
    @PreAuthorize("hasAuthority('activity:edit')")
    public String detail(@PathVariable Long id, Model model) {
        CouponActivity activity = activityService.findById(id);
        model.addAttribute("activity", activity);
        model.addAttribute("statuses", ActivityStatus.values());
        return "activity-detail";
    }

    @GetMapping("/activities/issue")
    @PreAuthorize("hasAuthority('activity:issue')")
    public String issueModule(@RequestParam(required = false) Long activityId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "0") int claimPageIndex,
                              Model model) {
        Page<CouponActivity> activityPage = activityService.findPage(page, PAGE_SIZE);
        model.addAttribute("activityPage", activityPage);
        model.addAttribute("activities", activityPage.getContent());
        Long resolvedId = activityId;
        if (resolvedId == null && !activityPage.getContent().isEmpty()) {
            resolvedId = activityPage.getContent().get(0).getId();
        }
        if (resolvedId != null) {
            CouponActivity activity = activityService.findById(resolvedId);
            model.addAttribute("selectedActivity", activity);
            org.springframework.data.domain.Page<?> claimPage = activityService.findClaimPage(resolvedId, claimPageIndex, CLAIM_PAGE_SIZE);
            model.addAttribute("claimPage", claimPage);
            model.addAttribute("claims", claimPage.getContent());
        }
        return "activity-issue";
    }

    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('activity:edit')")
    public String create(@Valid @ModelAttribute ActivityForm activityForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.activityForm", bindingResult);
            redirectAttributes.addFlashAttribute("activityForm", activityForm);
            redirectAttributes.addFlashAttribute("error", "请检查活动字段");
            return "redirect:/activities";
        }
        try {
            activityService.create(activityForm);
            redirectAttributes.addFlashAttribute("success", "活动已创建，默认状态为草稿");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("activityForm", activityForm);
        }
        return "redirect:/activities";
    }

    @PostMapping("/activities/{id}/status")
    @PreAuthorize("hasAuthority('activity:edit')")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ActivityStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            activityService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "活动状态已更新为 " + status.name());
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/activities/" + id + "/edit";
    }

    @PostMapping("/api/activities/{id}/issue")
    @ResponseBody
    @PreAuthorize("hasAuthority('activity:issue')")
    public String issue(@PathVariable Long id, @RequestParam(defaultValue = "system") String claimant) {
        String couponCode = activityService.issueCoupon(id, claimant);
        return "发券成功，券码：" + couponCode;
    }
}
