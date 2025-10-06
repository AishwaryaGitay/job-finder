package com.jobfinder.main;

import com.jobfinder.model.Interview;
import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;
import com.jobfinder.model.User;
import com.jobfinder.service.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


    //     // Initialize services
         SignupService signupService = new SignupService();
         LoginService loginService = new LoginService();
         JobService jobService = new JobService();
         JobApplicationService jobApplicationService = new JobApplicationService();
         InterviewService interviewService = new InterviewService(); // New interview service
         FileUploadService fileUploadService = new FileUploadService();
         ProfileUpdateService profileUpdateService = new ProfileUpdateService();
         PasswordResetService passwordResetService = new PasswordResetService();
         
         displayGeneralMainMenu(scanner, signupService, loginService, jobService, profileUpdateService, fileUploadService, jobApplicationService,interviewService, passwordResetService);

         scanner.close();
         
    }
    public static void displayGeneralMainMenu(
    	    Scanner scanner, 
    	    SignupService signupService, 
    	    LoginService loginService, 
    	    JobService jobService, 
    	    ProfileUpdateService profileUpdateService,
    	    FileUploadService fileUploadService,
    	    JobApplicationService jobApplicationService,
    	    InterviewService interviewService,
    	    PasswordResetService passwordResetService
    	) {
         User loggedInUser = null;

         System.out.println("===== Job Finder Application =====");

         while (true) {
             if (loggedInUser == null) {
                 System.out.println("\nSelect an option:");
                 System.out.println("==================");
                 System.out.println("1. Register");
                 System.out.println("2. Login as Job Seeker");
                 System.out.println("3. Login as Employer");
                 System.out.println("4. Exit");

                 System.out.print("Your choice: ");
                 String choice = scanner.nextLine();

                 if (choice.equals("1")) {
                     // Registration logic
                     registerUser(scanner, signupService);
                 } else if (choice.equals("2")) {
                     // Login as Job Seeker
                     loggedInUser = loginUser(scanner, loginService, "job_seeker");
                 } else if (choice.equals("3")) {
                     // Login as Employer
                     loggedInUser = loginUser(scanner, loginService, "employer");
                 } else if (choice.equals("4")) {
                     System.out.println("Thank you for using the Job Finder Application. Goodbye!");
                     break;
                 } else {
                     System.out.println("Invalid option. Please try again.");
                 }
             } else {
                 // Post-login menu based on user role
                 if ("job_seeker".equalsIgnoreCase(loggedInUser.getRole())) {
                     jobSeekerMenu(scanner, loggedInUser, jobService, profileUpdateService, signupService, loginService,fileUploadService, jobApplicationService,interviewService, passwordResetService);
                 } else if ("employer".equalsIgnoreCase(loggedInUser.getRole())) {
                     employerMenu(scanner, loggedInUser, jobService, profileUpdateService, signupService, loginService, jobApplicationService,interviewService, passwordResetService);
                 }
                 loggedInUser = null;
             }
         }
     }

     public static void registerUser(Scanner scanner, SignupService signupService) {
         System.out.print("Enter your name (or company name for employers): ");
         String name = scanner.nextLine();
         System.out.print("Enter your email: ");
         String email = scanner.nextLine();
         System.out.print("Enter your password: ");
         String password = scanner.nextLine();
         System.out.print("Enter your role (job_seeker or employer): ");
         String role = scanner.nextLine();
         String location = null;
         String previousJobTitle = null;
         String previousSalary = null;
         String highestEducation = null;
         String companyDescription = null;
         String website = null;

         if ("job_seeker".equalsIgnoreCase(role)) {
             System.out.print("Enter your location: ");
             for (int i = 0; i < Location.values().length; i++) {
                 System.out.println((i + 1) + ". " + Location.values()[i]);
             }
             int locationChoice = Integer.parseInt(scanner.nextLine());
             if (locationChoice >= 1 && locationChoice <= Location.values().length) {
                 location = Location.values()[locationChoice - 1].name();
             } else {
                 System.out.println("Invalid choice. Please select a valid location.");
                 return;
             }
             System.out.print("Enter your previous job title: ");
             previousJobTitle = scanner.nextLine();
             System.out.print("Enter your previous salary: ");
             previousSalary = scanner.nextLine();
             System.out.print("Enter your highest education: ");
             highestEducation = scanner.nextLine();
         } else if ("employer".equalsIgnoreCase(role)) {
             System.out.println("Select your company location:");
             for (int i = 0; i < Location.values().length; i++) {
                 System.out.println((i + 1) + ". " + Location.values()[i]);
             }
             int locationChoice = Integer.parseInt(scanner.nextLine());
             if (locationChoice >= 1 && locationChoice <= Location.values().length) {
                 location = Location.values()[locationChoice - 1].name();
             } else {
                 System.out.println("Invalid choice. Please select a valid location.");
                 return;
             }
             System.out.print("Enter your company description: ");
             companyDescription = scanner.nextLine();
             System.out.print("Enter your company website: ");
             website = scanner.nextLine();
         }

         User newUser = signupService.registerUser(name, email, password, role, location, previousJobTitle, previousSalary, companyDescription, website, highestEducation);

         if (newUser != null) {
             System.out.println("You have been successfully registered!");
             System.out.println("Here are your profile details:");
             System.out.println(newUser);
         } else {
             System.out.println("Registration failed. Email might already be registered.");
         }
     }

     public static User loginUser(Scanner scanner, LoginService loginService, String expectedRole) {
         System.out.print("Enter your email: ");
         String email = scanner.nextLine();
         System.out.print("Enter your password: ");
         String password = scanner.nextLine();

         User user = loginService.login(email, password);
         if (user != null && expectedRole.equalsIgnoreCase(user.getRole())) {
             return user;
         } else {
             System.out.println("Login failed. Please check your credentials and role.");
             return null;
         }
     }

     public static void jobSeekerMenu(
    	        Scanner scanner,
    	        User loggedInUser,
    	        JobService jobService,
    	        ProfileUpdateService profileUpdateService,
    	        SignupService signupService,
    	        LoginService loginService,
    	        FileUploadService fileUploadService,
    	        JobApplicationService jobApplicationService,
    	        InterviewService interviewService,
    	        PasswordResetService passwordResetService
    	) {
    	    while (loggedInUser != null) {
    	        System.out.println("\n===== Job Seeker Menu =====");
    	        System.out.println("1. View All Jobs");
    	        System.out.println("2. View All Available Jobs");
    	        System.out.println("3. Update Profile");
    	        System.out.println("4. View Profile Details");
    	        System.out.println("5. Upload Resume and Cover Letter");
    	        System.out.println("6. Apply for a Job");
    	        System.out.println("7. Track or Filter Application Statuses");
    	        System.out.println("8. View Scheduled Interviews");
    	        System.out.println("9. View Completed Interviews");
    	        System.out.println("10. Logout");
    	        System.out.println("11. Delete Cover Letter");
    	        System.out.println("12. Delete Resume");
    	        System.out.println("13. View FAQs");
    	        System.out.println("14. Password Reset");


    	        System.out.print("Your choice: ");
    	        String choice = scanner.nextLine();

    	        switch (choice) {
    	            case "1":
    	                jobService.displayAllJobs();
    	                break;
    	            case "2":
    	                jobService.displayOpenJobs();
    	                break;
    	            case "3":
    	                updateJobSeekerProfile(scanner, loggedInUser, profileUpdateService);
    	                break;
    	            case "4":
    	            	profileUpdateService.viewUserSummary(loggedInUser);
    	                break;
    	            case "5":
    	                uploadResumeAndCoverLetter(scanner, loggedInUser, fileUploadService);
    	                break;
    	            case "6":
    	                if (loggedInUser.getResume() == null || loggedInUser.getCoverLetter() == null) {
    	                    System.out.println("Please upload your resume and cover letter first.");
    	                    break;
    	                }
    	                applyForJobs(scanner, loggedInUser, jobApplicationService);
    	                break;
    	            case "7":
    	                trackJobApplicatonStatus(scanner, loggedInUser, jobService, jobApplicationService);
    	                break;
    	            case "8":
    	                List<Interview> scheduledInterviews = interviewService.getScheduledInterviewsForJobSeeker(loggedInUser.getUserId());
    	                displayScheduledInterviews(scheduledInterviews);
    	                break;
    	            case "9":
    	                List<Interview> completedInterviews = interviewService.getCompletedInterviewsForJobSeeker(loggedInUser.getUserId());
    	                displayCompletedInterviews(completedInterviews);
    	                break;
    	            case "10":
    	                System.out.println("You have been logged out.");
    	                loggedInUser = null;
    	                break;
    	            case "11":
    	                deleteCoverLetter(scanner, loggedInUser, fileUploadService);
    	                break;
    	            case "12":
    	                deleteResume(scanner, loggedInUser, fileUploadService);
    	                break;
    	            case "13": // New Case for Viewing FAQs
    	                String faqs = jobService.getFAQs();
    	                System.out.println("\n===== Frequently Asked Questions =====");
    	                System.out.println(faqs);
    	                break;
    	            case "14":
    	            	System.out.println("Enter old password : ");
    	            	String oldPassword = scanner.nextLine();
    	            	System.out.println("Enter new password : ");
    	            	String newPassword = scanner.nextLine();
    	            	
    	            	passwordResetService.resetPassword(loggedInUser, oldPassword, newPassword);
    	            	break;

    	            default:
    	                System.out.println("Invalid option. Please try again.");
    	        }
    	    }
    	}


     
  
     public static void employerMenu(
    	        Scanner scanner,
    	        User loggedInUser,
    	        JobService jobService,
    	        ProfileUpdateService profileUpdateService,
    	        SignupService signupService,
    	        LoginService loginService,
    	        JobApplicationService jobApplicationService,
    	        InterviewService interviewService,
    	        PasswordResetService passwordResetService
    	) {
    	    while (loggedInUser != null) {
    	        System.out.println("\n===== Employer Menu =====");
    	        System.out.println("1. Create a new Job");
    	        System.out.println("2. View All Posted Jobs");
    	        System.out.println("3. Update Job Details");
    	        System.out.println("4. Update Profile");
    	        System.out.println("5. View Profile Details");
    	        System.out.println("6. Review Submitted Job Applications");
    	        System.out.println("7. Schedule Interview for an Application");
    	        System.out.println("8. Cancel Interview");
    	        System.out.println("9. Reschedule Interview");
    	        System.out.println("10. Mark Interview as Completed");
    	        System.out.println("11. Reset Password");
    	        System.out.println("12. Logout");

    	        System.out.print("Your choice: ");
    	        String choice = scanner.nextLine();

    	        switch (choice) {
    	            case "1":
    	                createJob(scanner, loggedInUser, jobService);
    	                break;
    	            case "2":
    	                jobService.displayJobsByEmployer(loggedInUser.getUserId());
    	                break;
    	            case "3":
    	                updateJobDetails(scanner, loggedInUser, jobService);
    	                break;
    	            case "4":
    	                updateEmployerProfile(scanner, loggedInUser, profileUpdateService);
    	                break;
    	            case "5":
    	                profileUpdateService.viewUserSummary(loggedInUser);
    	                break;
    	            case "6":
    	                reviewJobApplications(scanner, loggedInUser, jobService);
    	                break;
    	            case "7": // Schedule Interview
    	                System.out.print("Enter the Job ID for which you want to schedule an interview: ");
    	                int jobId = Integer.parseInt(scanner.nextLine());

    	                System.out.print("Enter the Applicant (Job Seeker) ID: ");
    	                int jobSeekerId = Integer.parseInt(scanner.nextLine());

    	                System.out.print("Enter the date of the interview (YYYY-MM-DD): ");
    	                String date = scanner.nextLine();

    	                System.out.print("Enter the time of the interview (HH:MM): ");
    	                String time = scanner.nextLine();

    	                System.out.print("Enter the meeting link: ");
    	                String meetingLink = scanner.nextLine();

    	                System.out.print("Enter additional details: ");
    	                String details = scanner.nextLine();

    	                int interviewId = (int) (Math.random() * 100000); // Generate unique ID
    	                interviewService.scheduleInterview(new Interview(interviewId, jobId, jobSeekerId, date, time, meetingLink, details, "Scheduled"));

    	                System.out.println("Interview scheduled successfully with ID: " + interviewId);
    	                break;
    	            case "8": // Cancel Interview
    	                System.out.print("Enter the Interview ID to cancel: ");
    	                int cancelInterviewId = Integer.parseInt(scanner.nextLine());

    	                if (interviewService.cancelInterview(cancelInterviewId)) {
    	                    System.out.println("Interview canceled successfully.");
    	                } else {
    	                    System.out.println("Failed to cancel the interview. Please check the Interview ID.");
    	                }
    	                break;

    	            case "9": // Reschedule Interview
    	                System.out.print("Enter the Interview ID to reschedule: ");
    	                int rescheduleInterviewId = Integer.parseInt(scanner.nextLine());

    	                System.out.print("Enter the new date (YYYY-MM-DD): ");
    	                String newDate = scanner.nextLine();

    	                System.out.print("Enter the new time (HH:MM): ");
    	                String newTime = scanner.nextLine();

    	                if (interviewService.rescheduleInterview(rescheduleInterviewId, newDate, newTime)) {
    	                    System.out.println("Interview rescheduled successfully.");
    	                } else {
    	                    System.out.println("Failed to reschedule the interview. Please check the Interview ID.");
    	                }
    	                break;
    	            case "10": // Mark Interview as Completed
    	                System.out.print("Enter the Interview ID to mark as completed: ");
    	                int completedInterviewId = Integer.parseInt(scanner.nextLine());

    	                if (interviewService.markInterviewAsCompleted(completedInterviewId)) {
    	                    System.out.println("Interview marked as completed successfully.");
    	                } else {
    	                    System.out.println("Failed to mark the interview as completed. Please check the Interview ID.");
    	                }
    	                break;
    	            case "11":
    	            	System.out.println("Enter old password : ");
    	            	String oldPassword = scanner.nextLine();
    	            	System.out.println("Enter new password : ");
    	            	String newPassword = scanner.nextLine();
    	            	
    	            	passwordResetService.resetPassword(loggedInUser, oldPassword, newPassword);
    	            case "12":
    	                System.out.println("You have been logged out.");
    	                loggedInUser = null;
    	                break;
    	            default:
    	                System.out.println("Invalid option. Please try again.");
    	        }
    	    }
    	}


     
     public static void trackJobApplicatonStatus(Scanner scanner, User loggedInUser, JobService jobService, JobApplicationService jobApplicationService) {
    	 
    	 System.out.println("Select an action for your applications:");
		    System.out.println("1. Check for updates");
		    System.out.println("2. Filter by status");
		    String actionChoice = scanner.nextLine();

		    switch (actionChoice) {
		        case "1": // Check for updates
		            System.out.println("Checking for updates on your applications...");
		            jobApplicationService.checkForUpdates(loggedInUser.getUserId());
		            break;
		        case "2": // Filter by status
		            System.out.println("Select the status to filter by:");
		            System.out.println("1. Submitted");
		            System.out.println("2. Under_Review");
		            System.out.println("3. Interviewed");
		            System.out.println("4. Hired");
		            System.out.println("5. Rejected");

		            String statusInput = scanner.nextLine();
		            JobApplication.ApplicationStatus status = null;
		            switch (statusInput) {
		                case "1": status = JobApplication.ApplicationStatus.SUBMITTED; break;
		                case "2": status = JobApplication.ApplicationStatus.UNDER_REVIEW; break;
		                case "3": status = JobApplication.ApplicationStatus.INTERVIEWED; break;
		                case "4": status = JobApplication.ApplicationStatus.HIRED; break;
		                case "5": status = JobApplication.ApplicationStatus.REJECTED; break;
		                default:
		                    System.out.println("Invalid choice. Please select a number between 1 and 5.");
		                    break;
		            }

		            List<JobApplication> filteredApplications = jobApplicationService.getApplicationsByStatus(loggedInUser.getUserId(), status);
		            if (filteredApplications.isEmpty()) {
		                System.out.println("No applications found with the status: " + status);
		            } else {
		                System.out.println("Applications with status " + status + ":");
		                for (JobApplication app : filteredApplications) {
		                    Job job = jobService.getJobById(app.getJobId());
		                    String jobTitle = job != null ? job.getTitle() : "Unknown Title";
		                    System.out.println("Job ID: " + app.getJobId() + ", Title: " + jobTitle + ", Status: " + app.getStatus());
		                }
		            }
		            break;
		        default:
		            System.out.println("Invalid action selected. Please choose either 1 or 2.");
		    }
    	 
     }
     
    
     
     public static void updateApplicationStatus(Scanner scanner, User loggedInUser, JobService jobService, JobApplicationService jobApplicationService) {
    	 
    	 System.out.println("Fetching jobs you have posted...");
		    jobService.displayJobsByEmployer(loggedInUser.getUserId());

		    System.out.print("Enter the Job ID of the job for which you want to update applications: ");
		    int jobId;
		    try {
		        jobId = Integer.parseInt(scanner.nextLine());
		    } catch (NumberFormatException e) {
		        System.out.println("Invalid input. Please enter a numeric Job ID.");
		        //continue;
		        return;
		    }

		    List<JobApplication> applications = jobApplicationService.getApplicationsByJob(jobId);
		    if (applications.isEmpty()) {
		        System.out.println("No applications found for Job ID: " + jobId);
		        //continue;
		        return;
		    }

		    System.out.println("Select an Application ID from the following:");
		    applications.forEach(application -> System.out.println("Application ID: " + application.getUserId() + ", Current Status: " + application.getStatus()));

		    System.out.print("Enter the Application ID you want to update: ");
		    int applicationId = Integer.parseInt(scanner.nextLine());

		    System.out.println("Available Statuses: 1. Submitted, 2. Under Review, 3. Interviewed, 4. Hired, 5. Rejected");
		    System.out.print("Select a new status by entering the corresponding number: ");
		    int statusChoice = Integer.parseInt(scanner.nextLine());
		    JobApplication.ApplicationStatus newStatus = getStatusFromChoice(statusChoice);

		    if (jobApplicationService.updateApplicationStatus(applicationId, newStatus, loggedInUser.getUserId())) {
		        System.out.println("Application status updated successfully.");
		    } else {
		        System.out.println("Failed to update application status. Please check the details and your permissions.");
		    }
     }
     
     
     public static JobApplication.ApplicationStatus getStatusFromChoice(int choice) {
 	    switch (choice) {
 	        case 1: return JobApplication.ApplicationStatus.SUBMITTED;
 	        case 2: return JobApplication.ApplicationStatus.UNDER_REVIEW;
 	        case 3: return JobApplication.ApplicationStatus.INTERVIEWED;
 	        case 4: return JobApplication.ApplicationStatus.HIRED;
 	        case 5: return JobApplication.ApplicationStatus.REJECTED;
 	        default: return JobApplication.ApplicationStatus.SUBMITTED; // Default case or handle error
 	    }
 	}
     
     public static void deleteCoverLetter(Scanner scanner, User loggedInUser, FileUploadService fileUploadService) {
    	    System.out.println("Deleting cover letter...");
    	    boolean success = fileUploadService.deleteCoverLetterOfJobSeeker(loggedInUser);
    	    if (success) {
    	        System.out.println("Cover letter deleted successfully.");
    	    } else {
    	        System.out.println("Failed to delete cover letter.");
    	    }
    	}

     
     public static void deleteResume(Scanner scanner, User loggedInUser, FileUploadService fileUploadService) {
    	    System.out.println("Deleting resume...");
    	    boolean success = fileUploadService.deleteResumeOfJobSeeker(loggedInUser);
    	    if (success) {
    	        System.out.println("Resume deleted successfully.");
    	    } else {
    	        System.out.println("Failed to delete resume.");
    	    }
    	}

     public static void displayScheduledInterviews(List<Interview> scheduledInterviews) {
    	    if (scheduledInterviews == null || scheduledInterviews.isEmpty()) {
    	        System.out.println("No scheduled interviews found.");
    	        return;
    	    }

    	    System.out.println("\n===== Scheduled Interviews =====");
    	    for (Interview interview : scheduledInterviews) {
    	        System.out.println("---------------------------------------");
    	        System.out.println("Interview ID: " + interview.getInterviewId());
    	        System.out.println("Job ID: " + interview.getJobId());
    	        System.out.println("Job Seeker ID: " + interview.getJobSeekerId());
    	        System.out.println("Date: " + interview.getDate());
    	        System.out.println("Time: " + interview.getTime());
    	        System.out.println("Meeting Link: " + interview.getMeetingLink());
    	        System.out.println("Details: " + interview.getDetails());
    	        System.out.println("Status: " + interview.getStatus());
    	    }
    	    System.out.println("---------------------------------------");
    	}
     
     public static void displayCompletedInterviews(List<Interview> completedInterviews) {
    	    if (completedInterviews == null || completedInterviews.isEmpty()) {
    	        System.out.println("No completed interviews found.");
    	        return;
    	    }

    	    System.out.println("\n===== Completed Interviews =====");
    	    for (Interview interview : completedInterviews) {
    	        System.out.println("---------------------------------------");
    	        System.out.println("Interview ID: " + interview.getInterviewId());
    	        System.out.println("Job ID: " + interview.getJobId());
    	        System.out.println("Job Seeker ID: " + interview.getJobSeekerId());
    	        System.out.println("Date: " + interview.getDate());
    	        System.out.println("Time: " + interview.getTime());
    	        System.out.println("Meeting Link: " + interview.getMeetingLink());
    	        System.out.println("Details: " + interview.getDetails());
    	        System.out.println("Status: " + interview.getStatus());
    	    }
    	    System.out.println("---------------------------------------");
    	}


     public static void reviewJobApplications(Scanner scanner, User loggedInUser, JobService jobService) {
    	 
    	 System.out.println("Fetching jobs you have posted...");
		    jobService.displayJobsByEmployer(loggedInUser.getUserId());

		    System.out.print("Enter the Job ID of the job for which you want to review applications: ");
		    int jobId;
		    try {
		        jobId = Integer.parseInt(scanner.nextLine());
		    } catch (NumberFormatException e) {
		        System.out.println("Invalid input. Please enter a numeric Job ID.");
		        //continue;
		        return;
		    }

		    Job selectedJob = jobService.getJobById(jobId);
		    if (selectedJob == null || selectedJob.getEmployerId() != loggedInUser.getUserId()) {
		        System.out.println("Invalid Job ID or you do not have permission to review applications for this job.");
		        //continue;
		        return;
		    }

		    // Prompt the employer whether they want to review all applications or only filtered ones
		    System.out.println("Do you want to review all applications or only those that meet the job criteria? (1: All, 2: Filtered)");
		    String filterChoice = scanner.nextLine();
		    switch (filterChoice) {
		        case "1":
		            ReviewJobApplication.reviewApplications(jobId, loggedInUser.getUserId());
		            break;
		        case "2":
		            ReviewJobApplication.reviewFilteredApplications(jobId, loggedInUser.getUserId());
		            break;
		        default:
		            System.out.println("Invalid choice. Please select '1' for All or '2' for Filtered.");
		            //continue;
		            return;
		    }
     }
     
     
     public static void uploadResumeAndCoverLetter(Scanner scanner, User loggedInUser, FileUploadService fileUploadService) {
    	 System.out.print("Enter the file path for your resume: ");
         String resumePath = scanner.nextLine();
         boolean resumeUploaded = fileUploadService.uploadFile(loggedInUser, resumePath, "resume");
         System.out.println(resumeUploaded ? "Resume uploaded successfully!" : "Resume upload failed.");

         System.out.print("Enter the file path for your cover letter: ");
         String coverLetterPath = scanner.nextLine();
         boolean coverLetterUploaded = fileUploadService.uploadFile(loggedInUser, coverLetterPath,
                  "cover_letter");
        System.out.println(coverLetterUploaded ? "Cover letter uploaded successfully!"
                : "Cover letter upload failed."); 
     }
     
     
     public static void applyForJobs(Scanner scanner, User loggedInUser, JobApplicationService jobApplicationService) {

		    System.out.print("Enter the Job ID you want to apply for: ");
		    int jobId = Integer.parseInt(scanner.nextLine());

		    System.out.print("Enter your total years of experience: ");
		    int experienceYears = Integer.parseInt(scanner.nextLine());

		    System.out.println("Enter your skills (separated by comma):");
		    String[] skillsArray = scanner.nextLine().split(",");
		    Set<String> skills = new HashSet<>(Arrays.asList(skillsArray));

		    boolean success = jobApplicationService.applyForJob(loggedInUser, jobId, experienceYears, skills);
		    if (success) {
		        System.out.println("Application submitted successfully.");
		    } else {
		        System.out.println("Failed to submit your application.");
		    }
     }

     public static void updateJobSeekerProfile(Scanner scanner, User loggedInUser, ProfileUpdateService profileUpdateService) {
         System.out.print("Enter updated previous job title (or press Enter to keep current): ");
         String newPreviousJobTitle = scanner.nextLine();

         System.out.print("Enter updated previous salary (or press Enter to keep current): ");
         String newPreviousSalary = scanner.nextLine();

         System.out.print("Enter new highest education (or press Enter to keep current): ");
         String newHighestEducation = scanner.nextLine();

         System.out.println("Select new location (or press Enter to keep current): ");
         for (int i = 0; i < Location.values().length; i++) {
             System.out.println((i + 1) + ". " + Location.values()[i]);
         }
         System.out.print("Enter the number for the new location: ");
         String newLocationChoice = scanner.nextLine();
         String newLocation = null;

         if (!newLocationChoice.isEmpty()) {
             try {
                 int locationChoice = Integer.parseInt(newLocationChoice);
                 if (locationChoice >= 1 && locationChoice <= Location.values().length) {
                     newLocation = Location.values()[locationChoice - 1].name();
                 } else {
                     System.out.println("Invalid location. Location not updated.");
                     newLocation = loggedInUser.getLocation();
                 }
             } catch (NumberFormatException e) {
                 System.out.println("Invalid input. Location not updated.");
                 newLocation = loggedInUser.getLocation();
             }
         } else {
             newLocation = loggedInUser.getLocation();
         }

         profileUpdateService.updateJobseekerProfile(loggedInUser, newLocation, newPreviousJobTitle, newPreviousSalary, newHighestEducation);
     }

     public static void createJob(Scanner scanner, User loggedInUser, JobService jobService) {
         System.out.print("Enter job title: ");
         String title = scanner.nextLine();
         System.out.print("Enter job description: ");
         String description = scanner.nextLine();
         System.out.print("Enter the required experience in years: ");
         int experienceYears = -1;
         while (experienceYears < 0) {
             String input = scanner.nextLine();
             try {
                 experienceYears = Integer.parseInt(input);
                 if (experienceYears < 0) {
                     System.out.println("Please enter a valid number of years (greater than or equal to 0): ");
                 }
             } catch (NumberFormatException e) {
                 System.out.println("Invalid input. Please enter a whole number: ");
             }
         }
         System.out.println("Select industry:");
         for (int i = 0; i < Industry.values().length; i++) {
             System.out.println((i + 1) + ". " + Industry.values()[i]);
         }
         int industryChoice = Integer.parseInt(scanner.nextLine());
         Industry industry = null;
         if (industryChoice >= 1 && industryChoice <= Industry.values().length) {
             industry = Industry.values()[industryChoice - 1];
         } else {
             System.out.println("Invalid choice. Please select a valid industry.");
             return;
         }
         System.out.println("Select job location:");
         for (int i = 0; i < Location.values().length; i++) {
             System.out.println((i + 1) + ". " + Location.values()[i]);
         }
         int locationChoice = Integer.parseInt(scanner.nextLine());
         Location location = null;
         if (locationChoice >= 1 && locationChoice <= Location.values().length) {
             location = Location.values()[locationChoice - 1];
         } else {
             System.out.println("Invalid choice. Please select a valid location.");
             return;
         }
         System.out.print("Enter job salary in £(GBP)(or press Enter to skip): £ ");
         String salary = scanner.nextLine();
         System.out.println("Select job type:");
         System.out.println("1. FULL-TIME");
         System.out.println("2. PART-TIME");
         System.out.println("3. CONTRACT");
         System.out.println("4. INTERNSHIP");
         int jobTypeChoice = Integer.parseInt(scanner.nextLine());
         JobType jobType = null;
         switch (jobTypeChoice) {
             case 1:
                 jobType = JobType.FULL_TIME;
                 break;
             case 2:
                 jobType = JobType.PART_TIME;
                 break;
             case 3:
                 jobType = JobType.CONTRACT;
                 break;
             case 4:
                 jobType = JobType.INTERNSHIP;
                 break;
             default:
                 System.out.println("Invalid choice. Defaulting to FULL_TIME.");
                 jobType = JobType.FULL_TIME;
                 break;
         }
         
         System.out.println("Enter required skills (separated by comma, or press Enter to skip):");
	        String skillsInput = scanner.nextLine();
	        Set<String> requiredSkills = new HashSet<>();
	        if (!skillsInput.isEmpty()) {
	            String[] skills = skillsInput.split(","); 
	            for (String skill : skills) {
	                requiredSkills.add(skill.trim());
	            }
	        }
         jobService.createJob(loggedInUser.getUserId(), loggedInUser.getName(), title, description, location, salary.isEmpty() ? null : salary, jobType, industry, experienceYears, requiredSkills);
     }

     public static void updateJobDetails(Scanner scanner, User loggedInUser, JobService jobService) {
         int employerId = loggedInUser.getUserId();
         jobService.displayJobsByEmployer(employerId);
         System.out.print("Enter the Job ID of the job you want to update: ");
         int jobId = Integer.parseInt(scanner.nextLine());
         System.out.println("Leave fields empty if you do not want to update them.");
         System.out.print("Enter new job title (or press Enter to keep current): ");
         String newTitle = scanner.nextLine();
         System.out.print("Enter new job description (or press Enter to keep current): ");
         String newDescription = scanner.nextLine();
         System.out.print("Enter the required years of experience (or press Enter to keep current): ");
         String experienceYearsStr = scanner.nextLine();
         int newExperienceYears = experienceYearsStr.isEmpty() ? -1 : Integer.parseInt(experienceYearsStr);
         System.out.println("Do you want to change the job location? (y/n): ");
         String changeLocation = scanner.nextLine();
         Location newLocation = null;
         if (changeLocation.equalsIgnoreCase("y")) {
             System.out.println("Select new job location (or press Enter to keep current): ");
             Location[] locations = Location.values();
             for (int i = 0; i < locations.length; i++) {
                 System.out.println((i + 1) + ". " + locations[i]);
             }
             System.out.print("Enter the number for the new location: ");
             String newLocationChoice = scanner.nextLine();
             if (!newLocationChoice.isEmpty()) {
                 try {
                     int locationChoice = Integer.parseInt(newLocationChoice);
                     if (locationChoice >= 1 && locationChoice <= locations.length) {
                         newLocation = locations[locationChoice - 1];
                     } else {
                         System.out.println("Invalid location. Location not updated.");
                     }
                 } catch (NumberFormatException e) {
                     System.out.println("Invalid input. Location not updated.");
                 }
             }
         }
         System.out.println("Do you want to change the job industry? (y/n): ");
         String changeIndustry = scanner.nextLine();
         Industry newIndustry = null;
         if (changeIndustry.equalsIgnoreCase("y")) {
             System.out.println("Select new job industry (or press Enter to keep current): ");
             Industry[] industries = Industry.values();
             for (int i = 0; i < industries.length; i++) {
                 System.out.println((i + 1) + ". " + industries[i]);
             }
             System.out.print("Enter the number for the new industry: ");
             String newIndustryChoice = scanner.nextLine();
             if (!newIndustryChoice.isEmpty()) {
                 try {
                     int industryChoice = Integer.parseInt(newIndustryChoice);
                     if (industryChoice >= 1 && industryChoice <= industries.length) {
                         newIndustry = industries[industryChoice - 1];
                     } else {
                         System.out.println("Invalid industry. Industry not updated.");
                     }
                 } catch (NumberFormatException e) {
                     System.out.println("Invalid input. Industry not updated.");
                 }
             }
         }
         System.out.print("Enter new job salary (or press Enter to keep current): £ ");
         String newSalary = scanner.nextLine();
         System.out.println("Do you want to change the job type? (y/n): ");
         String changeJobType = scanner.nextLine();
         JobType newJobType = null;
         if (changeJobType.equalsIgnoreCase("y")) {
             System.out.println("Select job type:");
             System.out.println("1. FULL-TIME");
             System.out.println("2. PART-TIME");
             System.out.println("3. CONTRACT");
             System.out.println("4. INTERNSHIP");
             int jobTypeChoice = Integer.parseInt(scanner.nextLine());
             switch (jobTypeChoice) {
                 case 1:
                     newJobType = JobType.FULL_TIME;
                     break;
                 case 2:
                     newJobType = JobType.PART_TIME;
                     break;
                 case 3:
                     newJobType = JobType.CONTRACT;
                     break;
                 case 4:
                     newJobType = JobType.INTERNSHIP;
                     break;
                 default:
                     System.out.println("Invalid choice. Job type not updated.");
                     break;
             }
         }
         System.out.println("Do you want to change the job status? (y/n): ");
         String changeJobStatus = scanner.nextLine();
         JobStatus newJobStatus = null;
         if (changeJobStatus.equalsIgnoreCase("y")) {
             System.out.println("Select job status:");
             System.out.println("1. OPEN");
             System.out.println("2. CLOSED");
             int jobStatusChoice = Integer.parseInt(scanner.nextLine());
             switch (jobStatusChoice) {
                 case 1:
                     newJobStatus = JobStatus.OPEN;
                     break;
                 case 2:
                     newJobStatus = JobStatus.CLOSED;
                     break;
                 default:
                     System.out.println("Invalid choice. Job status not updated.");
                     break;
             }
         }
         
         System.out.println("Do you want to update the required skills? (y/n): ");
		    String updateSkills = scanner.nextLine();
		    Set<String> newRequiredSkills = new HashSet<>();
		    if (updateSkills.equalsIgnoreCase("y")) {
		        System.out.println("Enter new skills (comma-separated):");
		        String skillsInput = scanner.nextLine();
		        if (!skillsInput.isEmpty()) {
		            String[] skillsArray = skillsInput.split(",");
		            for (String skill : skillsArray) {
		                newRequiredSkills.add(skill.trim());
		            }
		        }
		    } else {
		        newRequiredSkills = null; // Pass null if no update is to be made
		        System.out.println("Skills remain unchanged.");
		    }
		    
         boolean success = jobService.updateJobDetails(employerId, jobId, newTitle.isEmpty() ? null : newTitle,
                 newDescription.isEmpty() ? null : newDescription, newLocation, newSalary.isEmpty() ? null : newSalary,
                 newJobType, newJobStatus, newIndustry, newExperienceYears, newRequiredSkills);
         if (success) {
             System.out.println("Job details updated successfully!");
         } else {
             System.out.println("Failed to update job. Please check the Job ID or your authorization.");
         }
     }

     public static void updateEmployerProfile(Scanner scanner, User loggedInUser, ProfileUpdateService profileUpdateService) {
    	    System.out.print("Enter new company description (or press Enter to keep current): ");
    	    String newCompanyDescription = scanner.nextLine();
    	    
    	    System.out.print("Enter new company website (or press Enter to keep current): ");
    	    String newWebsite = scanner.nextLine();
    	    
    	    System.out.println("Select new company location (or press Enter to keep current): ");
    	    for (int i = 0; i < Location.values().length; i++) {
    	        System.out.println((i + 1) + ". " + Location.values()[i]);
    	    }
    	    System.out.print("Enter the number for the new location: ");
    	    String newLocationChoice = scanner.nextLine();
    	    String newLocation = null;
    	    
    	    if (!newLocationChoice.isEmpty()) {
    	        try {
    	            int locationChoice = Integer.parseInt(newLocationChoice);
    	            if (locationChoice >= 1 && locationChoice <= Location.values().length) {
    	                newLocation = Location.values()[locationChoice - 1].name();
    	            } else {
    	                System.out.println("Invalid location. Location not updated.");
    	                newLocation = loggedInUser.getLocation();
    	            }
    	        } catch (NumberFormatException e) {
    	            System.out.println("Invalid input. Location not updated.");
    	            newLocation = loggedInUser.getLocation();
    	        }
    	    } else {
    	        newLocation = loggedInUser.getLocation();
    	    }

    	    profileUpdateService.updateEmployerProfile(loggedInUser, newCompanyDescription, newWebsite, newLocation);
    	}



}