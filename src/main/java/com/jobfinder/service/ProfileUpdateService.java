package com.jobfinder.service;

import com.jobfinder.model.User;
import com.jobfinder.utils.JsonFileHandler;

import java.util.List;
import java.util.Optional;

public class ProfileUpdateService {
	
	private String filePath_jobseeker = "job_seekers.json";
	private String filePath_employer = "employers.json";
	
	public ProfileUpdateService() {
		this.filePath_jobseeker = "job_seekers.json";
		this.filePath_employer  = "employers.json";
	}
	
	public ProfileUpdateService(String filePathForJobSeeker, String filePathForEmployers) {
		this.filePath_jobseeker = filePathForJobSeeker;
		this.filePath_employer  = filePathForEmployers;
	}
//
//    private static final String JOB_SEEKER_FILE_PATH = "job_seekers.json";
//    private static final String EMPLOYER_FILE_PATH = "employers.json";
//	
    public void viewUserSummary(User loggedInUser) {
        
    	if (loggedInUser == null) {
            System.out.println("No user is logged in.");
            return;
        }
        
        String filePath = loggedInUser.getRole().equalsIgnoreCase("job_seeker") ?
        		filePath_jobseeker : filePath_employer;
        
        List<User> userList = JsonFileHandler.loadUsersFromFile(filePath);
        
        Optional<User> userOpt = userList.stream()
                .filter(user -> user.getUserId() == loggedInUser.getUserId())
                .findFirst();

        if (!userOpt.isPresent()) {
            System.out.println("User not found.");
            return;
        }

        User profileUser = userOpt.get();

        System.out.println("\n================== User Profile Summary =====================");
        System.out.println("Name: " + profileUser.getName());
        System.out.println("Email: " + profileUser.getEmail());
        System.out.println("Role: " + profileUser.getRole());

        if ("job_seeker".equalsIgnoreCase(profileUser.getRole())) {
            System.out.println("Resume: " + (loggedInUser.getResume() != null ? loggedInUser.getResume() : "Not uploaded"));
            System.out.println("Cover Letter: " + (loggedInUser.getCoverLetter() != null ? loggedInUser.getCoverLetter() : "Not uploaded"));
            System.out.println("Previous Job Title: " + profileUser.getPreviousJobTitle());
            System.out.println("Previous Salary: " + profileUser.getPreviousSalary());
            System.out.println("Highest Education: " + profileUser.getHighestEducation());
        } else if ("employer".equalsIgnoreCase(profileUser.getRole())) {
            System.out.println("Company Description: " + profileUser.getCompanyDescription());
            System.out.println("Website: " + profileUser.getWebsite());
            System.out.println("Location: " + profileUser.getLocation());
        }
        System.out.println("==============================================================\n");
    }
    
    public void updateEmployerProfile(User loggedInUser, String newCompanyDescription, String newWebsite, String newLocation) {
    	
    	if (loggedInUser == null) {
            System.out.println("No user is logged in. Profile update failed.");
            return;
       }

        String filePath = loggedInUser.getRole().equalsIgnoreCase("job_seeker") ?
        		filePath_jobseeker : filePath_employer;
        List<User> users = JsonFileHandler.loadUsersFromFile(filePath);
    	
    	for (User user : users) {
            if (user.getUserId() == loggedInUser.getUserId() && "employer".equalsIgnoreCase(user.getRole())) {
                if (newCompanyDescription != null && !newCompanyDescription.isEmpty()) {
                    user.setCompanyDescription(newCompanyDescription);
                }
                if (newWebsite != null && !newWebsite.isEmpty()) {
                    user.setWebsite(newWebsite);
                }
                if (newLocation != null && !newLocation.isEmpty()) {
                    user.setLocation(newLocation);
                }
                
                if (JsonFileHandler.saveUsersToFile(users, filePath_employer)) {
                    System.out.println("Profile updated successfully.");
                } else {
                    System.out.println("Failed to save profile updates.");
                }
                return;
            }
        }
        System.out.println("User not found or not an employer.");
    }
    
    
    public void updateJobseekerProfile(User loggedInUser, String newLocation, String newPreviousJobTitle, String newPreviousSalary, String newHighestEducation) {
        
    	if (loggedInUser == null) {
            System.out.println("No user is logged in. Profile update failed.");
            return;
       }

        String filePath = loggedInUser.getRole().equalsIgnoreCase("job_seeker") ?
        		filePath_jobseeker : filePath_employer;
        List<User> users = JsonFileHandler.loadUsersFromFile(filePath);
    	
    	for (User user : users) {
            if (user.getUserId() == loggedInUser.getUserId() && "job_seeker".equalsIgnoreCase(user.getRole())) {
                
            	if (newLocation != null && !newLocation.isEmpty()) {
                    user.setLocation(newLocation);
                }
                if (newPreviousJobTitle != null && !newPreviousJobTitle.isEmpty()) {
                    user.setPreviousJobTitle(newPreviousJobTitle);
                }
                if (newPreviousSalary != null && !newPreviousSalary.isEmpty()) {
                    user.setPreviousSalary(newPreviousSalary);
                }
                if (newHighestEducation != null && !newHighestEducation.isEmpty()) {
                    user.setHighestEducation(newHighestEducation);
                }

                if (JsonFileHandler.saveUsersToFile(users, filePath_jobseeker)) {
                    System.out.println("Profile updated successfully.");
                } else {
                    System.out.println("Failed to save profile updates.");
                }
                return;
            }
        }
        System.out.println("User not found or not a job seeker.");
    }
}
