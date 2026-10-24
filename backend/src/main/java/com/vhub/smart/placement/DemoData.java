package com.vhub.smart.placement;
import com.vhub.smart.placement.entity.Job;
import com.vhub.smart.placement.repository.JobRepository;
import org.springframework.context.annotation.*;
import org.springframework.boot.CommandLineRunner;
@Configuration
@Profile("demo")
public class DemoData {
    @Bean CommandLineRunner seedJobs(JobRepository jobs) { return args -> {
        if (jobs.count() > 0) return;
        Job job = new Job(); job.setCompanyName("Example Company (demo)");
        job.setRole("Java Full Stack Intern"); job.setDescription("Sample vacancy for reviewing the application workflow. Not a real opening.");
        job.setLocation("Remote"); job.setRequiredSkills("Java,Spring Boot,React,SQL"); jobs.save(job);
    }; }
}
