package com.jobfinder.utils;

public class JobPortalFAQ {

    public static String getFAQ() {
        StringBuilder faqBuilder = new StringBuilder();
        faqBuilder.append("========== Job Portal FAQ ==========\n");
        faqBuilder.append("\n1. How do I create an account?\n");
        faqBuilder.append("   Select the 'Register' option from the main menu, enter your details, and follow the instructions to complete your registration.\n");

        faqBuilder.append("\n2. How do I apply for a job?\n");
        faqBuilder.append("   After logging in as a job seeker, choose the option to view available jobs. Note the Job ID, then use the 'Apply for a Job' option and provide the Job ID to submit your application.\n");

        faqBuilder.append("\n3. Can I update my profile information?\n");
        faqBuilder.append("   Yes, select the 'Update Profile' option in the menu after logging in. Follow the prompts to update your details.\n");

        faqBuilder.append("\n4. How can I reset my password?\n");
        faqBuilder.append("   From the main menu, choose the 'Forgot Password' option. Enter your registered email and follow the instructions.\n");

        faqBuilder.append("\n5. Is there any fee for applying to jobs?\n");
        faqBuilder.append("   No, applying to jobs using this portal is completely free.\n");

        faqBuilder.append("\n6. How do I contact customer support?\n");
        faqBuilder.append("   Support is not available directly in this terminal application. For assistance, please email support@jobportal.com.\n");

        faqBuilder.append("\n7. How do I search for jobs matching my skills?\n");
        faqBuilder.append("   Use the 'Filter or Search Jobs' option in the job seeker menu to find jobs based on your criteria such as skills, location, or job type.\n");

        faqBuilder.append("\n8. How can I track my job applications?\n");
        faqBuilder.append("   Select the 'Track or Filter Application Statuses' option in the job seeker menu to view and filter your submitted applications.\n");

        faqBuilder.append("\n9. Can I upload multiple resumes?\n");
        faqBuilder.append("   This portal supports one active resume and cover letter at a time. To update, use the 'Upload Resume and Cover Letter' option.\n");

        faqBuilder.append("\n10. How do I delete my account?\n");
        faqBuilder.append("    Deleting accounts is not currently supported in this terminal application. Contact support@jobportal.com for assistance.\n");

        // Footer
        faqBuilder.append("\n=====================================\n");

        // Return the FAQ as a single string
        return faqBuilder.toString();
    }
}
