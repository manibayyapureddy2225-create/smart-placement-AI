package com.vhub.smart.placement.service;

import com.vhub.smart.placement.entity.JobApplication;
import com.vhub.smart.placement.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private com.vhub.smart.placement.repository.JobRepository jobs;

    public JobApplication applyForJob(Long jobId, Long studentId) {
        if (jobId == null) throw new IllegalArgumentException("Job ID is required");
        var job = jobs.findById(jobId).orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (!"ACTIVE".equals(job.getStatus())) throw new IllegalArgumentException("This job is closed");
        if (jobApplicationRepository.findByJobIdAndStudentId(jobId, studentId).isPresent()) {
            throw new RuntimeException("Already applied for this job!");
        }

        JobApplication application = new JobApplication();
        application.setJobId(jobId);
        application.setStudentId(studentId);
        return jobApplicationRepository.save(application);
    }

    public List<JobApplication> getApplicationsByStudent(Long studentId) {
        return jobApplicationRepository.findByStudentId(studentId);
    }

    public List<JobApplication> getApplicationsByJob(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }
}