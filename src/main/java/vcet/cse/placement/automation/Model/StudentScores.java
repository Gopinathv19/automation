package vcet.cse.placement.automation.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;

@Entity
@Table(name="student_scores")
public class StudentScores {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="university_no",nullable=false)
    private Students student;

    @Column(name="test_name")
    private String testName;

    @Column(name="score")
    private Double score;

    public StudentScores(){}

    public StudentScores(Students student, String testName, Double score){
        this.student=student;
        this.testName=testName;
        this.score=score;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id=id;
    }
    public void setStudent(Students student){
        this.student=student;
    }
    public void setTestName(String testName){
        this.testName=testName;
    }
    public String getTestName(){
        return testName;
    }
    public void setScore(Double score){
        this.score=score;
    }
    public Double getScore(){
        return score;
    }

    public Students getStudent(){
        return student;
    }
    
}
