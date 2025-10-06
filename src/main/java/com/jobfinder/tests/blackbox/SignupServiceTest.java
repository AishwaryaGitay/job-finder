package com.jobfinder.tests.blackbox;

import com.jobfinder.model.User;
import com.jobfinder.service.SignupService;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.JsonFileHandler;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SignupServiceTest {

    private static final String MOCK_JOB_SEEKER_FILE = "job_seekers.json";
    private static final String MOCK_EMPLOYER_FILE = "employers.json";

    private SignupService signupService;

    @BeforeEach
    public void setup() {
        signupService = new SignupService();
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_JOB_SEEKER_FILE);
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_EMPLOYER_FILE);
    }

    @AfterEach
    public void cleanup() {
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_JOB_SEEKER_FILE);
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_EMPLOYER_FILE);
    }

    @Test
    public void testRegisterJobSeeker_Success() {
        User newJobSeeker = signupService.registerUser(
            "Alice",
            "alice@test.com",
            "password123",
            Constants.JOB_SEEKER,
            "New York",
            "Software Developer",
            "100000",
            null,
            null,
            "Bachelor's"
        );

        assertNotNull(newJobSeeker, "Job seeker registration should succeed.");
        assertEquals("alice@test.com", newJobSeeker.getEmail(), "Registered email should match input.");
    }

    @Test
    public void testRegisterEmployer_Success() {
        User newEmployer = signupService.registerUser(
            "TechCorp",
            "techcorp@test.com",
            "password123",
            Constants.EMPLOYER,
            "London",
            null,
            null,
            "Tech Company",
            "techcorp.com",
            null
        );

        assertNotNull(newEmployer, "Employer registration should succeed.");
        assertEquals("techcorp@test.com", newEmployer.getEmail(), "Registered email should match input.");
    }


}
