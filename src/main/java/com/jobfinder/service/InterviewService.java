package com.jobfinder.service;

import com.jobfinder.model.Interview;
import com.jobfinder.utils.JsonFileHandler;

import java.util.List;
import java.util.stream.Collectors;

public class InterviewService {
    private static final String INTERVIEW_FILE_PATH = "scheduledInterviews.json";

    public List<Interview> getScheduledInterviewsForJobSeeker(int jobSeekerId) {
        List<Interview> allInterviews = JsonFileHandler.loadInterviewsFromFile(INTERVIEW_FILE_PATH);
        return allInterviews.stream()
                .filter(interview -> interview.getJobSeekerId() == jobSeekerId)
                .collect(Collectors.toList());
    }
    
    public boolean markInterviewAsCompleted(int interviewId) {
        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(INTERVIEW_FILE_PATH);
        for (Interview interview : interviews) {
            if (interview.getInterviewId() == interviewId) {
                interview.setStatus("Completed");
                JsonFileHandler.saveInterviewsToFile(interviews, INTERVIEW_FILE_PATH);
                return true; 
            }
        }
        return false; 
    }

    public List<Interview> getCompletedInterviewsForJobSeeker(int jobSeekerId) {
        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(INTERVIEW_FILE_PATH);
        return interviews.stream()
                .filter(interview -> interview.getJobSeekerId() == jobSeekerId && "Completed".equalsIgnoreCase(interview.getStatus()))
                .collect(Collectors.toList());
    }
    
    public void scheduleInterview(Interview interview) {
        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile("scheduledInterviews.json");
        interviews.add(interview);
        JsonFileHandler.saveInterviewsToFile(interviews, "scheduledInterviews.json");
    }

    public void updateInterview(int interviewId, String newDetails) {
        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile("scheduledInterviews.json");
        for (Interview interview : interviews) {
            if (interview.getInterviewId() == interviewId) {
                interview.setDetails(newDetails);
                JsonFileHandler.saveInterviewsToFile(interviews, "scheduledInterviews.json");
                break;
            }
        }
    }

    public boolean cancelInterview(int interviewId) {
    List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(INTERVIEW_FILE_PATH);

    boolean removed = interviews.removeIf(interview -> interview.getInterviewId() == interviewId);
    if (removed) {
        JsonFileHandler.saveInterviewsToFile(interviews, INTERVIEW_FILE_PATH);
        System.out.println("Interview canceled successfully.");
        return true;
    } else {
        System.out.println("Interview not found. Cancelation failed.");
        return false;
    }
}

public boolean rescheduleInterview(int interviewId, String newDate, String newTime) {
    List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(INTERVIEW_FILE_PATH);

    for (Interview interview : interviews) {
        if (interview.getInterviewId() == interviewId) {
            interview.setDate(newDate);
            interview.setTime(newTime);
            JsonFileHandler.saveInterviewsToFile(interviews, INTERVIEW_FILE_PATH);
            System.out.println("Interview rescheduled successfully.");
            return true;
        }
    }
    System.out.println("Interview not found. Rescheduling failed.");
    return false;
}

}
