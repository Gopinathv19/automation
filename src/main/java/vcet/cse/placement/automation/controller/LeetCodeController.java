package vcet.cse.placement.automation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;
import vcet.cse.placement.automation.service.LeetCodeSchedulerService;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.LeetCodeWeeklyHistory;
import vcet.cse.placement.automation.repo.LeetCodeWeeklyHistoryRepository;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leetcode")
@Slf4j
public class LeetCodeController {
    
    @Autowired
    private StudentsDatabaseCollector studentsDB;

    @Autowired
    private LeetCodeWeeklyHistoryRepository leetCodeHistoryRepo;

    @Autowired
    private LeetCodeSchedulerService leetCodeService;
    
    @GetMapping("/update")
    public ResponseEntity<String> updateStats() {
        log.info("Received request to update LeetCode stats");
        try {
            // Get all students with LeetCode usernames
            List<Students> students = studentsDB.findAll().stream()
                .filter(s -> s.getLeetcodeUsername() != null && !s.getLeetcodeUsername().isEmpty())
                .collect(Collectors.toList());
                
            log.info("Found {} students with LeetCode usernames", students.size());
            
            if (students.isEmpty()) {
                return ResponseEntity.ok("No students found with LeetCode usernames");
            }

            int successCount = 0;
            for (Students student : students) {
                try {
                    Map<String, Integer> stats = leetCodeService.fetchLeetCodeStats(student.getLeetcodeUsername());
                    if (stats != null) {
                        LeetCodeWeeklyHistory history = new LeetCodeWeeklyHistory();
                        history.setStudent(student);
                        history.setEasySolved(stats.get("easySolved"));
                        history.setMediumSolved(stats.get("mediumSolved"));
                        history.setHardSolved(stats.get("hardSolved"));
                        history.setWeeklyDate(LocalDate.now());
                        
                        // Calculate LeetCode score
                        double leetcodeScore = (stats.get("easySolved") * 1.0) +
                                             (stats.get("mediumSolved") * 2.0) +
                                             (stats.get("hardSolved") * 3.0);
                        history.setLeetcodeScore(leetcodeScore);
                        
                        leetCodeHistoryRepo.save(history);
                        successCount++;
                        log.info("Successfully updated stats for {}", student.getLeetcodeUsername());
                    }
                } catch (Exception e) {
                    log.error("Error updating stats for student {}: {}", 
                        student.getLeetcodeUsername(), e.getMessage());
                }
                
                try {
                    Thread.sleep(1000); // Add delay between requests
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            return ResponseEntity.ok(String.format("Updated LeetCode stats for %d/%d students", 
                successCount, students.size()));
                
        } catch (Exception e) {
            log.error("Error updating LeetCode stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body("Error updating LeetCode stats: " + e.getMessage());
        }
    }

    @GetMapping("/stats/{universityNo}")
    public ResponseEntity<Object> getStudentStats(@PathVariable Long universityNo) {
        log.info("Fetching LeetCode stats for student: {}", universityNo);
        try {
            Optional<Students> student = studentsDB.findById(universityNo);
            if (student.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<LeetCodeWeeklyHistory> stats = leetCodeHistoryRepo.findByStudentOrderByWeeklyDateDesc(student.get());
            if (stats.isEmpty()) {
                return ResponseEntity.ok("No LeetCode stats found for student");
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching stats: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error fetching stats: " + e.getMessage());
        }
    }

    @GetMapping("/stats/latest/{universityNo}")
    public ResponseEntity<Object> getLatestStats(@PathVariable Long universityNo) {
        log.info("Fetching latest LeetCode stats for student: {}", universityNo);
        try {
            Optional<Students> student = studentsDB.findById(universityNo);
            if (student.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<LeetCodeWeeklyHistory> latestStats = 
                leetCodeHistoryRepo.findFirstByStudentOrderByWeeklyDateDesc(student.get());
            
            if (latestStats.isEmpty()) {
                return ResponseEntity.ok("No LeetCode stats found for student");
            }
            return ResponseEntity.ok(latestStats.get());
        } catch (Exception e) {
            log.error("Error fetching latest stats: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error fetching latest stats: " + e.getMessage());
        }
    }

    @GetMapping("/stats/all")
    public ResponseEntity<Object> getAllStats() {
        log.info("Fetching all LeetCode stats");
        try {
            List<LeetCodeWeeklyHistory> allStats = leetCodeHistoryRepo.findAllByOrderByWeeklyDateDesc();
            if (allStats.isEmpty()) {
                return ResponseEntity.ok("No LeetCode stats found");
            }
            return ResponseEntity.ok(allStats);
        } catch (Exception e) {
            log.error("Error fetching all stats: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error fetching all stats: " + e.getMessage());
        }
    }

    @GetMapping("/stats/date/{date}")
    public ResponseEntity<?> getStatsByDate(@PathVariable String date) {
        log.info("Fetching LeetCode stats for date: {}", date);
        try {
            LocalDate statsDate = LocalDate.parse(date);
            List<LeetCodeWeeklyHistory> stats = leetCodeHistoryRepo.findByWeeklyDate(statsDate);
            if (stats.isEmpty()) {
                return ResponseEntity.ok("No LeetCode stats found for date: " + date);
            }
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching stats by date: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error fetching stats by date: " + e.getMessage());
        }
    }
} 