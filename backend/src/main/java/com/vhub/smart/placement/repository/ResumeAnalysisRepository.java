package com.vhub.smart.placement.repository;

import com.vhub.smart.placement.entity.ResumeAnalysis;
import com.vhub.smart.placement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findByUser(User user);
}