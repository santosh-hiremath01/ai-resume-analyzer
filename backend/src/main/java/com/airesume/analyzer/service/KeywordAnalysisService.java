package com.airesume.analyzer.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KeywordAnalysisService {

    /*
     * =========================================================
     * STOP WORDS
     * =========================================================
     */

    private static final Set<String> STOP_WORDS = new HashSet<>(
            Arrays.asList(
                    "a",
                    "an",
                    "the",
                    "and",
                    "or",
                    "but",
                    "for",
                    "from",
                    "to",
                    "with",
                    "without",
                    "in",
                    "on",
                    "at",
                    "by",
                    "of",
                    "as",
                    "is",
                    "are",
                    "was",
                    "were",
                    "be",
                    "been",
                    "being",
                    "have",
                    "has",
                    "had",
                    "will",
                    "would",
                    "can",
                    "could",
                    "should",
                    "may",
                    "might",
                    "must",
                    "this",
                    "that",
                    "these",
                    "those",
                    "their",
                    "our",
                    "your",
                    "you",
                    "we",
                    "they",
                    "it",
                    "i",
                    "job",
                    "role",
                    "position",
                    "candidate",
                    "candidates",
                    "company",
                    "team",
                    "teams",
                    "work",
                    "working",
                    "experience",
                    "experienced",
                    "required",
                    "requirements",
                    "requirement",
                    "preferred",
                    "preferably",
                    "looking",
                    "seeking",
                    "skills",
                    "skill",
                    "knowledge",
                    "strong",
                    "good",
                    "excellent",
                    "related",
                    "relevant",
                    "using",
                    "used",
                    "use",
                    "develop",
                    "developing",
                    "development",
                    "build",
                    "building",
                    "create",
                    "creating",
                    "maintain",
                    "maintaining",
                    "support",
                    "supporting",
                    "provide",
                    "providing",
                    "manage",
                    "managing",
                    "ensure",
                    "ensuring",
                    "projects",
                    "project",
                    "year",
                    "years",
                    "month",
                    "months"
            )
    );

    /*
     * =========================================================
     * IMPORTANT TECHNICAL PHRASES
     * =========================================================
     */

    private static final List<String> KNOWN_PHRASES = Arrays.asList(

            // Programming
            "Java",
            "Python",
            "JavaScript",
            "TypeScript",
            "C++",
            "C#",
            "Golang",
            "Ruby",
            "PHP",
            "Kotlin",
            "Swift",

            // Java
            "Spring",
            "Spring Boot",
            "Spring Framework",
            "Spring Security",
            "Spring Data",
            "Hibernate",
            "JPA",
            "JDBC",
            "Maven",
            "Gradle",
            "JUnit",
            "TestNG",

            // Frontend / Web
            "HTML",
            "HTML5",
            "CSS",
            "CSS3",
            "React",
            "React.js",
            "Angular",
            "Vue.js",
            "Node.js",
            "Express.js",
            "Next.js",
            "REST API",
            "REST APIs",
            "RESTful API",
            "GraphQL",
            "Web Services",

            // Database
            "MySQL",
            "PostgreSQL",
            "Oracle",
            "SQL Server",
            "Microsoft SQL Server",
            "MongoDB",
            "Redis",
            "DynamoDB",
            "Database Management",
            "Database Design",

            // Cloud
            "AWS",
            "Amazon Web Services",
            "Azure",
            "Microsoft Azure",
            "Google Cloud",
            "Google Cloud Platform",
            "GCP",
            "Cloud Computing",

            // DevOps
            "Docker",
            "Kubernetes",
            "Jenkins",
            "GitHub Actions",
            "CI/CD",
            "Continuous Integration",
            "Continuous Deployment",
            "Terraform",
            "Ansible",
            "Linux",
            "Shell Scripting",

            // Data
            "Data Analysis",
            "Data Analytics",
            "Data Science",
            "Data Engineering",
            "Data Visualization",
            "Data Cleaning",
            "Data Mining",
            "Data Modeling",
            "Data Pipelines",
            "ETL",
            "Business Intelligence",

            // Python / Data
            "Pandas",
            "NumPy",
            "Matplotlib",
            "Seaborn",
            "Scikit-learn",
            "SciPy",
            "Jupyter Notebook",

            // BI
            "Power BI",
            "Microsoft Power BI",
            "Tableau",
            "Microsoft Excel",
            "Advanced Excel",

            // AI / ML
            "Machine Learning",
            "Deep Learning",
            "Artificial Intelligence",
            "Generative AI",
            "Natural Language Processing",
            "NLP",
            "Computer Vision",
            "Neural Networks",
            "TensorFlow",
            "PyTorch",
            "Large Language Models",
            "LLM",
            "LLMs",
            "Transformers",

            // Big Data
            "Apache Spark",
            "Spark",
            "Hadoop",
            "Apache Kafka",
            "Kafka",
            "Apache Airflow",
            "Airflow",

            // Security
            "Cybersecurity",
            "Cyber Security",
            "Network Security",
            "Information Security",
            "Application Security",
            "Cloud Security",
            "Penetration Testing",
            "Vulnerability Assessment",
            "Incident Response",
            "Cryptography",
            "SIEM",
            "SOC",

            // Testing
            "Manual Testing",
            "Automation Testing",
            "Software Testing",
            "API Testing",
            "Performance Testing",
            "Unit Testing",
            "Integration Testing",
            "Selenium",
            "Postman",

            // Software Engineering
            "Object Oriented Programming",
            "Object-Oriented Programming",
            "OOP",
            "Data Structures",
            "Algorithms",
            "Design Patterns",
            "System Design",
            "Microservices",
            "Software Development",
            "Agile",
            "Scrum",
            "Jira",
            "Git",
            "GitHub",
            "GitLab",

            // Professional skills
            "Requirements Gathering",
            "Requirements Analysis",
            "Stakeholder Management",
            "Project Management",
            "Team Management",
            "Communication Skills",
            "Problem Solving",
            "Critical Thinking",
            "Analytical Skills",
            "Leadership",
            "Time Management"
    );

    /*
     * =========================================================
     * MAIN METHOD
     * =========================================================
     */

    public Map<String, List<String>> analyzeKeywords(
            String resumeText,
            String jobDescription) {

        Map<String, List<String>> result =
                new LinkedHashMap<>();

        if (resumeText == null) {
            resumeText = "";
        }

        if (jobDescription == null) {
            jobDescription = "";
        }

        String resume =
                normalizeText(resumeText);

        String job =
                normalizeText(jobDescription);

        List<String> extractedKeywords =
                extractKeywords(job);

        extractedKeywords =
                removeDuplicates(extractedKeywords);

        List<String> matchedKeywords =
                new ArrayList<>();

        List<String> missingKeywords =
                new ArrayList<>();

        for (String keyword : extractedKeywords) {

            if (containsKeyword(resume, keyword)) {

                matchedKeywords.add(keyword);

            } else {

                missingKeywords.add(keyword);
            }
        }

        result.put("matched", matchedKeywords);
        result.put("missing", missingKeywords);
        result.put("extracted", extractedKeywords);

        return result;
    }

    /*
     * =========================================================
     * EXTRACT KEYWORDS FROM JOB DESCRIPTION
     * =========================================================
     */

    private List<String> extractKeywords(
            String jobDescription) {

        List<String> keywords =
                new ArrayList<>();

        /*
         * First detect known technical phrases.
         */

        for (String phrase : KNOWN_PHRASES) {

            if (containsKeyword(
                    jobDescription,
                    phrase)) {

                keywords.add(phrase);
            }
        }

        /*
         * Then detect meaningful individual words.
         */

        Pattern pattern =
                Pattern.compile(
                        "\\b[A-Za-z][A-Za-z0-9+#.-]{2,}\\b"
                );

        Matcher matcher =
                pattern.matcher(jobDescription);

        while (matcher.find()) {

            String word =
                    matcher.group();

            word =
                    cleanKeyword(word);

            if (word.isEmpty()) {
                continue;
            }

            String lower =
                    word.toLowerCase();

            if (STOP_WORDS.contains(lower)) {
                continue;
            }

            if (word.length() < 3) {
                continue;
            }

            if (word.matches("\\d+")) {
                continue;
            }

            /*
             * Do not add a word if it is already part
             * of an important known phrase.
             */

            if (isInsideKnownPhrase(
                    jobDescription,
                    word)) {

                continue;
            }

            keywords.add(
                    formatKeyword(word)
            );
        }

        /*
         * Add selected useful two-word phrases.
         */

        keywords.addAll(
                extractUsefulPhrases(jobDescription)
        );

        return removeDuplicates(keywords);
    }

    /*
     * =========================================================
     * USEFUL TWO-WORD PHRASES
     * =========================================================
     */

    private List<String> extractUsefulPhrases(
            String text) {

        List<String> phrases =
                new ArrayList<>();

        String[] words =
                text.split("\\s+");

        for (int i = 0;
             i < words.length - 1;
             i++) {

            String first =
                    cleanKeyword(words[i]);

            String second =
                    cleanKeyword(words[i + 1]);

            if (first.length() < 3 ||
                    second.length() < 3) {

                continue;
            }

            if (STOP_WORDS.contains(
                    first.toLowerCase())) {

                continue;
            }

            if (STOP_WORDS.contains(
                    second.toLowerCase())) {

                continue;
            }

            String phrase =
                    first + " " + second;

            if (isKnownPhrase(phrase)) {
                continue;
            }

            if (isUsefulPhrase(
                    first,
                    second)) {

                phrases.add(
                        phrase.toLowerCase()
                );
            }
        }

        return phrases;
    }

    /*
     * =========================================================
     * USEFUL PHRASE CHECK
     * =========================================================
     */

    private boolean isUsefulPhrase(
            String first,
            String second) {

        String combined =
                first.toLowerCase()
                        + " "
                        + second.toLowerCase();

        String[] prefixes = {

                "data ",
                "software ",
                "system ",
                "application ",
                "business ",
                "technical ",
                "cloud ",
                "web ",
                "mobile ",
                "machine ",
                "deep ",
                "artificial ",
                "natural ",
                "computer ",
                "quality ",
                "security ",
                "network ",
                "database ",
                "performance ",
                "automation ",
                "continuous ",
                "object ",
                "project "
        };

        for (String prefix : prefixes) {

            if (combined.startsWith(prefix)) {
                return true;
            }
        }

        String[] importantWords = {

                "analysis",
                "analytics",
                "engineering",
                "development",
                "management",
                "testing",
                "design",
                "security",
                "visualization",
                "programming",
                "learning",
                "deployment",
                "architecture",
                "communication",
                "leadership",
                "problem",
                "solving"
        };

        for (String word : importantWords) {

            if (first.equalsIgnoreCase(word) ||
                    second.equalsIgnoreCase(word)) {

                return true;
            }
        }

        return false;
    }

    /*
     * =========================================================
     * CHECK KEYWORD
     * =========================================================
     */

    private boolean containsKeyword(
            String text,
            String keyword) {

        if (text == null ||
                keyword == null ||
                keyword.trim().isEmpty()) {

            return false;
        }

        String normalized =
                normalizeText(keyword);

        List<String> variations =
                createVariations(normalized);

        for (String variation : variations) {

            if (containsAsWord(
                    text,
                    variation)) {

                return true;
            }
        }

        return false;
    }

    /*
     * =========================================================
     * WORD-BOUNDARY MATCH
     * =========================================================
     */

    private boolean containsAsWord(
            String text,
            String keyword) {

        if (text == null ||
                keyword == null ||
                keyword.isEmpty()) {

            return false;
        }

        String escaped =
                Pattern.quote(keyword);

        String regex =
                "(?i)(?<![a-z0-9+#])"
                        + escaped
                        + "(?![a-z0-9+#])";

        return Pattern.compile(regex)
                .matcher(text)
                .find();
    }

    /*
     * =========================================================
     * KEYWORD VARIATIONS
     * =========================================================
     */

    private List<String> createVariations(
            String keyword) {

        Set<String> variations =
                new LinkedHashSet<>();

        variations.add(keyword);

        if (keyword.equals("react.js")) {

            variations.add("react");
        }

        if (keyword.equals("node.js")) {

            variations.add("nodejs");
            variations.add("node");
        }

        if (keyword.equals("express.js")) {

            variations.add("express");
        }

        if (keyword.equals("spring boot")) {

            variations.add("springboot");
        }

        if (keyword.equals("rest api") ||
                keyword.equals("rest apis") ||
                keyword.equals("restful api")) {

            variations.add("rest");
            variations.add("restapi");
        }

        if (keyword.equals(
                "object oriented programming")) {

            variations.add("oop");
        }

        if (keyword.equals(
                "object-oriented programming")) {

            variations.add("oop");
        }

        if (keyword.equals("machine learning")) {

            variations.add("machinelearning");
        }

        if (keyword.equals("deep learning")) {

            variations.add("deeplearning");
        }

        if (keyword.equals("data analysis")) {

            variations.add("dataanalysis");
        }

        if (keyword.equals("data analytics")) {

            variations.add("dataanalytics");
        }

        if (keyword.equals("microsoft excel")) {

            variations.add("excel");
        }

        if (keyword.equals("power bi")) {

            variations.add("powerbi");
        }

        if (keyword.equals("microsoft power bi")) {

            variations.add("powerbi");
        }

        if (keyword.equals(
                "amazon web services")) {

            variations.add("aws");
        }

        if (keyword.equals(
                "google cloud platform")) {

            variations.add("gcp");
        }

        if (keyword.equals(
                "natural language processing")) {

            variations.add("nlp");
        }

        if (keyword.equals(
                "large language models")) {

            variations.add("llm");
            variations.add("llms");
        }

        /*
         * Punctuation-free variation.
         */

        variations.add(
                keyword
                        .replace(" ", "")
                        .replace("-", "")
                        .replace(".", "")
        );

        return new ArrayList<>(variations);
    }

    /*
     * =========================================================
     * NORMALIZE TEXT
     * =========================================================
     */

    private String normalizeText(
            String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase()
                .replaceAll(
                        "[\\r\\n\\t]+",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }

    /*
     * =========================================================
     * CLEAN KEYWORD
     * =========================================================
     */

    private String cleanKeyword(
            String keyword) {

        if (keyword == null) {
            return "";
        }

        return keyword
                .trim()
                .replaceAll(
                        "^[^a-zA-Z0-9+#]+",
                        ""
                )
                .replaceAll(
                        "[^a-zA-Z0-9+#.\\-]+$",
                        ""
                );
    }

    /*
     * =========================================================
     * FORMAT KEYWORD
     * =========================================================
     */

    private String formatKeyword(
            String keyword) {

        for (String phrase : KNOWN_PHRASES) {

            if (phrase.equalsIgnoreCase(keyword)) {
                return phrase;
            }
        }

        return keyword.toLowerCase();
    }

    /*
     * =========================================================
     * KNOWN PHRASE CHECK
     * =========================================================
     */

    private boolean isKnownPhrase(
            String phrase) {

        for (String known : KNOWN_PHRASES) {

            if (known.equalsIgnoreCase(phrase)) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================================================
     * CHECK WHETHER WORD BELONGS TO KNOWN PHRASE
     * =========================================================
     */

    private boolean isInsideKnownPhrase(
            String text,
            String word) {

        String lowerText =
                text.toLowerCase();

        String lowerWord =
                word.toLowerCase();

        for (String phrase : KNOWN_PHRASES) {

            String lowerPhrase =
                    phrase.toLowerCase();

            if (!lowerPhrase.contains(" ")) {
                continue;
            }

            if (!lowerPhrase.contains(lowerWord)) {
                continue;
            }

            if (containsAsWord(
                    lowerText,
                    lowerPhrase)) {

                return true;
            }
        }

        return false;
    }

    /*
     * =========================================================
     * REMOVE DUPLICATES
     * =========================================================
     */

    private List<String> removeDuplicates(
            List<String> values) {

        List<String> result =
                new ArrayList<>();

        Set<String> seen =
                new HashSet<>();

        for (String value : values) {

            if (value == null ||
                    value.trim().isEmpty()) {

                continue;
            }

            String normalized =
                    value.toLowerCase()
                            .trim();

            if (seen.add(normalized)) {

                result.add(value);
            }
        }

        return result;
    }
}