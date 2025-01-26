package vcet.cse.placement.automation.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "students")
public class Students {
    
    @Id
    @NotNull
    @Column(name = "university_no", unique = true)
    private Long universityNo;

    @NotNull
    @Column(name = "class_name", length = 30)
    private String className;

    @NotNull
    @Column(name = "roll_no", unique = true, length = 10)
    private String rollNo;

    @NotNull
    @Column(length = 100)
    private String name;

    @NotNull
    @Column(length = 10)
    private String gender;

    // LeetCode fields
    @Column(name = "leetcode_username", length = 50)
    private String leetcodeUsername;
    
    @Column(name = "easy_solved", nullable = false)
    private Integer easyProblemsSolved = 0;

    @Column(name = "medium_solved", nullable = false)
    private Integer mediumProblemsSolved = 0;

    @Column(name = "hard_solved", nullable = false)
    private Integer hardProblemsSolved = 0;

    @Column(name = "leetcode_score", nullable = false)
    private Double leetcodeScore = 0.0;

    // Aptitude Test Scores
    @Column(name = "apti_test1", nullable = false)
    private Double aptiTest1Score = 0.0;

    @Column(name = "apti_test2", nullable = false)
    private Double aptiTest2Score = 0.0;

    @Column(name = "apti_test3", nullable = false)
    private Double aptiTest3Score = 0.0;

    // Technical Aptitude Test Scores
    @Column(name = "tech_apti_test1", nullable = false)
    private Double techAptiTest1Score = 0.0;

    @Column(name = "tech_apti_test2", nullable = false)
    private Double techAptiTest2Score = 0.0;

    @Column(name = "tech_apti_test3", nullable = false)
    private Double techAptiTest3Score = 0.0;

    // Programming Test Scores
    @Column(name = "prog_test1", nullable = false)
    private Double programmingTest1Score = 0.0;

    @Column(name = "prog_test2", nullable = false)
    private Double programmingTest2Score = 0.0;

    @Column(name = "prog_test3", nullable = false)
    private Double programmingTest3Score = 0.0;

    // LeetCode score constants
    private static final double EASY_PROBLEM_SCORE = 10.0;
    private static final double MEDIUM_PROBLEM_SCORE = 50.0;
    private static final double HARD_PROBLEM_SCORE = 100.0;

    // Add calculated fields
    @Transient
    public Double getTotalAptitudeScore() {
        return aptiTest1Score + aptiTest2Score + aptiTest3Score;
    }

    @Transient
    public Double getAverageAptitudeScore() {
        return getTotalAptitudeScore() / 3.0;
    }

    @Transient
    public Double getTotalTechnicalScore() {
        return techAptiTest1Score + techAptiTest2Score + techAptiTest3Score;
    }

    @Transient
    public Double getAverageTechnicalScore() {
        return getTotalTechnicalScore() / 3.0;
    }

    @Transient
    public Double getTotalProgrammingScore() {
        return programmingTest1Score + programmingTest2Score + programmingTest3Score;
    }

    @Transient
    public Double getAverageProgrammingScore() {
        return getTotalProgrammingScore() / 3.0;
    }

    @Transient
    public Integer getTotalProblemsSolved() {
        return easyProblemsSolved + mediumProblemsSolved + hardProblemsSolved;
    }

    @Transient
    public Double calculateLeetCodeScore() {
        return (easyProblemsSolved * EASY_PROBLEM_SCORE) +
               (mediumProblemsSolved * MEDIUM_PROBLEM_SCORE) +
               (hardProblemsSolved * HARD_PROBLEM_SCORE);
    }

    // Add these fields after other fields
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        lastUpdated = LocalDateTime.now();
    }

    // Add getter and setter
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Constructors
    public Students() {

    }

    public Students(Long universityNo, String className, String rollNo, String name, String gender) {
        this.universityNo = universityNo;
        this.className = className;
        this.rollNo = rollNo;
        this.name = name;
        this.gender = gender;
    }

    // Getters and Setters for all fields
    public Long getUniversityNo() {
        return universityNo;
    }

    public void setUniversityNo(Long universityNo) {
        this.universityNo = universityNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public Integer getEasyProblemsSolved() {
        return easyProblemsSolved;
    }

    public void setEasyProblemsSolved(Integer easyProblemsSolved) {
        this.easyProblemsSolved = easyProblemsSolved;
    }

    public Integer getMediumProblemsSolved() {
        return mediumProblemsSolved;
    }

    public void setMediumProblemsSolved(Integer mediumProblemsSolved) {
        this.mediumProblemsSolved = mediumProblemsSolved;
    }

    public Integer getHardProblemsSolved() {
        return hardProblemsSolved;
    }

    public void setHardProblemsSolved(Integer hardProblemsSolved) {
        this.hardProblemsSolved = hardProblemsSolved;
    }

    public Double getLeetcodeScore() {
        return leetcodeScore;
    }

    public void setLeetcodeScore(Double leetcodeScore) {
        this.leetcodeScore = leetcodeScore;
    }

    public Double getAptiTest1Score() {
        return aptiTest1Score;
    }

    public void setAptiTest1Score(Double aptiTest1Score) {
        this.aptiTest1Score = aptiTest1Score;
    }

    public Double getAptiTest2Score() {
        return aptiTest2Score;
    }

    public void setAptiTest2Score(Double aptiTest2Score) {
        this.aptiTest2Score = aptiTest2Score;
    }

    public Double getAptiTest3Score() {
        return aptiTest3Score;
    }

    public void setAptiTest3Score(Double aptiTest3Score) {
        this.aptiTest3Score = aptiTest3Score;
    }

    public Double getTechAptiTest1Score() {
        return techAptiTest1Score;
    }

    public void setTechAptiTest1Score(Double techAptiTest1Score) {
        this.techAptiTest1Score = techAptiTest1Score;
    }

    public Double getTechAptiTest2Score() {
        return techAptiTest2Score;
    }

    public void setTechAptiTest2Score(Double techAptiTest2Score) {
        this.techAptiTest2Score = techAptiTest2Score;
    }

    public Double getTechAptiTest3Score() {
        return techAptiTest3Score;
    }

    public void setTechAptiTest3Score(Double techAptiTest3Score) {
        this.techAptiTest3Score = techAptiTest3Score;
    }

    public Double getProgrammingTest1Score() {
        return programmingTest1Score;
    }

    public void setProgrammingTest1Score(Double programmingTest1Score) {
        this.programmingTest1Score = programmingTest1Score;
    }

    public Double getProgrammingTest2Score() {
        return programmingTest2Score;
    }

    public void setProgrammingTest2Score(Double programmingTest2Score) {
        this.programmingTest2Score = programmingTest2Score;
    }

    public Double getProgrammingTest3Score() {
        return programmingTest3Score;
    }

    public void setProgrammingTest3Score(Double programmingTest3Score) {
        this.programmingTest3Score = programmingTest3Score;
    }
}