package com.jobfinder.tests.blackbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobfinder.main.Application;
import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.Interview;
import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
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
import com.jobfinder.utils.JsonFileHandler;

public class MainMenuTest {

	private static final String TEST_EMPLOYERS_FILE = "src/test/resources/test_employers.json";
	private static final String TEST_JOB_SEEKERS_FILE = "src/test/resources/test_job_seekers.json";
	private static final String TEST_JOB_FILE = "src/test/resources/test_jobs.json";
	private static final String TEST_INTERVIEW_FILE = "src/test/resources/test_interviews.json";
	private static final String TEST_APPLICATION_FILE = "src/test/resources/test_jobapplication.json";

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
        this.idGenerator = new IdGenerator();
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

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    private String getOutput() {
        return outputStream.toString().trim();
    }

    @Test
    void testValidRegistrationInput() {
        simulateInput("1\nJoe Guest\njoe@gmail.com\njoe123\njob_seeker\n1\nSenior Consultant\n35000\nMsc in Cybersecurity\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );
        
        String output = getOutput();

        assertTrue(output.contains("Enter your name (or company name for employers):"), "Should prompt for name.");
        assertTrue(output.contains("Enter your email:"), "Should prompt for email.");
        assertTrue(output.contains("Enter your password:"), "Should prompt for password.");
        assertTrue(output.contains("Enter your role (job_seeker or employer):"), "Should prompt for role.");
        assertFalse(output.contains("Invalid role specified."), "Role should be valid.");
        assertFalse(output.contains("Registration failed"), "Registration should not fail.");

        assertTrue(output.contains("You have been successfully registered!"), "Registration should be successful.");

    }

    @Test
    void testInvalidMenuOption() {
        simulateInput("5\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Invalid option. Please try again."), "Invalid option message should be displayed.");
        assertTrue(output.contains("Thank you for using the Job Finder Application"), "Exit message should be displayed.");
    }

