package com.jobfinder.tests.whitebox;

import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import com.jobfinder.model.User;
import com.jobfinder.service.JobApplicationService;
import com.jobfinder.utils.JsonFileHandler;
import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;

import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class JobApplicationServiceTest {

    private static final String MOCK_APPLICATION_FILE = "job_applications.json";
    private static final String MOCK_USER_FILE = "job_seekers.json";
    private static final String MOCK_JOB_FILE = "jobs.json";

    private JobApplicationService jobApplicationService;
    private User mockJobSeeker;
    private Job mockJob;
    private JobApplication mockApplication;

    @BeforeEach
    public void setup() {
        jobApplicationService = new JobApplicationService();

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
        mockJobSeeker.setResume("uploads/test/test_resume.txt");
        mockJobSeeker.setCoverLetter("uploads/test/test_cover_letter.txt");

        mockJob = new Job(
            1,
            101, 
            "TechCorp",
            "Backend Developer",
            "Develop and maintain backend systems.",
            Location.LEICESTER,
            "$120,000",
            JobStatus.OPEN,
            JobType.FULL_TIME,
            Industry.TECHNOLOGY,
            3,
            LocalDate.now(),
            Set.of("Java", "Spring", "SQL")
        );

        mockApplication = new JobApplication(
            mockJob.getJobId(),
            mockJobSeeker.getUserId(),
            mockJobSeeker.getResume(),
            mockJobSeeker.getCoverLetter(),
            3, // Experience years
            Set.of("Java", "Spring"),
            JobApplication.ApplicationStatus.SUBMITTED,
            false
        );

        JsonFileHandler.saveUsersToFile(List.of(mockJobSeeker), "job_seekers.json");
        JsonFileHandler.saveJobsToFile(List.of(mockJob), "jobs.json");
        JsonFileHandler.saveApplicationsToFile(List.of(mockApplication), "job_applications.json");

        System.out.println("Setup Complete");
        System.out.println("Mock Job: " + JsonFileHandler.loadJobsFromFile("jobs.json"));
        System.out.println("Mock User: " + JsonFileHandler.loadUsersFromFile("job_seekers.json"));
        System.out.println("Mock Applications: " + JsonFileHandler.loadApplicationsFromFile("job_applications.json"));
    }

    @Test
    public void testApplyForJob_UserIsNull() {
        boolean result = jobApplicationService.applyForJob(null, mockJob.getJobId(), 3, Set.of("Java"));
        assertFalse(result, "Application should fail when user is null.");
    }

    @Test
    public void testApplyForJob_UserNotJobSeeker() {
        mockJobSeeker.setRole("employer");
        boolean result = jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        assertFalse(result, "Application should fail when user is not a job seeker.");
    }

    @Test
    public void testApplyForJob_MissingResume() {
        mockJobSeeker.setResume(null);
        boolean result = jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        assertFalse(result, "Application should fail when resume is missing.");
    }

    @Test
    public void testApplyForJob_MissingCoverLetter() {
        mockJobSeeker.setCoverLetter(null);
        boolean result = jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        assertFalse(result, "Application should fail when cover letter is missing.");
    }

    @Test
    public void testApplyForJob_JobNotFound() {
        boolean result = jobApplicationService.applyForJob(mockJobSeeker, 999, 3, Set.of("Java"));
        assertFalse(result, "Application should fail when job is not found.");
    }

    @Test
    public void testUpdateApplicationStatus_ApplicationNotFound() {
        boolean result = jobApplicationService.updateApplicationStatus(999, JobApplication.ApplicationStatus.HIRED, 101);
        assertFalse(result, "Should fail when application is not found.");
    }

    @Test
    public void testUpdateApplicationStatus_EmployerMismatch() {
        boolean result = jobApplicationService.updateApplicationStatus(mockJobSeeker.getUserId(), JobApplication.ApplicationStatus.HIRED, 999);
        assertFalse(result, "Should fail when employer ID does not match.");
    }

    @Test
    public void testUpdateApplicationStatus_Success() {
        jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        boolean result = jobApplicationService.updateApplicationStatus(mockJobSeeker.getUserId(), JobApplication.ApplicationStatus.HIRED, 101);
        assertTrue(result, "Status update should succeed when conditions are met.");

        List<JobApplication> applications = JsonFileHandler.loadApplicationsFromFile(MOCK_APPLICATION_FILE);
        assertEquals(JobApplication.ApplicationStatus.HIRED, applications.get(0).getStatus(), "Application status should be updated.");
    }

    @Test
    public void testDisplayApplicationStatuses_NoJobs() {
        JsonFileHandler.saveJobsToFile(List.of(), MOCK_JOB_FILE);
        jobApplicationService.displayApplicationStatuses(101, null);
        String output = getCapturedOutput();
    }

    private String getCapturedOutput() {
		return null;
	}



    @Test
    public void testCheckForUpdates_WithUpdates() {
        jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        jobApplicationService.updateApplicationStatus(mockJobSeeker.getUserId(), JobApplication.ApplicationStatus.HIRED, 101);
        jobApplicationService.checkForUpdates(mockJobSeeker.getUserId());
        List<JobApplication> applications = JsonFileHandler.loadApplicationsFromFile(MOCK_APPLICATION_FILE);
        assertTrue(applications.get(0).isUpdated(), "Update flag should be set.");
    }

    @Test
    public void testCheckForUpdates_NoNewUpdates() {
        jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java"));
        jobApplicationService.checkForUpdates(mockJobSeeker.getUserId());
    }
    
    @Test
    public void testGetApplicationsByStatus_MatchingStatus() {
        JobApplication mockApplication = new JobApplication(
            mockJob.getJobId(),
            mockJobSeeker.getUserId(),
            mockJobSeeker.getResume(),
            mockJobSeeker.getCoverLetter(),
            3,
            Set.of("Java", "Spring"),
            JobApplication.ApplicationStatus.SUBMITTED,
            false
        );
        JsonFileHandler.saveApplicationsToFile(List.of(mockApplication), MOCK_APPLICATION_FILE);
        List<JobApplication> applications = jobApplicationService.getApplicationsByStatus(mockJobSeeker.getUserId(), JobApplication.ApplicationStatus.SUBMITTED);
        assertEquals(1, applications.size(), "Should return one application matching the status.");
        assertEquals(mockApplication.getJobId(), applications.get(0).getJobId(), "Returned application should match the job ID.");
        assertEquals(JobApplication.ApplicationStatus.SUBMITTED, applications.get(0).getStatus(), "Returned application should have the correct status.");
    }

    @Test
    public void testReloadUserFromFile_UserExists() {
        User reloadedUser = jobApplicationService.reloadUserFromFile(mockJobSeeker.getEmail());
        assertNotNull(reloadedUser, "User should be found.");
        assertEquals(mockJobSeeker.getEmail(), reloadedUser.getEmail(), "Reloaded user should match the email.");
        assertEquals(mockJobSeeker.getName(), reloadedUser.getName(), "Reloaded user should match the name.");
    }

    @Test
    public void testReloadUserFromFile_UserDoesNotExist() {
        User reloadedUser = jobApplicationService.reloadUserFromFile("nonexistent@test.com");
        assertNull(reloadedUser, "User should not be found for a non-existent email.");
    }
}
