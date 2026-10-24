package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.entity.JobApplication;
import com.vhub.smart.placement.service.JobApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    @Autowired
    private JobApplicationService applicationService;

    @Autowired
    private com.vhub.smart.placement.repository.UserRepository users;

    // Student applies for a job via JSON Body
    @PostMapping("/apply")
    public ResponseEntity<?> applyForJob(@RequestBody JobApplication request, java.security.Principal principal) {
        try {
            JobApplication application = applicationService.applyForJob(request.getJobId(), users.findByEmail(principal.getName()).orElseThrow().getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Student views their applied jobs
    @GetMapping("/mine")
    public ResponseEntity<List<JobApplication>> getStudentApplications(java.security.Principal principal) {
        return ResponseEntity.ok(applicationService.getApplicationsByStudent(users.findByEmail(principal.getName()).orElseThrow().getId()));
    }

    // Admin views applications for a specific job
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<JobApplication>> getJobApplications(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }
}