package com.vhub.smart.placement.repository;

import com.vhub.smart.placement.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    @Query("SELECT j FROM Job j WHERE j.status = :status")
    List<Job> findByStatus(@Param("status") String status);
}