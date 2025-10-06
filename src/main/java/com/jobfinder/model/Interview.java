package com.jobfinder.model;

public class Interview {
    private int interviewId;    
    private int jobId;         
    private int jobSeekerId;     
    private String date;         
    private String time;         
    private String meetingLink;  
    private String details;     
    private String status;       

    public Interview(int interviewId, int jobId, int jobSeekerId, String date, String time, String meetingLink, String details, String status) {
        this.interviewId = interviewId;
        this.jobId = jobId;
        this.jobSeekerId = jobSeekerId;
        this.date = date;
        this.time = time;
        this.meetingLink = meetingLink;
        this.details = details;
        this.status = status;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(int jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Interview{" +
                "interviewId=" + interviewId +
                ", jobId=" + jobId +
                ", jobSeekerId=" + jobSeekerId +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", meetingLink='" + meetingLink + '\'' +
                ", details='" + details + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
