package com.jobfinder.tests.whitebox;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.User;
import com.jobfinder.service.LoginService;
import com.jobfinder.service.SignupService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceWhiteboxTest {

	private LoginService loginService;
    private SignupService signupService;

    private static final String TEST_JOB_SEEKER_FILE = "src/test/resources/test_job_seekers.json";
    private static final String TEST_EMPLOYER_FILE = "src/test/resources/test_employers.json";
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

     @BeforeEach
     void setUp() {
         this.loginService = new LoginService();
         this.signupService = new SignupService();
         System.setOut(new PrintStream(this.outputStream));
     }
     
     @AfterEach
     void clearTestFiles() {
         try (FileWriter jobSeekerWriter = new FileWriter(TEST_JOB_SEEKER_FILE);
              FileWriter employerWriter = new FileWriter(TEST_EMPLOYER_FILE)) {
             jobSeekerWriter.write("[]");
             employerWriter.write("[]");
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    
     @Test
    void testJobSeekerLoginSuccess() {
    	 
    	signupService.registerJobSeeker("Alisha Solanki", "alisha@gmail.com", "ali123", Location.COVENTRY.name(), "Lawyer", "26000", "LLB");
        User loggedInUser = loginService.login("alisha@gmail.com", "ali123");
        assertNotNull(loggedInUser, "Login should succeed for a valid job seeker.");
        assertEquals("Alisha Solanki", loggedInUser.getName(), "The logged-in user's name should match.");
        assertEquals(Location.COVENTRY.name(), loggedInUser.getLocation(), "The logged-in user's location should match.");
        assertEquals("Lawyer", loggedInUser.getPreviousJobTitle(), "The logged-in user's previous job title should match.");
        assertEquals("26000", loggedInUser.getPreviousSalary(), "The logged-in user's previous salary should match.");
        assertEquals("LLB", loggedInUser.getHighestEducation(), "The logged-in user's highest education should match.");
    }

     
     @Test
    void testLoginEmployerSuccess() {
    	signupService.registerEmployer("Fiserv", Location.OXFORD.name(), "fiserv@gmail.com", "fi123", "This company is famous worldwide", "www.fiserv.com");
        User loggedInUser = loginService.login("fiserv@gmail.com", "fi123");
        assertNotNull(loggedInUser, "Login should succeed for a valid employer.");
        assertEquals("Fiserv", loggedInUser.getName(), "The logged-in employer's name should match.");
        assertEquals(Location.OXFORD.name(), loggedInUser.getLocation(), "The logged-in employer's location should match.");
        assertEquals("This company is famous worldwide", loggedInUser.getCompanyDescription(), "The logged-in employer's company description should match.");
        assertEquals("www.fiserv.com", loggedInUser.getWebsite(), "The logged-in employer's company website should match.");
    }
    
  
}

