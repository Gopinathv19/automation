package vcet.cse.placement.automation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vcet.cse.placement.automation.Model.LeetCodeWeeklyHistory;
import vcet.cse.placement.automation.Model.Students;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeetCodeWeeklyHistoryRepository extends JpaRepository<LeetCodeWeeklyHistory, Long> {
    
    // Find last 7 records for a student
    @Query("SELECT l FROM LeetCodeWeeklyHistory l WHERE l.student.universityNo = :universityNo " +
           "ORDER BY l.weeklyDate DESC")
    List<LeetCodeWeeklyHistory> findLastSevenRecords(@Param("universityNo") Long universityNo);

    // Find latest record for a student
    @Query("SELECT l FROM LeetCodeWeeklyHistory l WHERE l.student.universityNo = :universityNo " +
           "ORDER BY l.weeklyDate DESC LIMIT 1")
    Optional<LeetCodeWeeklyHistory> findLatestRecord(@Param("universityNo") Long universityNo);

    // Find all records for a student
    List<LeetCodeWeeklyHistory> findByStudentUniversityNoOrderByWeeklyDateDesc(Long universityNo);

    // Find records between dates
    @Query("SELECT l FROM LeetCodeWeeklyHistory l WHERE l.student.universityNo = :universityNo " +
           "AND l.weeklyDate BETWEEN :startDate AND :endDate ORDER BY l.weeklyDate DESC")
    List<LeetCodeWeeklyHistory> findRecordsBetweenDates(
        @Param("universityNo") Long universityNo,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Find records for a specific date
    List<LeetCodeWeeklyHistory> findByWeeklyDate(LocalDate date);

    // Find students who improved their score in the last week
    @Query("SELECT l FROM LeetCodeWeeklyHistory l WHERE l.weeklyDate = :currentDate " +
           "AND l.totalSolved > (SELECT prev.totalSolved FROM LeetCodeWeeklyHistory prev " +
           "WHERE prev.student = l.student AND prev.weeklyDate = :previousDate)")
    List<LeetCodeWeeklyHistory> findStudentsWithImprovement(
        @Param("currentDate") LocalDate currentDate,
        @Param("previousDate") LocalDate previousDate
    );

    List<LeetCodeWeeklyHistory> findByStudentOrderByWeeklyDateDesc(Students student);
    Optional<LeetCodeWeeklyHistory> findFirstByStudentOrderByWeeklyDateDesc(Students student);
    List<LeetCodeWeeklyHistory> findAllByOrderByWeeklyDateDesc();
}
