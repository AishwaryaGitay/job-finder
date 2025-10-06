package com.jobfinder.tests.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
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
import com.jobfinder.utils.JsonFileHandler;

public class JobServiceWhiteboxTest {

		private static final String TEST_JOB_FILE = "src/test/resources/test_jobs.json";
    private JobService jobService;
    private IdGenerator idGenerator;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private JobPortalFAQ jobPortalFAQ;
    
    @BeforeEach
    void setUp() {
        jobService = new JobService(TEST_JOB_FILE);
        idGenerator = new IdGenerator();
        System.setOut(new PrintStream(this.outputStream));
        jobPortalFAQ = new JobPortalFAQ();
    }
    
    @AfterEach
    public void clearTestFileOnce() {
        try (FileWriter writer = new FileWriter(TEST_JOB_FILE)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testCreateJobSuccess() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        Job job = jobService.createJob(employerId, "Google", "Software Engineer",
                "Develop scalable software solutions.",
                Location.LONDON, "120000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 5, Set.of("Java", "Spring", "Kubernetes"));

        assertNotNull(job, "Job should be successfully created");
        assertEquals("Google", job.getCompanyName(), "Company name should match");
        assertEquals("Software Engineer", job.getTitle(), "Title should match");
        assertEquals("Develop scalable software solutions.", job.getDescription(), "Description should match");
        assertEquals(Location.LONDON, job.getLocation(), "Location should match");
        assertEquals("120000", job.getSalary(), "Salary should match");
        assertEquals(JobStatus.OPEN, job.getJobStatus(), "Job Status should match");
        assertEquals(JobType.FULL_TIME, job.getJobType(), "Job Type should match");
        assertEquals(Industry.TECHNOLOGY, job.getIndustry(), "Industry should match");
        assertEquals(5, job.getRequiredExperienceYears(), "Required experience should match");
        assertTrue(job.getRequiredSkills().contains("Java"), "Required skills should include 'Java'");
        assertTrue(job.getRequiredSkills().contains("Spring"), "Required skills should include 'Spring'");
    }
    
    
    @Test
    void testCreateJobWithNullSkills() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        Job job = jobService.createJob(employerId, "Microsoft", "Product Manager",
                "Manage cross-functional teams to deliver products.",
                Location.BATH, "150000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 7, null);

        assertNotNull(job, "Job should be successfully created");
        assertEquals("Microsoft", job.getCompanyName(), "Company name should match");
        assertEquals(JobType.FULL_TIME, job.getJobType(), "Job Type should match");
        assertTrue(job.getRequiredSkills().isEmpty(), "Required skills should be empty");
    }
    
    
    @Test
    void testJobIsSavedToFile() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        jobService.createJob(employerId, "Amazon", "Data Scientist",
                "Analyze big data to derive insights.",
                Location.BIRMINGHAM, "130000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 3, Set.of("Python", "Machine Learning"));

        List<Job> loadedJobs = JsonFileHandler.loadJobsFromFile(TEST_JOB_FILE);
        assertEquals(1, loadedJobs.size(), "Job should be saved to the file");
        assertEquals("Amazon", loadedJobs.get(0).getCompanyName(), "Saved job company name should match");
    }

    
    
    @Test
    void testDisplayJobsByEmployerWithJobs() {
        int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
        int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);
        
        jobService.createJob(employerId1, "Google", "Software Engineer",
                "Develop scalable software solutions.",
                Location.MANCHESTER, "120000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 3, Set.of("Java", "Spring"));
        
        jobService.createJob(employerId2, "Tesla", "Mechanical Engineer",
                "Design and build vehicles.",
                Location.SHEFFIELD, "110000", JobType.FULL_TIME,
                Industry.AUTOMOTIVE, 3, Set.of("SolidWorks", "Manufacturing"));

        jobService.displayJobsByEmployer(employerId1);
        jobService.displayJobsByEmployer(employerId2);

        String output = outputStream.toString();
        assertTrue(output.contains("===== Jobs Posted by You ====="), "Output should indicate the start of employer jobs list");
        assertTrue(output.contains("Software Engineer"), "Output should contain job title");
        assertTrue(output.contains("Mechanical Engineer"), "Output should contain job title");
    }

    @Test
    void testDisplayJobsByEmployerNoJobs() {
        
    	int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER); // employer ID with no jobs posted

        jobService.displayJobsByEmployer(employerId);

        String output = outputStream.toString();
        assertTrue(output.contains("No jobs posted."), "Output should indicate that no jobs are posted by the employer");
    }
    
    
    @Test
    void testGetJobByIdSuccess() {
    	
    	int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	Job job = jobService.createJob(employerId, "JAWK Group", "Software Engineer",
                "Design and implement software solutions.",
                Location.BRISTOL, "110000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 3, Set.of("ReactJs", "VueJS"));

        Job fetchedJob = jobService.getJobById(job.getJobId());

        assertNotNull(fetchedJob, "Job should be found");
        assertEquals(job.getJobId(), fetchedJob.getJobId(), "Job ID should match");
        assertEquals("JAWK Group", fetchedJob.getCompanyName(), "Company name should match");
    }

    
    @Test
    void testGetJobByIdNotFound() {
        int jobId = idGenerator.generateUniqueId(Constants.JOB);

        Job fetchedJob = jobService.getJobById(jobId);

        assertNull(fetchedJob, "Job should not be found");
        String output = outputStream.toString();
        assertTrue(output.contains("Job with ID " + jobId + " not found."), "Output should indicate that the job was not found");
    }


    @Test
    void testGetAllJobs() {
    	
    	int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	
    	Job job1 = jobService.createJob(employerId1, "Apple", "Data Scientist", "Analyze large datasets to extract meaningful insights.", Location.CAMBRIDGE, "20000", JobType.CONTRACT, Industry.TECHNOLOGY, 3, Set.of("Python", "Data Analysis"));
    	Job job2 = jobService.createJob(employerId1, "Netflix", "UI/UX Designer", "Design user-friendly interfaces for web and mobile applications.", Location.CANTERBURY, "22000", JobType.PART_TIME, Industry.MEDIA_ENTERTAINMENT, 3, Set.of("Figma", "Design Systems"));

        List<Job> allJobs = jobService.getAllJobs();

        assertNotNull(allJobs, "Job list should not be null");
        assertTrue(allJobs.contains(job1), "Job list should contain job1");
        assertTrue(allJobs.contains(job2), "Job list should contain job2");
    }

    
    @Test
    void testUpdateJobDetailsSuccess() {
        int employerId = idGenerator.generateUniqueId(Constants.EMPLOYER);
        Job job = jobService.createJob(employerId, "IBM", "Software Engineer",
                "Develop test test test.",
                Location.LONDON, "20900", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 5, Set.of("Java", "Spring Boot", "Docker"));

        boolean updated = jobService.updateJobDetails(employerId, job.getJobId(),
                "Senior Software Engineer", "Develop and scale innovative solutions.",
                Location.CHESTER, "140000", JobType.FULL_TIME, JobStatus.OPEN,
                Industry.TECHNOLOGY, 6, Set.of("Java", "Spring", "Kubernetes-1"));

        Job updatedJob = jobService.getJobById(job.getJobId());

        assertTrue(updated, "Job should be successfully updated");
        assertEquals("Senior Software Engineer", updatedJob.getTitle(), "Title should be updated");
        assertEquals("Develop and scale innovative solutions.", updatedJob.getDescription(), "Description should be updated");
        assertEquals(Location.CHESTER, updatedJob.getLocation(), "Location should be updated");
        assertEquals("140000", updatedJob.getSalary(), "Salary should be updated");
        assertEquals(JobType.FULL_TIME, updatedJob.getJobType(), "Job Type should be updated");
        assertEquals(JobStatus.OPEN, updatedJob.getJobStatus(), "Job Status should be updated");
        assertEquals(6, updatedJob.getRequiredExperienceYears(), "Experience years should be updated");
        assertTrue(updatedJob.getRequiredSkills().contains("Kubernetes-1"), "Skills should be updated");
    }

    
    @Test
    void testUpdateJobDetailsUnauthorized() {
        int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
        int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);

        Job job = jobService.createJob(employerId1, "EQ Technologics", "Data Scientist",
                "Analyze big data to derive insights.",
                Location.DURHAM, "130000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 3, Set.of("Python", "Machine Learning"));

        boolean updated = jobService.updateJobDetails(employerId2, job.getJobId(),
                "Senior Data Scientist", "Lead big data analysis.",
                Location.EXETER, "150000", JobType.FULL_TIME, JobStatus.OPEN,
                Industry.TECHNOLOGY, 5, Set.of("Python", "Big Data"));

        assertFalse(updated, "Update should fail when the employer does not own the job");
        String output = outputStream.toString();
        assertTrue(output.contains("Job not found or you are not authorized to update this job."), "Output should indicate unauthorized update");
    }
    
    
    @Test
    void testDeleteJobUnauthorized() {
        int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
        int employerId2 = idGenerator.generateUniqueId(Constants.EMPLOYER);
        Job job = jobService.createJob(employerId1, "Amazon", "Data Engineer",
                "Handle big data processing.", Location.BIRMINGHAM, "95000", JobType.FULL_TIME,
                Industry.TECHNOLOGY, 4, Set.of("Python", "Spark"));

        boolean isDeleted = jobService.deleteJob(employerId2, job.getJobId()); // Employer ID does not match

        assertFalse(isDeleted, "Deletion should fail due to unauthorized employer");
        assertTrue(jobService.getAllJobs().contains(job), "Job should remain in the list");
        String output = outputStream.toString();
        assertTrue(output.contains("Job not found or you are not authorized to delete this job."),
                   "Output should indicate unauthorized access");
    }


    @Test
    void testGetJobsByEmployer() {
    	int employerId1 = idGenerator.generateUniqueId(Constants.EMPLOYER);
    	
    	Job job1 = jobService.createJob(employerId1, "Unilever", "UI/UX designer", "create good designs for UI", Location.CARLISLE, "34000", JobType.CONTRACT, Industry.TECHNOLOGY, 3, Set.of("visual design", "Wireframe"));
    	Job job2 = jobService.createJob(employerId1, "Barclays", "Auditor", "British multinational universal bank.", Location.BIRMINGHAM, "45000", JobType.FULL_TIME, Industry.FINANCE, 3, Set.of("Blance sheets", "cash flows"));

        List<Job> result = jobService.getJobsByEmployer(employerId1);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(job -> job.getEmployerId() == employerId1));
    }
    
    @Test
    void testGetFAQStringBuilderUsage() {
        String faqContent = JobPortalFAQ.getFAQ();

        assertTrue(faqContent.startsWith("========== Job Portal FAQ =========="));
        assertTrue(faqContent.endsWith("=====================================\n"));
    }


    @Test
    void testGetFAQNumberOfQuestions() {
    	String faqContent = JobPortalFAQ.getFAQ();

        long questionCount = faqContent.lines()
            .filter(line -> line.matches("\\d+\\.\\s.*"))
            .count();

        assertEquals(10, questionCount, "FAQ does not contain the correct number of questions.");
    }


    @Test
    void testGetFAQQuestionFormat() {
    	String faqContent = JobPortalFAQ.getFAQ();

        // Check that all question lines follow the expected format
        Pattern questionPattern = Pattern.compile("\\d+\\.\\sHow do I .*\\?");
        Matcher matcher = questionPattern.matcher(faqContent);

        int matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }

        assertEquals(5, matchCount, "Not all questions follow the correct format.");
    }

}
