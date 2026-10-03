package com.airesume.analyzer.controller;

import com.airesume.analyzer.model.AnalysisResult;
import com.airesume.analyzer.model.AtsScoreBreakdown;
import com.airesume.analyzer.service.AtsScoreService;
import com.airesume.analyzer.service.KeywordAnalysisService;
import com.airesume.analyzer.service.PdfTextExtractionService;
import com.airesume.analyzer.service.ResumeSectionService;
import com.airesume.analyzer.service.RoleProfileService;
import com.airesume.analyzer.service.SkillAnalysisService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(
        origins = {
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "https://leafy-fudge-7f3836.netlify.app"
        }
)
@RequestMapping("/api/resumes")
public class ResumeController {

    private final PdfTextExtractionService pdfTextExtractionService;
    private final ResumeSectionService resumeSectionService;
    private final SkillAnalysisService skillAnalysisService;
    private final KeywordAnalysisService keywordAnalysisService;
    private final AtsScoreService atsScoreService;
    private final RoleProfileService roleProfileService;

    public ResumeController(
            PdfTextExtractionService pdfTextExtractionService,
            ResumeSectionService resumeSectionService,
            SkillAnalysisService skillAnalysisService,
            KeywordAnalysisService keywordAnalysisService,
            AtsScoreService atsScoreService,
            RoleProfileService roleProfileService) {

        this.pdfTextExtractionService = pdfTextExtractionService;
        this.resumeSectionService = resumeSectionService;
        this.skillAnalysisService = skillAnalysisService;
        this.keywordAnalysisService = keywordAnalysisService;
        this.atsScoreService = atsScoreService;
        this.roleProfileService = roleProfileService;
    }

    /*
     * =========================================================
     * GET AVAILABLE JOB ROLES
     * =========================================================
     */

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAvailableRoles() {

        return ResponseEntity.ok(
                roleProfileService.getAvailableRoles()
        );
    }

    /*
     * =========================================================
     * UPLOAD AND ANALYZE RESUME
     * =========================================================
     */

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("targetRole")
            String targetRole,

