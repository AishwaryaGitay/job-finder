package com.jobfinder.model;

import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;

import java.time.LocalDate;
import java.util.Set;

public class Job {

    private int jobId;
    private int employerId;
    private String companyName;
    private String title;
    private String description;
    private Location location;
    private String salary;
    private JobStatus jobStatus;
    private JobType jobType;
    private Industry industry;          
    private LocalDate postedDate;     
    private int requiredExperienceYears; 
    private Set<String> requiredSkills; 
    
  public Job(int jobId, int employerId, String companyName, String title, String description, Location location,
			String salary, JobStatus jobStatus, JobType jobType, Industry industry,
			int requiredExperienceYears, LocalDate postedDate, Set<String> requiredSkills) {
		super();
		this.jobId = jobId;
		this.employerId = employerId;
		this.companyName = companyName;
		this.title = title;
		this.description = description;
		this.location = location;
		this.salary = salary;
		this.jobStatus = jobStatus;
		this.jobType = jobType;
		this.industry = industry;
		this.requiredExperienceYears = requiredExperienceYears;
		this.postedDate = postedDate;
		this.requiredSkills = requiredSkills;
	}

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getEmployerId() {
        return employerId;
    }

    public void setEmployerId(int employerId) {
        this.employerId = employerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }
    
    public int getRequiredExperienceYears() {
        return requiredExperienceYears;
    }
    
    public void setRequiredExperienceYears(int requiredExperienceYears) {
        this.requiredExperienceYears = requiredExperienceYears;
    }
    
    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(Set<String> requiredSkills) {
		this.requiredSkills = requiredSkills;
	}

	@Override
    public String toString() {
        return "Job{" +
                "jobId=" + jobId +
                ", employerId=" + employerId +
                ", companyName='" + companyName + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", salary='" + salary + '\'' +
                ", jobStatus=" + jobStatus +
                ", jobType=" + jobType +
                ", industry='" + industry + '\'' +
                ", postedDate=" + postedDate +
                ", requiredExperienceYears=" + requiredExperienceYears +
                ", requiredSkills=" + requiredSkills +
                '}';
    }
}