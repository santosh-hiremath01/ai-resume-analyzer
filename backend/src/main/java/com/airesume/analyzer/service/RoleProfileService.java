package com.airesume.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleProfileService {

    private final Map<String, List<String>> roleSkills = new LinkedHashMap<>();

    public RoleProfileService() {

        // =====================================================
        // JAVA DEVELOPER
        // =====================================================

        roleSkills.put("Java Developer", Arrays.asList(
                "Java",
                "OOP",
                "Collections",
                "Exception Handling",
                "Multithreading",
                "Spring",
                "Spring Boot",
                "Hibernate",
                "JPA",
                "JDBC",
                "REST API",
                "SQL",
                "MySQL",
                "Maven",
                "Git",
                "JUnit"
        ));

        // =====================================================
        // PYTHON DEVELOPER
        // =====================================================

        roleSkills.put("Python Developer", Arrays.asList(
                "Python",
                "OOP",
                "Django",
                "Flask",
                "FastAPI",
                "REST API",
                "SQL",
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "Git",
                "Docker",
                "Pytest"
        ));

        // =====================================================
        // FULL STACK DEVELOPER
        // =====================================================

        roleSkills.put("Full Stack Developer", Arrays.asList(
                "HTML",
                "CSS",
                "JavaScript",
                "React",
                "Angular",
                "Node.js",
                "Express.js",
                "Java",
                "Python",
                "Spring Boot",
                "REST API",
                "SQL",
                "MySQL",
                "MongoDB",
                "Git",
                "GitHub",
                "Docker"
        ));

        // =====================================================
        // FRONTEND DEVELOPER
        // =====================================================

        roleSkills.put("Frontend Developer", Arrays.asList(
                "HTML",
                "CSS",
                "JavaScript",
                "TypeScript",
                "React",
                "Angular",
                "Vue.js",
                "Responsive Design",
                "REST API",
                "Git",
                "GitHub"
        ));

        // =====================================================
        // BACKEND DEVELOPER
        // =====================================================

        roleSkills.put("Backend Developer", Arrays.asList(
                "Java",
                "Python",
                "Node.js",
                "Spring Boot",
                "Django",
                "Flask",
                "Express.js",
                "REST API",
                "Microservices",
                "SQL",
                "MySQL",
                "PostgreSQL",
                "MongoDB",
                "Git",
                "Docker"
        ));

        // =====================================================
        // DATA ANALYST
        // =====================================================

        roleSkills.put("Data Analyst", Arrays.asList(
                "SQL",
                "Python",
                "Excel",
                "Power BI",
                "Tableau",
                "Pandas",
                "NumPy",
                "Matplotlib",
                "Statistics",
                "Data Analysis",
                "Data Visualization",
                "Data Cleaning",
                "MySQL"
        ));

        // =====================================================
        // DATA SCIENTIST
        // =====================================================

        roleSkills.put("Data Scientist", Arrays.asList(
                "Python",
                "SQL",
                "Pandas",
                "NumPy",
                "Scikit-learn",
                "Machine Learning",
                "Deep Learning",
                "Statistics",
                "Data Analysis",
                "Data Visualization",
                "TensorFlow",
                "PyTorch",
                "Matplotlib"
        ));

        // =====================================================
        // DATA ENGINEER
        // =====================================================

        roleSkills.put("Data Engineer", Arrays.asList(
                "Python",
                "SQL",
                "ETL",
                "Data Pipelines",
                "Apache Spark",
                "Hadoop",
                "Kafka",
                "Airflow",
                "AWS",
                "Azure",
                "Docker",
                "PostgreSQL",
                "MySQL"
        ));

        // =====================================================
        // MACHINE LEARNING ENGINEER
        // =====================================================

        roleSkills.put("Machine Learning Engineer", Arrays.asList(
                "Python",
                "Machine Learning",
                "Deep Learning",
                "Scikit-learn",
                "TensorFlow",
                "PyTorch",
                "Pandas",
                "NumPy",
                "Statistics",
                "NLP",
                "Computer Vision",
                "Model Deployment",
                "Docker",
                "Git"
        ));

        // =====================================================
        // AI ENGINEER
        // =====================================================

        roleSkills.put("AI Engineer", Arrays.asList(
                "Python",
                "Machine Learning",
                "Deep Learning",
                "Artificial Intelligence",
                "TensorFlow",
                "PyTorch",
                "NLP",
                "Computer Vision",
                "Generative AI",
                "LLM",
                "Transformers",
                "Docker",
                "Git"
        ));

        // =====================================================
        // DEVOPS ENGINEER
        // =====================================================

        roleSkills.put("DevOps Engineer", Arrays.asList(
                "Linux",
                "Git",
                "GitHub",
                "Docker",
                "Kubernetes",
                "Jenkins",
                "CI/CD",
                "AWS",
                "Azure",
                "Terraform",
                "Ansible",
                "Shell Scripting",
                "Monitoring",
                "Prometheus",
                "Grafana"
        ));

        // =====================================================
        // CLOUD ENGINEER
        // =====================================================

        roleSkills.put("Cloud Engineer", Arrays.asList(
                "AWS",
                "Azure",
                "Google Cloud",
                "Cloud Computing",
                "Linux",
                "Docker",
                "Kubernetes",
                "Terraform",
                "Networking",
                "Security",
                "CI/CD",
                "Git"
        ));

        // =====================================================
        // CYBERSECURITY ANALYST
        // =====================================================

        roleSkills.put("Cybersecurity Analyst", Arrays.asList(
                "Cybersecurity",
                "Network Security",
                "Information Security",
                "Linux",
                "Firewalls",
                "SIEM",
                "SOC",
                "Incident Response",
                "Vulnerability Assessment",
                "Penetration Testing",
                "Python",
                "Cryptography"
        ));

        // =====================================================
        // QA / TEST AUTOMATION ENGINEER
        // =====================================================

        roleSkills.put("QA Engineer", Arrays.asList(
                "Manual Testing",
                "Automation Testing",
                "Selenium",
                "Java",
                "Python",
                "JUnit",
                "TestNG",
                "API Testing",
                "REST API",
                "Postman",
                "SQL",
                "Git",
                "Jenkins"
        ));
    }


    // =========================================================
    // GET ALL ROLES
    // =========================================================

    public List<String> getAvailableRoles() {

        return new ArrayList<>(roleSkills.keySet());
    }


    // =========================================================
    // GET SKILLS FOR A PARTICULAR ROLE
    // =========================================================

    public List<String> getSkillsForRole(String role) {

        if (role == null || role.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return roleSkills.getOrDefault(
                role,
                new ArrayList<>()
        );
    }


    // =========================================================
    // CHECK WHETHER ROLE EXISTS
    // =========================================================

    public boolean isValidRole(String role) {

        return role != null &&
                roleSkills.containsKey(role);
    }
}