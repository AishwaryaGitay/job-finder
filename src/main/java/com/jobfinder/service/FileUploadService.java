package com.jobfinder.service;

import com.jobfinder.model.User;
import com.jobfinder.utils.JsonFileHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileUploadService {
    private static final String JOB_SEEKER_FILE_PATH = "job_seekers.json";

    public boolean uploadFile(User loggedInUser, String filePath, String fileType) {
        if (loggedInUser == null) {
            System.out.println("No user is logged in. File upload failed.");
            return false;
        }

        if (!"job_seeker".equalsIgnoreCase(loggedInUser.getRole())) {
            System.out.println("Only job seekers can upload resumes or cover letters.");
            return false;
        }

        File inputFile = new File(filePath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.out.println("Invalid file. Please provide a valid file path.");
            return false;
        }

        try {
            String userDir = "uploads/" + loggedInUser.getUserId();
            File userFolder = new File(userDir);
            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            String newFilePath = userDir + "/" + fileType + "_" + inputFile.getName();
            Files.copy(inputFile.toPath(), new File(newFilePath).toPath());
            System.out.println(fileType + " uploaded successfully to: " + newFilePath);

            updateUserFile(loggedInUser, fileType, newFilePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error uploading file: " + e.getMessage());
            return false;
        }
    }

    
    private void updateUserFile(User loggedInUser, String fileType, String filePath) {
        List<User> users = JsonFileHandler.loadUsersFromFile(JOB_SEEKER_FILE_PATH);
        boolean userUpdated = false;

        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(loggedInUser.getEmail())) {
                if ("resume".equalsIgnoreCase(fileType)) {
                    user.setResume(filePath);
                    System.out.println("Resume path updated: " + filePath);
                } else if ("cover_letter".equalsIgnoreCase(fileType)) {
                    user.setCoverLetter(filePath);
                    System.out.println("Cover letter path updated: " + filePath);
                }
                userUpdated = true;
                break;
            }
        }

        if (!userUpdated) {
            System.out.println("User not found in job_seekers.json: " + loggedInUser.getEmail());
        }

        boolean saveSuccess = JsonFileHandler.saveUsersToFile(users, JOB_SEEKER_FILE_PATH);
        if (saveSuccess) {
            System.out.println("User data saved successfully to job_seekers.json");
        } else {
            System.out.println("Failed to save user data to job_seekers.json");
        }
    }
    
   public boolean deleteResumeOfJobSeeker(User loggedInUser) {
       if (loggedInUser == null || !"job_seeker".equalsIgnoreCase(loggedInUser.getRole())) {
           System.out.println("Only job seekers can delete resumes.");
           return false;
       }

       String resumePath = loggedInUser.getResume();
       return checkAndDeleteFile(resumePath, "Resume", loggedInUser);
   }
 
   public boolean deleteCoverLetterOfJobSeeker(User loggedInUser) {
       if (loggedInUser == null || !"job_seeker".equalsIgnoreCase(loggedInUser.getRole())) {
           System.out.println("Only job seekers can delete cover letters.");
           return false;
       }

       String coverLetterPath = loggedInUser.getCoverLetter();
       System.out.println(coverLetterPath);
       return checkAndDeleteFile(coverLetterPath, "Cover Letter", loggedInUser);
   }

   private boolean checkAndDeleteFile(String filePath, String fileType, User loggedInUser) {
       if (filePath == null || filePath.isEmpty()) {
           System.out.println(fileType + " path is not set.");
           return false;
       }

       File file = new File(filePath);
       if (file.exists() && file.isFile()) {
           if (file.delete()) {
               System.out.println(fileType + " deleted successfully.");
               updateUserFile(loggedInUser, fileType.equals("Resume") ? "resume" : "cover_letter", null);
               return true;
           } else {
               System.out.println("Failed to delete " + fileType + ".");
               return false;
           }
       } else {
           System.out.println(fileType + " file not found.");
           return false;
       }
   }

}
