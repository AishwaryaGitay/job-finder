package com.jobfinder.tests.blackbox;

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

class LoginServiceBlackboxTest {

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
    void testLogin_NoSuchUser() {
    	User user = loginService.login("qwerty111@gmail.com", "password123");
        assertNull(user, "Login should fail if there is no such user present.");
    }
        
    @Test
    void testLoginIncorrectPassword() {
    	signupService.registerJobSeeker("Testing User", "test@gmail.com", "www123", Location.BATH.name(), "Opthalmologist", "40000", "MBBS");
        User loggedInUser = loginService.login("test@gmail.com", "12345");
        
        String output = outputStream.toString();
        assertNull(loggedInUser, "Login should fail for an incorrect password.");
        assertTrue(output.contains("Incorrect password for user: "));
    }
    
    @Test 
    void testLoginNonExistentEmail() {
        User loggedInUser = loginService.login("myuser@gmil.com", "ha123");
        
        String output = outputStream.toString();
        assertNull(loggedInUser, "Login should fail for a non-existent email.");
        assertTrue(output.contains("Login failed. Email not found."));
    }
    
    @Test 
    void testLoginNullInputs() {
        User loggedInUser = loginService.login(null, null);
        
        String output = outputStream.toString();
        assertNull(loggedInUser, "Login should fail when email and password are null.");
        assertTrue(output.contains("Email or password cannot be null."));
    }
  
}
