package com.airesume.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillAnalysisService {

    private final List<String> technicalSkills = Arrays.asList(

            // Programming Languages
            "Java",
            "Python",
            "JavaScript",
            "C",
            "C++",

            // Backend
            "Spring Boot",
            "Spring",
            "Hibernate",
            "JDBC",
            "REST API",
            "REST APIs",
            "Node.js",
            "Express.js",

            // Frontend
            "HTML",
            "CSS",
            "React",
            "React.js",
            "Angular",

            // Databases
            "MySQL",
            "MongoDB",
            "SQL",

            // Tools
            "Git",
            "GitHub",
            "Docker",
            "Maven",

            // Cloud
            "AWS",
            "Azure",
            "Google Cloud",

            // Other
            "OOP",
            "Data Structures",
            "Algorithms"
    );

    public List<String> analyzeSkills(String resumeText) {

        List<String> detectedSkills = new ArrayList<>();

        String text = resumeText.toLowerCase();

        for (String skill : technicalSkills) {

            if (text.contains(skill.toLowerCase())) {

                detectedSkills.add(skill);
            }
        }

        return detectedSkills;
    }
}