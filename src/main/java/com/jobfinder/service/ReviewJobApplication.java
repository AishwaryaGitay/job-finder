package com.jobfinder.service;

import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewJobApplication {

    private static JobApplicationService jobApplicationService = new JobApplicationService();
    private static JobService jobService = new JobService();

    // Add setters for dependency injection
    public static void setJobService(JobService service) {
        jobService = service;
    }

    public static void setJobApplicationService(JobApplicationService service) {
        jobApplicationService = service;
    }
//    private static Job fetchAndValidateJob(int jobId, int employerId) {
//        Job job = jobService.getJobById(jobId);
//        if (job == null || job.getEmployerId() != employerId) {
//            System.out.println(job == null ? "Job not found with ID: " + jobId : 
//                "You can only review applications for jobs you posted.");
//            return null;
//        }
//        return job;
//    }
    
    private static Job fetchAndValidateJob(int jobId, int employerId) {
        Job job = jobService.getJobById(jobId);
        if (job == null) {
            System.out.println("Job not found with ID: " + jobId);
            return null;
        }
        if (job.getEmployerId() != employerId) {
            System.out.println("You can only review applications for jobs you posted.");
            return null;
        }
        return job;
    }
 
    public static void reviewApplications(int jobId, int employerId) {
        Job job = fetchAndValidateJob(jobId, employerId);
        if (job == null) return;

        List<JobApplication> applications = jobApplicationService.getApplicationsByJob(jobId);
        displayApplications(applications, jobId);
    }
    
    public static void reviewFilteredApplications(int jobId, int employerId) {
        Job job = fetchAndValidateJob(jobId, employerId);
        if (job == null) return;

        List<JobApplication> applications = jobApplicationService.getApplicationsByJob(jobId);
        List<JobApplication> filteredApplications = applications.stream()
            .filter(app -> app.getApplicantExperienceYears() >= job.getRequiredExperienceYears() && 
                           app.getApplicantSkills().containsAll(job.getRequiredSkills()))
            .collect(Collectors.toList());

        displayApplications(filteredApplications, jobId);
    }

    private static void displayApplications(List<JobApplication> applications, int jobId) {
        if (applications.isEmpty()) {
            System.out.println("No applications found for Job ID: " + jobId);
            return;
        }

        System.out.println("\n===== Applications for Job ID: " + jobId + " =====");
        for (JobApplication app : applications) {
            System.out.println("Applicant ID: " + app.getUserId());
            System.out.println("Applicant Name: " + app.getApplicantName());
            System.out.println("Resume: " + app.getResumePath());
            System.out.println("Cover Letter: " + app.getCoverLetterPath());
            System.out.println("Experience Years: " + app.getApplicantExperienceYears());
            System.out.println("Skills: " + String.join(", ", app.getApplicantSkills()));
            System.out.println("-------------------------------");
        }
    }
}

