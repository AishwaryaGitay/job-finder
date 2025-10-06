package com.jobfinder.service;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.Job;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;
import com.jobfinder.utils.JobPortalFAQ;
import com.jobfinder.utils.JsonFileHandler;

public class JobService {

	private String filePath = "jobs.json";
    private List<Job> jobs;
    private IdGenerator idGenerator;

    public JobService() {
        this.jobs = JsonFileHandler.loadJobsFromFile(filePath);
        this.idGenerator = new IdGenerator();
    }
    
    public JobService (String filePath) {
    	this.filePath = filePath;
    	this.jobs = JsonFileHandler.loadJobsFromFile(filePath);
        this.idGenerator = new IdGenerator();
    }

    public Job createJob(int employerId, String companyName, String title, String description, Location location,
            String salary, JobType jobType, Industry industry, int requiredExperienceYears, Set<String> requiredSkills) {
    	
    	int jobId = idGenerator.generateUniqueId(Constants.JOB);
    	LocalDate postedDate = LocalDate.now();
    	Job newJob = new Job(jobId, employerId, companyName, title, description, location, salary, JobStatus.OPEN, jobType, industry, requiredExperienceYears, postedDate, requiredSkills != null ? requiredSkills : new HashSet<String>());
    	jobs.add(newJob);
        JsonFileHandler.saveJobsToFile(jobs, filePath);
        System.out.println("Job created successfully!");
        return newJob;
    }
    
    
    public Job getJobById(int jobId) {
        Job job = jobs.stream()
                      .filter(j -> j.getJobId() == jobId)
                      .findFirst()
                      .orElse(null);
        if (job == null) {
            System.out.println("Job with ID " + jobId + " not found.");
        }
        return job;
    }


    public List<Job> getAllJobs() {
        return jobs;
    }

    public void displayJobsByEmployer(int employerId) {
        List<Job> employerJobs = jobs.stream()
                .filter(job -> job.getEmployerId() == employerId)
                .collect(Collectors.toList());

        if (employerJobs.isEmpty()) {
            System.out.println("No jobs posted.");
            return;
        }

        System.out.println("\n===== Jobs Posted by You =====");
        for (Job job : employerJobs) {
            printJobDetails(job);
        }
    }

    public boolean updateJobDetails(int employerId, int jobId, String newTitle, String newDescription,
             Location newLocation, String newSalary, JobType newJobType, JobStatus newJobStatus,
             Industry newIndustry, int newExperienceYears, Set<String> newRequiredSkills) {
        
        Job jobToUpdate = getJobById(jobId);

        if (jobToUpdate == null || jobToUpdate.getEmployerId() != employerId) {
            System.out.println("Job not found or you are not authorized to update this job.");
            return false;
        }

        if (newTitle != null && !newTitle.isEmpty()) jobToUpdate.setTitle(newTitle);
        if (newDescription != null && !newDescription.isEmpty()) jobToUpdate.setDescription(newDescription);
        if (newLocation != null) jobToUpdate.setLocation(newLocation);
        if (newSalary != null && !newSalary.isEmpty()) jobToUpdate.setSalary(newSalary);
        if (newJobType != null) jobToUpdate.setJobType(newJobType);
        if (newJobStatus != null) jobToUpdate.setJobStatus(newJobStatus);
        if (newIndustry != null) jobToUpdate.setIndustry(newIndustry);
        if (newExperienceYears > 0) jobToUpdate.setRequiredExperienceYears(newExperienceYears);
        if (newRequiredSkills != null && !newRequiredSkills.isEmpty()) jobToUpdate.setRequiredSkills(newRequiredSkills);

        JsonFileHandler.saveJobsToFile(jobs, filePath);
        return true;
    }

    // Delete a job by employer
    public boolean deleteJob(int employerId, int jobId) {
        Job jobToDelete = getJobById(jobId);

        if (jobToDelete == null || jobToDelete.getEmployerId() != employerId) {
            System.out.println("Job not found or you are not authorized to delete this job.");
            return false;
        }

        jobs.remove(jobToDelete);
        JsonFileHandler.saveJobsToFile(jobs, filePath);
        return true;
    }

    // Display all jobs
    public void displayAllJobs() {
        if (jobs.isEmpty()) {

            System.out.println("No jobs available at the moment.");
            return;
        }

        System.out.println("\n===== Displaying All Jobs =====");
        for (Job job : jobs) {
            printJobDetails(job);
        }
    }

    // Display open jobs only
    public void displayOpenJobs() {
        List<Job> openJobs = jobs.stream()
                .filter(job -> job.getJobStatus() == JobStatus.OPEN)
                .collect(Collectors.toList());

        if (openJobs.isEmpty()) {
            System.out.println("No open jobs available at the moment.");
            return;
        }

        System.out.println("\n===== Open Jobs =====");
        for (Job job : openJobs) {
            printJobDetails(job);
        }
    }

    // Filter jobs based on criteria
    public List<Job> filterJobs(Industry industry, Location location, JobType employmentType, int requiredExperience, String maxSalary) {
        return jobs.stream()
                .filter(job -> (industry == null || job.getIndustry() == industry) &&
                        (location == null || job.getLocation() == location) &&
                        (employmentType == null || job.getJobType() == employmentType) &&
                        (requiredExperience <= 0 || job.getRequiredExperienceYears() >= requiredExperience) &&
                        (maxSalary == null || maxSalary.isBlank() || isSalaryWithinRange(job.getSalary(), maxSalary)))
                .collect(Collectors.toList());
    }

    public List<Job> sortJobsByRecent() {
        return jobs.stream()
                .sorted(Comparator.comparing(Job::getPostedDate).reversed())
                .collect(Collectors.toList());
    }

    public void printJobDetails(Job job) {
        System.out.println(
                "------------------------------------------------------------------------------------------------------------------");
        System.out.println("Job ID: " + job.getJobId());
        System.out.println("Company Name: " + job.getCompanyName());
        System.out.println("Title: " + job.getTitle());
        System.out.println("Description: " + job.getDescription());
        System.out.println("Required Experience: " + job.getRequiredExperienceYears() + " years");
        System.out.println("Location: " + job.getLocation().name());
        System.out.println("Industry: " + job.getIndustry().name());
        System.out.println("Salary: " + (job.getSalary() != null ? job.getSalary() : "Not specified"));
        if(job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
        System.out.println("Required Skills: " + String.join(", ", job.getRequiredSkills()));
        }
        else {
        	System.out.println("Required Skills: Not Specified");
        }
        System.out.println("Status: " + job.getJobStatus());
        System.out.println("Type of Employment: " + job.getJobType().getDescription());
        System.out.println("Posted Date: " + (job.getPostedDate() != null ? job.getPostedDate() : "Not specified"));
        System.out.println(
                "------------------------------------------------------------------------------------------------------------------");
    }

    private boolean isSalaryWithinRange(String salary, String maxSalary) {
        if (salary == null || salary.isBlank() || maxSalary == null || maxSalary.isBlank()) return true;
        try {
            double salaryValue = Double.parseDouble(salary.replace("$", ""));
            double maxSalaryValue = Double.parseDouble(maxSalary.replace("$", ""));
            return salaryValue <= maxSalaryValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public List<Job> getJobsByEmployer(int employerId) {
        return jobs.stream()
                   .filter(job -> job.getEmployerId() == employerId)
                   .collect(Collectors.toList());
    }


    //83.As an user i want a simple FAQ page to address common platform usage questions
    public String getFAQs(){
        return JobPortalFAQ.getFAQ();
    }


}
