package com.jobfinder.utils;

import com.jobfinder.model.Interview;
import com.jobfinder.model.Job;
import com.jobfinder.model.JobApplication;
import com.jobfinder.model.JobApplication.ApplicationStatus;
import com.jobfinder.model.User;
import com.jobfinder.main.enums.Industry;
import com.jobfinder.main.enums.JobStatus;
import com.jobfinder.main.enums.JobType;
import com.jobfinder.main.enums.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsonFileHandler {

    private static final String JOB_FILE_PATH = "jobs.json";
    private static final String USER_FILE_PATH = "users.json";
    private static final String APPLICATION_FILE_PATH = "job_applications.json";

    public static List<User> loadUsersFromFile(String filePath) {
        List<User> userList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return userList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int userId = obj.getInt("userId");
                String name = obj.getString("name");
                String email = obj.getString("email");
                String password = obj.getString("password");
                String role = obj.getString("role");

                if ("job_seeker".equalsIgnoreCase(role)) {
                	String location = obj.optString("location", null);
                    String previousJobTitle = obj.optString("previousJobTitle", null);
                    String previousSalary = obj.optString("previousSalary", null);
                    String highestEducation = obj.optString("highestEducation", null);
                    User jobSeeker = new User(userId, name, email, password, location, previousJobTitle, previousSalary, highestEducation);
                    jobSeeker.setResume(obj.optString("resume", null));
                    jobSeeker.setCoverLetter(obj.optString("coverLetter", null));
                    userList.add(jobSeeker);
                } else if ("employer".equalsIgnoreCase(role)) {
                	String location = obj.optString("location", null);
                    String companyDescription = obj.optString("companyDescription", null);
                    String website = obj.optString("website", null);
                    User employer = new User(userId, name, email, password, location, companyDescription, website);
                    userList.add(employer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        return userList;
    }

    public static boolean saveUsersToFile(List<User> users, String filePath) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("userId", user.getUserId());
            userJson.put("name", user.getName());
            userJson.put("email", user.getEmail());
            userJson.put("password", user.getPassword());
            userJson.put("role", user.getRole());

            if ("job_seeker".equalsIgnoreCase(user.getRole())) {
            	if (user.getLocation() != null) {
                    userJson.put("location", user.getLocation());
                }
                if (user.getPreviousJobTitle() != null) {
                    userJson.put("previousJobTitle", user.getPreviousJobTitle());
                }
                if (user.getPreviousSalary() != null) {
                    userJson.put("previousSalary", user.getPreviousSalary());
                }
                if (user.getHighestEducation() != null) {
                    userJson.put("highestEducation", user.getHighestEducation());
                }
                if (user.getResume() != null) {
                    userJson.put("resume", user.getResume());
                }
                if (user.getCoverLetter() != null) {
                    userJson.put("coverLetter", user.getCoverLetter());
                }
            }

            if ("employer".equalsIgnoreCase(user.getRole())) {
                if (user.getLocation() != null) {
                    userJson.put("location", user.getLocation());
                }
                if (user.getCompanyDescription() != null) {
                    userJson.put("companyDescription", user.getCompanyDescription());
                }
                if (user.getWebsite() != null) {
                    userJson.put("website", user.getWebsite());
                }
            }

            jsonArray.put(userJson);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static List<Job> loadJobsFromFile(String filePath) {
        List<Job> jobList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return jobList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int jobId = obj.getInt("jobId");
                int employerId = obj.getInt("employerId");
                String companyName = obj.getString("companyName");
                String title = obj.getString("title");
                String description = obj.getString("description");
                Location location = Location.valueOf(obj.getString("location").toUpperCase());                String salary = obj.optString("salary", null);

                JobStatus jobStatus = JobStatus.valueOf(obj.getString("jobStatus").toUpperCase());
                JobType jobType = JobType.valueOf(obj.getString("jobType").toUpperCase());
                Industry industry = Industry.valueOf(obj.getString("industry").toUpperCase());
                
                DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
                LocalDate postedDate = LocalDate.parse(obj.getString("postedDate"), dateFormatter);
                
           
                int requiredExperienceYears = obj.getInt("requiredExperienceYears");
                JSONArray skillsArray = obj.getJSONArray("requiredSkills");
                Set<String> requiredSkills = new HashSet<>();
                for (int j = 0; j < skillsArray.length(); j++) {
                    requiredSkills.add(skillsArray.getString(j));
                }

                jobList.add(new Job(jobId, employerId, companyName, title, description, location, salary, jobStatus, jobType,industry, requiredExperienceYears, postedDate, requiredSkills));
            }
        } catch (IOException e) {
            System.err.println("Error loading jobs from file: " + e.getMessage());
        }

        return jobList;
    }

    public static void saveJobsToFile(List<Job> jobs, String filePath) {
        JSONArray jsonArray = new JSONArray();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
        for (Job job : jobs) {
            JSONObject jobJson = new JSONObject();
            jobJson.put("jobId", job.getJobId());
            jobJson.put("employerId", job.getEmployerId());
            jobJson.put("companyName", job.getCompanyName());
            jobJson.put("title", job.getTitle());
            jobJson.put("description", job.getDescription());
            jobJson.put("location", job.getLocation().name());
            jobJson.put("industry", job.getIndustry().name());
            jobJson.put("salary", job.getSalary());
            jobJson.put("requiredExperienceYears", job.getRequiredExperienceYears());
            jobJson.put("jobStatus", job.getJobStatus().name());
            jobJson.put("jobType", job.getJobType().name());
            jobJson.put("postedDate", job.getPostedDate().format(dateFormatter));
            jobJson.put("requiredExperienceYears", job.getRequiredExperienceYears());
            JSONArray skillsArray = new JSONArray();
			if (job.getRequiredSkills() != null && !job.getRequiredSkills().isEmpty()) {
				
				for (String skill : job.getRequiredSkills()) {
					skillsArray.put(skill);
				}
			}
				jobJson.put("requiredSkills", skillsArray);
				jsonArray.put(jobJson);
			
		}

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            System.err.println("Error saving jobs to file: " + e.getMessage());
        }
    }

	 
    public static List<JobApplication> loadApplicationsFromFile(String filePath) {
        List<JobApplication> applicationList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return applicationList;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int jobId = obj.getInt("jobId");
                int userId = obj.getInt("userId");
                String resumePath = obj.getString("resumePath");
                String coverLetterPath = obj.getString("coverLetterPath");
                int applicantExperienceYears = obj.getInt("applicantExperienceYears");
                Set<String> applicantSkills = new HashSet<>();
                JSONArray skillsArray = obj.getJSONArray("applicantSkills");
                for (int j = 0; j < skillsArray.length(); j++) {
                    applicantSkills.add(skillsArray.getString(j));
                }
                ApplicationStatus status = ApplicationStatus.valueOf(obj.getString("status"));
                boolean updated = obj.optBoolean("updated", false); // Default to false if not specified

                JobApplication application = new JobApplication(jobId, userId, resumePath, coverLetterPath,
                    applicantExperienceYears, applicantSkills, status, updated);
                applicationList.add(application);
            }
        } catch (IOException e) {
            System.err.println("Error loading job applications: " + e.getMessage());
        }

        return applicationList;
    }


    public static void saveApplicationsToFile(List<JobApplication> applications, String filePath) {
        JSONArray jsonArray = new JSONArray();
        for (JobApplication app : applications) {
            JSONObject appJson = new JSONObject();
            appJson.put("jobId", app.getJobId());
            appJson.put("userId", app.getUserId());
            appJson.put("coverLetterPath", app.getCoverLetterPath());
            appJson.put("resumePath", app.getResumePath());
            appJson.put("applicantExperienceYears", app.getApplicantExperienceYears());
            JSONArray skillsArray = new JSONArray(app.getApplicantSkills());
            appJson.put("applicantSkills", skillsArray);
            appJson.put("status", app.getStatus().toString());
            appJson.put("updated", app.isUpdated()); // Save the 'updated' status
            jsonArray.put(appJson);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Indentation for readability
        } catch (IOException e) {
            System.err.println("Error saving job applications: " + e.getMessage());
        }
    }

    public static List<Interview> loadInterviewsFromFile(String filePath) {
        List<Interview> interviewList = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return interviewList; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONArray jsonArray = new JSONArray(content.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Interview interview = new Interview(
                    obj.getInt("interviewId"),
                    obj.getInt("jobId"),
                    obj.getInt("jobSeekerId"),
                    obj.getString("date"),
                    obj.getString("time"),
                    obj.getString("meetingLink"),
                    obj.getString("details"),
                    obj.getString("status")
                );
                interviewList.add(interview);
            }
        } catch (IOException e) {
            System.err.println("Error loading interviews: " + e.getMessage());
        }

        return interviewList;
    }
    public static void saveInterviewsToFile(List<Interview> interviews, String filePath) {
        JSONArray jsonArray = new JSONArray();
        for (Interview interview : interviews) {
            JSONObject obj = new JSONObject();
            obj.put("interviewId", interview.getInterviewId());
            obj.put("jobId", interview.getJobId());
            obj.put("jobSeekerId", interview.getJobSeekerId());
            obj.put("date", interview.getDate());
            obj.put("time", interview.getTime());
            obj.put("meetingLink", interview.getMeetingLink());
            obj.put("details", interview.getDetails());
            obj.put("status", interview.getStatus());
            jsonArray.put(obj);
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(4)); // Pretty print with 4 spaces
        } catch (IOException e) {
            System.err.println("Error saving interviews: " + e.getMessage());
        }
    }
}