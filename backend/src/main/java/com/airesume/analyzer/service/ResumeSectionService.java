package com.airesume.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ResumeSectionService {

    public Map<String, Boolean> analyzeSections(String resumeText) {

        Map<String, Boolean> sections = new LinkedHashMap<>();

        String text = resumeText.toLowerCase();

        sections.put(
                "Skills",
                containsAny(text, "skills", "technical skills", "technologies")
        );

        sections.put(
                "Education",
                containsAny(text, "education", "academic background", "qualification")
        );

        sections.put(
                "Experience",
                containsAny(
                        text,
                        "experience",
                        "work experience",
                        "professional experience"
                )
        );

        sections.put(
                "Projects",
                containsAny(text, "projects", "project experience")
        );

        sections.put(
                "Certifications",
                containsAny(text, "certifications", "certificates", "certification")
        );

        sections.put(
                "Achievements",
                containsAny(text, "achievements", "awards", "accomplishments")
        );

        return sections;
    }

    private boolean containsAny(String text, String... keywords) {

        for (String keyword : keywords) {

            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}