package com.airesume.analyzer.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {

    private String fileName;

    private int atsScore;

    private Map<String, Boolean> sections;

    private List<String> skills;

    private List<String> matchedKeywords;

    private List<String> missingKeywords;

    private AtsScoreBreakdown scoreBreakdown;

    private List<String> recommendations;

    public AnalysisResult() {
    }

    public AnalysisResult(
            String fileName,
            int atsScore,
            Map<String, Boolean> sections,
            List<String> skills,
            List<String> matchedKeywords,
            List<String> missingKeywords,
            AtsScoreBreakdown scoreBreakdown,
            List<String> recommendations) {

        this.fileName = fileName;
        this.atsScore = atsScore;
        this.sections = sections;
        this.skills = skills;
        this.matchedKeywords = matchedKeywords;
        this.missingKeywords = missingKeywords;
        this.scoreBreakdown = scoreBreakdown;
        this.recommendations = recommendations;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public Map<String, Boolean> getSections() {
        return sections;
    }

    public void setSections(
            Map<String, Boolean> sections) {

        this.sections = sections;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(
            List<String> skills) {

        this.skills = skills;
    }

    public List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    public void setMatchedKeywords(
            List<String> matchedKeywords) {

        this.matchedKeywords = matchedKeywords;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(
            List<String> missingKeywords) {

        this.missingKeywords = missingKeywords;
    }

    public AtsScoreBreakdown getScoreBreakdown() {
        return scoreBreakdown;
    }

    public void setScoreBreakdown(
            AtsScoreBreakdown scoreBreakdown) {

        this.scoreBreakdown = scoreBreakdown;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<String> recommendations) {

        this.recommendations = recommendations;
    }
}