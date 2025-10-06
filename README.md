# Job Finder Application 💼

<div align="center">

**Terminal-based job management system with comprehensive testing and Agile methodology**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat&logo=openjdk)](https://www.oracle.com/java/)
[![JUnit5](https://img.shields.io/badge/JUnit5-86%25_Coverage-25A162?style=flat&logo=junit5)](https://junit.org/junit5/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apache-maven)](https://maven.apache.org/)


</div>

---

## 📖 Project Overview

The Job Finder Application is a terminal-based system that facilitates job search and recruitment processes. Built entirely in Java, the application serves two primary user types:

- **Job Seekers** - Browse job listings, submit applications, track application status, and manage interview schedules
- **Employers** - Post job openings, review applications, schedule interviews, and manage recruitment workflows

The project emphasizes **software quality assurance** with comprehensive testing strategies achieving **86% test coverage**, and demonstrates practical application of **Agile methodologies** including sprint planning, user story estimation, and project tracking.

---

## ✨ Features

### Core Functionality
- 🔐 **User Authentication** - Secure login for job seekers and employers
- 📝 **Job Management** - Create, view, update, and delete job postings
- 📋 **Application Tracking** - Track job applications with status updates
- 📅 **Interview Scheduling** - Schedule and manage interviews
- 📁 **File Upload** - Attach resumes and documents
- 💾 **JSON Persistence** - Store data in JSON files

---

## 🎯 Key Highlights

- **Object-Oriented Design**: Modular architecture following SOLID principles with clear separation of concerns
- **Comprehensive Testing**: 86% overall test coverage using JUnit 5, implementing both Black-Box and White-Box testing strategies
- **Agile Methodology**: Three two-week sprints with Planning Poker estimation, PERT diagrams for critical path analysis, and COCOMO cost estimation
- **JSON Persistence**: Lightweight data storage using JSON files for user data, job postings, and application records
- **Clean Architecture**: Well-structured codebase with distinct layers for presentation, business logic, and data access


---

## 🛠️ Tech Stack

- **Java 17** - Core programming language
- **JUnit 5** - Testing framework
- **Maven** - Build & dependency management
- **JSON** - Data persistence
- **OOP Design Patterns** - Singleton, Factory, Strategy

---

### 📊 Project Management Approach

**Agile Implementation:**
- **Sprint Planning**: 3 sprints × 2 weeks each
- **User Story Estimation**: Planning Poker technique for story point assignment
- **Project Tracking**: Burndown charts to monitor progress
- **PERT Analysis**: Critical path identification for task dependencies
- **COCOMO Model**: Effort and cost estimation for project planning

**Testing Metrics:**
- Total Test Cases: 150+
- Test Coverage: 86%
- Defect Detection Rate: 95%
- Code Review Coverage: 100%

---

## 🏗️ Project Structure
```bash
job-finder/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/jobfinder/
│   │           ├── main/          
│   │           ├── model/         
│   │           ├── services/     
│   │           ├── utils/        
│   │           └── tests/
│   │               ├── Blackbox/  # Black-box tests
│   │               └── Whitebox/  # White-box tests
│   └── test/
│       └── resources/
├── data/
│   ├── job_seeker.json
│   ├── employer.json
│   ├── jobs.json
│   └── last_ids.json
├── pom.xml
└── README.md
```
---

## 🚀 Getting Started

### Prerequisites
```bash
- JDK 17 or higher
- Maven 3.8+
- Eclipse IDE (or IntelliJ IDEA)
```

### Installation
1. Clone the Repository
```bash
git clone https://github.com/your-username/job-finder.git
cd job-finder
```

2. Import into Eclipse

- Open Eclipse IDE
File → Import → Maven → Existing Maven Projects
- Browse to the cloned folder and select it
- Click Finish

3. Build the Project
```bash
mvn clean install
```

5. Run the Application
Option A: Eclipse

- Right-click Application.java
- Run As → Java Application

Option B: Terminal
```bash
mvn exec:java -Dexec.mainClass="com.jobfinder.main.Application"
```

## 🧪 Testing
Test Coverage: 86%
#### Comprehensive testing strategy using:

- ✅ Black-Box Testing - Equivalence partitioning, boundary value analysis
- ✅ White-Box Testing - Statement coverage, branch coverage, path coverage
- ✅ Decision Table Testing - Complex logic validation
- ✅ JUnit 5 - Modern testing framework

#### Running Tests

#### Run All Tests
```bash
mvn test
```
#### Run Specific Test Package
1. Black-box tests only
```bash
mvn test -Dtest="com.jobfinder.tests.Blackbox.*"
```

2. White-box tests only
```bash
mvn test -Dtest="com.jobfinder.tests.Whitebox.*"
```
#### In Eclipse

Right-click on test package → Run As → JUnit Test

#### Test Reports
After running tests, view coverage report:
#### Generate coverage report
```bash
mvn jacoco:report
```


## 👤 Author

**Aishwarya Gitay**

- 💼 [LinkedIn](https://www.linkedin.com/in/aishwarya-luniya-gitay/)
- 📧 Email: aishwaryagitay@gmail.com
- 🐙 GitHub: [@AishwaryaGitay](https://github.com/AishwaryaGitay)

---

<div align="center">

**⭐ Star this repo if you found it helpful!**

Made with ☕ and 💻 by Aishwarya Gitay

</div>
