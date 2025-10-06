package com.jobfinder.tests.blackbox;

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
//    private static final String MOCK_USER_FILE = "job_seekers.json";
//    private static final String MOCK_JOB_FILE = "jobs.json";

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

        // Create a mock job
        mockJob = new Job(
            1,
            101, // Employer ID
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

        // Save mock data
        JsonFileHandler.saveUsersToFile(List.of(mockJobSeeker), "job_seekers.json");
        JsonFileHandler.saveJobsToFile(List.of(mockJob), "jobs.json");
        JsonFileHandler.saveApplicationsToFile(List.of(mockApplication), "job_applications.json");

        System.out.println("Setup Complete");
        System.out.println("Mock Job: " + JsonFileHandler.loadJobsFromFile("jobs.json"));
        System.out.println("Mock User: " + JsonFileHandler.loadUsersFromFile("job_seekers.json"));
        System.out.println("Mock Applications: " + JsonFileHandler.loadApplicationsFromFile("job_applications.json"));
    }




    @AfterEach
    public void cleanup() {
//        // Clear mock data
//        JsonFileHandler.saveUsersToFile(List.of(), MOCK_USER_FILE);
//        JsonFileHandler.saveApplicationsToFile(List.of(), MOCK_APPLICATION_FILE);
//        JsonFileHandler.saveJobsToFile(List.of(), MOCK_JOB_FILE);
    }

    @Test
    public void testApplyForJob_Success() {
        boolean result = jobApplicationService.applyForJob(mockJobSeeker, mockJob.getJobId(), 3, Set.of("Java", "Spring"));
        List<JobApplication> applications = JsonFileHandler.loadApplicationsFromFile(MOCK_APPLICATION_FILE);

        assertTrue(result, "Job application should succeed when all conditions are met.");
        assertEquals(mockJobSeeker.getUserId(), applications.get(0).getUserId(), "Application should belong to the correct user.");
        assertEquals(mockJob.getJobId(), applications.get(0).getJobId(), "Application should belong to the correct job.");
    }

    @Test
    public void testUpdateApplicationStatus_Success() {
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

        boolean result = jobApplicationService.updateApplicationStatus(mockJobSeeker.getUserId(), JobApplication.ApplicationStatus.HIRED, mockJob.getEmployerId());
        System.out.println("===========================");
        System.out.println(result);
        System.out.println("===========================");
        List<JobApplication> applications = JsonFileHandler.loadApplicationsFromFile(MOCK_APPLICATION_FILE);

        assertTrue(result, "Application status update should succeed.");
        assertEquals(JobApplication.ApplicationStatus.HIRED, applications.get(0).getStatus(), "Application status should be updated.");
    }

    @Test
    public void testGetApplicationsByUser() {
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

        List<JobApplication> applications = jobApplicationService.getApplicationsByUser(mockJobSeeker.getUserId());

        assertEquals(1, applications.size(), "One application should be retrieved for the user.");
        assertEquals(mockJobSeeker.getUserId(), applications.get(0).getUserId(), "The retrieved application should belong to the user.");
    }

    @Test
    public void testDisplayApplicationStatuses() {
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
        jobApplicationService.displayApplicationStatuses(mockJob.getEmployerId(), null);

    }

    @Test
    public void testCheckForUpdates() {
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

        jobApplicationService.checkForUpdates(mockJobSeeker.getUserId());

        List<JobApplication> applications = JsonFileHandler.loadApplicationsFromFile(MOCK_APPLICATION_FILE);
        assertTrue(applications.get(0).isUpdated(), "The update flag should be set to true after checking for updates."); 
    } 
} 