    @Test
    void testExitOption() {
        simulateInput("4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Thank you for using the Job Finder Application. Goodbye!"), "Exit message should be displayed.");
    }
    
    
    @Test
    void testRegisterUserForJobSeeker() {
    	
        String input = "Nikhil\nnikhil@example.com\n86901996\njob_seeker\n1\nDesign Engineer\n50000\nMaster's Degree in Automotive\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        Scanner scanner = new Scanner(System.in);
        Application.registerUser(scanner, signupService);

        // Validate the output
        String output = out.toString();
        assertTrue(output.contains("You have been successfully registered!"));
    }

    @Test
    void testRegisterUserForEmployer() {
    	
        String input = "Bajaj\nbajaj@example.com\nbj1122\nemployer\n2\nInnovative solutions\nwww.bajaj.com\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        Scanner scanner = new Scanner(System.in);
        Application.registerUser(scanner, signupService);

        // Validate the output
        String output = out.toString();
        assertTrue(output.contains("You have been successfully registered!"));
        assertTrue(output.contains("Bajaj"));
        assertTrue(output.contains("employer"));
    }
    
    
    @Test
    void testRegisterUserWithInvalidRole() {
    	
        String input = "David Liam\ndavid@example.com\ndav117\nno_role\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Call the method
        Scanner scanner = new Scanner(System.in);
        Application.registerUser(scanner, signupService);

        // Validate the output
        String output = out.toString();
        assertTrue(output.contains("Invalid role specified."));
    }
    
    @Test
    void testSuccessfulLoginAsJobSeeker() {
        // Preload a job seeker
        User jobSeeker = new User(1, "Alice", "alice@test.com", "password123", Location.LONDON.name(), "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(jobSeeker), TEST_JOB_SEEKERS_FILE);

        simulateInput("2\nalice@test.com\npassword123\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("===== Job Seeker Menu ====="), "Job seeker menu should load after successful login.");
    }
    @Test
    void testCreateJobAsEmployer() {
        // Preload an employer
        User employer = new User(1, "TechCorp", "techcorp@test.com", "password123", Location.LONDON.name(), "Tech Company", "www.techcorp.com");
        JsonFileHandler.saveUsersToFile(List.of(employer), TEST_EMPLOYERS_FILE);

        simulateInput("3\ntechcorp@test.com\npassword123\n1\nBackend Developer\nDevelop backend systems\n1\n120000\n3\nJava,Spring\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Job created successfully!"), "Employer should be able to create a job.");
    }

    @Test
    void testViewAllJobsAsJobSeeker() {
        // Preload jobs
        Job job = new Job(1, 101, "TechCorp", "Backend Developer", "Develop backend systems.", Location.LONDON, "£120,000", JobStatus.OPEN, JobType.FULL_TIME, Industry.TECHNOLOGY, 3, LocalDate.now(), Set.of("Java", "Spring"));
        JsonFileHandler.saveJobsToFile(List.of(job), TEST_JOB_FILE);

        // Preload a job seeker
        User jobSeeker = new User(1, "Alice", "alice@test.com", "password123", Location.LONDON.name(), "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(jobSeeker), TEST_JOB_SEEKERS_FILE);

        simulateInput("2\nalice@test.com\npassword123\n1\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        System.out.println("=========================================");
        System.out.println(output);
        System.out.println("=========================================");
        assertTrue(output.contains("Backend Developer"), "Job seeker should see available jobs.");
    }

    @Test
    void testApplyForJobWithoutResumeAndCoverLetter() {
        // Preload a job seeker without resume and cover letter
        User jobSeeker = new User(1, "Alice", "alice@test.com", "password123", Location.LONDON.name(), "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveUsersToFile(List.of(jobSeeker), TEST_JOB_SEEKERS_FILE);

        simulateInput("2\nalice@test.com\npassword123\n6\n101\n3\nJava,Spring\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Please upload your resume and cover letter first."), "Application should fail without resume and cover letter.");
    }



    @Test
    void testViewApplicationsForJob() {
        // Preload a job and an application
        Job job = new Job(1, 101, "TechCorp", "Backend Developer", "Develop backend systems.", Location.LONDON, "£120,000", JobStatus.OPEN, JobType.FULL_TIME, Industry.TECHNOLOGY, 3, LocalDate.now(), Set.of("Java", "Spring"));
        JsonFileHandler.saveJobsToFile(List.of(job), TEST_JOB_FILE);

        JobApplication application = new JobApplication(1, 1, "resume_path", "cover_letter_path", 3, Set.of("Java", "Spring"), JobApplication.ApplicationStatus.SUBMITTED, false);
        JsonFileHandler.saveApplicationsToFile(List.of(application), TEST_JOB_FILE);

        // Preload an employer
        User employer = new User(1, "TechCorp", "techcorp@test.com", "password123", Location.LONDON.name(), "Tech Company", "www.techcorp.com");
        JsonFileHandler.saveUsersToFile(List.of(employer), TEST_EMPLOYERS_FILE);

        simulateInput("3\ntechcorp@test.com\npassword123\n6\n1\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Application ID: 1"), "Employer should see applications for their job.");
    }
    @Test
    void testReviewApplicationsForInvalidJobId() {
        // Preload an employer
        User employer = new User(1, "TechCorp", "techcorp@test.com", "password123", Location.LONDON.name(), "Tech Company", "www.techcorp.com");
        JsonFileHandler.saveUsersToFile(List.of(employer), TEST_EMPLOYERS_FILE);

        simulateInput("3\ntechcorp@test.com\npassword123\n7\n999\n4\n");

        Application.displayGeneralMainMenu(
            new Scanner(System.in),
            signupService,
            loginService,
            jobService,
            profileUpdateService,
            fileUploadService,
            jobApplicationService,
            interviewService,
            passwordResetService
        );

        String output = getOutput();
        assertTrue(output.contains("Invalid Job ID or you do not have permission to review applications for this job."), "Review should fail for invalid Job ID.");
    }


    @Test
    void testUpdateEmployerProfile_AllFieldsUpdated() {
        String input = "Updated Company Description\nwww.updatedcompany.com\n2\n";
        simulateInput(input);

        User employer = new User(101, "TechCorp", "techcorp@test.com", "password123", Location.LONDON.name(), "Tech Company", "www.techcorp.com");
        Application.updateEmployerProfile(new Scanner(System.in), employer, profileUpdateService);

        assertEquals("Tech Company", employer.getCompanyDescription());
        assertEquals("www.techcorp.com", employer.getWebsite());
        assertEquals(Location.LONDON.name(), employer.getLocation());
    }
    @Test
    void testUpdateJobSeekerProfile_AllFieldsUpdated() {
        String input = "Updated Job Title\n75000\nUpdated Education\n2\n";
        simulateInput(input);

        User jobSeeker = new User(1, "Alice", "alice@test.com", "password123", Location.LONDON.name(), "Software Developer", "100000", "Bachelor's");
        Application.updateJobSeekerProfile(new Scanner(System.in), jobSeeker, profileUpdateService);

        assertEquals("Software Developer", jobSeeker.getPreviousJobTitle());
        assertEquals("100000", jobSeeker.getPreviousSalary());
        assertEquals("Bachelor's", jobSeeker.getHighestEducation());
        assertEquals(Location.LONDON.name(), jobSeeker.getLocation());
    }
    @Test
    public void testDisplayScheduledInterviews_NoInterviews() {
        // Arrange: Empty list of scheduled interviews
        List<Interview> scheduledInterviews = new ArrayList<>();

        // Act
        Application.displayScheduledInterviews(scheduledInterviews);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("No scheduled interviews found."), "Should display no interviews message for empty list.");
    }

    @Test
    public void testDisplayScheduledInterviews_WithInterviews() {
        // Arrange: List with scheduled interviews
        List<Interview> scheduledInterviews = List.of(
                new Interview(1, 101, 201, "2025-02-20", "10:00", "https://meeting.com", "Initial screening", "Scheduled"),
                new Interview(2, 102, 202, "2025-02-21", "14:00", "https://anothermeeting.com", "Technical round", "Scheduled")
        );

        // Act
        Application.displayScheduledInterviews(scheduledInterviews);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("===== Scheduled Interviews ====="), "Should display scheduled interviews header.");
        assertTrue(output.contains("Interview ID: 1"), "Should display details for Interview ID 1.");
        assertTrue(output.contains("Interview ID: 2"), "Should display details for Interview ID 2.");
        assertTrue(output.contains("Job ID: 101"), "Should display Job ID 101.");
        assertTrue(output.contains("Meeting Link: https://meeting.com"), "Should display correct meeting link.");
        assertTrue(output.contains("Details: Initial screening"), "Should display correct details.");
    }

    @Test
    public void testDisplayCompletedInterviews_NoInterviews() {
        // Arrange: Empty list of completed interviews
        List<Interview> completedInterviews = new ArrayList<>();

        // Act
        Application.displayCompletedInterviews(completedInterviews);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("No completed interviews found."), "Should display no interviews message for empty list.");
    }

    @Test
    public void testDisplayCompletedInterviews_WithInterviews() {
        // Arrange: List with completed interviews
        List<Interview> completedInterviews = List.of(
                new Interview(3, 103, 203, "2025-02-18", "09:00", "https://completedmeeting.com", "HR round", "Completed"),
                new Interview(4, 104, 204, "2025-02-19", "11:00", "https://othermeeting.com", "Final round", "Completed")
        );

        // Act
        Application.displayCompletedInterviews(completedInterviews);

        // Assert
        String output = getOutput();
        assertTrue(output.contains("===== Completed Interviews ====="), "Should display completed interviews header.");
        assertTrue(output.contains("Interview ID: 3"), "Should display details for Interview ID 3.");
        assertTrue(output.contains("Interview ID: 4"), "Should display details for Interview ID 4.");
        assertTrue(output.contains("Job ID: 103"), "Should display Job ID 103.");
        assertTrue(output.contains("Meeting Link: https://completedmeeting.com"), "Should display correct meeting link.");
        assertTrue(output.contains("Details: HR round"), "Should display correct details.");
    }
    
    @Test
    void testTrackJobApplicationStatus_CheckForUpdates() {
        // Prepare test data
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");
        JobApplication testApplication = new JobApplication(101, loggedInUser.getUserId(), "resume.txt", "coverLetter.txt", 3, Set.of("Java"), JobApplication.ApplicationStatus.SUBMITTED, false);
        JsonFileHandler.saveApplicationsToFile(List.of(testApplication), TEST_APPLICATION_FILE);

        // Simulate input for "Check for updates"
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Call the method
        Application.trackJobApplicatonStatus(new Scanner(System.in), loggedInUser, jobService, jobApplicationService);

        // Verify output
        String output = getOutput();
        System.out.println("=========================================");
        System.out.println(output);
        System.out.println("=========================================");
        assertTrue(output.contains("Checking for updates on your applications..."), "Should notify user that updates are being checked.");
    }

    @Test
    void testTrackJobApplicationStatus_FilterByStatus_NoApplications() {
        // Prepare test data
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");
        JsonFileHandler.saveApplicationsToFile(List.of(), TEST_APPLICATION_FILE); // Save no applications

        // Simulate input for "Filter by status" and select "Submitted"
        String input = "2\n1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Call the method
        Application.trackJobApplicatonStatus(new Scanner(System.in), loggedInUser, jobService, jobApplicationService);

        // Verify output
        String output = getOutput();
        System.out.println("=========================================");
        System.out.println(output);
        System.out.println("=========================================");
        assertTrue(output.contains("No applications found with the status: SUBMITTED"), "Should notify user when no applications are found.");
    }

    @Test
    void testTrackJobApplicationStatus_FilterByStatus_WithApplications() {
        // Prepare test data
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");
        Job mockJob = new Job(101, 201, "TechCorp", "Backend Developer", "Develop backend systems", Location.LEICESTER, "120,000", JobStatus.OPEN, JobType.FULL_TIME, Industry.TECHNOLOGY, 3, LocalDate.now(), Set.of("Java", "Spring"));
        JobApplication testApplication = new JobApplication(mockJob.getJobId(), loggedInUser.getUserId(), "resume.txt", "coverLetter.txt", 3, Set.of("Java"), JobApplication.ApplicationStatus.HIRED, false);

        JsonFileHandler.saveJobsToFile(List.of(mockJob), TEST_JOB_FILE);
        JsonFileHandler.saveApplicationsToFile(List.of(testApplication), TEST_APPLICATION_FILE);

        // Simulate input for "Filter by status" and select "Hired"
        String input = "2\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Call the method
        Application.trackJobApplicatonStatus(new Scanner(System.in), loggedInUser, jobService, jobApplicationService);

        // Verify output
        String output = getOutput();
        System.out.println(output);
        assertTrue(output.contains("Applications with status HIRED:"), "Should list applications with the 'Hired' status.");
        assertTrue(output.contains("Job ID: 101, Title: Backend Developer, Status: HIRED"), "Should display correct job details.");
    }

    @Test
    void testTrackJobApplicationStatus_InvalidStatusChoice() {
        // Simulate input for "Filter by status" with invalid status choice
        String input = "2\n6\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Prepare test data
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");

        // Call the method
        Application.trackJobApplicatonStatus(new Scanner(System.in), loggedInUser, jobService, jobApplicationService);

        // Verify output
        String output = getOutput();
        assertTrue(output.contains("Invalid choice. Please select a number between 1 and 5."), "Should notify user of invalid status choice.");
    }
    @Test
    void testGetStatusFromChoice_ValidChoices() {
        // Test valid choices
        assertEquals(JobApplication.ApplicationStatus.SUBMITTED, Application.getStatusFromChoice(1), "Should return SUBMITTED for choice 1");
        assertEquals(JobApplication.ApplicationStatus.UNDER_REVIEW, Application.getStatusFromChoice(2), "Should return UNDER_REVIEW for choice 2");
        assertEquals(JobApplication.ApplicationStatus.INTERVIEWED, Application.getStatusFromChoice(3), "Should return INTERVIEWED for choice 3");
        assertEquals(JobApplication.ApplicationStatus.HIRED, Application.getStatusFromChoice(4), "Should return HIRED for choice 4");
        assertEquals(JobApplication.ApplicationStatus.REJECTED, Application.getStatusFromChoice(5), "Should return REJECTED for choice 5");
    }

    @Test
    void testGetStatusFromChoice_InvalidChoice() {
        // Test invalid choices
        assertEquals(JobApplication.ApplicationStatus.SUBMITTED, Application.getStatusFromChoice(0), "Should return SUBMITTED for invalid choice 0");
        assertEquals(JobApplication.ApplicationStatus.SUBMITTED, Application.getStatusFromChoice(6), "Should return SUBMITTED for invalid choice 6");
        assertEquals(JobApplication.ApplicationStatus.SUBMITTED, Application.getStatusFromChoice(-1), "Should return SUBMITTED for negative choice -1");
    }




    @Test
    void testDeleteResume_Success() {
        // Prepare a user with a resume
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");
        loggedInUser.setResume("resume.txt");
        JsonFileHandler.saveUsersToFile(List.of(loggedInUser), TEST_JOB_SEEKERS_FILE);

        // Simulate deletion
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        Application.deleteResume(new Scanner(System.in), loggedInUser, fileUploadService);

        // Verify output and data
        String output = getOutput();
        assertTrue(output.contains("Deleting resume..."), "Should indicate deletion process");
        assertTrue(output.contains("Resume deleted successfully."), "Should indicate successful deletion");

        // Verify resume is removed
        User updatedUser = JsonFileHandler.loadUsersFromFile(TEST_JOB_SEEKERS_FILE).get(0);
        assertNull(updatedUser.getResume(), "Resume should be null after deletion");
    }

    @Test
    void testDeleteResume_Failure() {
        // Prepare a user without a resume
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");
        loggedInUser.setResume(null);
        JsonFileHandler.saveUsersToFile(List.of(loggedInUser), TEST_JOB_SEEKERS_FILE);

        // Simulate deletion
        System.setIn(new ByteArrayInputStream("\n".getBytes()));
        Application.deleteResume(new Scanner(System.in), loggedInUser, fileUploadService);

        // Verify output
        String output = getOutput();
        assertTrue(output.contains("Deleting resume..."), "Should indicate deletion process");
        assertTrue(output.contains("Failed to delete resume."), "Should indicate deletion failure");
    }

    @Test
    void testTrackJobApplicationStatus_InvalidActionChoice() {
        // Simulate input for an invalid action choice
        String input = "3\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Prepare test data
        User loggedInUser = new User(1, "Alice", "alice@test.com", "password", "New York", "Software Developer", "100000", "Bachelor's");

        // Call the method
        Application.trackJobApplicatonStatus(new Scanner(System.in), loggedInUser, jobService, jobApplicationService);

        // Verify output
        String output = getOutput();
        assertTrue(output.contains("Invalid action selected. Please choose either 1 or 2."), "Should notify user of invalid action choice.");
    }

