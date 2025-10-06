package com.jobfinder.tests.blackbox;

import com.jobfinder.model.Interview;
import com.jobfinder.service.InterviewService;
import com.jobfinder.utils.JsonFileHandler;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterviewServiceTest {

    private static final String MOCK_JSON_PATH = "scheduledInterviews.json";
    private InterviewService interviewService;

    @BeforeEach
    public void setup() {
        interviewService = new InterviewService();
        JsonFileHandler.saveInterviewsToFile(List.of(), MOCK_JSON_PATH);
    }

    @AfterEach
    public void cleanup() {
        JsonFileHandler.saveInterviewsToFile(List.of(), MOCK_JSON_PATH);
    }

    @Test
    public void testGetScheduledInterviewsForJobSeeker() {
        Interview interview1 = new Interview(1, 101, 1, "2025-01-25", "10:00 AM", "link", "Details", "Scheduled");
        Interview interview2 = new Interview(2, 102, 1, "2025-01-26", "11:00 AM", "link", "Details", "Completed");
        JsonFileHandler.saveInterviewsToFile(List.of(interview1, interview2), MOCK_JSON_PATH);

        List<Interview> scheduledInterviews = interviewService.getScheduledInterviewsForJobSeeker(1);
        assertEquals(1, scheduledInterviews.size(), "Only scheduled interviews should be returned.");
    }

    @Test
    public void testMarkInterviewAsCompleted() {
        Interview interview = new Interview(1, 101, 1, "2025-01-25", "10:00 AM", "link", "Details", "Scheduled");
        JsonFileHandler.saveInterviewsToFile(List.of(interview), MOCK_JSON_PATH);

        assertTrue(interviewService.markInterviewAsCompleted(1),
                "Marking interview as completed should succeed for an existing interview.");
    }

    @Test
    public void testScheduleInterview() {
        Interview newInterview = new Interview(1, 101, 1, "2025-01-25", "10:00 AM", "link", "Details", "Scheduled");

        interviewService.scheduleInterview(newInterview);

        List<Interview> interviews = JsonFileHandler.loadInterviewsFromFile(MOCK_JSON_PATH);
        assertEquals(1, interviews.size(), "Scheduled interview should be added to the JSON file.");
        assertEquals("Scheduled", interviews.get(0).getStatus(), "The status of the new interview should be 'Scheduled'.");
    }
 
    @Test
    public void testCancelInterview_ExistingInterview() {
        Interview interview = new Interview(1, 101, 1, "2025-01-25", "10:00 AM", "link", "Details", "Scheduled");
        JsonFileHandler.saveInterviewsToFile(List.of(interview), MOCK_JSON_PATH);

        assertTrue(interviewService.cancelInterview(1), "Cancel should succeed for an existing interview.");
    }

    @Test
    public void testRescheduleInterview_ValidInterview() {
        Interview interview = new Interview(1, 101, 1, "2025-01-25", "10:00 AM", "link", "Details", "Scheduled");
        JsonFileHandler.saveInterviewsToFile(List.of(interview), MOCK_JSON_PATH);

        assertTrue(interviewService.rescheduleInterview(1, "2025-02-01", "2:00 PM"),
                "Reschedule should succeed for an existing interview.");
    }
}
