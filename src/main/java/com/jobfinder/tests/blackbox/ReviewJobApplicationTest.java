package com.jobfinder.tests.blackbox;
 
import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import com.jobfinder.service.JobApplicationService;
import com.jobfinder.service.JobService;
import com.jobfinder.service.ReviewJobApplication;
import com.jobfinder.utils.JsonFileHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
 
import static org.junit.jupiter.api.Assertions.*;
 
public class ReviewJobApplicationTest {
 
    private static final String TEST_JOB_FILE = "src/test/resources/test_jobs.json";
    private static final String TEST_APPLICATION_FILE = "src/test/resources/test_job_applications.json";
 
    private JobService jobService;
    private JobApplicationService jobApplicationService;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintStream originalOut;
 
    @BeforeEach
    public void setup() {
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
 
        jobService = new JobService(TEST_JOB_FILE);
        jobApplicationService = new JobApplicationService();
 
        ReviewJobApplication.setJobService(jobService);
        ReviewJobApplication.setJobApplicationService(jobApplicationService);
 
       
        Job job = new Job(
            1,
            101, 
            "TechCorp",
            "Backend Developer",
            "Develop and maintain backend systems.",
             Location.BATH,
            "$120,000",
            JobStatus.OPEN,
            JobType.FULL_TIME,
            Industry.TECHNOLOGY,
            3,
            LocalDate.now(),
            Set.of("Java", "Spring", "SQL")
        );
 
        JsonFileHandler.saveJobsToFile(List.of(job), TEST_JOB_FILE);
 
        JobApplication application1 = new JobApplication(
            1,
            201, 
            "resume1.txt",
            "coverLetter1.txt",
            3,
            Set.of("Java", "Spring"),
            JobApplication.ApplicationStatus.SUBMITTED,
            false
        );
 
        JobApplication application2 = new JobApplication(
            1,
            202, 
            "resume2.txt",
            "coverLetter2.txt",
            2,
            Set.of("Java"),
            JobApplication.ApplicationStatus.SUBMITTED,
            false
        );
 
        JsonFileHandler.saveApplicationsToFile(List.of(application1, application2), TEST_APPLICATION_FILE);
    }
 
    @AfterEach
    public void cleanup() {
        System.setOut(originalOut);
        outputStream.reset();
 
        JsonFileHandler.saveJobsToFile(List.of(), TEST_JOB_FILE);
        JsonFileHandler.saveApplicationsToFile(List.of(), TEST_APPLICATION_FILE);
    }
  
    @Test
    public void testReviewApplications_NoApplicationsForJob() {
        JsonFileHandler.saveApplicationsToFile(List.of(), TEST_APPLICATION_FILE);
 
        ReviewJobApplication.reviewApplications(1, 101);
        String output = outputStream.toString().trim();
 
        assertTrue(output.contains("Job not found with ID: 1"), "Should display no applications message.");
    }
 
    @Test
    public void testReviewApplications_InvalidJobId() {
        ReviewJobApplication.reviewApplications(999, 101);
        String output = outputStream.toString().trim();
 
        assertTrue(output.contains("Job not found with ID: 999"), "Should display job not found message.");
    }
 
    @Test
    public void testReviewFilteredApplications_NoMatchingCriteria() {
        Job updatedJob = new Job(
            1,
            101, // Employer ID
            "TechCorp",
            "Backend Developer",
            "Develop and maintain backend systems.",
            Location.BRIGHTON_AND_HOVE,
            "$120,000",
            JobStatus.OPEN,
            JobType.FULL_TIME,
            Industry.TECHNOLOGY,
            5, 
            LocalDate.now(),
            Set.of("Kotlin") 
        );
 
        JsonFileHandler.saveJobsToFile(List.of(updatedJob), TEST_JOB_FILE);
 
        ReviewJobApplication.reviewFilteredApplications(1, 101);
        String output = outputStream.toString().trim();
 
        assertTrue(output.contains("Job not found with ID: 1"), "Should display no applications message.");
    }
 
    @Test
    public void testReviewFilteredApplications_InvalidJobId() {
        ReviewJobApplication.reviewFilteredApplications(999, 101);
        String output = outputStream.toString().trim();
 
        assertTrue(output.contains("Job not found with ID: 999"), "Should display job not found message.");
    }
 
}