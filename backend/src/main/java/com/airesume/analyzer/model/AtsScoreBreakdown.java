package com.airesume.analyzer.model;

public class AtsScoreBreakdown {

    private int keywordScore;
    private int skillScore;
    private int sectionScore;
    private int contactScore;
    private int contentQualityScore;

    public AtsScoreBreakdown() {
    }

    public AtsScoreBreakdown(
            int keywordScore,
            int skillScore,
            int sectionScore,
            int contactScore,
            int contentQualityScore) {

        this.keywordScore = keywordScore;
        this.skillScore = skillScore;
        this.sectionScore = sectionScore;
        this.contactScore = contactScore;
        this.contentQualityScore = contentQualityScore;
    }

    public int getKeywordScore() {
        return keywordScore;
    }

    public void setKeywordScore(int keywordScore) {
        this.keywordScore = keywordScore;
    }

    public int getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(int skillScore) {
        this.skillScore = skillScore;
    }

    public int getSectionScore() {
        return sectionScore;
    }

    public void setSectionScore(int sectionScore) {
        this.sectionScore = sectionScore;
    }

    public int getContactScore() {
        return contactScore;
    }

    public void setContactScore(int contactScore) {
        this.contactScore = contactScore;
    }

    public int getContentQualityScore() {
        return contentQualityScore;
    }

    public void setContentQualityScore(int contentQualityScore) {
        this.contentQualityScore = contentQualityScore;
    }
}