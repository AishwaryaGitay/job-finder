package com.jobfinder.service;

import com.jobfinder.model.User;
import com.jobfinder.utils.JsonFileHandler;

import java.util.List;

public class PasswordResetService {

    private static final String JOB_SEEKER_FILE_PATH = "job_seekers.json";
    private static final String EMPLOYER_FILE_PATH = "employers.json";

    public String resetPassword(User loggedInUser, String oldPassword, String newPassword) {
        if (loggedInUser == null) {
            return "No user is logged in.";
        }

        if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
            return "Invalid input. Old password and new password are required.";
        }

        String filePath = loggedInUser.getRole().equalsIgnoreCase("job_seeker") 
                ? JOB_SEEKER_FILE_PATH 
                : EMPLOYER_FILE_PATH;

        List<User> users = JsonFileHandler.loadUsersFromFile(filePath);

        for (User user : users) {
            if (user.getUserId() == loggedInUser.getUserId()) {
                if (!user.getPassword().equals(oldPassword)) {
                    return "The old password you entered is incorrect.";
                }

                user.setPassword(newPassword);
                boolean success = JsonFileHandler.saveUsersToFile(users, filePath);
                if (success) {
                    return "Password reset successfully.";
                } else {
                    return "Failed to save changes. Please try again later.";
                }
            }
        }

        return "Error: Logged-in user not found in the system.";
    }
}