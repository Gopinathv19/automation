package vcet.cse.placement.automation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vcet.cse.placement.automation.Model.Students;
import java.util.List;
import java.util.Map;

@Repository
public interface StudentsDatabaseCollector extends JpaRepository<Students, Long> {
    @Query("SELECT s FROM Students s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(s.universityNo AS string) LIKE CONCAT('%', :searchTerm, '%') OR " +
           "LOWER(s.leetcodeUsername) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Students> searchStudents(@Param("searchTerm") String searchTerm);

    @Query("SELECT NEW map(s.name as name, " +
           "s.universityNo as universityNo, " +
           "s.aptiTest1Score as test1, " +
           "s.aptiTest2Score as test2, " +
           "s.aptiTest3Score as test3) " +
           "FROM Students s " +
           "ORDER BY (s.aptiTest1Score + s.aptiTest2Score + s.aptiTest3Score) DESC")
    List<Map<String, Object>> findAllAptitudeScores();

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

    @Query("SELECT s FROM Students s WHERE s.universityNo = :universityNo")
    Students findByUniversityNo(@Param("universityNo") Long universityNo);

    @Query("SELECT s.className, AVG(s.leetcodeScore) FROM Students s WHERE s.batch = :batch GROUP BY s.className")
    List<Object[]> findAverageLeetcodeScoresByClass(@Param("batch") int batch);

    @Query("SELECT s.className, AVG(sc.score) FROM Students s JOIN s.studentScores sc WHERE s.batch = :batch GROUP BY s.className")
    List<Object[]> findAverageAptitudeScoresByClass(@Param("batch") int batch);

    @Query("SELECT s FROM Students s WHERE s.batch = :batch AND s.leetcodeScore = " +
           "(SELECT MAX(s2.leetcodeScore) FROM Students s2 WHERE s2.batch = :batch)")
    Students findTopLeetcodeStudentByBatch(@Param("batch") int batch);

    @Query("SELECT s FROM Students s WHERE s.batch = :batch AND s.universityNo IN " +
           "(SELECT ss.student.universityNo FROM StudentScores ss " +
           "GROUP BY ss.student.universityNo " +
           "HAVING AVG(ss.score) = " +
           "(SELECT MAX(avg_score) FROM " +
           "(SELECT AVG(ss2.score) as avg_score FROM StudentScores ss2 " +
           "JOIN ss2.student s2 WHERE s2.batch = :batch GROUP BY ss2.student.universityNo) scores))")
    Students findTopAptitudeStudentByBatch(@Param("batch") int batch);

    @Query("SELECT AVG(ss.score) FROM StudentScores ss WHERE ss.student.universityNo = :universityNo")
    Double getAverageAptitudeScore(@Param("universityNo") Long universityNo);

    @Query("SELECT new map(" +
           "s.universityNo as universityNo, " +
           "s.name as name, " +
           "s.className as className, " +
           "s.rollNo as rollNo, " +
           "(SELECT SUM(sc.score) FROM StudentScores sc WHERE sc.student = s) as totalScore, " +
           "(SELECT AVG(sc.score) FROM StudentScores sc WHERE sc.student = s) as averageScore) " +
           "FROM Students s " +
           "WHERE s.batch = :batch")
    List<Map<String, Object>> findAllAptitudeScoresByBatch(@Param("batch") int batch);

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
}