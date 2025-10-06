package com.jobfinder.tests.blackbox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.Job;
import com.jobfinder.service.JobService;
import com.jobfinder.utils.Constants;
import com.jobfinder.utils.IdGenerator;
import com.jobfinder.utils.JobPortalFAQ;

public class JobServiceBlackboxTest {

	//private static final String TEST_JOB_FILE = "/job_application/src/test/resources/test_jobs.json";
	private static final String TEST_JOB_FILE = "src/test/resources/test_jobs.json";

	private JobService jobService;
    private IdGenerator idGenerator;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private JobPortalFAQ jobPortalFAQ;

    
    @BeforeEach
    void setUp() { 
        this.jobService = new JobService(TEST_JOB_FILE);
        this.idGenerator = new IdGenerator();
        System.setOut(new PrintStream(this.outputStream));
        this.jobPortalFAQ = jobPortalFAQ;
    }
        
    @AfterAll
    public static void clearTestFileOnce() {
        try (FileWriter writer = new FileWriter(TEST_JOB_FILE)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testCreateJobWithValidInputs() {
    	int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        String companyName = "IBM";
        String title = "Software Engineer";
        String description = "Develop and maintain software.";
        Location location = Location.LONDON;
        String salary = "100000";
        JobType jobType = JobType.FULL_TIME;
        Industry industry = Industry.TECHNOLOGY;
        int requiredExperienceYears = 3;
        Set<String> requiredSkills = Set.of("Java", "Spring", "SQL");

        Job createdJob = jobService.createJob(
            employerId, companyName, title, description, location, salary, jobType, industry, requiredExperienceYears, requiredSkills
        );

        assertNotNull(createdJob);
        assertEquals(employerId, createdJob.getEmployerId());
        assertEquals(companyName, createdJob.getCompanyName());
        assertEquals(title, createdJob.getTitle());
        assertTrue(createdJob.getRequiredSkills().contains("Java"));
    }


    @Test
    void testCreateJobOutputSuccess() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        jobService.createJob(employerId, "Tesla", "Mechanical Engineer",
                "Design and build cutting-edge vehicles.",
                Location.BATH, "110000", JobType.FULL_TIME,
                Industry.AUTOMOTIVE, 4, Set.of("SolidWorks", "Manufacturing"));

        String output = outputStream.toString();
        assertTrue(output.contains("Job created successfully!"), "Output should confirm successful job creation");
    }


    @Test
    void testDisplayJobsByEmployerWithJobs() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        
        jobService.createJob(employerId, "Meta", "Data Scientist",
                "Analyze big data.",
                Location.WAKEFIELD, "130000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 3, Set.of("Python", "Machine Learning"));         

        jobService.displayJobsByEmployer(employerId);

        String output = outputStream.toString();
        assertTrue(output.contains("Data Scientist"), "Output should contain job title");
        assertTrue(output.contains("Meta"), "Output should contain company name");
        assertTrue(output.contains("Analyze big data."), "Output should contain job description");
        assertTrue(output.contains("Location: WAKEFIELD"), "Output should contain job location");
    }
    
    
    @Test
    void testUpdateJobDetailsJobNotFound() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        int invalidJobId = idGenerator.generateUniqueId(Constants.JOB);

        boolean updated = jobService.updateJobDetails(employerId, invalidJobId,
                "New Title", "New Description", Location.MANCHESTER, "130000",
                JobType.CONTRACT, JobStatus.CLOSED, Industry.MEDIA_ENTERTAINMENT, 3, Set.of("Python"));

        assertFalse(updated, "Update should fail when job is not found");
        String output = outputStream.toString();
        assertTrue(output.contains("Job not found or you are not authorized to update this job."), "Output should indicate job not found");
    }

    
    
    @Test
    void testUpdateJobDetailsWithEmptyParams() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        
        Job job = jobService.createJob(employerId, "NEXT", "Data Scientist",
                "Analyze and clean large datasets.",
                Location.LEEDS, "100000", JobType.CONTRACT,
                Industry.TECHNOLOGY, 3, Set.of("Python", "Data Analysis"));

        boolean updated = jobService.updateJobDetails(employerId, job.getJobId(),
                "", null, null, "", null, null, null, 0, null);

        Job updatedJob = jobService.getJobById(job.getJobId());

