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

    // get the entire students data

    private StudentsDatabaseCollector studentsDB;

    // post the entire students data
    
    public List<Students> getStudents() {
        return studentsDB.findAll();
    }

    // ... existing code ...

public void updateAllStudentData(Long universityNo, Students studentUpdate) {
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
    
    // Update scores
    existingStudent.setEasyProblemsSolved(studentUpdate.getEasyProblemsSolved());
    existingStudent.setMediumProblemsSolved(studentUpdate.getMediumProblemsSolved());
    existingStudent.setHardProblemsSolved(studentUpdate.getHardProblemsSolved());
    existingStudent.setLeetcodeScore(studentUpdate.calculateLeetCodeScore());
    
 
    
    studentsDB.save(existingStudent);
}

// ... existing code ...

    public void addStudents(Students students) {
        if (students.getUniversityNo() == null) {
            throw new IllegalArgumentException("University number cannot be null");
        }
        if (students.getName() == null || students.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }
        if (students.getRollNo() == null || students.getRollNo().trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number cannot be empty");
        }
        if (students.getClassName() == null || students.getClassName().trim().isEmpty()) {
            throw new IllegalArgumentException("Class name cannot be empty");
        }
        
        studentsDB.save(students);
    }

    public List<Students> getStudentsWithLeetcode() {
        return studentsDB.findAll();   
    }

    public Students getStudentWithLeetcode(Long universityNo) {
        return studentsDB.findById(universityNo)
            .orElseThrow(() -> new RuntimeException("Student not found"));
    }
 
         
    @Transactional
    public void updateLeetcode(Long universityNo, Students leetcodeUpdate) {
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
        
        if (leetcodeUpdate.getLeetcodeUsername() == null) {
            throw new IllegalArgumentException("LeetCode username cannot be null");
        }
        
        // Update LeetCode stats
        student.setLeetcodeUsername(leetcodeUpdate.getLeetcodeUsername());
        student.setEasyProblemsSolved(leetcodeUpdate.getEasyProblemsSolved());
        student.setMediumProblemsSolved(leetcodeUpdate.getMediumProblemsSolved());
        student.setHardProblemsSolved(leetcodeUpdate.getHardProblemsSolved());
        
        // Calculate and set the LeetCode score
        student.setLeetcodeScore(student.calculateLeetCodeScore());
        
        studentsDB.save(student);
    }

 

 
 

    public Students getStudentByUniversityNo(Long universityNo) {
        return studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
    }

    public Map<String, Object> getStudentLeetcodeData(Long universityNo) {
        Students student = getStudentByUniversityNo(universityNo);
        Map<String, Object> leetcodeData = new HashMap<>();
        
        leetcodeData.put("universityNo", student.getUniversityNo());
        leetcodeData.put("name", student.getName());
        leetcodeData.put("username", student.getLeetcodeUsername());
        leetcodeData.put("easyProblemsSolved", student.getEasyProblemsSolved());
        leetcodeData.put("mediumProblemsSolved", student.getMediumProblemsSolved());
        leetcodeData.put("hardProblemsSolved", student.getHardProblemsSolved());
         
        leetcodeData.put("leetcodeScore", student.getLeetcodeScore());
        
        return leetcodeData;
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