            @RequestParam("jobDescription")
            String jobDescription) {

        try {

            /*
             * =================================================
             * 1. VALIDATE FILE
             * =================================================
             */

            if (file == null || file.isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please upload a resume file.");
            }

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    !originalFileName
                            .toLowerCase()
                            .endsWith(".pdf")) {

                return ResponseEntity
                        .badRequest()
                        .body("Please upload a PDF resume.");
            }

            /*
             * =================================================
             * 2. VALIDATE TARGET ROLE
             * =================================================
             */

            if (targetRole == null ||
                    targetRole.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please select a target job role.");
            }

            if (!roleProfileService.isValidRole(
                    targetRole.trim())) {

                return ResponseEntity
                        .badRequest()
                        .body("Invalid target job role.");
            }

            /*
             * =================================================
             * 3. VALIDATE JOB DESCRIPTION
             * =================================================
             */

            if (jobDescription == null ||
                    jobDescription.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body("Please provide a job description.");
            }

            /*
             * =================================================
             * 4. EXTRACT TEXT FROM RESUME PDF
             * =================================================
             */

            String extractedText =
                    pdfTextExtractionService.extractText(file);

            if (extractedText == null ||
                    extractedText.trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Could not extract text from the resume PDF."
                        );
            }

            /*
             * =================================================
             * 5. ANALYZE RESUME SECTIONS
             * =================================================
             */

            Map<String, Boolean> sections =
                    resumeSectionService.analyzeSections(
                            extractedText
                    );

            /*
             * =================================================
             * 6. DETECT RESUME SKILLS
             * =================================================
             */

            List<String> skills =
                    skillAnalysisService.analyzeSkills(
                            extractedText
                    );

            if (skills == null) {
                skills = new ArrayList<>();
            }

            /*
             * =================================================
             * 7. ADD ROLE-SPECIFIC SKILLS
             *
             * Role profile is supplementary.
             * Job description remains the primary source.
             * =================================================
             */

            List<String> roleSkills =
                    roleProfileService.getSkillsForRole(
                            targetRole.trim()
                    );

            if (roleSkills != null) {

                String resumeLower =
                        extractedText.toLowerCase();

                for (String roleSkill : roleSkills) {

                    if (roleSkill == null ||
                            roleSkill.trim().isEmpty()) {

                        continue;
                    }

                    if (resumeLower.contains(
                            roleSkill.toLowerCase()
                    ) &&
                            !containsIgnoreCase(
                                    skills,
                                    roleSkill
                            )) {

                        skills.add(roleSkill);
                    }
                }
            }

            /*
             * =================================================
             * 8. ANALYZE JOB DESCRIPTION
             *
             * Extract:
             * - matched keywords
             * - missing keywords
             * - extracted keywords
             * =================================================
             */

            Map<String, List<String>> keywordAnalysis =
                    keywordAnalysisService.analyzeKeywords(
                            extractedText,
                            jobDescription
                    );

            List<String> matchedKeywords =
                    keywordAnalysis.get("matched");

            if (matchedKeywords == null) {

                matchedKeywords =
                        new ArrayList<>();
            }

            List<String> missingKeywords =
                    keywordAnalysis.get("missing");

            if (missingKeywords == null) {

                missingKeywords =
                        new ArrayList<>();
            }

            /*
             * =================================================
             * 9. CALCULATE TOTAL JD KEYWORDS
             * =================================================
             */

            int totalJobKeywordCount =
                    matchedKeywords.size()
                            + missingKeywords.size();

            /*
             * =================================================
             * 10. CALCULATE SCORE BREAKDOWN
             * =================================================
             */

            AtsScoreBreakdown scoreBreakdown =
                    atsScoreService.calculateBreakdown(
                            sections,
                            matchedKeywords.size(),
                            totalJobKeywordCount,
                            skills.size(),
                            extractedText
                    );

            /*
             * =================================================
             * 11. CALCULATE FINAL ATS SCORE
             * =================================================
             */

            int atsScore =
                    scoreBreakdown.getKeywordScore()
                            + scoreBreakdown.getSkillScore()
                            + scoreBreakdown.getSectionScore()
                            + scoreBreakdown.getContactScore()
                            + scoreBreakdown.getContentQualityScore();

            /*
             * Keep score safely between 0 and 100.
             */

            atsScore =
                    Math.max(
                            0,
                            Math.min(100, atsScore)
                    );

            /*
             * =================================================
             * 12. GENERATE RECOMMENDATIONS
             * =================================================
             */

            List<String> recommendations =
                    generateRecommendations(
                            missingKeywords,
                            sections,
                            skills,
                            extractedText,
                            scoreBreakdown
                    );

            /*
             * =================================================
             * 13. CREATE FINAL RESULT
             * =================================================
             */

            AnalysisResult result =
                    new AnalysisResult(
                            originalFileName,
                            atsScore,
                            sections,
                            skills,
                            matchedKeywords,
                            missingKeywords,
                            scoreBreakdown,
                            recommendations
                    );

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Error while processing resume: "
                                    + e.getMessage()
                    );
        }
    }

    /*
     * =========================================================
     * GENERATE RESUME RECOMMENDATIONS
     * =========================================================
     */

    private List<String> generateRecommendations(
            List<String> missingKeywords,
            Map<String, Boolean> sections,
            List<String> skills,
            String resumeText,
            AtsScoreBreakdown scoreBreakdown) {

        List<String> recommendations =
                new ArrayList<>();

        /*
         * -----------------------------------------------------
         * MISSING KEYWORDS
         * -----------------------------------------------------
         */

        if (missingKeywords != null &&
                !missingKeywords.isEmpty()) {

            int maximumSuggestions =
                    Math.min(
                            missingKeywords.size(),
                            5
                    );

            for (int i = 0;
                 i < maximumSuggestions;
                 i++) {

                String keyword =
                        missingKeywords.get(i);

                if (keyword == null ||
                        keyword.trim().isEmpty()) {

                    continue;
                }

                recommendations.add(
                        "Consider adding \""
                                + keyword
                                + "\" to your resume if you have relevant experience."
                );
            }
        }

        /*
         * -----------------------------------------------------
         * SECTION RECOMMENDATIONS
         * -----------------------------------------------------
         */

        if (sections != null) {

            Boolean projects =
                    sections.get("Projects");

            if (Boolean.FALSE.equals(projects)) {

                recommendations.add(
                        "Add a Projects section to demonstrate practical experience."
                );
            }

            Boolean skillsSection =
                    sections.get("Skills");

            if (Boolean.FALSE.equals(skillsSection)) {

                recommendations.add(
                        "Add a clearly labeled Skills section containing relevant technical skills."
                );
            }

            Boolean experience =
                    sections.get("Experience");

            if (Boolean.FALSE.equals(experience)) {

                recommendations.add(
                        "Add an Experience section if you have internship, work, or relevant practical experience."
                );

            }

            Boolean education =
                    sections.get("Education");

            if (Boolean.FALSE.equals(education)) {

                recommendations.add(
                        "Add an Education section with your degree and institution."
                );
            }
        }

        /*
         * -----------------------------------------------------
         * SKILL RECOMMENDATION
         * -----------------------------------------------------
         */

        if (skills == null ||
                skills.size() < 5) {

            recommendations.add(
                    "Highlight more relevant technical skills that are supported by your actual experience."
            );
        }

        /*
         * -----------------------------------------------------
         * CONTACT INFORMATION
         * -----------------------------------------------------
         */

        if (scoreBreakdown != null &&
                scoreBreakdown.getContactScore() < 10) {

            recommendations.add(
                    "Make sure your resume contains both a professional email address and phone number."
            );
        }

        /*
         * -----------------------------------------------------
         * CONTENT LENGTH
         * -----------------------------------------------------
         */

        if (scoreBreakdown != null &&
                scoreBreakdown.getContentQualityScore() < 5) {

            recommendations.add(
                    "Add more relevant resume content, such as project details, achievements, responsibilities, and measurable results."
            );
        }

        /*
         * -----------------------------------------------------
         * DEFAULT RECOMMENDATION
         * -----------------------------------------------------
         */

        if (recommendations.isEmpty()) {

            recommendations.add(
                    "Your resume has good alignment with the supplied job description. Continue tailoring keywords and achievements to each job application."
            );
        }

        /*
         * Limit recommendations so the UI remains clean.
         */

        if (recommendations.size() > 8) {

            return new ArrayList<>(
                    recommendations.subList(0, 8)
            );
        }

        return recommendations;
    }

    /*
     * =========================================================
     * CASE-INSENSITIVE LIST CHECK
     * =========================================================
     */

    private boolean containsIgnoreCase(
            List<String> list,
            String value) {

        if (list == null ||
                value == null) {

            return false;
        }

        for (String item : list) {

            if (item != null &&
                    item.equalsIgnoreCase(value)) {

                return true;
            }
        }

        return false;
    }
}