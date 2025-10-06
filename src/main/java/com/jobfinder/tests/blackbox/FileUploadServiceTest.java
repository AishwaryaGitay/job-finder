package com.jobfinder.tests.blackbox;

import org.junit.jupiter.api.*;
import com.jobfinder.model.User;
import com.jobfinder.service.FileUploadService;
import com.jobfinder.utils.JsonFileHandler;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadServiceTest {

    private FileUploadService fileUploadService;
    private User mockJobSeeker;
    private String mockResumePath;
    private String mockCoverLetterPath;

    
    private void addMockUserToJson() {
        List<User> users = List.of(mockJobSeeker);
        JsonFileHandler.saveUsersToFile(users, "job_seekers.json");
    }

    @BeforeEach
    public void setup() throws Exception {
        fileUploadService = new FileUploadService();

        // Create a mock job seeker
        mockJobSeeker = new User(
            1,
            "Alice",
            "alice@test.com",
            "password123",
            "New York",
            "Software Developer",
            "100000",
            "Bachelor's"
        ); 

        mockResumePath = "uploads/test/test_resume.txt";
        mockCoverLetterPath = "uploads/test/test_cover_letter.txt";

        new File("uploads/test").mkdirs(); // Ensure directory exists
        try (FileWriter resumeWriter = new FileWriter(mockResumePath)) {
            resumeWriter.write("Mock Resume Content");
        }
        try (FileWriter coverLetterWriter = new FileWriter(mockCoverLetterPath)) {
            coverLetterWriter.write("Mock Cover Letter Content");
        }

        addMockUserToJson();
    }


    @AfterEach
    public void cleanup() {
//        new File(mockResumePath).delete();
        new File("uploads/1/resume_test_resume.txt").delete();
        new File("uploads/1/cover_letter_test_cover_letter.txt").delete();

//        new File("job_seekers_test.json").delete();
    }
    @Test
    public void testMockFileSetup() {
        File resumeFile = new File(mockResumePath);
        File coverLetterFile = new File(mockCoverLetterPath);

        assertTrue(resumeFile.exists(), "Mock resume file should exist");
        assertTrue(coverLetterFile.exists(), "Mock cover letter file should exist");
    }

    @Test
    public void testUploadValidResume() {
        assertTrue(fileUploadService.uploadFile(mockJobSeeker, mockResumePath, "resume"));

        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        System.out.print(updatedUser);
        File uploadedFile = new File("uploads/1/resume_test_resume.txt");
        assertTrue(uploadedFile.exists(), "Uploaded file should exist at the expected location.");

        assertNotNull(updatedUser, "Updated user should not be null");
        System.out.println(updatedUser.getResume());
        assertEquals("uploads/1/resume_test_resume.txt", updatedUser.getResume());
    }

 
    @Test
    public void testUploadValidCoverLetter() {
        assertTrue(fileUploadService.uploadFile(mockJobSeeker, mockCoverLetterPath, "cover_letter"));

        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        File uploadedFile = new File("uploads/1/cover_letter_test_cover_letter.txt");
        assertTrue(uploadedFile.exists(), "Uploaded file should exist at the expected location.");

        assertNotNull(updatedUser, "Updated user should not be null");
        assertEquals("uploads/1/cover_letter_test_cover_letter.txt", updatedUser.getCoverLetter());
    }


    @Test
    public void testDeleteValidResume() {
        fileUploadService.uploadFile(mockJobSeeker, mockResumePath, "resume");

        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);        
        System.out.println("================================");
        System.out.println(updatedUser);
        assertTrue(fileUploadService.deleteResumeOfJobSeeker(updatedUser));
        users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);        
        System.out.println("================================");
        System.out.println(updatedUser);
        assertFalse(new File("uploads/1/resume_test_resume.txt").exists());
        assertNotNull(updatedUser, "Updated user should not be null");
        System.out.println(updatedUser);

        assertNull(updatedUser.getResume(), "Resume path should be null after deletion");
    }


    @Test
    public void testDeleteValidCoverLetter() {
        assertTrue(fileUploadService.uploadFile(mockJobSeeker, mockCoverLetterPath, "cover_letter"));

        List<User> users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);
        System.out.println("================================");
        System.out.println(updatedUser);

        assertNotNull(updatedUser.getCoverLetter(), "Cover letter path should not be null after upload");
        assertTrue(new File("uploads/1/cover_letter_test_cover_letter.txt").exists(), "Uploaded file should exist");

        assertTrue(fileUploadService.deleteCoverLetterOfJobSeeker(updatedUser));

        users = JsonFileHandler.loadUsersFromFile("job_seekers.json");
        updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);
        System.out.println("================================");
        System.out.println(updatedUser);
        assertNull(updatedUser.getCoverLetter(), "Cover letter path should be null after deletion");
        assertFalse(new File("uploads/1/cover_letter_test_cover_letter.txt").exists(), "Uploaded file should not exist after deletion");
    }

}
