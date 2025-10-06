package com.jobfinder.model;

public class User {

    private int userId;        
    private String name;       
    private String email;       
    private String password;   
    private String role;      
    private String location;    
    private String resume;     
    private String coverLetter; 
    private String companyDescription;  
    private String website;            
    private String previousJobTitle; 
    private String previousSalary;   
    private String highestEducation;  
    public User() {
        this.userId = 1;
        this.name = "";
        this.email = "";
        this.password = "";
        this.role = "";
        this.location = null;
        this.resume = null;
        this.coverLetter = null;
        this.companyDescription = null;
        this.website = null;
        this.previousJobTitle = null;
        this.previousSalary = null;
        this.highestEducation = null;
    }


    public User(int userId, String name, String email, String password, String location, String previousJobTitle, String previousSalary, String highestEducation) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "job_seeker";
        this.location = location;
        this.previousJobTitle = previousJobTitle;
        this.previousSalary = previousSalary;
        this.highestEducation = highestEducation;
    }


    public User(int userId, String name, String email, String password, String location, String companyDescription, String website) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "employer";
        this.location = location;
        this.companyDescription = companyDescription;
        this.website = website;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getLocation() {
        return location;
    }

    public String getResume() {
        return resume;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getCompanyDescription() {
		return companyDescription;
	}


	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}


	public String getWebsite() {
		return website;
	}


	public void setWebsite(String website) {
		this.website = website;
	}

	

	public String getPreviousJobTitle() {
		return previousJobTitle;
	}


	public void setPreviousJobTitle(String previousJobTitle) {
		this.previousJobTitle = previousJobTitle;
	}


	public String getPreviousSalary() {
		return previousSalary;
	}
 

	public void setPreviousSalary(String previousSalary) {
		this.previousSalary = previousSalary;
	}


	public String getHighestEducation() {
		return highestEducation;
	}


	public void setHighestEducation(String highestEducation) {
		this.highestEducation = highestEducation;
	}
	
	@Override
	public String toString() {
	    return "User{" +
	            "userId=" + userId +
	            ", name='" + name + '\'' +
	            ", email='" + email + '\'' +
	            ", role='" + role + '\'' +
	            (location != null ? ", location='" + location + '\'' : "") +
	            (resume != null ? ", resume='" + resume + '\'' : "") +
	            (coverLetter != null ? ", coverLetter='" + coverLetter + '\'' : "") +
	            (companyDescription != null ? ", companyDescription='" + companyDescription + '\'' : "") +
	            (website != null ? ", website='" + website + '\'' : "") +
	            (previousJobTitle != null ? ", previousJobTitle='" + previousJobTitle + '\'' : "") +
	            (previousSalary != null ? ", previousSalary='" + previousSalary + '\'' : "") +
	            (highestEducation != null ? ", highestEducation='" + highestEducation + '\'' : "") +
	            '}';
	}
}
