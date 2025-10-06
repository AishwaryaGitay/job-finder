package com.jobfinder.tests.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobfinder.main.Application;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.User;
import com.jobfinder.service.FileUploadService;
import com.jobfinder.service.InterviewService;
import com.jobfinder.service.JobApplicationService;
import com.jobfinder.service.JobService;
import com.jobfinder.service.LoginService;
import com.jobfinder.service.PasswordResetService;
import com.jobfinder.service.ProfileUpdateService;
import com.jobfinder.service.SignupService;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;

public class MainMenuWhiteboxTest {

	
	private static final String TEST_EMPLOYERS_FILE = "src/test/resources/test_employers.json";
	private static final String TEST_JOB_SEEKERS_FILE = "src/test/resources/test_job_seekers.json";
	private static final String TEST_JOB_FILE = "src/test/resources/test_jobs.json";
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;
    SignupService signupService;
    LoginService loginService;
    JobService jobService;
    ProfileUpdateService profileUpdateService ;
    FileUploadService fileUploadService;
    JobApplicationService jobApplicationService;
    InterviewService interviewService;
    PasswordResetService passwordResetService;
    IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
        this.signupService = new SignupService(TEST_JOB_SEEKERS_FILE, TEST_EMPLOYERS_FILE);
        this.loginService = new LoginService(TEST_JOB_SEEKERS_FILE, TEST_EMPLOYERS_FILE);
        this.profileUpdateService = new ProfileUpdateService(TEST_JOB_SEEKERS_FILE, TEST_EMPLOYERS_FILE);
        this.jobService = new JobService(TEST_JOB_FILE);
        idGenerator = new IdGenerator();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
    
    @AfterEach
    void clearTestFiles() {
        try (FileWriter jobSeekerWriter = new FileWriter(TEST_JOB_SEEKERS_FILE);
             FileWriter employerWriter = new FileWriter(TEST_EMPLOYERS_FILE);
            		 FileWriter jobWriter = new FileWriter(TEST_JOB_FILE)) {
            jobSeekerWriter.write("[]");
            employerWriter.write("[]");
            jobWriter.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Test
    void testRegisterUserWithValidJobSeekerRole() {
    	
        String input = "Mohit Jain\nmohit@gmail.com\nMht155\njob_seeker\n1\nProject Manager\n80000\nBachelor's Degree in Finanace\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        Scanner scanner = new Scanner(System.in);
        Application.registerUser(scanner, signupService);

        // Capture the output
        String output = out.toString();

        // Validate the output
        assertTrue(output.contains("You have been successfully registered!"));
        assertTrue(output.contains("Mohit Jain"));
        assertTrue(output.contains("job_seeker"));
        assertTrue(output.contains("Project Manager"));
        assertTrue(output.contains("80000"));
    }
    
    
    
    @Test
    void testRegisterUserWithInvalidLocationChoice() {

    	String input = "Rachel Green\nrachel@yahoo.com\nrc123\njob_seeker\n999\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        Scanner scanner = new Scanner(System.in);
        Application.registerUser(scanner, signupService);

        // Capture the output
        String output = out.toString();

        // Validate the output
        assertTrue(output.contains("Invalid choice. Please select a valid location."));
    }
    
    
    @Test
    public void testUpdateEmployerProfile_AllFieldsUpdated() {
        String input = "New Description\nNew Website\n2\n";
        Scanner scanner = new Scanner(input);
        int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        User loggedInUser = new User(userId, "ABIL", "abil@gmail.com", "ab3344", Location.BRIGHTON_AND_HOVE.name(), "A nice company", "www.abil.com");       
        ProfileUpdateService profileUpdateService = new ProfileUpdateService();

        Application.updateEmployerProfile(scanner, loggedInUser, profileUpdateService);

        assertEquals("New Description", loggedInUser.getCompanyDescription());
        assertEquals("New Website", loggedInUser.getWebsite());
        assertEquals(Location.BIRMINGHAM.name(), loggedInUser.getLocation());
    }

}
