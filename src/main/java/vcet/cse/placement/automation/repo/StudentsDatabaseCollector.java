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
           "CAST(s.universityNo AS string) LIKE CONCAT('%', :searchTerm, '%')")
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
}