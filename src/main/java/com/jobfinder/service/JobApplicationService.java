package com.jobfinder.service;

import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import com.jobfinder.model.User;
import com.jobfinder.utils.JsonFileHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JobApplicationService {

    private static final String APPLICATION_FILE_PATH = "job_applications.json";
    private static final String USER_FILE_PATH = "job_seekers.json";
    private List<JobApplication> applications;
    private JobService jobService;

    public JobApplicationService() {
        this.applications = JsonFileHandler.loadApplicationsFromFile(APPLICATION_FILE_PATH);
        if (this.applications == null) {
            this.applications = new ArrayList<>();
        }
        this.jobService = new JobService();
    }

    public boolean applyForJob(User user, int jobId, int experienceYears, Set<String> skills) {
        if (user == null || !"job_seeker".equalsIgnoreCase(user.getRole())) {
            System.out.println("Only job seekers can apply for jobs.");
            return false;
        }

        if (user.getResume() == null || user.getCoverLetter() == null) {
            System.out.println("Please upload your resume and cover letter before applying.");
            return false;
        }

        Job job = jobService.getJobById(jobId);
        if (job == null) {
            System.out.println("Job not found.");
            return false;
        }

        JobApplication newApplication = new JobApplication(
            jobId,
            user.getUserId(),
            user.getResume(),
            user.getCoverLetter(),
            experienceYears,
            skills,
            JobApplication.ApplicationStatus.SUBMITTED,
            false
        );

        applications.add(newApplication);
        JsonFileHandler.saveApplicationsToFile(applications, APPLICATION_FILE_PATH);
        
        return true;
    }

    
    public boolean updateApplicationStatus(int applicationId, JobApplication.ApplicationStatus newStatus, int employerId) {
        System.out.println("===========================");
        System.out.println(applications);
        System.out.println("===========================");
        for (JobApplication app : applications) {
            if (app.getUserId() == applicationId) {
                Job job = jobService.getJobById(app.getJobId());
                if (job != null && job.getEmployerId() == employerId && app.getStatus() != newStatus) {
                    app.setStatus(newStatus);
                    app.setUpdated(true); 
                    JsonFileHandler.saveApplicationsToFile(applications, APPLICATION_FILE_PATH);
                    return true;
                }
            }
        } 
        return false;
    }

    
    
    public void displayApplicationStatuses(int employerId, Integer specificJobId) {
        List<Job> jobs = jobService.getJobsByEmployer(employerId);
        if (jobs.isEmpty()) {
            System.out.println("No jobs found for this employer.");
            return;
        }

        if (specificJobId != null) {
            Job specificJob = jobs.stream()
                                  .filter(j -> j.getJobId() == specificJobId)
                                  .findFirst()
                                  .orElse(null);
            if (specificJob == null) {
                System.out.println("Invalid Job ID or Job does not belong to this employer.");
                return;
            }
            jobs = List.of(specificJob);  
        }

        for (Job job : jobs) {
            System.out.println("\n===== Application Statuses for Job: " + job.getTitle() + " (ID: " + job.getJobId() + ") =====");
            List<JobApplication> applications = getApplicationsByJob(job.getJobId());
            if (applications.isEmpty()) {
                System.out.println("No applications submitted yet.");
                continue;
            }
            applications.forEach(app -> System.out.println("Applicant ID: " + app.getUserId() + " - Status: " + app.getStatus()));
        }
    }
    
    public void checkForUpdates(int userId) {
        List<JobApplication> userApplications = getApplicationsByUser(userId);
        if (userApplications.isEmpty()) {
            System.out.println("You have no applications submitted.");
            return;
        }

        boolean hasUpdates = false;
        for (JobApplication app : userApplications) {
            if (!app.isUpdated()) {
                System.out.println("Update for Job ID " + app.getJobId() + ": Status changed to " + app.getStatus());
                app.setUpdated(true); 
                hasUpdates = true;
            } else {
                System.out.println("Current status for Job ID " + app.getJobId() + ": " + app.getStatus());
            }
        }

        if (hasUpdates) {
            JsonFileHandler.saveApplicationsToFile(userApplications, APPLICATION_FILE_PATH);
        } else {
            System.out.println("Displayed current status for all applications.");
        }
    }
  
    public List<JobApplication> getApplicationsByUser(int userId) {
        List<JobApplication> userApplications = new ArrayList<>();
        for (JobApplication app : applications) {
            if (app.getUserId() == userId) {
                userApplications.add(app);
            }
        }
        return userApplications;
    }
    

    public List<JobApplication> getApplicationsByJob(int jobId) {
        List<JobApplication> jobApplications = new ArrayList<>();
        List<User> users = JsonFileHandler.loadUsersFromFile(USER_FILE_PATH);

        for (JobApplication app : applications) {
            if (app.getJobId() == jobId) {
                User user = users.stream()
                                 .filter(u -> u.getUserId() == app.getUserId())
                                 .findFirst()
                                 .orElse(null);
                if (user != null) {
                    app.setApplicantName(user.getName()); 
                }
                jobApplications.add(app);
            }
        }
        return jobApplications;
    }



    
    public List<JobApplication> getApplicationsByStatus(int userId, JobApplication.ApplicationStatus status) {
        List<JobApplication> allUserApps = getApplicationsByUser(userId);
        return allUserApps.stream()
                          .filter(app -> app.getStatus() == status)
                          .collect(Collectors.toList());
    }
    
    
    // Helper method to reload user data from JSON
    public User reloadUserFromFile(String email) {
        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }

}
