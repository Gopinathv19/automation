package vcet.cse.placement.automation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcet.cse.placement.automation.Model.Students;
 
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.exception.StudentNotFoundException;

import java.util.*;
 

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

 

 

 

 

    public Map<String, Object> getChartData() {
        List<Students> allStudents = studentsDB.findAll();
        Map<String, Object> chartData = new HashMap<>();
        
        // Calculate LeetCode difficulty distribution
        int totalProblems = allStudents.stream()
            .mapToInt(Students::getTotalProblemsSolved)
            .sum();
        
        if (totalProblems > 0) {
            int totalEasy = allStudents.stream()
                .mapToInt(Students::getEasyProblemsSolved)
                .sum();
            int totalMedium = allStudents.stream()
                .mapToInt(Students::getMediumProblemsSolved)
                .sum();
            int totalHard = allStudents.stream()
                .mapToInt(Students::getHardProblemsSolved)
                .sum();
                
            List<Integer> donutData = Arrays.asList(
                (totalEasy * 100) / totalProblems,
                (totalMedium * 100) / totalProblems,
                (totalHard * 100) / totalProblems
            );
            chartData.put("donutData", donutData);
        }
        
        // Calculate class-wise performance
        Map<String, List<Double>> classPerformance = new HashMap<>();
 
            
        chartData.put("classPerformance", classPerformance);
        
 

        Map<String, List<Double>> weeklyTrends = new HashMap<>();
        List<Double> leetcodeScores = new ArrayList<>();
        List<Double> aptitudeScores = new ArrayList<>();
        List<Double> overallScores = new ArrayList<>();

 

        weeklyTrends.put("leetcode", leetcodeScores);
        weeklyTrends.put("aptitude", aptitudeScores);
        weeklyTrends.put("overall", overallScores);
        
        // Add week labels
        List<String> weekLabels = new ArrayList<>();
 
        
        chartData.put("weeklyTrends", weeklyTrends);
        chartData.put("weekLabels", weekLabels);
        
        return chartData;
    }
        public List<Students> searchStudents(String query) {
            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return studentsDB.searchStudents(query.trim());

        }

        public ArrayList<Map<String, Object>> getToppers() {
            List<Students> allStudents = studentsDB.findAll();
            ArrayList<Map<String, Object>> toppers = new ArrayList<>();
            
            // Get LeetCode topper
            Students leetcodeTopper = allStudents.stream()
                .max(Comparator.comparing(Students::getLeetcodeScore))
                .orElse(null);
                
            if (leetcodeTopper != null) {
                Map<String, Object> leetcodeTopperMap = new HashMap<>();
                leetcodeTopperMap.put("name", leetcodeTopper.getName());
                leetcodeTopperMap.put("title", "LeetCode Topper");
                leetcodeTopperMap.put("univNo", leetcodeTopper.getUniversityNo());
                leetcodeTopperMap.put("greets", "Congratulations " + leetcodeTopper.getName());
                toppers.add(leetcodeTopperMap);
            }
            
 
 
            
            return toppers;
        }

    public List<Map<String, Object>> getAllAptitudeScores() {
        List<Map<String, Object>> scores = studentsDB.findAllAptitudeScores();
        
        // Calculate total and average for each student
        scores.forEach(score -> {
            Double test1 = (Double) score.get("test1");
            Double test2 = (Double) score.get("test2");
            Double test3 = (Double) score.get("test3");
            score.put("totalScore", test1 + test2 + test3);
        });
        
        return scores;
    }

    public List<Map<String, Object>> getAllLeetcodeScores() {
        List<Map<String, Object>> scores = studentsDB.findAllLeetcodeScores();
        
        // Add percentage calculations
        scores.forEach(score -> {
            Integer easy = (Integer) score.get("easy");
            Integer medium = (Integer) score.get("medium");
            Integer hard = (Integer) score.get("hard");
            score.put("easy",easy);
            score.put("medium",medium);
            score.put("hard",hard);
        });
        
        return scores;
    }

    @Transactional
    public void deleteStudent(Long universityNo) {
        if (!studentsDB.existsById(universityNo)) {
            throw new StudentNotFoundException(universityNo);
        }
        studentsDB.deleteById(universityNo);
    }
}

