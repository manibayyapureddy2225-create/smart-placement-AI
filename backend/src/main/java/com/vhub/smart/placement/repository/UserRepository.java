package com.vhub.smart.placement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vhub.smart.placement.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}