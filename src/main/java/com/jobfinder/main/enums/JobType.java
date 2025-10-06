package com.jobfinder.main.enums;

public enum JobType {
    FULL_TIME("Full-time position"),
    PART_TIME("Part-time position"),
    CONTRACT("Contract position"),
    INTERNSHIP("Internship position");

    private final String description;

    JobType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
