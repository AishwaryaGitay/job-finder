package com.jobfinder.tests.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobfinder.main.enums.Location;
import com.jobfinder.model.User;
import com.jobfinder.service.ProfileUpdateService;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;
import com.jobfinder.utils.JsonFileHandler;

public class ProfileUpdateWhiteboxTest {
	
	private static final String TEST_EMPLOYERS_FILE = "src/test/resources/test_employers.json";
	private static final String TEST_JOB_SEEKERS_FILE = "src/test/resources/test_job_seekers.json";

	private ProfileUpdateService profileUpdateService;
    private IdGenerator idGenerator;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private List<User> jobSeekers;
    private List<User> employers;

    @BeforeEach
    void setUp() {
        this.profileUpdateService = new ProfileUpdateService(TEST_JOB_SEEKERS_FILE,TEST_EMPLOYERS_FILE);
        this.idGenerator = new IdGenerator();
        System.setOut(new PrintStream(this.outputStream));
        this.jobSeekers = new ArrayList<User>();
        this.employers = new ArrayList<User>();
    }
    
    @AfterEach
    public void clearEmployerTestFileOnce() {
        try (FileWriter writer = new FileWriter(TEST_EMPLOYERS_FILE)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void clearJobSeekerTestFileOnce() {
        try (FileWriter writer = new FileWriter(TEST_JOB_SEEKERS_FILE)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    void testUpdateJobSeekerProfileSuccess() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.JOB_SEEKER);
        User newUser = new User(userId, "Anna Jones", "anna@gmail.com", "12345", Location.BATH.name(), "Sales Assistant", "20000", "BSc"); 
        jobSeekers.add(newUser);
        JsonFileHandler.saveUsersToFile(jobSeekers, TEST_JOB_SEEKERS_FILE);
        profileUpdateService.updateJobseekerProfile(newUser, Location.CAMBRIDGE.name(),  "Senior Sales Assistant", "25000", "B.A");
        
        List<User> jobSeekersFromFile = JsonFileHandler.loadUsersFromFile(TEST_JOB_SEEKERS_FILE);
        User updatedUser = jobSeekersFromFile.stream().filter(user -> user.getEmail().equals("anna@gmail.com")).findFirst().orElse(null);

        assertNotNull(updatedUser);
        assertEquals(Location.CAMBRIDGE.name(), updatedUser.getLocation());
        assertEquals("Senior Sales Assistant", updatedUser.getPreviousJobTitle());
        assertEquals("25000", updatedUser.getPreviousSalary());
        assertEquals("B.A", updatedUser.getHighestEducation());
    }
    
    
    @Test
    void testUpdateJobSeekerProfileWithEmptyFields() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.JOB_SEEKER);
    	User newUser = new User(userId, "Henry Roshan", "henry@gmail.com", "pass123", Location.LANCASTER.name(), "Consultant", "25000", "B.Tech in Construction");
    	jobSeekers.add(newUser);
        JsonFileHandler.saveUsersToFile(jobSeekers, TEST_JOB_SEEKERS_FILE);
    	
        profileUpdateService.updateJobseekerProfile(newUser, "", "", "","");
        
        List<User> jobSeekersFromFile = JsonFileHandler.loadUsersFromFile(TEST_JOB_SEEKERS_FILE);
        User updatedUser = jobSeekersFromFile.stream().filter(user -> user.getEmail().equals("henry@gmail.com")).findFirst().orElse(null);
        
        assertNotNull(updatedUser);
        assertEquals(Location.LANCASTER.name(), updatedUser.getLocation(), "Location should be the same.");
        assertEquals("Consultant", updatedUser.getPreviousJobTitle(), "Job Title should be the same.");
        assertEquals("25000", updatedUser.getPreviousSalary(), "Salary should be the same.");
        assertEquals("B.Tech in Construction", updatedUser.getHighestEducation(), "Highest Education should be the same.");
    }
    
    
    @Test
    void testViewUserSummary_JobSeekerProfile() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.JOB_SEEKER);
    	User newUser = new User(userId, "Gaurav Sharma", "gaurav@gmail.com", "gv123", Location.GLOUCESTER.name(), "Team Manager", "33000", "LLB");
    	jobSeekers.add(newUser);
        JsonFileHandler.saveUsersToFile(jobSeekers, TEST_JOB_SEEKERS_FILE);
        
        profileUpdateService.viewUserSummary(newUser);

        String output = outputStream.toString();
        assertTrue(output.contains("Name: Gaurav Sharma"));
        assertTrue(output.contains("Email: gaurav@gmail.com"));
        assertTrue(output.contains("Role: job_seeker"));
        assertTrue(output.contains("Resume: Not uploaded"));
        assertTrue(output.contains("Cover Letter: Not uploaded"));
        assertTrue(output.contains("Previous Job Title: Team Manager"));
        assertTrue(output.contains("Previous Salary: 33000"));
        assertTrue(output.contains("Highest Education: LLB"));
        assertTrue(output.contains("Location: Location"));
        
    }

    
    
    @Test
    void testUpdateEmployerProfileSuccess() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
		User newUser = new User(userId, "Triumph Motorcycles", "triumph@gmail.com", "t123", Location.LEEDS.name(),
				"For more than a 100 years Triumph's passion and innovation has shaped the evolution of the motorcycle. With an unparalleled history of legends, from category defining motorcycles to racing icons, Triumph is one of the world's most well known and loved motorcycle brands.",
				"www.triumph.co.uk");
		employers.add(newUser);
        JsonFileHandler.saveUsersToFile(employers, TEST_EMPLOYERS_FILE);
        
        profileUpdateService.updateEmployerProfile(newUser, "For more than a 50 years Triumph's passion and innovation has shaped the evolution of the motorcycle",  "www.triumph.com", Location.EXETER.name());
        
        List<User> employersFromFile = JsonFileHandler.loadUsersFromFile(TEST_EMPLOYERS_FILE);
        User updatedUser = employersFromFile.stream().filter(user -> user.getEmail().equals("triumph@gmail.com")).findFirst().orElse(null);

        assertNotNull(updatedUser);
        assertEquals("For more than a 50 years Triumph's passion and innovation has shaped the evolution of the motorcycle", updatedUser.getCompanyDescription());
        assertEquals("www.triumph.com", updatedUser.getWebsite());
        assertEquals(Location.EXETER.name(), updatedUser.getLocation());
    }
    
    
    @Test
    void testUpdateEmployerProfileWithEmptyFields() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	User newUser = new User(userId, "Calisoft", "calisoft@gmail.com", "cal123",
				Location.STOKE_ON_TRENT.name(), "test description for calisoft company.", "www.calisoft.com");    	
    	employers.add(newUser);
        JsonFileHandler.saveUsersToFile(employers, TEST_EMPLOYERS_FILE);
    	
        profileUpdateService.updateEmployerProfile(newUser, "", "", "");
        
        List<User> employersFromFile = JsonFileHandler.loadUsersFromFile(TEST_EMPLOYERS_FILE);
        User updatedUser = employersFromFile.stream().filter(user -> user.getEmail().equals("calisoft@gmail.com")).findFirst().orElse(null);
        
        assertNotNull(updatedUser); 
        assertEquals("test description for calisoft company.", updatedUser.getCompanyDescription());
        assertEquals("www.calisoft.com", updatedUser.getWebsite());
        assertEquals(Location.STOKE_ON_TRENT.name(), updatedUser.getLocation());
    }
    
    
    @Test
    void testViewUserSummary_EmployerProfile() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	User newUser = new User(userId, "YOUTUBE", "youtube@gmail.com", "you123",
				Location.BRIGHTON_AND_HOVE.name(), "test description youtube..", "www.youtube.com");    	
    	employers.add(newUser);
        JsonFileHandler.saveUsersToFile(employers, TEST_EMPLOYERS_FILE);
        
        profileUpdateService.viewUserSummary(newUser);

        String output = outputStream.toString();
        assertTrue(output.contains("Name: YOUTUBE"));
        assertTrue(output.contains("Email: youtube@gmail.com"));
        assertTrue(output.contains("Role: employer"));
        assertTrue(output.contains("Company Description: test description youtube.."));
        assertTrue(output.contains("Website: www.youtube.com"));
        assertTrue(output.contains("Location: BRIGHTON_AND_HOVE"));
    }
    
    
}
