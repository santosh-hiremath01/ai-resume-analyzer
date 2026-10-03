/* =========================================================
   AI RESUME ANALYZER
   Frontend JavaScript
   ========================================================= */

const API_BASE_URL = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", function () {
  /* =====================================================
       DOM ELEMENTS
       ===================================================== */

  const resumeFile = document.getElementById("resumeFile");

  const jobDescription = document.getElementById("jobDescription");

  const targetRole = document.getElementById("targetRole");

  const analyzeButton = document.getElementById("analyzeButton");

  const uploadArea = document.getElementById("uploadArea");

  const selectedFile = document.getElementById("selectedFile");

  const resultsSection = document.getElementById("results");

  /* =====================================================
       REQUIRED ELEMENT CHECK
       ===================================================== */

  if (!resumeFile) {
    console.error("AI Resume Analyzer: resumeFile element not found.");
    return;
  }

  if (!jobDescription) {
    console.error("AI Resume Analyzer: jobDescription element not found.");
    return;
  }

  if (!targetRole) {
    console.error("AI Resume Analyzer: targetRole element not found.");
    return;
  }

  if (!analyzeButton) {
    console.error("AI Resume Analyzer: analyzeButton element not found.");
    return;
  }

  /* =====================================================
       LOAD JOB ROLES
       ===================================================== */

  async function loadRoles() {
    try {
      targetRole.innerHTML = `
                <option value="">
                    Loading roles...
                </option>
            `;

      const response = await fetch(`${API_BASE_URL}/resumes/roles`);

      if (!response.ok) {
        throw new Error("Could not load job roles. Status: " + response.status);
      }

      const roles = await response.json();

      targetRole.innerHTML = `
                <option value="">
                    Select a target role
                </option>
            `;

      if (!Array.isArray(roles) || roles.length === 0) {
        targetRole.innerHTML = `
                    <option value="">
                        No roles available
                    </option>
                `;

        return;
      }

      roles.forEach(function (role) {
        const option = document.createElement("option");

        option.value = role;
        option.textContent = role;

        targetRole.appendChild(option);
      });

      console.log("AI Resume Analyzer: Roles loaded:", roles);
    } catch (error) {
      console.error("Role loading error:", error);

      targetRole.innerHTML = `
                <option value="">
                    Unable to load roles
                </option>
            `;
    }
  }

  /* =====================================================
       FILE SELECTION
       ===================================================== */

  resumeFile.addEventListener("change", function () {
    if (!resumeFile.files || resumeFile.files.length === 0) {
      if (selectedFile) {
        selectedFile.textContent = "";
      }

      return;
    }

    const file = resumeFile.files[0];

    const isPDF =
      file.type === "application/pdf" ||
      file.name.toLowerCase().endsWith(".pdf");

    /* FILE TYPE */

    if (!isPDF) {
      alert("Please upload a PDF file.");

      resumeFile.value = "";

      if (selectedFile) {
        selectedFile.textContent = "";
      }

      return;
    }

    /* FILE SIZE */

    if (file.size > 5 * 1024 * 1024) {
      alert("File size must be less than 5 MB.");

      resumeFile.value = "";

      if (selectedFile) {
        selectedFile.textContent = "";
      }

      return;
    }

    /* SHOW FILE */

    if (selectedFile) {
      selectedFile.textContent = "✓ " + file.name;
    }
  });

  /* =====================================================
       DRAG AND DROP
       ===================================================== */

  if (uploadArea) {
    uploadArea.addEventListener("dragover", function (event) {
      event.preventDefault();

      uploadArea.classList.add("drag-over");
    });

    uploadArea.addEventListener("dragleave", function () {
      uploadArea.classList.remove("drag-over");
    });

    uploadArea.addEventListener("drop", function (event) {
      event.preventDefault();

      uploadArea.classList.remove("drag-over");

      const files = event.dataTransfer.files;

      if (!files || files.length === 0) {
        return;
      }

      const file = files[0];

      const isPDF =
        file.type === "application/pdf" ||
        file.name.toLowerCase().endsWith(".pdf");

      if (!isPDF) {
        alert("Please upload a PDF file.");

        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        alert("File size must be less than 5 MB.");

        return;
      }

      try {
        const dataTransfer = new DataTransfer();

        dataTransfer.items.add(file);

        resumeFile.files = dataTransfer.files;
      } catch (error) {
        console.warn("Could not assign dropped file:", error);
      }

      if (selectedFile) {
        selectedFile.textContent = "✓ " + file.name;
      }
    });
  }

  /* =====================================================
       ANALYZE RESUME
       ===================================================== */

  analyzeButton.addEventListener("click", async function () {
    const file =
      resumeFile.files && resumeFile.files.length > 0
        ? resumeFile.files[0]
        : null;

    const selectedRole = targetRole.value.trim();

    const jd = jobDescription.value.trim();

    /* VALIDATE FILE */

    if (!file) {
      alert("Please upload your resume PDF.");

      return;
    }

    /* VALIDATE PDF */

    const isPDF =
      file.type === "application/pdf" ||
      file.name.toLowerCase().endsWith(".pdf");

    if (!isPDF) {
      alert("Please upload a PDF file.");

      return;
    }

    /* VALIDATE ROLE */

    if (!selectedRole) {
      alert("Please select a target job role.");

      return;
    }

    /* VALIDATE JD */

    if (!jd) {
      alert("Please enter the job description.");

      return;
    }

    /* =================================================
               FORM DATA
               ================================================= */

    const formData = new FormData();

    formData.append("file", file);

    formData.append("targetRole", selectedRole);

    formData.append("jobDescription", jd);

    /* =================================================
               BUTTON LOADING
               ================================================= */

    const originalButtonHTML = analyzeButton.innerHTML;

    analyzeButton.disabled = true;

    analyzeButton.classList.add("loading");

    analyzeButton.innerHTML = `
                <span>
                    Analyzing your resume...
                </span>

                <span class="button-arrow">
                    ◌
                </span>
            `;

    /* =================================================
               SEND REQUEST
               ================================================= */

    try {
      const response = await fetch(`${API_BASE_URL}/resumes/upload`, {
        method: "POST",
        body: formData,
      });

      if (!response.ok) {
        let errorMessage = "Resume analysis failed.";

        try {
          const errorText = await response.text();

          if (errorText) {
            errorMessage = errorText;
          }
        } catch (ignored) {
          // Keep default message.
        }

        throw new Error(errorMessage);
      }

      /* GET JSON */

      const result = await response.json();

      console.log("Resume Analysis Result:", result);

      /* DISPLAY */

      displayResults(result, selectedRole);
    } catch (error) {
      console.error("Resume analysis error:", error);

      alert(
        error.message || "Something went wrong while analyzing the resume.",
      );
    } finally {
      analyzeButton.disabled = false;

      analyzeButton.classList.remove("loading");

      analyzeButton.innerHTML = originalButtonHTML;
    }
  });

  /* =====================================================
       DISPLAY RESULTS
       ===================================================== */

  function displayResults(result, selectedRole) {
    if (!resultsSection) {
      console.error("Results section not found.");

      return;
    }

    /* =================================================
           BACKEND DATA
           ================================================= */

    const atsScore = Math.max(0, Math.min(100, Number(result.atsScore) || 0));

    const fileName = result.fileName || "Resume";

    const matchedKeywords = Array.isArray(result.matchedKeywords)
      ? result.matchedKeywords
      : [];

    const missingKeywords = Array.isArray(result.missingKeywords)
      ? result.missingKeywords
      : [];

    const skills = Array.isArray(result.skills) ? result.skills : [];

    const sections = result.sections || {};

    const scoreBreakdown = result.scoreBreakdown || {};

    const recommendations = Array.isArray(result.recommendations)
      ? result.recommendations
      : [];

    /* =================================================
           ATS GAUGE

           IMPORTANT:
           Use atsScore here.

           Do NOT use result outside this function.
           ================================================= */

    const scoreDegree = atsScore * 3.6;

    const scoreGauge = document.querySelector(".ats-score");

    if (scoreGauge) {
      scoreGauge.style.setProperty("--score-degree", `${scoreDegree}deg`);
    }

    /* =================================================
           SCORE BREAKDOWN
           ================================================= */

    const keywordScore = Number(scoreBreakdown.keywordScore || 0);

    const skillScore = Number(scoreBreakdown.skillScore || 0);

    const sectionScore = Number(scoreBreakdown.sectionScore || 0);

    const contactScore = Number(scoreBreakdown.contactScore || 0);

    const contentQualityScore = Number(scoreBreakdown.contentQualityScore || 0);

    /* =================================================
           KEYWORD COUNT
           ================================================= */

    const totalKeywords = matchedKeywords.length + missingKeywords.length;

    /* =================================================
           SECTION COUNT
           ================================================= */

    const sectionEntries = Object.entries(sections);

    const foundSections = sectionEntries.filter(function ([, value]) {
      return value === true;
    }).length;

    const totalSections = sectionEntries.length;

    /* =================================================
           SCORE BREAKDOWN HTML
           ================================================= */

    const scoreBreakdownHTML = `

            <div class="score-breakdown">

                <h3>
                    Score Breakdown
                </h3>

                <div class="score-row">
                    <span>
                        Keyword Match
                    </span>

                    <strong>
                        ${keywordScore} / 40
                    </strong>
                </div>

                <div class="score-row">
                    <span>
                        Skills
                    </span>

                    <strong>
                        ${skillScore} / 25
                    </strong>
                </div>

                <div class="score-row">
                    <span>
                        Resume Sections
                    </span>

                    <strong>
                        ${sectionScore} / 20
                    </strong>
                </div>

                <div class="score-row">
                    <span>
                        Contact Information
                    </span>

                    <strong>
                        ${contactScore} / 10
                    </strong>
                </div>

                <div class="score-row">
                    <span>
                        Content Quality
                    </span>

                    <strong>
                        ${contentQualityScore} / 5
                    </strong>
                </div>

                <div class="score-row score-total-row">
                    <span>
                        Total ATS Score
                    </span>

                    <strong>
                        ${atsScore} / 100
                    </strong>
                </div>

            </div>
        `;

    /* =================================================
           MATCHED KEYWORDS
           ================================================= */

    let matchedHTML = "";

    if (matchedKeywords.length > 0) {
      matchedHTML = matchedKeywords
        .map(function (keyword) {
          return `
                            <span class="keyword matched">
                                ${escapeHTML(keyword)}
                            </span>
                        `;
        })
        .join("");
    } else {
      matchedHTML = `
                <p class="empty-message">
                    No matched keywords found.
                </p>
            `;
    }

    /* =================================================
           MISSING KEYWORDS
           ================================================= */

    let missingHTML = "";

    if (missingKeywords.length > 0) {
      missingHTML = missingKeywords
        .map(function (keyword) {
          return `
                            <span class="keyword missing">
                                ${escapeHTML(keyword)}
                            </span>
                        `;
        })
        .join("");
    } else {
      missingHTML = `
                <p class="empty-message">
                    No missing keywords. Good job!
                </p>
            `;
    }

    /* =================================================
           SKILLS
           ================================================= */

    let skillsHTML = "";

    if (skills.length > 0) {
      skillsHTML = skills
        .map(function (skill) {
          return `
                            <span class="keyword skill">
                                ${escapeHTML(skill)}
                            </span>
                        `;
        })
        .join("");
    } else {
      skillsHTML = `
                <p class="empty-message">
                    No technical skills detected.
                </p>
            `;
    }

    /* =================================================
           RESUME SECTIONS
           ================================================= */

    let sectionsHTML = "";

    sectionEntries.forEach(function ([section, found]) {
      sectionsHTML += `

                    <div class="section-result">

                        <span>
                            ${escapeHTML(section)}
                        </span>

                        <strong class="${
                          found ? "section-found" : "section-missing"
                        }">

                            ${found ? "Found" : "Missing"}

                        </strong>

                    </div>
                `;
    });

    /* =================================================
           RECOMMENDATIONS
           ================================================= */

    let recommendationsHTML = "";

    if (recommendations.length > 0) {
      recommendationsHTML = recommendations
        .map(function (recommendation) {
          return `

                                <div class="recommendation-item">

                                    <span class="recommendation-icon">
                                        →
                                    </span>

                                    <span>
                                        ${escapeHTML(recommendation)}
                                    </span>

                                </div>
                            `;
        })
        .join("");
    } else {
      recommendationsHTML = `
                <p class="empty-message">
                    No additional recommendations.
                </p>
            `;
    }

    /* =================================================
           SCORE MESSAGE
           ================================================= */

    let scoreMessage = "";

    if (atsScore >= 80) {
      scoreMessage = `
                <p>
                    Your resume shows strong alignment
                    with the
                    ${escapeHTML(selectedRole)}
                    role and the supplied job description.
                </p>
            `;
    } else if (atsScore >= 60) {
      scoreMessage = `
                <p>
                    Your resume has a good foundation for
                    the
                    ${escapeHTML(selectedRole)}
                    role, but some areas can be improved.
                </p>
            `;
    } else {
      scoreMessage = `
                <p>
                    Your resume needs stronger alignment
                    with the
                    ${escapeHTML(selectedRole)}
                    role and supplied job description.
                </p>
            `;
    }

    /* =================================================
           SCORE TITLE
           ================================================= */

    let scoreTitle = "";

    if (atsScore >= 80) {
      scoreTitle = "Strong ATS compatibility";
    } else if (atsScore >= 60) {
      scoreTitle = "Good foundation";
    } else {
      scoreTitle = "Needs improvement";
    }

    /* =================================================
           BUILD RESULTS
           ================================================= */

    resultsSection.innerHTML = `

            <div class="results-container">

                <!-- ATS SCORE -->

                <div class="ats-score-card">

                    <div>

                        <p class="result-label">
                            ATS COMPATIBILITY
                        </p>

                        <div
                            class="ats-score"
                            style="--score-degree: ${scoreDegree}deg;"
                        >
                            ${atsScore}
                        </div>

                    </div>

                    <div>

                        <h3>
                            ${scoreTitle}
                        </h3>

                        ${scoreMessage}

                        <p class="analyzed-file">
                            ${escapeHTML(fileName)}
                        </p>

                    </div>

                </div>


                <!-- SUMMARY -->

                <div class="analysis-summary">

                    <div class="summary-card">

                        <span class="summary-title">
                            Keyword Match
                        </span>

                        <strong>
                            ${matchedKeywords.length}
                            /
                            ${totalKeywords}
                        </strong>

                    </div>


                    <div class="summary-card">

                        <span class="summary-title">
                            Skills Detected
                        </span>

                        <strong>
                            ${skills.length}
                        </strong>

                    </div>


                    <div class="summary-card">

                        <span class="summary-title">
                            Sections Found
                        </span>

                        <strong>
                            ${foundSections}
                            /
                            ${totalSections}
                        </strong>

                    </div>

                </div>


                <!-- SCORE BREAKDOWN -->

                ${scoreBreakdownHTML}


                <!-- RESULT GRID -->

                <div class="results-grid">

                    <!-- MATCHED KEYWORDS -->

                    <div class="result-card">

                        <div class="result-card-header">

                            <div>

                                <span class="result-card-label">
                                    MATCHED
                                </span>

                                <h3>
                                    Matched Keywords
                                </h3>

                            </div>

                            <div class="success-icon">
                                ✓
                            </div>

                        </div>

                        <div class="keyword-list">

                            ${matchedHTML}

                        </div>

                    </div>


                    <!-- MISSING KEYWORDS -->

                    <div class="result-card">

                        <div class="result-card-header">

                            <div>

                                <span class="result-card-label warning-label">
                                    MISSING
                                </span>

                                <h3>
                                    Missing Keywords
                                </h3>

                            </div>

                            <div class="warning-icon">
                                !
                            </div>

                        </div>

                        <div class="keyword-list">

                            ${missingHTML}

                        </div>

                    </div>


                    <!-- SKILLS -->

                    <div class="result-card">

                        <div class="result-card-header">

                            <div>

                                <span class="result-card-label">
                                    DETECTED
                                </span>

                                <h3>
                                    Detected Skills
                                </h3>

                            </div>

                            <div class="skills-count">
                                ${skills.length}
                            </div>

                        </div>

                        <div class="keyword-list">

                            ${skillsHTML}

                        </div>

                    </div>


                    <!-- SECTIONS -->

                    <div class="result-card">

                        <div class="result-card-header">

                            <div>

                                <span class="result-card-label">
                                    STRUCTURE
                                </span>

                                <h3>
                                    Resume Sections
                                </h3>

                            </div>

                        </div>

                        <div class="sections-list">

                            ${sectionsHTML}

                        </div>

                    </div>

                </div>


                <!-- RECOMMENDATIONS -->

                <div class="result-card recommendations-card">

                    <div class="result-card-header">

                        <div>

                            <span class="result-card-label">
                                AI INSIGHTS
                            </span>

                            <h3>
                                Recommendations
                            </h3>

                        </div>

                    </div>

                    <div class="recommendations-list">

                        ${recommendationsHTML}

                    </div>

                </div>

            </div>
        `;

    /* =================================================
           SHOW RESULTS
           ================================================= */

    resultsSection.style.display = "block";

    resultsSection.classList.add("visible");

    /* =================================================
           SCROLL TO RESULTS
           ================================================= */

    resultsSection.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  }

  /* =====================================================
       ESCAPE HTML
       ===================================================== */

  function escapeHTML(value) {
    if (value === null || value === undefined) {
      return "";
    }

    return String(value)
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  /* =====================================================
       INTERACTIVE DOT GRID
       Cyber-Mint version
       ===================================================== */

  function initInteractiveGrid() {
    const canvas = document.getElementById("gridCanvas");

    if (!canvas) {
      console.warn("AI Resume Analyzer: #gridCanvas not found.");

      return;
    }

    const ctx = canvas.getContext("2d");

    if (!ctx) {
      console.warn("Canvas 2D is not supported.");

      return;
    }

    /* GRID SETTINGS */

    const GRID_SIZE = 26;

    const POINTER_LERP = 0.1;

    const SIGMA_FACTOR = 0.42;

    const PULL_FACTOR = 0.3;

    const MAX_DPR = 2;

    const LINE_THRESHOLD = 0.004;

    /*
     * New Cyber-Mint accent.
     */
    const ACCENT_COLOR = "66, 245, 179";

    let width = 0;

    let height = 0;

    let dpr = 1;

    let rows = 0;

    let cols = 0;

    let points = [];

    const startTime = performance.now();

    const pointer = {
      x: 0,

      y: 0,

      targetX: 0,

      targetY: 0,

      hasEntered: false,

      isTouch: false,
    };

    /* =================================================
           CREATE GRID
           ================================================= */

    function createGrid() {
      points = [];

      cols = Math.ceil(width / GRID_SIZE) + 1;

      rows = Math.ceil(height / GRID_SIZE) + 1;

      for (let row = 0; row < rows; row++) {
        const currentRow = [];

        for (let col = 0; col < cols; col++) {
          const x = col * GRID_SIZE;

          const y = row * GRID_SIZE;

          currentRow.push({
            homeX: x,

            homeY: y,

            x: x,

            y: y,

            g: 0,
          });
        }

        points.push(currentRow);
      }
    }

    /* =================================================
           RESIZE
           ================================================= */

    function resizeCanvas() {
      width = window.innerWidth;

      height = window.innerHeight;

      dpr = Math.min(window.devicePixelRatio || 1, MAX_DPR);

      canvas.width = Math.round(width * dpr);

      canvas.height = Math.round(height * dpr);

      canvas.style.width = width + "px";

      canvas.style.height = height + "px";

      ctx.setTransform(dpr, 0, 0, dpr, 0, 0);

      createGrid();

      if (!pointer.hasEntered) {
        pointer.x = width * 0.5;

        pointer.y = height * 0.5;

        pointer.targetX = width * 0.5;

        pointer.targetY = height * 0.5;
      }
    }

    /* =================================================
           POINTER
           ================================================= */

    window.addEventListener(
      "pointermove",
      function (event) {
        if (event.pointerType === "touch") {
          pointer.isTouch = true;

          return;
        }

        pointer.isTouch = false;

        pointer.hasEntered = true;

        pointer.targetX = event.clientX;

        pointer.targetY = event.clientY;
      },
      {
        passive: true,
      },
    );

    /* =================================================
           LISSAJOUS DRIFT
           ================================================= */

    function getLissajousPosition(time) {
      const t = (time - startTime) / 1000;

      const normalizedX = 0.5 + 0.34 * Math.sin(t * 0.7) * Math.cos(t * 0.23);

      const normalizedY = 0.5 + 0.3 * Math.sin(t * 0.52 + 1.1);

      return {
        x: normalizedX * width,

        y: normalizedY * height,
      };
    }

    /* =================================================
           UPDATE POINTER
           ================================================= */

    function updatePointer(time) {
      if (!pointer.hasEntered || pointer.isTouch) {
        const drift = getLissajousPosition(time);

        pointer.targetX = drift.x;

        pointer.targetY = drift.y;
      }

      pointer.x += (pointer.targetX - pointer.x) * POINTER_LERP;

      pointer.y += (pointer.targetY - pointer.y) * POINTER_LERP;
    }

    /* =================================================
           DRAW GRID
           ================================================= */

    function drawGrid() {
      ctx.clearRect(0, 0, width, height);

      const minDimension = Math.min(width, height);

      const SIG = SIGMA_FACTOR * minDimension;

      const PULL = PULL_FACTOR * minDimension;

      /* =============================================
               DEFORM POINTS
               ============================================= */

      for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
          const point = points[row][col];

          const dx = pointer.x - point.homeX;

          const dy = pointer.y - point.homeY;

          const distance = Math.sqrt(dx * dx + dy * dy);

          const g = Math.exp(-(distance * distance) / (2 * SIG * SIG));

          point.g = g;

          if (distance > 0.001) {
            const directionX = dx / distance;

            const directionY = dy / distance;

            const displacement = g * PULL;

            point.x = point.homeX + directionX * displacement;

            point.y = point.homeY + directionY * displacement;
          } else {
            point.x = point.homeX;

            point.y = point.homeY;
          }
        }
      }

      /* =============================================
               DRAW GRID LINES
               ============================================= */

      ctx.lineWidth = 1;

      for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
          const point = points[row][col];

          /* RIGHT */

          if (col < cols - 1) {
            const right = points[row][col + 1];

            const averageG = (point.g + right.g) / 2;

            if (averageG >= LINE_THRESHOLD) {
              const alpha = 0.04 + averageG * 0.62;

              ctx.beginPath();

              ctx.moveTo(point.x, point.y);

              ctx.lineTo(right.x, right.y);

              ctx.strokeStyle = `rgba(${ACCENT_COLOR}, ${alpha})`;

              ctx.stroke();
            }
          }

          /* BOTTOM */

          if (row < rows - 1) {
            const bottom = points[row + 1][col];

            const averageG = (point.g + bottom.g) / 2;

            if (averageG >= LINE_THRESHOLD) {
              const alpha = 0.04 + averageG * 0.62;

              ctx.beginPath();

              ctx.moveTo(point.x, point.y);

              ctx.lineTo(bottom.x, bottom.y);

              ctx.strokeStyle = `rgba(${ACCENT_COLOR}, ${alpha})`;

              ctx.stroke();
            }
          }
        }
      }

      /* =============================================
               DRAW DOTS
               ============================================= */

      for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
          const point = points[row][col];

          const radius = 0.9 + point.g * 1.9;

          const alpha = 0.16 + point.g * 0.62;

          ctx.beginPath();

          ctx.arc(point.x, point.y, radius, 0, Math.PI * 2);

          ctx.fillStyle = `rgba(${ACCENT_COLOR}, ${alpha})`;

          ctx.fill();
        }
      }
    }

    /* =================================================
           ANIMATION
           ================================================= */

    function animate(time) {
      updatePointer(time);

      drawGrid();

      requestAnimationFrame(animate);
    }

    /* =================================================
           RESIZE OBSERVER
           ================================================= */

    if (typeof ResizeObserver !== "undefined") {
      const resizeObserver = new ResizeObserver(function () {
        resizeCanvas();
      });

      resizeObserver.observe(document.documentElement);
    } else {
      window.addEventListener("resize", resizeCanvas);
    }

    /* INITIALIZE */

    resizeCanvas();

    pointer.x = width * 0.5;

    pointer.y = height * 0.5;

    pointer.targetX = width * 0.5;

    pointer.targetY = height * 0.5;

    requestAnimationFrame(animate);
  }

  /* =====================================================
       INITIALIZE APPLICATION
       ===================================================== */

  loadRoles();

  initInteractiveGrid();
});
