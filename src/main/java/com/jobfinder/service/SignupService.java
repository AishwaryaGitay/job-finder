package com.jobfinder.service;

import java.util.List;

import com.jobfinder.model.User;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;
import com.jobfinder.utils.JsonFileHandler;

public class SignupService {
	
    private String JOB_SEEKER_FILE_PATH = "job_seekers.json";
    private String EMPLOYER_FILE_PATH = "employers.json";
    private List<User> jobSeekers;
    private List<User> employers;
    private IdGenerator idGenerator;

    public SignupService() {
    	idGenerator = new IdGenerator();
        this.jobSeekers = JsonFileHandler.loadUsersFromFile(JOB_SEEKER_FILE_PATH);
        this.employers = JsonFileHandler.loadUsersFromFile(EMPLOYER_FILE_PATH);
    }
    
    public SignupService(String jobSeekerFilePath, String employerFilePath) {
    	this.JOB_SEEKER_FILE_PATH = jobSeekerFilePath;
    	this.EMPLOYER_FILE_PATH = employerFilePath;
    	idGenerator = new IdGenerator();
    	this.jobSeekers = JsonFileHandler.loadUsersFromFile(JOB_SEEKER_FILE_PATH);
        this.employers = JsonFileHandler.loadUsersFromFile(EMPLOYER_FILE_PATH);
    }

    public boolean isEmailTaken(String email) {
        return isEmailTaken(email, jobSeekers) || isEmailTaken(email, employers);
    }
    private boolean isEmailTaken(String email, List<User> users) {
        System.out.println("Loaded job seekers: " + jobSeekers);
        return users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    
    public User registerUser(String name, String email, String password, String role, String location, String previousJobTitle, String previousSalary, String companyDescription, String website, String highestEducation) {
        if (Constants.JOB_SEEKER.equalsIgnoreCase(role)) {
            return registerJobSeeker(name, email, password, location, previousJobTitle, previousSalary, highestEducation);
        } else if (Constants.EMPLOYER.equalsIgnoreCase(role)) {
            return registerEmployer(name, location, email, password, companyDescription, website);
        } else {
            System.out.println("Invalid role specified.");
            return null;
        }
    }

    
    public User registerJobSeeker(String name, String email, String password, String location, String previousJobTitle, String previousSalary, String highestEducation) {
        if (isEmailTaken(email, jobSeekers)) {
            System.out.println("Error: Email already exists.");
            return null;
        }
        int userId = generateUniqueId(Constants.JOB_SEEKER);
        User newUser = new User(userId, name, email, password, location, previousJobTitle, previousSalary, highestEducation); 
        jobSeekers.add(newUser); 
        JsonFileHandler.saveUsersToFile(jobSeekers, JOB_SEEKER_FILE_PATH);
        System.out.println("Job seeker registration successful!");
        return newUser;
    }

    // Register a new employer   
    public User registerEmployer(String name, String location, String email, String password, String companyDescription, String website) {
        if (isEmailTaken(email, employers)) {
            System.out.println("Error: Employer email already exists.");
            return null;
        }
        int userId = generateUniqueId(Constants.EMPLOYER);
        User newEmployer = new User(userId, name, email, password, location, companyDescription, website); 
        employers.add(newEmployer);
        JsonFileHandler.saveUsersToFile(employers, EMPLOYER_FILE_PATH);
        System.out.println("Employer registration successful!");
        return newEmployer;
    }
    
    public int generateUniqueId(String type) {
        return idGenerator.generateUniqueId(type);
    }

}
