package com.airesume.analyzer.service;

import com.airesume.analyzer.model.AtsScoreBreakdown;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AtsScoreService {

    public AtsScoreBreakdown calculateBreakdown(
            Map<String, Boolean> sections,
            int matchedKeywordCount,
            int totalJobKeywordCount,
            int detectedSkillCount,
            String resumeText) {

        /*
         * =====================================================
         * 1. KEYWORD SCORE - 40 POINTS
         * =====================================================
         */

        double keywordScore = 0;

        if (totalJobKeywordCount > 0) {

            keywordScore =
                    ((double) matchedKeywordCount
                            / totalJobKeywordCount) * 40;
        }

        /*
         * =====================================================
         * 2. SKILL SCORE - 25 POINTS
         * =====================================================
         */

        double skillScore = 0;

        if (detectedSkillCount >= 10) {

            skillScore = 25;

        } else {

            skillScore =
                    ((double) detectedSkillCount / 10) * 25;
        }

        /*
         * =====================================================
         * 3. SECTION SCORE - 20 POINTS
         * =====================================================
         */

        int foundSections = 0;

        if (sections != null) {

            for (Boolean found : sections.values()) {

                if (Boolean.TRUE.equals(found)) {
                    foundSections++;
                }
            }
        }

        double sectionScore = 0;

        if (sections != null &&
                !sections.isEmpty()) {

            sectionScore =
                    ((double) foundSections
                            / sections.size()) * 20;
        }

        /*
         * =====================================================
         * 4. CONTACT INFORMATION - 10 POINTS
         * =====================================================
         */

        String text =
                resumeText == null
                        ? ""
                        : resumeText.toLowerCase();

        boolean hasEmail =
                text.contains("@");

        boolean hasPhone =
                text.matches("(?s).*\\b\\d{10}\\b.*");

        double contactScore = 0;

        if (hasEmail) {
            contactScore += 5;
        }

        if (hasPhone) {
            contactScore += 5;
        }

        /*
         * =====================================================
         * 5. CONTENT QUALITY - 5 POINTS
         * =====================================================
         */

        double contentQualityScore = 0;

        int textLength =
                resumeText == null
                        ? 0
                        : resumeText.length();

        if (textLength >= 500) {

            contentQualityScore = 5;

        } else if (textLength >= 250) {

            contentQualityScore = 3;

        } else if (textLength >= 100) {

            contentQualityScore = 1;
        }

        /*
         * =====================================================
         * ROUND EACH CATEGORY
         * =====================================================
         */

        return new AtsScoreBreakdown(

                (int) Math.round(keywordScore),

                (int) Math.round(skillScore),

                (int) Math.round(sectionScore),

                (int) Math.round(contactScore),

                (int) Math.round(contentQualityScore)
        );
    }

    /*
     * =========================================================
     * FINAL ATS SCORE
     * =========================================================
     */

    public int calculateScore(
            Map<String, Boolean> sections,
            int matchedKeywordCount,
            int totalJobKeywordCount,
            int detectedSkillCount,
            String resumeText) {

        AtsScoreBreakdown breakdown =
                calculateBreakdown(
                        sections,
                        matchedKeywordCount,
                        totalJobKeywordCount,
                        detectedSkillCount,
                        resumeText
                );

        int total =
                breakdown.getKeywordScore()
                        + breakdown.getSkillScore()
                        + breakdown.getSectionScore()
                        + breakdown.getContactScore()
                        + breakdown.getContentQualityScore();

        /*
         * Safety: keep score between 0 and 100.
         */

        return Math.max(
                0,
                Math.min(100, total)
        );
    }
}