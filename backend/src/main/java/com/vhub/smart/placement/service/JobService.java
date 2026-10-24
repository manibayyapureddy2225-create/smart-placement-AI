package com.vhub.smart.placement.service;

import com.vhub.smart.placement.entity.Job;
import com.vhub.smart.placement.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Job createJob(Job job) {
        if (job.getCompanyName() == null || job.getCompanyName().isBlank() || job.getRole() == null || job.getRole().isBlank())
            throw new IllegalArgumentException("Company name and role are required");
        if (job.getSalary() != null && (!Double.isFinite(job.getSalary()) || job.getSalary() < 0))
            throw new IllegalArgumentException("Salary must be a nonnegative finite number");
        if (job.getStatus() != null && !java.util.Set.of("ACTIVE", "CLOSED").contains(job.getStatus()))
            throw new IllegalArgumentException("Status must be ACTIVE or CLOSED");
        job.setId(null);
        return jobRepository.save(job);
    }

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByStatus("ACTIVE");
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
}