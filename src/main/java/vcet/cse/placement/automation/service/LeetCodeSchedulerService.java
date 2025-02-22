package vcet.cse.placement.automation.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    
    private final String LEETCODE_API = "https://leetcode.com/graphql";
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Scheduled(cron = "0 0 0 * * SUN") // Run every Sunday at midnight
    @Transactional
    public void updateWeeklyLeetCodeStats() {
        log.info("Starting weekly LeetCode stats update");
        List<Students> students = studentsDB.findAll();
        
        for (Students student : students) {
            try {
                if (student.getLeetcodeUsername() == null || student.getLeetcodeUsername().isEmpty()) {
                    log.warn("Skipping student {} - No LeetCode username", student.getRollNo());
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
                } else {
                    log.warn("Failed to fetch LeetCode stats for student: {}", student.getRollNo());
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
    
    @Transactional
    public void fetchAndUpdateLeetCodeStats(Students student) throws Exception {
        String query = String.format("""
            {"query": "{
              matchedUser(username: \"%s\") {
                username
                submitStats: submitStatsGlobal {
                  acSubmissionNum {
                    difficulty
                    count
                    submissions
                  }
                }
              }
            }"}
            """, student.getLeetcodeUsername());
            
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<String> request = new HttpEntity<>(query, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(
            LEETCODE_API,
            request,
            String.class
        );
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        
        JsonNode submissions = root.path("data")
                                 .path("matchedUser")
                                 .path("submitStats")
                                 .path("acSubmissionNum");
        
        int easySolved = 0;
        int mediumSolved = 0;
        int hardSolved = 0;
        
        for (JsonNode submission : submissions) {
            String difficulty = submission.path("difficulty").asText();
            int count = submission.path("count").asInt();
            
            switch (difficulty.toLowerCase()) {
                case "easy" -> easySolved = count;
                case "medium" -> mediumSolved = count;
                case "hard" -> hardSolved = count;
            }
        }
        
        // Update the student's current stats
        studentsDB.updateStudent(
            student.getUniversityNo(),
            student.getName(),
            student.getClassName(),
            student.getRollNo(),
            student.getGender(),
            student.getLeetcodeUsername(),
            student.getBatch(),
            easySolved,
            mediumSolved,
            hardSolved
        );
        
        // Weekly history will be handled by WeeklySchedulerService
    }

    public Map<String, Integer> fetchLeetCodeStats(String username) {
        if (username == null || username.isEmpty()) {
            log.warn("Empty LeetCode username provided");
            return null;
        }

        try {
            String graphqlQuery = """
                {
                    "query": "query getUserProfile($username: String!) {
                        matchedUser(username: $username) {
                            submitStats {
                                acSubmissionNum {
                                    difficulty
                                    count
                                }
                            }
                        }
                    }",
                    "variables": {
                        "username": "%s"
                    }
                }""".formatted(username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                LEETCODE_API,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode stats = root.path("data")
                                   .path("matchedUser")
                                   .path("submitStats")
                                   .path("acSubmissionNum");

                Map<String, Integer> results = new HashMap<>();
                results.put("easySolved", 0);
                results.put("mediumSolved", 0);
                results.put("hardSolved", 0);

                for (JsonNode stat : stats) {
                    String difficulty = stat.get("difficulty").asText();
                    int count = stat.get("count").asInt();
                    
                    switch (difficulty) {
                        case "Easy" -> results.put("easySolved", count);
                        case "Medium" -> results.put("mediumSolved", count);
                        case "Hard" -> results.put("hardSolved", count);
                    }
                }

                log.info("Successfully fetched stats for {}: {}", username, results);
                return results;
            }
            
            log.warn("Failed to fetch LeetCode stats for {}", username);
            return null;

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
