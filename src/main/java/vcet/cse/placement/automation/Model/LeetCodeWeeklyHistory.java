package vcet.cse.placement.automation.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="leetcode_weekly_history")
public class LeetCodeWeeklyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="university_no", referencedColumnName = "university_no", nullable=false)
    @JsonBackReference
    private Students student;

    @Column(name="easy_solved", nullable = false)
    private Integer easySolved = 0;

    @Column(name="medium_solved", nullable = false)
    private Integer mediumSolved = 0;

    @Column(name="hard_solved", nullable = false)
    private Integer hardSolved = 0;

    @Column(name="total_solved", nullable = false)
    private Integer totalSolved = 0;

    @Column(name="leetcode_score", nullable = false)
    private Double leetcodeScore = 0.0;

    @Column(name="weekly_date", nullable = false)
    private LocalDate weeklyDate;

    // Default Constructor
    public LeetCodeWeeklyHistory() {}

    // Constructor with Parameters
    public LeetCodeWeeklyHistory(Students student, Integer easySolved, Integer mediumSolved, Integer hardSolved, LocalDate weeklyDate,Double leetcode_score) {
        this.student = student;
        this.easySolved = easySolved;
        this.mediumSolved = mediumSolved;
        this.hardSolved = hardSolved;
        this.totalSolved = easySolved + mediumSolved + hardSolved; // Automatically set totalSolved
        this.leetcodeScore = leetcodeScore;
        this.weeklyDate = weeklyDate;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Students getStudent() { return student; }

    public void setStudent(Students student) { this.student = student; }

    public Integer getEasySolved() { return easySolved; }

    public void setEasySolved(Integer easySolved) { 
        this.easySolved = easySolved;
        updateTotalSolved();
    }

    public Integer getMediumSolved() { return mediumSolved; }

    public void setMediumSolved(Integer mediumSolved) { 
        this.mediumSolved = mediumSolved;
        updateTotalSolved();
    }

    public Integer getHardSolved() { return hardSolved; }

    public void setHardSolved(Integer hardSolved) { 
        this.hardSolved = hardSolved;
        updateTotalSolved();
    }

    public Integer getTotalSolved() { return totalSolved; }

    private void updateTotalSolved() {
        this.totalSolved = (this.easySolved != null ? this.easySolved : 0) +
                          (this.mediumSolved != null ? this.mediumSolved : 0) +
                          (this.hardSolved != null ? this.hardSolved : 0);
    }

    public Double getLeetcodeScore() { return leetcodeScore; }

    public void setLeetcodeScore(Double leetcodeScore) { this.leetcodeScore = leetcodeScore; }

    public LocalDate getWeeklyDate() { return weeklyDate; }

    public void setWeeklyDate(LocalDate weeklyDate) { this.weeklyDate = weeklyDate; }

    // Add method to get weekly progress
    public Integer getWeeklyProgress(LeetCodeWeeklyHistory previousRecord) {
        if (previousRecord == null) return this.totalSolved;
        return this.totalSolved - previousRecord.totalSolved;
    }
}
