package com.jobfinder.tests.blackbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobfinder.main.enums.Location;
import com.jobfinder.model.User;
import com.jobfinder.service.ProfileUpdateService;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;
import com.jobfinder.utils.JsonFileHandler;

public class ProfileUpdateBlackboxTest {
	
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
    void testUpdateJobSeekerProfileWithInvalidUserId() {
    	
        int userId = idGenerator.generateUniqueId(Constants.JOB_SEEKER);
        User newUser = new User(userId, "Test User", "testuser@gmail.com", "test123", Location.CHESTER.name(), "Senior Manager", "35000", "PHd");
        jobSeekers.add(newUser);
        JsonFileHandler.saveUsersToFile(jobSeekers, TEST_JOB_SEEKERS_FILE);
        
        User invalidUser = new User(userId + 2, "Alex Want", "alex.want@gmail.com", "password", Location.LONDON.name(), "Intern", "10000", "High School");
        
        profileUpdateService.updateJobseekerProfile(invalidUser, Location.CAMBRIDGE.name(), "Senior Sales Assistant", "25000", "B.A");

        List<User> jobSeekersFromFile = JsonFileHandler.loadUsersFromFile(TEST_JOB_SEEKERS_FILE);
        User updatedUser = jobSeekersFromFile.stream().filter(user -> user.getEmail().equals("testuser@gmail.com")).findFirst().orElse(null);

        assertNotNull(updatedUser); 

        assertEquals(Location.CHESTER.name(), updatedUser.getLocation());
        assertEquals("Senior Manager", updatedUser.getPreviousJobTitle());
        assertEquals("35000", updatedUser.getPreviousSalary());
        assertEquals("PHd", updatedUser.getHighestEducation());

        assertTrue(outputStream.toString().contains("User not found or not a job seeker.")); 
    }
    
    @Test
    void testUpdateJobSeekerProfileOutputSuccess() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.JOB_SEEKER);
    	User newUser = new User(userId, "Nik Shah", "nik.shah@gmail.com", "nik123", Location.COVENTRY.name(), "Teaching Assistant", "15000", "MSc in Psychology");
        jobSeekers.add(newUser);
        JsonFileHandler.saveUsersToFile(jobSeekers, TEST_JOB_SEEKERS_FILE);
    	
        profileUpdateService.updateJobseekerProfile(newUser, Location.BRADFORD.name(), "Senior Teaching Assistant", "20000","PHd in Psychology");
    	
        String output = outputStream.toString();
        assertTrue(output.contains("Profile updated successfully."), "Output should confirm successful profile update.");
    }
    
    
    
    //For Employers    
    
    @Test
    void testUpdateEmployerProfileWithInvalidUserId() {
    	
        int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        User newUser = new User(userId, "JAWK Softwares", "jawk@gmail.com", "j123", Location.BRISTOL.name(), "test description for jawk softwares.", "www.jawk.com");
        employers.add(newUser);
        JsonFileHandler.saveUsersToFile(employers, TEST_EMPLOYERS_FILE);
        
        User invalidUser = new User(userId + 2, "IBM", "ibm@gmail.com", "ibm123", Location.CANTERBURY.name(), "ibm description testing.", "www.ibm.com");
        
        profileUpdateService.updateEmployerProfile(invalidUser, "updating the description for jawk via invalid user", "www.jawkupdate.com", Location.OXFORD.name());

        List<User> employersFromFile = JsonFileHandler.loadUsersFromFile(TEST_EMPLOYERS_FILE);
        User updatedUser = employersFromFile.stream().filter(user -> user.getEmail().equals("jawk@gmail.com")).findFirst().orElse(null);

        assertNotNull(updatedUser); // Ensure the valid user is still in the file

        assertEquals("test description for jawk softwares.", updatedUser.getCompanyDescription());
        assertEquals("www.jawk.com", updatedUser.getWebsite());
        assertEquals(Location.BRISTOL.name(), updatedUser.getLocation());

        assertTrue(outputStream.toString().contains("User not found or not an employer.")); 
    }
    
    @Test
    void testUpdateEmployerProfileOutputSuccess() {
    	
    	int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
		User newUser = new User(userId, "SumaSoft Pvt Ltd", "sumasoft@gmail.com", "sum123",
				Location.MILTON_KEYNES.name(), "test description for sumasoft company.", "www.sumasoft.com");
		employers.add(newUser);
        JsonFileHandler.saveUsersToFile(employers, TEST_EMPLOYERS_FILE);
    	
		profileUpdateService.updateEmployerProfile(newUser,
				"Suma Soft is a leading IT solution provider and consulting company delivering deep domain expertise, digital expertise, and innovation to help enterprises leverage digital processes and excel in their business.",
				"www.sumasoftcareers.com", Location.LICHFIELD.name());
    	
        String output = outputStream.toString();
        assertTrue(output.contains("Profile updated successfully."), "Output should confirm successful profile update.");
    }
    
    
    
    @Test
    void testUpdateJobSeekerProfile_NoUserLoggedIn() {
    	    	
        profileUpdateService.updateJobseekerProfile(null, Location.KINGSTON_UPON_HULL.name(), "Senior Professor", "45000","PHd in Quantum Computing");
    	
        assertEquals("No user is logged in. Profile update failed.", outputStream.toString().trim());
    }
    
    
    @Test
    void testUpdateEmployerProfile_NoUserLoggedIn() {
    	    	
        profileUpdateService.updateEmployerProfile(null, "Description for my company", "www.amex.com", Location.PLYMOUTH.name());

        assertEquals("No user is logged in. Profile update failed.", outputStream.toString().trim());
    }
    



}
