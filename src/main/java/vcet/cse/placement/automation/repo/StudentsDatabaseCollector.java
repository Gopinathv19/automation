package vcet.cse.placement.automation.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.LeetCodeWeeklyHistory;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Repository
public interface StudentsDatabaseCollector extends JpaRepository<Students, Long> {

    // this is used to search the students in the Data Base
    @Query("SELECT s FROM Students s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "CAST(s.universityNo AS string) LIKE CONCAT('%', :searchTerm, '%') OR " +
            "LOWER(s.leetcodeUsername) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Students> searchStudents(@Param("searchTerm") String searchTerm);



    // this is used to return all the students with their respective leetcode scores and stats
    @Query("SELECT NEW map(s.name as name, " +
            "s.universityNo as universityNo, " +
            "s.easyProblemsSolved as easy, " +
            "s.mediumProblemsSolved as medium, " +
            "s.hardProblemsSolved as hard, " +
            "(s.easyProblemsSolved + s.mediumProblemsSolved + s.hardProblemsSolved) as total, " +
            "s.leetcodeScore as score) " +
            "FROM Students s " +
            "WHERE s.leetcodeUsername IS NOT NULL " +
            "ORDER BY s.leetcodeScore DESC")
    List<Map<String, Object>> findAllLeetcodeScores();


    // this also used to search the student but i dont use this
    @Query("SELECT s FROM Students s WHERE s.universityNo = :universityNo")
    Students findByUniversityNo(@Param("universityNo") Long universityNo);



   // this is used to return the top peformer of the leetocde
    @Query("SELECT s FROM Students s WHERE s.batch = :batch AND s.leetcodeScore = " +
            "(SELECT MAX(s2.leetcodeScore) FROM Students s2 WHERE s2.batch = :batch)")
    Students findTopLeetcodeStudentByBatch(@Param("batch") int batch);


    // used to return the list of the  student for the table
    @Query("SELECT new map(" +
            "s.universityNo as universityNo, " +
            "s.name as name, " +
            "s.className as className, " +
            "s.rollNo as rollNo, " +
            "s.easyProblemsSolved as easyProblemsSolved, " +
            "s.mediumProblemsSolved as mediumProblemsSolved, " +
            "s.hardProblemsSolved as hardProblemsSolved, " +
            "(s.easyProblemsSolved + s.mediumProblemsSolved + s.hardProblemsSolved) as totalProblemsSolved, " +
            "(s.easyProblemsSolved + s.mediumProblemsSolved * 2 + s.hardProblemsSolved * 3) as leetcodeScore) " +
            "FROM Students s " +
            "WHERE s.batch = :batch")
    List<Map<String, Object>> findAllLeetcodeScoresByBatch(@Param("batch") int batch);


    // this will help me to update the scores of the students once i have fetched from the leetcode api
    @Modifying
    @Query("UPDATE Students s SET " +
            "s.name = :name, " +
            "s.className = :className, " +
            "s.rollNo = :rollNo, " +
            "s.gender = :gender, " +
            "s.leetcodeUsername = :leetcodeUsername, " +
            "s.batch = :batch, " +
            "s.easyProblemsSolved = :easyProblemsSolved, " +
            "s.mediumProblemsSolved = :mediumProblemsSolved, " +
            "s.hardProblemsSolved = :hardProblemsSolved " +
            "WHERE s.universityNo = :universityNo")
    void updateStudent(
            @Param("universityNo") Long universityNo,
            @Param("name") String name,
            @Param("className") String className,
            @Param("rollNo") String rollNo,
            @Param("gender") String gender,
            @Param("leetcodeUsername") String leetcodeUsername,
            @Param("batch") Integer batch,
            @Param("easyProblemsSolved") Integer easyProblemsSolved,
            @Param("mediumProblemsSolved") Integer mediumProblemsSolved,
            @Param("hardProblemsSolved") Integer hardProblemsSolved
    );

    // Add the weekly history check query
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM LeetCodeWeeklyHistory l WHERE l.student = ?1 AND l.weeklyDate = ?2")
    boolean existsWeeklyHistoryByStudentAndDate(Students student, LocalDate date);

    // Add method to save weekly history
    @Modifying
    @Query("INSERT INTO LeetCodeWeeklyHistory (student, easySolved, mediumSolved, hardSolved, totalSolved, leetcodeScore, weeklyDate) " +
           "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)")
    void saveWeeklyHistory(Students student, Integer easySolved, Integer mediumSolved, Integer hardSolved, Integer totalSolved, Double leetcodeScore, LocalDate weeklyDate);

    // Find all students who have a LeetCode username
    List<Students> findByLeetcodeUsernameIsNotNull();
    
    // Find students by LeetCode username
    List<Students> findByLeetcodeUsername(String leetcodeUsername);
}


