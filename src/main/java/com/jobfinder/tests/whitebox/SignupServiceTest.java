package com.jobfinder.tests.whitebox;

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
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_JOB_SEEKER_FILE);
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_EMPLOYER_FILE);

        User mockJobSeeker = new User(
            1, 
            "Alice", 
            "alice@test.com", 
            "password123", 
            "New York", 
            "Software Developer", 
            "100000", 
            "Bachelor's"
        );

        User mockEmployer = new User(
            2, 
            "TechCorp", 
            "techcorp@test.com", 
            "password123", 
            "London", 
            "Tech Company", 
            "techcorp.com"
        );

        JsonFileHandler.saveUsersToFile(List.of(mockJobSeeker), MOCK_JOB_SEEKER_FILE);
        JsonFileHandler.saveUsersToFile(List.of(mockEmployer), MOCK_EMPLOYER_FILE);

        signupService = new SignupService();
    }


    @AfterEach
    public void cleanup() {
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_JOB_SEEKER_FILE);
        JsonFileHandler.saveUsersToFile(List.of(), MOCK_EMPLOYER_FILE);
    }

    @Test
    public void testIsEmailTaken_EmailNotRegistered() {
        boolean result = signupService.isEmailTaken("newuser@test.com");
        assertFalse(result, "Email should not be taken if not registered.");
    }

    @Test
    public void testIsEmailTaken_EmailAlreadyRegisteredAsJobSeeker() {
        User mockJobSeeker = new User(1, "Alice", "alice@test.com", "password123", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(mockJobSeeker), MOCK_JOB_SEEKER_FILE);
        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);
System.out.println(updatedUser);
        boolean result = signupService.isEmailTaken("alice@test.com");
        System.out.println(updatedUser.getEmail());
        assertTrue(result, "Email should be taken if already registered as a job seeker.");
    }

    @Test
    public void testIsEmailTaken_EmailAlreadyRegisteredAsEmployer() {
        User mockEmployer = new User(1, "TechCorp", "techcorp@test.com", "password123", "London", "Tech Company", "techcorp.com");
        JsonFileHandler.saveUsersToFile(List.of(mockEmployer), MOCK_EMPLOYER_FILE);
        List<User> users = JsonFileHandler.loadUsersFromFile("employers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockEmployer.getEmail()))
                .findFirst() 
                .orElse(null);

        boolean result = signupService.isEmailTaken("techcorp@test.com");
        assertTrue(result, "Email should be taken if already registered as an employer.");
    }

    @Test
    public void testRegisterJobSeeker_Success() {
        User newJobSeeker = signupService.registerUser(
            "Alice",
            "alicee@test.com",
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
        assertEquals("alicee@test.com", newJobSeeker.getEmail(), "Registered email should match input.");

    }

    @Test
    public void testRegisterEmployer_Success() {
        User newEmployer = signupService.registerUser(
            "TechCorp",
            "techcorpp@test.com",
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
        assertEquals("techcorpp@test.com", newEmployer.getEmail(), "Registered email should match input.");

    }

    @Test
    public void testRegisterEmployer_EmailAlreadyExists() {
        User mockEmployer = new User(1, "TechCorp", "techcorp@test.com", "password123", "London", "Tech Company", "techcorp.com");
        JsonFileHandler.saveUsersToFile(List.of(mockEmployer), MOCK_EMPLOYER_FILE);

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

        assertNull(newEmployer, "Registration should fail if email already exists.");
    }

    @Test
    public void testRegisterUser_InvalidRole() {
        User result = signupService.registerUser(
            "Alice",
            "alice@test.com",
            "password123",
            "invalid_role",
            "New York",
            "Software Developer",
            "100000",
            null,
            null,
            "Bachelor's"
        );

        assertNull(result, "Registration should fail for invalid role.");
    }
}
