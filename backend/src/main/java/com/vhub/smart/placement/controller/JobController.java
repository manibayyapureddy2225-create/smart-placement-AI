package com.vhub.smart.placement.controller;

import com.vhub.smart.placement.entity.Job;
import com.vhub.smart.placement.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    private JobService jobService;

    // Admin create job API
    @PostMapping("/admin/jobs")
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        try {
            Job createdJob = jobService.createJob(job);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Student view active jobs
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    // Student view specific job details
    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(jobService.getJobById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}