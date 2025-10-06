package com.jobfinder.tests.whitebox;

import org.junit.jupiter.api.*;
import com.jobfinder.model.User;
import com.jobfinder.service.FileUploadService;
import com.jobfinder.utils.JsonFileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadServiceTest {

    private FileUploadService fileUploadService;
    private User mockJobSeeker;
    private String validResumePath;
    private String validCoverLetterPath;
    private static final String MOCK_JSON_PATH = "job_seekers.json";

    @BeforeEach
    public void setup() throws IOException {
        fileUploadService = new FileUploadService();

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

        validResumePath = "uploads/mock/test_resume.txt";
        validCoverLetterPath = "uploads/mock/test_cover_letter.txt";

        new File("uploads/mock").mkdirs();
        try (FileWriter resumeWriter = new FileWriter(validResumePath)) {
            resumeWriter.write("Mock Resume Content");
        }
        try (FileWriter coverLetterWriter = new FileWriter(validCoverLetterPath)) {
            coverLetterWriter.write("Mock Cover Letter Content");
        }

        JsonFileHandler.saveUsersToFile(List.of(mockJobSeeker), MOCK_JSON_PATH);
    }

    @AfterEach
    public void cleanup() {
        new File(validResumePath).delete();
        new File(validCoverLetterPath).delete();
        new File("uploads/1/resume_test_resume.txt").delete();
        new File("uploads/1/cover_letter_test_cover_letter.txt").delete();
        new File("uploads/1").delete();
        new File(MOCK_JSON_PATH).delete();
    }

    @Test
    public void testUploadFile_ValidResume() throws IOException {
        assertTrue(fileUploadService.uploadFile(mockJobSeeker, validResumePath, "resume"),
                "Upload should succeed for a valid resume file.");

        List<User> users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should not be null.");
        assertEquals("uploads/1/resume_test_resume.txt", updatedUser.getResume(),
                "The user's resume path should be updated in the JSON.");
        assertTrue(new File("uploads/1/resume_test_resume.txt").exists(),
                "The uploaded file should exist.");
    }

    @Test
    public void testUploadFile_ValidCoverLetter() throws IOException {
        assertTrue(fileUploadService.uploadFile(mockJobSeeker, validCoverLetterPath, "cover_letter"),
                "Upload should succeed for a valid cover letter file.");

        List<User> users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should not be null.");
        assertEquals("uploads/1/cover_letter_test_cover_letter.txt", updatedUser.getCoverLetter(),
                "The user's cover letter path should be updated in the JSON.");
        assertTrue(new File("uploads/1/cover_letter_test_cover_letter.txt").exists(),
                "The uploaded file should exist.");
    }

    @Test
    public void testUploadFile_InvalidUser() {
        assertFalse(fileUploadService.uploadFile(null, validResumePath, "resume"),
                "Upload should fail for a null user.");
    }

    @Test
    public void testUploadFile_InvalidRole() {
        mockJobSeeker.setRole("employer");
        assertFalse(fileUploadService.uploadFile(mockJobSeeker, validResumePath, "resume"),
                "Upload should fail for a non-job seeker user.");
    }

    @Test
    public void testUploadFile_NonexistentFile() {
        assertFalse(fileUploadService.uploadFile(mockJobSeeker, "invalid_path.txt", "resume"),
                "Upload should fail for a nonexistent file.");
    }

    @Test
    public void testDeleteResume_ValidPath() {
        fileUploadService.uploadFile(mockJobSeeker, validResumePath, "resume");

        List<User> users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should exist before deletion.");
        assertTrue(fileUploadService.deleteResumeOfJobSeeker(updatedUser),
                "Resume deletion should succeed for a valid file.");
        assertFalse(new File("uploads/1/resume_test_resume.txt").exists(),
                "The uploaded resume file should no longer exist.");

        users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should not be null.");
        assertNull(updatedUser.getResume(), "The user's resume path should be null after deletion.");
    }

    @Test
    public void testDeleteCoverLetter_ValidPath() {
        fileUploadService.uploadFile(mockJobSeeker, validCoverLetterPath, "cover_letter");

        List<User> users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        User updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should exist before deletion.");
        assertTrue(fileUploadService.deleteCoverLetterOfJobSeeker(updatedUser),
                "Cover letter deletion should succeed for a valid file.");
        assertFalse(new File("uploads/1/cover_letter_test_cover_letter.txt").exists(),
                "The uploaded cover letter file should no longer exist.");

        users = JsonFileHandler.loadUsersFromFile(MOCK_JSON_PATH);
        updatedUser = users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(mockJobSeeker.getEmail()))
                .findFirst()
                .orElse(null);

        assertNotNull(updatedUser, "Updated user should not be null.");
        assertNull(updatedUser.getCoverLetter(), "The user's cover letter path should be null after deletion.");
    }

    @Test
    public void testDeleteResume_NoResumeSet() {
        mockJobSeeker.setResume(null);
        assertFalse(fileUploadService.deleteResumeOfJobSeeker(mockJobSeeker),
                "Resume deletion should fail when no resume is set.");
    }

    @Test
    public void testDeleteCoverLetter_NoCoverLetterSet() {
        mockJobSeeker.setCoverLetter(null);
        assertFalse(fileUploadService.deleteCoverLetterOfJobSeeker(mockJobSeeker),
                "Cover letter deletion should fail when no cover letter is set.");
    }

    @Test
    public void testDeleteResume_InvalidPath() {
        mockJobSeeker.setResume("invalid/path/nonexistent_file.txt");
        assertFalse(fileUploadService.deleteResumeOfJobSeeker(mockJobSeeker),
                "Resume deletion should fail for an invalid file path.");
    }

    @Test
    public void testDeleteCoverLetter_InvalidPath() {
        mockJobSeeker.setCoverLetter("invalid/path/nonexistent_file.txt");
        assertFalse(fileUploadService.deleteCoverLetterOfJobSeeker(mockJobSeeker),
                "Cover letter deletion should fail for an invalid file path.");
    }
    
    @Test
    public void testUploadFile_IOException() {
        String invalidPath = "invalidPath/invalidFile.txt";
        assertFalse(fileUploadService.uploadFile(mockJobSeeker, invalidPath, "resume"),
                "Upload should fail due to IOException.");
    }

    @Test
    public void testUpdateUserFile_UserNotFound() {
        User nonExistentUser = new User(999, "NonExistent", "nonexistent@test.com", "password", "job_seeker","test","");
        fileUploadService.uploadFile(nonExistentUser, validResumePath, "resume");
    }

    @Test
    public void testUpdateUserFile_FailedSave() {
        new File(MOCK_JSON_PATH).setWritable(false);
        assertFalse(fileUploadService.uploadFile(mockJobSeeker, validResumePath, "resume"),
                "Upload should fail due to inability to save user data.");
        new File(MOCK_JSON_PATH).setWritable(true);
    }

    @Test
    public void testDeleteResume_InvalidRole() {
        mockJobSeeker.setRole("employer"); 
        assertFalse(fileUploadService.deleteResumeOfJobSeeker(mockJobSeeker),
                "Resume deletion should fail for an invalid user role.");
    }

    @Test
    public void testDeleteCoverLetter_InvalidRole() {
        mockJobSeeker.setRole("employer");
        assertFalse(fileUploadService.deleteCoverLetterOfJobSeeker(mockJobSeeker),
                "Cover letter deletion should fail for an invalid user role.");
    }

    @Test
    public void testCheckAndDeleteFile_FailedDeletion() throws IOException {
        String undeletableFilePath = "uploads/mock/undeletable_file.txt";
        File undeletableFile = new File(undeletableFilePath);
        undeletableFile.createNewFile();
        undeletableFile.setWritable(false); // Make the file undeletable

        mockJobSeeker.setResume(undeletableFilePath);
        assertFalse(fileUploadService.deleteResumeOfJobSeeker(mockJobSeeker),
                "Resume deletion should fail when the file cannot be deleted.");
        undeletableFile.setWritable(true); // Restore permissions for cleanup
        undeletableFile.delete();
    }
}
