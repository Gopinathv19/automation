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
import java.time.temporal.ChronoField;

@CrossOrigin
@Service
public class HomeService{
    @Autowired
    private StudentsDatabaseCollector studentsDB;
      public Map<String, Object> getChartData(int batch) {
        Map<String, Object> chartData = new HashMap<>();
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        // 1. Get all students for the batch
        List<Students> batchStudents = studentsDB.findAll().stream()
                .filter(student -> student.getBatch() == batch)
                .collect(Collectors.toList());

        // 2. Calculate Donut Chart Data (total performance)
        Map<String, Integer> classLeetcodeScores = batchStudents.stream()
                .collect(Collectors.groupingBy(
                        Students::getClassName,
                        Collectors.summingInt(student -> 
                                student.getEasyProblemsSolved() + 
                                student.getMediumProblemsSolved() + 
                                student.getHardProblemsSolved()
                        )
                ));

        // Convert to percentages for donut chart
        int totalProblems = classLeetcodeScores.values().stream()
                .mapToInt(Integer::intValue).sum();
        List<Integer> donutData = new ArrayList<>();
        if (totalProblems > 0) {
            donutData = classLeetcodeScores.values().stream()
                    .map(problems -> (problems * 100) / totalProblems)
                    .collect(Collectors.toList());
        } else {
            donutData = Arrays.asList(0, 0, 0); // For A, B, C sections
        }
        chartData.put("donutData", donutData);

        // 3. Calculate Line Chart Data (weekly progress for current month)
        Map<String, List<Double>> weeklyScoresByClass = new HashMap<>();
        weeklyScoresByClass.put("csea", new ArrayList<>());
        weeklyScoresByClass.put("cseb", new ArrayList<>());
        weeklyScoresByClass.put("csec", new ArrayList<>());

        // Get all weeks in the current month
        List<LocalDate> weeksInMonth = getWeeksInCurrentMonth(currentDate);

        // Calculate weekly scores for each class
        for (LocalDate weekStart : weeksInMonth) {
            LocalDate weekEnd = weekStart.plusDays(6);

            // Calculate scores for each class
            Map<String, Double> classScores = batchStudents.stream()
                    .collect(Collectors.groupingBy(
                            Students::getClassName,
                            Collectors.averagingDouble(student -> 
                                    student.getWeeklyHistory().stream()
                                            .filter(history -> 
                                                    !history.getWeeklyDate().isBefore(weekStart) && 
                                                    !history.getWeeklyDate().isAfter(weekEnd))
                                            .mapToDouble(LeetCodeWeeklyHistory::getLeetcodeScore)
                                            .average()
                                            .orElse(0.0)
                            )
                    ));

            // Add scores to respective lists
            weeklyScoresByClass.get("csea").add(classScores.getOrDefault("CSE A", 0.0));
            weeklyScoresByClass.get("cseb").add(classScores.getOrDefault("CSE B", 0.0));
            weeklyScoresByClass.get("csec").add(classScores.getOrDefault("CSE C", 0.0));
        }

        // Add month name and weekly data to chart data
        Map<String, Object> lineData = new HashMap<>();
        lineData.put("monthName", currentDate.getMonth().toString());
        lineData.put("weeks", weeksInMonth.stream()
                .map(date -> "Week " + date.get(ChronoField.ALIGNED_WEEK_OF_MONTH))
                .collect(Collectors.toList()));
        lineData.put("csea", weeklyScoresByClass.get("csea"));
        lineData.put("cseb", weeklyScoresByClass.get("cseb"));
        lineData.put("csec", weeklyScoresByClass.get("csec"));

        chartData.put("lineData", lineData);
        return chartData;
    }

    // Helper method to get weeks in current month
    private List<LocalDate> getWeeksInCurrentMonth(LocalDate date) {
        List<LocalDate> weeks = new ArrayList<>();
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        LocalDate currentWeekStart = firstDayOfMonth;
        while (!currentWeekStart.isAfter(lastDayOfMonth)) {
            weeks.add(currentWeekStart);
            currentWeekStart = currentWeekStart.plusWeeks(1);
        }
        return weeks;
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