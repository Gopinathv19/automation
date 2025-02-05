package  vcet.cse.placement.automation.service;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.StudentScores;
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

@CrossOrigin
@Service
public class HomeService{
    @Autowired
    private StudentsDatabaseCollector studentsDB;

      
   /* Method to get the eligibility data */

   public Map<String,Integer> getEligibility(){
      Map<String,Integer> eligibility = new HashMap<>();
      eligibility.put("eligible",180);
      eligibility.put("notEligible",20);

      return eligibility;
   }

  /* Method to get the gender score data */

  public Map<String,Integer> getGenderScore(){
      Map<String,Integer> genderScore = new HashMap<>();
      genderScore.put("boys",60);   
      genderScore.put("girls",40);

      return genderScore;

    }                       
   

    /* Method to get the chart data for the home profile page */

 
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
        
        // 2. Bar Chart Data - Aptitude Performance by Class
        Map<String, Double> classAptitudeScores = batchStudents.stream()
            .collect(Collectors.groupingBy(
                Students::getClassName,
                Collectors.averagingDouble(student -> 
                    student.getStudentScores().stream()
                        .mapToDouble(StudentScores::getScore)
                        .average()
                        .orElse(0.0)
                )
            ));
        
        List<Double> barData = new ArrayList<>(classAptitudeScores.values());
        chartData.put("barData", barData);
        
        // 3. Line Chart Data - Weekly Performance Trends
        Map<String, List<Double>> lineData = new HashMap<>();
        
        // LeetCode weekly averages
        List<Double> leetcodeScores = new ArrayList<>();
        for (int week = 1; week <= 6; week++) {
            double weekAvg = batchStudents.stream()
                .mapToDouble(Students::getLeetcodeScore)
                .average()
                .orElse(0.0);
            leetcodeScores.add(weekAvg);
        }
        
        // Aptitude weekly averages
        List<Double> aptitudeScores = new ArrayList<>();
        for (int week = 1; week <= 6; week++) {
            double weekAvg = batchStudents.stream()
                .flatMap(s -> s.getStudentScores().stream())
                .mapToDouble(StudentScores::getScore)
                .average()
                .orElse(0.0);
            aptitudeScores.add(weekAvg);
        }
        
        // Overall weekly averages
        List<Double> overallScores = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            double overall = (leetcodeScores.get(i) + aptitudeScores.get(i)) / 2.0;
            overallScores.add(overall);
        }
        
        lineData.put("leetcode", leetcodeScores);
        lineData.put("aptitude", aptitudeScores);
        lineData.put("overall", overallScores);
        
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
                
                /* Get Aptitude topper */
    
                Students aptitudeTopper = studentsDB.findTopAptitudeStudentByBatch(batch);
                if (aptitudeTopper != null) {
                    Map<String, Object> aptitudeTopperMap = new HashMap<>();
                    aptitudeTopperMap.put("name", aptitudeTopper.getName());
                    aptitudeTopperMap.put("title", "Aptitude Topper");
                    aptitudeTopperMap.put("univNo", aptitudeTopper.getUniversityNo());
                    aptitudeTopperMap.put("score", studentsDB.getAverageAptitudeScore(aptitudeTopper.getUniversityNo()));
                    aptitudeTopperMap.put("greets", "Congratulations " + aptitudeTopper.getName());
                    toppers.add(aptitudeTopperMap);
                }
                
                return toppers;
            }


     public List<Map<String, Object>> getAllAptitudeScores(int batch) {
                return studentsDB.findAllAptitudeScoresByBatch(batch);
            }
        
            public List<Map<String, Object>> getAllLeetcodeScores(int batch) {
                return studentsDB.findAllLeetcodeScoresByBatch(batch);
            }
}