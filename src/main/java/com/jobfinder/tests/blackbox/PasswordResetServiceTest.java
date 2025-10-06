package com.jobfinder.tests.blackbox;

import com.jobfinder.model.User;
import com.jobfinder.service.PasswordResetService;
import com.jobfinder.utils.JsonFileHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordResetServiceTest {

    private static final String MOCK_JOB_SEEKER_FILE = "job_seekers.json";
    private static final String MOCK_EMPLOYER_FILE = "employers.json";

    private PasswordResetService passwordResetService;

    @BeforeEach
    public void setUp() {
        passwordResetService = new PasswordResetService();
        clearMockFiles();
    }

    @AfterEach
    public void tearDown() {
        clearMockFiles();
    }

    private void clearMockFiles() {
        try (FileWriter jobSeekerWriter = new FileWriter(MOCK_JOB_SEEKER_FILE);
             FileWriter employerWriter = new FileWriter(MOCK_EMPLOYER_FILE)) {
            jobSeekerWriter.write("[]");
            employerWriter.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testResetPassword_NoUserLoggedIn() {
        String result = passwordResetService.resetPassword(null, "oldPassword", "newPassword");
        assertEquals("No user is logged in.", result, "The system should prompt that no user is logged in.");
    }

    @Test
    public void testResetPassword_NullPasswords() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");

        String result = passwordResetService.resetPassword(mockUser, null, "newPassword");
        assertEquals("Invalid input. Old password and new password are required.", result, "Null old password should return invalid input message.");

        result = passwordResetService.resetPassword(mockUser, "oldPassword", null);
        assertEquals("Invalid input. Old password and new password are required.", result, "Null new password should return invalid input message.");
    }

    @Test
    public void testResetPassword_EmptyPasswords() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");

        String result = passwordResetService.resetPassword(mockUser, "", "newPassword");
        assertEquals("Invalid input. Old password and new password are required.", result, "Empty old password should return invalid input message.");

        result = passwordResetService.resetPassword(mockUser, "oldPassword", "");
        assertEquals("Invalid input. Old password and new password are required.", result, "Empty new password should return invalid input message.");
    }

    @Test
    public void testResetPassword_IncorrectOldPassword() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(mockUser), MOCK_JOB_SEEKER_FILE);

        String result = passwordResetService.resetPassword(mockUser, "wrongPassword", "newPassword");
        assertEquals("The old password you entered is incorrect.", result, "Incorrect old password should not allow password reset.");
    }

    @Test
    public void testResetPassword_Success() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(mockUser), MOCK_JOB_SEEKER_FILE);

        String result = passwordResetService.resetPassword(mockUser, "oldPassword", "newPassword");
        assertEquals("Password reset successfully.", result, "Valid inputs should allow password reset.");

        List<User> users = JsonFileHandler.loadUsersFromFile(MOCK_JOB_SEEKER_FILE);
        User updatedUser = users.stream().filter(user -> user.getUserId() == mockUser.getUserId()).findFirst().orElse(null);
        assertEquals("newPassword", updatedUser.getPassword(), "The user's password should be updated in the system.");
    }

    @Test
    public void testResetPassword_UserNotFound() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_JOB_SEEKER_FILE); 

        String result = passwordResetService.resetPassword(mockUser, "oldPassword", "newPassword");
        assertEquals("Error: Logged-in user not found in the system.", result, "The system should return a user not found message.");
    }
 
    @Test
    public void testResetPassword_SaveFailure() {
        User mockUser = new User(1, "Alice", "alice@test.com", "oldPassword", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(mockUser), MOCK_JOB_SEEKER_FILE);

        PasswordResetService passwordResetService = new PasswordResetService() {
            @Override
            public String resetPassword(User loggedInUser, String oldPassword, String newPassword) {
                JsonFileHandler.saveUsersToFile(List.of(), "/invalid/path/job_seekers.json");
                return super.resetPassword(loggedInUser, oldPassword, newPassword);
            }
        };

        String result = passwordResetService.resetPassword(mockUser, "oldPassword", "newPassword");
        assertEquals("Failed to save changes. Please try again later.", result, "System should return a failure message if saving fails.");
    }
}
