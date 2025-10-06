package com.jobfinder.service;

import com.jobfinder.model.User;
import com.jobfinder.utils.JsonFileHandler;

import java.util.List;

public class LoginService {

	private String JOB_SEEKER_FILE_PATH = "job_seekers.json";
    private String EMPLOYER_FILE_PATH = "employers.json";
    
    public LoginService() {
    	
    }
    
    public LoginService(String jobSeekerFilePath, String employerFilePath) {
    	this.JOB_SEEKER_FILE_PATH = jobSeekerFilePath;
    	this.EMPLOYER_FILE_PATH = employerFilePath;
    }

    public User login(String email, String password) {
        if (email == null || password == null) {
            System.out.println("Email or password cannot be null.");
            return null;
        }

        User user = validateLogin(email, password, JsonFileHandler.loadUsersFromFile(JOB_SEEKER_FILE_PATH));
        if (user != null) {
            return user;
        }

        user = validateLogin(email, password, JsonFileHandler.loadUsersFromFile(EMPLOYER_FILE_PATH));
        if (user != null) {
            return user;
        }

        System.out.println("Login failed. Email not found.");
        return null;
    }

    private User validateLogin(String email, String password, List<User> users) {

        if (users == null || users.isEmpty()) {
            System.out.println("No users found");

            return null;
        }

        for (User user : users) {
            if (email.equalsIgnoreCase(user.getEmail())) {
                if (password.equals(user.getPassword())) {
                    System.out.println("Login successful! Welcome: " + user.getName() + " (" + user.getRole() + ")");
                    return user;
                } else {
                    System.out.println("Incorrect password for user: " + user.getName());
                    return null;
                }
            }
        }

        return null;
    }
}
