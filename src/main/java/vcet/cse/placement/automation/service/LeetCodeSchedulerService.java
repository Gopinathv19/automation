package vcet.cse.placement.automation.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.LeetCodeWeeklyHistory;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.repo.LeetCodeWeeklyHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
 

@Service
@Slf4j
public class LeetCodeSchedulerService {

    @Autowired
    private StudentsDatabaseCollector studentsDB;
    
    @Autowired
    private LeetCodeWeeklyHistoryRepository leetCodeHistoryRepo;
    
    @Scheduled(cron = "0 0 0 * * SUN") // Run every Sunday at midnight
    @Transactional
    public void updateWeeklyLeetCodeStats() {
        log.info("Starting weekly LeetCode stats update");
        List<Students> students = studentsDB.findAll();
        
        for (Students student : students) {
            try {
                if (student.getLeetcodeUsername() == null || student.getLeetcodeUsername().isEmpty()) {
                    log.warn("Skipping student {} - No LeetCode username", student.getUniversityNo());
                    continue;
                }

                Map<String, Integer> stats = fetchLeetCodeStats(student.getLeetcodeUsername());
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
                    log.info("Successfully updated LeetCode stats for student: {}", student.getRollNo());
                }
            } catch (Exception e) {
                log.error("Error updating LeetCode stats for student: {} - {}", 
                    student.getRollNo(), e.getMessage());
            }
            
            try {
                Thread.sleep(500); // Delay between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Completed weekly LeetCode stats update");
    }

    public Map<String, Integer> fetchLeetCodeStats(String username) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://leetcode-stats-api.herokuapp.com/" + username;
            
            String response = restTemplate.getForObject(url, String.class);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            
            Map<String, Integer> results = new HashMap<>();
            results.put("easySolved", root.path("eassySolved").asInt());
            results.put("mediumSolved", root.path("mediumSolved").asInt());
            results.put("hardSolved", root.path("hardSolved").asInt());
            
            return results;
            
        } catch (Exception e) {
            log.error("Error fetching LeetCode stats for {}: {}", username, e.getMessage());
            return null;
        }
    }

    // Get weekly progress for a student
    public List<LeetCodeWeeklyHistory> getStudentWeeklyProgress(Long universityNo) {
        return leetCodeHistoryRepo.findLastSevenRecords(universityNo);
    }

    // Get progress between dates
    public List<LeetCodeWeeklyHistory> getProgressBetweenDates(
        Long universityNo, 
        LocalDate startDate, 
        LocalDate endDate
    ) {
        return leetCodeHistoryRepo.findRecordsBetweenDates(universityNo, startDate, endDate);
    }
}
