package vcet.cse.placement.automation.Model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.*;


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
    @Column(name="name",length = 100)
    private String name;

    @NotNull
    @Column(name="gender",length = 10)
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
    @Column(name="batch",nullable =false)
    private Integer batch;



    // LeetCode score constants
        private static final double EASY_PROBLEM_SCORE = 10.0;
        private static final double MEDIUM_PROBLEM_SCORE = 50.0;
        private static final double HARD_PROBLEM_SCORE = 100.0;

    // aptitude_test_scores

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<LeetCodeWeeklyHistory> weeklyHistory = new ArrayList<>();

    public List<LeetCodeWeeklyHistory> getWeeklyHistory() {
        return weeklyHistory;
    }

    public void setWeeklyHistory(List<LeetCodeWeeklyHistory> weeklyHistory) {
        this.weeklyHistory = weeklyHistory;
    }

    public void addWeeklyHistory(Integer easy, Integer medium, Integer hard, Double score) {
        LeetCodeWeeklyHistory history = new LeetCodeWeeklyHistory(this, easy, medium, hard, score, LocalDate.now());
        weeklyHistory.add(history);
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
        return calculateLeetCodeScore();
    }

    public void setLeetcodeScore(Double leetcodeScore) {
        this.leetcodeScore =leetcodeScore;
    }

    public void setBatch(Integer batch){
        this.batch=batch;
    }

    public  Integer getBatch(){
        return this.batch;
    }
}