package com.vhub.smart.placement.repository;

import com.vhub.smart.placement.entity.Resume;
import com.vhub.smart.placement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByUser(User user);
}