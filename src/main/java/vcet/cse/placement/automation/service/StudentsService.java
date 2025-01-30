package vcet.cse.placement.automation.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.Model.StudentScores;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.exception.StudentNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

 

@CrossOrigin
@Service
public class StudentsService {
@Autowired

    
     
    private StudentsDatabaseCollector studentsDB;
 
    /* Get all the students data from the data base */
    public List<Students> getStudents() {
        return studentsDB.findAll();
    }

 
    /* Add the particular student in the data base*/
    public void addStudents(Students students) {
        try{
            validateStudent(students);
        }
        catch(Exception e){
            throw e;
        }
        
        studentsDB.save(students);
    }
    /* Add all the students send in to the request */
    public void addAllStudents(List<Students> students){
        List<String> errors = new ArrayList<>();
        int rowNum = 0;
        for(Students student : students){
            rowNum++;
            try {
                validateStudent(student);
                if(studentsDB.existsById(student.getUniversityNo())){
                    errors.add(String.format("Row %d: Student with University No %d already exists", rowNum, student.getUniversityNo()));
                }
            } catch (Exception e) {
                errors.add(String.format("Row %d: %s", rowNum, e.getMessage()));
            }
        }
        
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }
        
        
        studentsDB.saveAll(students);
    }
    /* Methos to validate the students data */
    private void validateStudent(Students student) {
        List<String> validationErrors = new ArrayList<>();
    
        if (student.getUniversityNo() == null) {
            validationErrors.add("University number cannot be null");
        }
    
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            validationErrors.add("Name cannot be empty");
        }
    
        if (student.getRollNo() == null || student.getRollNo().trim().isEmpty()) {
            validationErrors.add("Roll No cannot be empty");
        }
    
        if (student.getClassName() == null || student.getClassName().trim().isEmpty()) {
            validationErrors.add("Class name cannot be empty");
        }
    
        if (student.getBatch() == null) {
            validationErrors.add("Batch cannot be null");
        }
    
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", validationErrors));
        }
    }
 
 
    /* method to update the students data for a particular student */
    public void updateStudentData(Long universityNo, Students studentUpdate) {
        Students existingStudent = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
        
        // Validate required fields
        if (studentUpdate.getName() == null || studentUpdate.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (studentUpdate.getRollNo() == null || studentUpdate.getRollNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Roll No cannot be empty");
        }
        
        // Update fields
        existingStudent.setName(studentUpdate.getName());
        existingStudent.setClassName(studentUpdate.getClassName());
        existingStudent.setRollNo(studentUpdate.getRollNo());
        existingStudent.setGender(studentUpdate.getGender());
        existingStudent.setLeetcodeUsername(studentUpdate.getLeetcodeUsername());
        existingStudent.setUniversityNo(studentUpdate.getUniversityNo());
        existingStudent.setLeetcodeUsername(studentUpdate.getLeetcodeUsername());
        existingStudent.setBatch(studentUpdate.getBatch());
        studentsDB.save(existingStudent);
    }
 

 
    /* method that gets the student information by using the university no */

    public Students getStudentByUniversityNo(Long universityNo) {
        return studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
    }


    /* Method to get the chart data for the home profile page */

 
    public Map<String, Object> getChartData(int batch) {
        List<Students> batchStudents = studentsDB.findAll().stream()
            .filter(student -> student.getBatch() == batch)
            .collect(Collectors.toList());
        
        Map<String, Object> chartData = new HashMap<>();
        
        // 1. Donut Chart Data - LeetCode Performance by Class
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




      /* function to get the student by the queryin using the params */


        public List<Students> searchStudents(String query) {
            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return studentsDB.searchStudents(query.trim());

        }

     /* Method to get the toppers for the home page */

        public ArrayList<Map<String, Object>> getToppers(int batch) {
            ArrayList<Map<String, Object>> toppers = new ArrayList<>();
            
            // Get LeetCode topper
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
            
            // Get Aptitude topper
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

    @Transactional
    public void deleteStudent(Long universityNo) {
        if (!studentsDB.existsById(universityNo)) {
            throw new StudentNotFoundException(universityNo);
        }
        studentsDB.deleteById(universityNo);
    }
}

