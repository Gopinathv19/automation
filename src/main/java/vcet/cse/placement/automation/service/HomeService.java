package  vcet.cse.placement.automation.service;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.LeetCodeWeeklyHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.time.LocalDate;
import java.util.stream.DoubleStream;

@CrossOrigin
@Service
public class HomeService{
    @Autowired
    private StudentsDatabaseCollector studentsDB;
      public Map<String, Object> getChartData(int batch) {
        List<Students> batchStudents = studentsDB.findAll().stream()
            .filter(student -> student.getBatch() == batch)
            .collect(Collectors.toList());
        
        Map<String, Object> chartData = new HashMap<>();
        
        // 1. Donut Chart Data-LeetCode Performance by Class

        Map<String, Integer> classLeetcodeScores = batchStudents.stream()
            .collect(Collectors.groupingBy(
                Students::getClassName,
                Collectors.summingInt(student -> 
                    student.getEasyProblemsSolved() + 
                    student.getMediumProblemsSolved() + 
                    student.getHardProblemsSolved()
                )
            ));
        
        // Convert to percentages for the donut chart

        int totalProblems = classLeetcodeScores.values().stream().mapToInt(Integer::intValue).sum();
        List<Integer> donutData = new ArrayList<>();
        if (totalProblems > 0) {
            donutData = classLeetcodeScores.values().stream()
                .map(problems -> (problems * 100) / totalProblems)
                .collect(Collectors.toList());
        } else {
            donutData = Arrays.asList(0, 0, 0); // For A, B, C sections
        }
        chartData.put("donutData", donutData);
        
        // 2. Line Chart Data - Weekly LeetCode Performance
        List<LocalDate> last5Weeks = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        // Get the last 5 weeks' dates
        for (int i = 0; i < 5; i++) {
            last5Weeks.add(currentDate.minusWeeks(i));
        }
        Collections.reverse(last5Weeks); // Sort from oldest to newest
        
        // Get weekly averages for LeetCode scores
        List<Double> weeklyLeetcodeScores = last5Weeks.stream()
            .map(week -> studentsDB.findAll().stream()
                .filter(student -> student.getBatch() == batch)
                .flatMap(student -> student.getWeeklyHistory().stream())
                .filter(history -> history.getWeeklyDate().equals(week))
                .mapToDouble(LeetCodeWeeklyHistory::getLeetcodeScore)
                .average()
                .orElse(0.0))
            .collect(Collectors.toList());
        
        Map<String, List<?>> lineData = new HashMap<>();
        lineData.put("leetcode", weeklyLeetcodeScores);
        lineData.put("aptitude", Arrays.asList(0, 0, 0, 0, 0)); // Placeholder for aptitude data
        lineData.put("overall", Arrays.asList(0, 0, 0, 0, 0));  // Placeholder for overall data
        
        chartData.put("lineData", lineData);
        
        return chartData;
    }



    /* function to get the student by the querying using the params */


    public List<Students> searchStudents(String query) {
            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return studentsDB.searchStudents(query.trim());

        }


    /* Method to get the toppers for the home page */

     public ArrayList<Map<String, Object>> getToppers(int batch) {
                ArrayList<Map<String, Object>> toppers = new ArrayList<>();
                
                /*Get the topper-of the leetcode */
    
                Students leetcodeTopper = studentsDB.findTopLeetcodeStudentByBatch(batch);
                if (leetcodeTopper != null) {
                    Map<String, Object> leetcodeTopperMap = new HashMap<>();
                    leetcodeTopperMap.put("name", leetcodeTopper.getName());
                    leetcodeTopperMap.put("title", "LeetCode Topper");
                    leetcodeTopperMap.put("univNo", leetcodeTopper.getUniversityNo());
                    leetcodeTopperMap.put("score", leetcodeTopper.getLeetcodeScore());
                    leetcodeTopperMap.put("greets", "Congratulations " + leetcodeTopper.getName());
                    toppers.add(leetcodeTopperMap);
                }
                return toppers;
            }


            public List<Map<String, Object>> getAllLeetcodeScores(int batch) {
                return studentsDB.findAllLeetcodeScoresByBatch(batch);
            }
}