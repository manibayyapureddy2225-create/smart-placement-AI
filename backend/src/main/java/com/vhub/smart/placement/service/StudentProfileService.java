package com.vhub.smart.placement.service;

import com.vhub.smart.placement.dto.StudentProfileRequest;
import com.vhub.smart.placement.entity.StudentProfile;
import com.vhub.smart.placement.entity.User;
import com.vhub.smart.placement.repository.StudentProfileRepository;
import com.vhub.smart.placement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileService {

    @Autowired
    private StudentProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public StudentProfile saveOrUpdateProfile(String email, StudentProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        StudentProfile profile = profileRepository.findByUser(user)
                .orElse(new StudentProfile());

        profile.setUser(user);
        profile.setPhone(request.getPhone());
        profile.setCollege(request.getCollege());
        profile.setDegree(request.getDegree());
        profile.setBranch(request.getBranch());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setSkills(request.getSkills());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());
        profile.setCareerObjective(request.getCareerObjective());

        return profileRepository.save(profile);
    }

    public StudentProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return profileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found for this user!"));
    }
}