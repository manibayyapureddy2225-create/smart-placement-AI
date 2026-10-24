package com.vhub.smart.placement.repository;

import com.vhub.smart.placement.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByStudentId(Long studentId);
    List<JobApplication> findByJobId(Long jobId);
    Optional<JobApplication> findByJobIdAndStudentId(Long jobId, Long studentId);
}