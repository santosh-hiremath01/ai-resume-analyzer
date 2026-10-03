\# AI Resume Analyzer



An AI-powered Resume Analyzer that evaluates a candidate's resume against a selected job role and job description. The system extracts text from PDF resumes, analyzes skills, sections, keywords, and generates an ATS-style score with recommendations.



\---



\## 🚀 Features



\- Upload resume in PDF format

\- Extract text from PDF resumes

\- Select a target job role

\- Enter a job description

\- Detect technical skills from the resume

\- Analyze important resume sections

\- Compare resume keywords with the job description

\- Identify matched and missing keywords

\- Generate an ATS compatibility score

\- Display ATS score breakdown

\- Provide resume improvement recommendations

\- Interactive and responsive dashboard

\- Cyber-Mint / Aurora themed user interface



\---



\## 🛠️ Technologies Used



\### Backend



\- Java

\- Spring Boot

\- Maven

\- Apache PDFBox

\- REST APIs



\### Frontend



\- HTML5

\- CSS3

\- JavaScript

\- Canvas API



\### Development Tools



\- Visual Studio Code

\- Git

\- GitHub



\---



\## 📂 Project Structure



```text

AI Resume Analyzer

│

├── backend

│   ├── src

│   │   ├── main

│   │   │   ├── java

│   │   │   │   └── com

│   │   │   │       └── airesume

│   │   │   │           └── analyzer

│   │   │   │               ├── controller

│   │   │   │               ├── model

│   │   │   │               └── service

│   │   │   └── resources

│   │   │       └── application.properties

│   │   └── test

│   ├── pom.xml

│   └── mvnw.cmd

│

├── frontend

│   ├── index.html

│   ├── script.js

│   └── style.css

│

├── .gitignore

└── README.md

```



\---



\## ⚙️ How to Run the Project



\### Prerequisites



Make sure the following are installed:



\- Java 21 or later

\- Maven

\- Visual Studio Code

\- Git

\- Modern web browser



\### 1. Clone the Repository



```bash

git clone https://github.com/santosh-hiremath01/ai-resume-analyzer.git

cd ai-resume-analyzer

```



\### 2. Start the Spring Boot Backend



Open a terminal:



```powershell

cd backend

.\\mvnw.cmd spring-boot:run

```



The backend runs on:



```text

http://localhost:8080

```



\### 3. Test the Backend



Open another terminal:



```powershell

curl.exe http://localhost:8080/api/test

```



Expected response:



```text

AI Resume Analyzer Backend is running!

```



\### 4. Start the Frontend



Open another terminal:



```powershell

cd frontend

```



Then open `index.html` using \*\*VS Code Live Server\*\*.



The frontend will normally be available at:



```text

http://127.0.0.1:5500

```



\---



\## 🔌 API Endpoints



\### Test Backend



```text

GET /api/test

```



\### Get Available Job Roles



```text

GET /api/resumes/roles

```



\### Analyze Resume



```text

POST /api/resumes/upload

```



The resume analysis request accepts:



\- Resume PDF

\- Target job role

\- Job description



\---



\## 🔄 Application Workflow



```text

User uploads Resume PDF

&#x20;       ↓

Frontend sends Resume + Job Role + Job Description

&#x20;       ↓

Spring Boot REST API

&#x20;       ↓

PDF Text Extraction

&#x20;       ↓

Resume Section Analysis

&#x20;       ↓

Technical Skill Detection

&#x20;       ↓

Job Description Keyword Analysis

&#x20;       ↓

ATS Score Calculation

&#x20;       ↓

Recommendations Generated

&#x20;       ↓

Results Returned as JSON

&#x20;       ↓

Frontend Displays ATS Dashboard

```



\---



\## 📊 ATS Analysis



The application analyzes multiple aspects of a resume, including:



\- Resume sections

\- Technical skills

\- Job description keywords

\- Matched keywords

\- Missing keywords

\- Role-specific skills

\- Overall ATS compatibility



The final dashboard presents the analysis in an easy-to-understand format.



\---



\## 🎯 Supported Job Roles



The application currently supports:



\- Java Developer

\- Python Developer

\- Full Stack Developer

\- Frontend Developer

\- Backend Developer

\- Data Analyst

\- Data Scientist

\- Data Engineer

\- Machine Learning Engineer

\- AI Engineer

\- DevOps Engineer

\- Cloud Engineer

\- Cybersecurity Analyst

\- QA Engineer



\---



\## 🔮 Future Improvements



\- AI/LLM-powered resume recommendations

\- Resume grammar and writing analysis

\- Resume section quality scoring

\- Authentication and user accounts

\- Resume history and comparison

\- Database integration

\- Resume report download

\- Cloud deployment

\- Advanced semantic matching between resumes and job descriptions



\---



\## 👨‍💻 Author



\*\*Santosh Hiremath\*\*



AI Resume Analyzer — Full Stack Java Project



\---

