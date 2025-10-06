package com.jobfinder.model;

import java.util.Set;

public class JobApplication {
    private int jobId;
    private int userId;
    private String resumePath;
    private String coverLetterPath;
    private int applicantExperienceYears;
    private Set<String> applicantSkills;
    private ApplicationStatus status;
    private transient String applicantName;
    private boolean updated;
    
    public enum ApplicationStatus {
        SUBMITTED, UNDER_REVIEW, INTERVIEWED, HIRED, REJECTED
    }

	    public JobApplication(int jobId, int userId, String resumePath, String coverLetterPath, 
	                          int applicantExperienceYears, Set<String> applicantSkills) {
	        this(jobId, userId, resumePath, coverLetterPath, applicantExperienceYears, 
	             applicantSkills, ApplicationStatus.SUBMITTED, false);
	    }


    public JobApplication(int jobId, int userId, String resumePath, String coverLetterPath, 
        int applicantExperienceYears, Set<String> applicantSkills, 
        ApplicationStatus status, boolean updated) {
		this.jobId = jobId;
		this.userId = userId;
		this.resumePath = resumePath;
		this.coverLetterPath = coverLetterPath;
		this.applicantExperienceYears = applicantExperienceYears;
		this.applicantSkills = applicantSkills;
		this.status = status;
		this.updated = updated; 
	}

    public int getJobId() {
        return jobId;
    }

    public int getUserId() {
        return userId;
    }

    public String getResumePath() {
        return resumePath;
    }

    public String getCoverLetterPath() {
        return coverLetterPath;
    }
    
    public int getApplicantExperienceYears() {
        return applicantExperienceYears;
    }

    public Set<String> getApplicantSkills() {
        return applicantSkills;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "JobApplication{" +
                "jobId=" + jobId +
                ", userId=" + userId +
                ", resumePath='" + resumePath + '\'' +
                ", coverLetterPath='" + coverLetterPath + '\'' +
                ", applicantExperienceYears=" + applicantExperienceYears +
                ", applicantSkills=" + applicantSkills +
                ", status=" + status +
                '}';
    }

}