        assertTrue(updated, "Job should be updated with empty parameters where applicable");
        assertEquals("Data Scientist", updatedJob.getTitle(), "Title should remain unchanged");
        assertEquals("Analyze and clean large datasets.", updatedJob.getDescription(), "Description should remain unchanged");
        assertEquals(Location.LEEDS, updatedJob.getLocation(), "Location should remain unchanged");
        assertEquals("100000", updatedJob.getSalary(), "Salary should remain unchanged");
    }
    
    
    @Test
    void testDeleteJobSuccess() {
    	
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        Job job = jobService.createJob(employerId, "Cisco", "Backend Engineer",
                "Develop scalable APIs.", Location.MILTON_KEYNES, "90000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 5, Set.of("Java", "Spring", "Hibernate"));

        boolean isDeleted = jobService.deleteJob(employerId, job.getJobId());

        assertTrue(isDeleted, "Job should be successfully deleted");
        assertFalse(jobService.getAllJobs().contains(job), "Job list should not contain the deleted job");
    }
    
    @Test
    void testDisplayAllJobsWithJobs() {

    	int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	jobService.createJob(employerId1, "TestDisplay - 1", "Test Title - 1",
                "Test Description - 1.",
                Location.LEICESTER, "10000", JobType.CONTRACT,
                Industry.HEALTHCARE, 3, Set.of("Testing1", "Testing2"));
    	jobService.createJob(employerId2, "TestDisplay - 2", "Test Title - 2",
                "Test Description - 2.",
                Location.LEICESTER, "12000", JobType.INTERNSHIP,
                Industry.CONSTRUCTION, 3, Set.of("construction", "building"));

        jobService.displayAllJobs();

        String output = outputStream.toString();
        assertTrue(output.contains("===== Displaying All Jobs ====="), "Header should be displayed");
        assertTrue(output.contains("TestDisplay - 1"), "Job details for Google should be displayed");
        assertTrue(output.contains("TestDisplay - 2"), "Job details for Amazon should be displayed");
    }
    
    
    
    @Test
    void testDisplayOpenJobsMixedStatus() {
    	int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	
    	Job job1 = jobService.createJob(employerId1, "Tata Consultancy Services", "Database Administrator",
                "Handle database operations.",
                Location.OXFORD, "35000", JobType.CONTRACT,
                Industry.REAL_ESTATE, 3, Set.of("postgreSQL", "MongoDB"));
    	
    	Job job2 = jobService.createJob(employerId2, "Infosys", "Project Lead",
                "Handle all the projects in a team.",
                Location.NOTTINGHAM, "60000", JobType.FULL_TIME,
                Industry.EDUCATION, 3, Set.of("Agile method", "Management"));
    	
    	jobService.updateJobDetails(employerId2, job2.getJobId(), null, null, null, null, null, JobStatus.CLOSED, null, 3, null);

        jobService.displayOpenJobs();

        String output = outputStream.toString();
        assertTrue(output.contains("===== Open Jobs ====="), "Header should be displayed");
        assertTrue(output.contains("Tata Consultancy Services"), "Open job details for Google should be displayed");
        assertFalse(output.contains("Infosys"), "Closed job details should not be displayed");
    }
    
    
    @Test
    void testPrintJobDetailsWithAllFields() {
    	int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	
    	Job job = jobService.createJob(employerId, "Lucid Motors", "Senior Manufacturing Engineer",
                "Handle manufacturing and other related operations.",
                Location.SALFORD, "45000", JobType.CONTRACT,
                Industry.MANUFACTURING, 3, Set.of("Quality Control Management", "Vehicle audits"));

        jobService.printJobDetails(job);
        
        String formattedDate = job.getPostedDate().toString(); 
        Set<String> expectedSkills = new HashSet<>(Arrays.asList("Quality Control Management", "Vehicle audits"));
        String output = outputStream.toString();
        String skillsOutput = output.substring(output.indexOf("Required Skills:") + "Required Skills:".length(),
                output.indexOf("Status:")).trim();
        Set<String> actualSkills = new HashSet<>(Arrays.asList(skillsOutput.split(",\\s*")));
        
        assertTrue(output.contains("Job ID: " + job.getJobId()), "Job ID should be correctly displayed.");
        assertTrue(output.contains("Company Name: Lucid Motors"), "Company Name should be correctly displayed.");
        assertTrue(output.contains("Title: Senior Manufacturing Engineer"), "Title should be correctly displayed.");
        assertTrue(output.contains("Description: Handle manufacturing and other related operations."), "Description should be correctly displayed.");
        assertTrue(output.contains("Required Experience: 3 years"), "Experience should be correctly displayed.");
        assertTrue(output.contains("Location: SALFORD"), "Location should be correctly displayed.");
        assertTrue(output.contains("Industry: MANUFACTURING"), "Industry should be correctly displayed.");
        assertTrue(output.contains("Salary: 45000"), "Salary should be correctly displayed.");
        assertTrue(expectedSkills.equals(actualSkills), "Skills should be correctly displayed.");
        assertTrue(output.contains("Status: OPEN"), "Job status should be correctly displayed.");
        assertTrue(output.contains("Type of Employment: Contract position"), "Job type should be correctly displayed.");
        assertTrue(output.contains("Posted Date: " + formattedDate), "Posted date should be correctly displayed.");
    }



    @Test
    void testGetFAQKeyQuestions() {
        String faqContent = JobPortalFAQ.getFAQ();

        assertTrue(faqContent.contains("How do I create an account?"));
        assertTrue(faqContent.contains("How do I apply for a job?"));
        assertTrue(faqContent.contains("Can I update my profile information?"));
        assertTrue(faqContent.contains("How can I reset my password?"));
        assertTrue(faqContent.contains("Is there any fee for applying to jobs?"));
        assertTrue(faqContent.contains("How do I contact customer support?"));
        assertTrue(faqContent.contains("How do I search for jobs matching my skills?"));
        assertTrue(faqContent.contains("How can I track my job applications?"));
        assertTrue(faqContent.contains("Can I upload multiple resumes?"));
        assertTrue(faqContent.contains("How do I delete my account?"));      
        assertTrue(faqContent.contains("====================================="));
    }
    
    @Test
    void testGetFAQSpecificDetails() {
    	String faqContent = JobPortalFAQ.getFAQ();

        assertTrue(faqContent.contains("Register' option from the main menu"));
        assertTrue(faqContent.contains("No, applying to jobs using this portal is completely free."));
        assertTrue(faqContent.contains("Contact support@jobportal.com for assistance."));
        assertTrue(faqContent.contains("Support is not available directly in this terminal application. For assistance, please email support@jobportal.com."));
        assertTrue(faqContent.contains("This portal supports one active resume and cover letter at a time."));
        
    }




}