//    @Test
//    void testUpdateApplicationStatus_Valid() {
//        // Mock data
//        JobApplication application = new JobApplication(101, 201, "resume.pdf", "coverLetter.pdf", 3, Set.of("Java", "Spring"), JobApplication.ApplicationStatus.SUBMITTED, false);
//        JsonFileHandler.saveApplicationsToFile(List.of(application), TEST_APPLICATION_FILE);
//
//        boolean result = jobApplicationService.updateApplicationStatus(201, JobApplication.ApplicationStatus.UNDER_REVIEW, 101);
//
//        // Assertions
//        assertTrue(result, "The application status should be updated successfully.");
//        JobApplication updatedApplication = JsonFileHandler.loadApplicationsFromFile(TEST_APPLICATION_FILE).get(0);
//        assertEquals(JobApplication.ApplicationStatus.UNDER_REVIEW, updatedApplication.getStatus(), "The status should be updated to UNDER_REVIEW.");
//    }

//    @Test
//    void testDisplayCompletedInterviews_Valid() {
//        // Mock data
//        Interview interview1 = new Interview(1, 101, 201, "2025-01-01", "10:00 AM", "https://meeting-link.com/1", "First round", "Completed");
//        Interview interview2 = new Interview(2, 102, 202, "2025-01-02", "2:00 PM", "https://meeting-link.com/2", "Technical round", "Completed");
//        JsonFileHandler.saveInterviewsToFile(List.of(interview1, interview2), TEST_INTERVIEW_FILE);
//
//        // Call the method
//        List<Interview> completedInterviews = interviewService.getCompletedInterviewsForJobSeeker(201);
//
//        // Assertions
//        assertEquals(1, completedInterviews.size(), "There should be one completed interview for Job Seeker ID 201.");
//        assertEquals(interview1, completedInterviews.get(0), "The interview details should match.");
//    }

    @Test
    public void testUpdateEmployerProfile_KeepCurrentValues() {
        String input = "\n\n\n"; // Keep current values
        Scanner scanner = new Scanner(input);
        int userId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        User loggedInUser = new User(userId, "Amex", "amex@gmail.com", "amex111", Location.PLYMOUTH.name(), "A big company", "www.amex.com");       

        Application.updateEmployerProfile(scanner, loggedInUser, profileUpdateService);

        assertEquals("A big company", loggedInUser.getCompanyDescription());
        assertEquals("www.amex.com", loggedInUser.getWebsite());
        assertEquals(Location.PLYMOUTH.name(), loggedInUser.getLocation());
    }
    
    

}
