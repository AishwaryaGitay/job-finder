package com.jobfinder.tests.whitebox;

import com.jobfinder.model.Interview;
import com.jobfinder.service.InterviewService;
import com.jobfinder.utils.JsonFileHandler;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterviewServiceTest {

    private static final String MOCK_INTERVIEW_FILE = "scheduledInterviews.json";
    private InterviewService interviewService;

    @BeforeEach
    public void setup() {
        interviewService = new InterviewService();

        // Create mock interviews and save to file
        List<Interview> mockInterviews = new ArrayList<>();
        mockInterviews.add(new Interview(1, 101, 1, "2025-01-20", "10:00 AM", "http://meetinglink1.com", "Initial screening", "Scheduled"));
        mockInterviews.add(new Interview(2, 102, 1, "2025-01-21", "2:00 PM", "http://meetinglink2.com", "Technical round", "Scheduled"));
        mockInterviews.add(new Interview(3, 103, 2, "2025-01-22", "3:00 PM", "http://meetinglink3.com", "HR discussion", "Completed"));

        JsonFileHandler.saveInterviewsToFile(mockInterviews, MOCK_INTERVIEW_FILE);
    }

    @AfterEach
    public void cleanup() {
        new File(MOCK_INTERVIEW_FILE).delete();
    }

    @Test
    public void testGetScheduledInterviewsForJobSeeker() {
        List<Interview> interviews = interviewService.getScheduledInterviewsForJobSeeker(1);

        assertNotNull(interviews, "Scheduled interviews should not be null.");
        assertEquals(2, interviews.size(), "Job seeker 1 should have 2 scheduled interviews.");

        Interview firstInterview = interviews.get(0);
        assertEquals(1, firstInterview.getInterviewId(), "Interview ID should match.");
        assertEquals("Scheduled", firstInterview.getStatus(), "Interview status should be 'Scheduled'.");
    }

    @Test
    public void testMarkInterviewAsCompleted() {
        boolean result = interviewService.markInterviewAsCompleted(1);

        assertTrue(result, "Marking interview as completed should succeed.");

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_INTERVIEW_FILE);
        Interview updatedInterview = interviews.stream().filter(i -> i.getInterviewId() == 1).findFirst().orElse(null);

        assertNotNull(updatedInterview, "Updated interview should exist.");
        assertEquals("Completed", updatedInterview.getStatus(), "Interview status should be updated to 'Completed'.");
    }

    @Test
    public void testGetCompletedInterviewsForJobSeeker() {
        List<Interview> completedInterviews = interviewService.getCompletedInterviewsForJobSeeker(2);

        assertNotNull(completedInterviews, "Completed interviews should not be null.");
        assertEquals(1, completedInterviews.size(), "Job seeker 2 should have 1 completed interview.");

        Interview interview = completedInterviews.get(0);
        assertEquals(3, interview.getInterviewId(), "Completed interview ID should match.");
        assertEquals("Completed", interview.getStatus(), "Interview status should be 'Completed'.");
    }

    @Test
    public void testScheduleInterview() {
        Interview newInterview = new Interview(4, 104, 3, "2025-01-25", "11:00 AM", "http://meetinglink4.com", "Final round", "Scheduled");

        interviewService.scheduleInterview(newInterview);

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_INTERVIEW_FILE);
        Interview scheduledInterview = interviews.stream().filter(i -> i.getInterviewId() == 4).findFirst().orElse(null);

        assertNotNull(scheduledInterview, "Scheduled interview should be added.");
        assertEquals("Final round", scheduledInterview.getDetails(), "Interview details should match.");
    }

    @Test
    public void testUpdateInterviewDetails() {
        String newDetails = "Updated technical round";
        interviewService.updateInterview(2, newDetails);

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_INTERVIEW_FILE);
        Interview updatedInterview = interviews.stream().filter(i -> i.getInterviewId() == 2).findFirst().orElse(null);

        assertNotNull(updatedInterview, "Updated interview should exist.");
        assertEquals(newDetails, updatedInterview.getDetails(), "Interview details should be updated.");
    }

    @Test
    public void testCancelInterview() {
        boolean result = interviewService.cancelInterview(2);

        assertTrue(result, "Canceling interview should succeed.");

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_INTERVIEW_FILE);
        Interview canceledInterview = interviews.stream().filter(i -> i.getInterviewId() == 2).findFirst().orElse(null);

        assertNull(canceledInterview, "Canceled interview should not exist.");
    }

    @Test
    public void testRescheduleInterview() {
        String newDate = "2025-01-26";
        String newTime = "4:00 PM";

        boolean result = interviewService.rescheduleInterview(1, newDate, newTime);

        assertTrue(result, "Rescheduling interview should succeed.");

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_INTERVIEW_FILE);
        Interview rescheduledInterview = interviews.stream().filter(i -> i.getInterviewId() == 1).findFirst().orElse(null);

        assertNotNull(rescheduledInterview, "Rescheduled interview should exist.");
        assertEquals(newDate, rescheduledInterview.getDate(), "Interview date should be updated.");
        assertEquals(newTime, rescheduledInterview.getTime(), "Interview time should be updated.");
    }

    @Test
    public void testCancelNonExistentInterview() {
        boolean result = interviewService.cancelInterview(999);

        assertFalse(result, "Canceling a non-existent interview should fail.");
    }

    @Test
    public void testRescheduleNonExistentInterview() {
        boolean result = interviewService.rescheduleInterview(999, "2025-01-30", "2:00 PM");

        assertFalse(result, "Rescheduling a non-existent interview should fail.");
    }
}
