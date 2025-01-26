package vcet.cse.placement.automation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import vcet.cse.placement.automation.exception.StudentNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
    
    // Update other scores
    existingStudent.setAptiTest1Score(studentUpdate.getAptiTest1Score());
    existingStudent.setAptiTest2Score(studentUpdate.getAptiTest2Score());
    existingStudent.setAptiTest3Score(studentUpdate.getAptiTest3Score());
    existingStudent.setTechAptiTest1Score(studentUpdate.getTechAptiTest1Score());
    existingStudent.setTechAptiTest2Score(studentUpdate.getTechAptiTest2Score());
    existingStudent.setTechAptiTest3Score(studentUpdate.getTechAptiTest3Score());
    existingStudent.setProgrammingTest1Score(studentUpdate.getProgrammingTest1Score());
    existingStudent.setProgrammingTest2Score(studentUpdate.getProgrammingTest2Score());
    existingStudent.setProgrammingTest3Score(studentUpdate.getProgrammingTest3Score());
    
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
    public Map<String, Object> getEligibility() {
        List<Students> allStudents = studentsDB.findAll();
        Map<String, Object> eligibility = new HashMap<>();
        
        // A student is eligible if their total score is above 60%
        long eligibleCount = allStudents.stream()
            .filter(student -> {
                double leetcodeScore = student.getLeetcodeScore();
                double aptiScore = student.getAverageAptitudeScore();
                double overallScore = (leetcodeScore + aptiScore) / 2;
                return overallScore >= 60.0;
            })
            .count();
        
        eligibility.put("eligible", eligibleCount);
        eligibility.put("notEligible", allStudents.size() - eligibleCount);
        return eligibility;
    }
    public Map<String, Object> getGenderPerformanceScore() {
        List<Students> allStudents = studentsDB.findAll();
        Map<String, Object> genderPerformanceScore = new HashMap<>();
        
        // Calculate average scores for male students
        double maleAverageScore = allStudents.stream()
            .filter(s -> "Male".equalsIgnoreCase(s.getGender()))
            .mapToDouble(s -> (s.getLeetcodeScore() + s.getAverageAptitudeScore() + s.getAverageTechnicalScore()) / 3)
            .average()
            .orElse(0.0);
        
        // Calculate average scores for female students
        double femaleAverageScore = allStudents.stream()
            .filter(s -> "Female".equalsIgnoreCase(s.getGender()))
            .mapToDouble(s -> (s.getLeetcodeScore() + s.getAverageAptitudeScore() + s.getAverageTechnicalScore()) / 3)
            .average()
            .orElse(0.0);
        
        genderPerformanceScore.put("boysAverageScore", maleAverageScore);
        genderPerformanceScore.put("girlsAverageScore", femaleAverageScore);
        
        return genderPerformanceScore;
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

    @Transactional
    public void updateAptitudeScores(Long universityNo, Students aptiUpdate) {
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
        
        if (aptiUpdate.getAptiTest1Score() == null && 
            aptiUpdate.getAptiTest2Score() == null && 
            aptiUpdate.getAptiTest3Score() == null) {
            throw new IllegalArgumentException("At least one aptitude score must be provided");
        }
        
        // Update only if the new score is not null
        if (aptiUpdate.getAptiTest1Score() != null) {
            student.setAptiTest1Score(aptiUpdate.getAptiTest1Score());
        }
        if (aptiUpdate.getAptiTest2Score() != null) {
            student.setAptiTest2Score(aptiUpdate.getAptiTest2Score());
        }
        if (aptiUpdate.getAptiTest3Score() != null) {
            student.setAptiTest3Score(aptiUpdate.getAptiTest3Score());
        }
        
        studentsDB.save(student);
    }

    @Transactional
    public void updateTechAptitudeScores(Long universityNo, Students techAptiUpdate) {
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
        
        if (techAptiUpdate.getTechAptiTest1Score() == null && 
            techAptiUpdate.getTechAptiTest2Score() == null && 
            techAptiUpdate.getTechAptiTest3Score() == null) {
            throw new IllegalArgumentException("At least one technical score must be provided");
        }
        
        // Update only if the new score is not null
        if (techAptiUpdate.getTechAptiTest1Score() != null) {
            student.setTechAptiTest1Score(techAptiUpdate.getTechAptiTest1Score());
        }
        if (techAptiUpdate.getTechAptiTest2Score() != null) {
            student.setTechAptiTest2Score(techAptiUpdate.getTechAptiTest2Score());
        }
        if (techAptiUpdate.getTechAptiTest3Score() != null) {
            student.setTechAptiTest3Score(techAptiUpdate.getTechAptiTest3Score());
        }
        
        studentsDB.save(student);
    }

    @Transactional
    public void updateProgrammingScores(Long universityNo, Students progUpdate) {
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
        
        if (progUpdate.getProgrammingTest1Score() == null && 
            progUpdate.getProgrammingTest2Score() == null && 
            progUpdate.getProgrammingTest3Score() == null) {
            throw new IllegalArgumentException("At least one programming score must be provided");
        }
        
        // Update only if the new score is not null
        if (progUpdate.getProgrammingTest1Score() != null) {
            student.setProgrammingTest1Score(progUpdate.getProgrammingTest1Score());
        }
        if (progUpdate.getProgrammingTest2Score() != null) {
            student.setProgrammingTest2Score(progUpdate.getProgrammingTest2Score());
        }
        if (progUpdate.getProgrammingTest3Score() != null) {
            student.setProgrammingTest3Score(progUpdate.getProgrammingTest3Score());
        }
        
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

    public Map<String, Object> getStudentAptitudeData(Long universityNo) {
        Students student = getStudentByUniversityNo(universityNo);
        Map<String, Object> aptitudeData = new HashMap<>();
        
        aptitudeData.put("universityNo", student.getUniversityNo());
        aptitudeData.put("name", student.getName());
        aptitudeData.put("aptiTest1Score", student.getAptiTest1Score());
        aptitudeData.put("aptiTest2Score", student.getAptiTest2Score());
        aptitudeData.put("aptiTest3Score", student.getAptiTest3Score());
        
        return aptitudeData;
    }

    public Map<String, Object> getStudentTechnicalData(Long universityNo) {
        Students student = getStudentByUniversityNo(universityNo);
        Map<String, Object> technicalData = new HashMap<>();
        
        technicalData.put("universityNo", student.getUniversityNo());
        technicalData.put("name", student.getName());
        technicalData.put("techAptiTest1Score", student.getTechAptiTest1Score());
        technicalData.put("techAptiTest2Score", student.getTechAptiTest2Score());
        technicalData.put("techAptiTest3Score", student.getTechAptiTest3Score());
        
        return technicalData;
    }

    public Map<String, Object> getStudentProgrammingData(Long universityNo) {
        Students student = getStudentByUniversityNo(universityNo);
        Map<String, Object> programmingData = new HashMap<>();
        
        programmingData.put("universityNo", student.getUniversityNo());
        programmingData.put("name", student.getName());
        programmingData.put("programmingTest1Score", student.getProgrammingTest1Score());
        programmingData.put("programmingTest2Score", student.getProgrammingTest2Score());
        programmingData.put("programmingTest3Score", student.getProgrammingTest3Score());
        
        return programmingData;
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
        allStudents.stream()
            .collect(Collectors.groupingBy(Students::getClassName))
            .forEach((className, students) -> {
                double avgLeetcode = students.stream()
                    .mapToDouble(Students::getLeetcodeScore)
                    .average()
                    .orElse(0.0);
                    
                double avgAptitude = students.stream()
                    .mapToDouble(Students::getAverageAptitudeScore)
                    .average()
                    .orElse(0.0);
                    
                double avgTechnical = students.stream()
                    .mapToDouble(Students::getAverageTechnicalScore)
                    .average()
                    .orElse(0.0);
                    
                classPerformance.put(className, Arrays.asList(
                    avgLeetcode,
                    avgAptitude,
                    avgTechnical
                ));
            });
            
        chartData.put("classPerformance", classPerformance);
        
        // Calculate weekly trends for current month
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        int weeksInMonth = (int) ChronoUnit.WEEKS.between(startOfMonth, now) + 1;

        Map<String, List<Double>> weeklyTrends = new HashMap<>();
        List<Double> leetcodeScores = new ArrayList<>();
        List<Double> aptitudeScores = new ArrayList<>();
        List<Double> overallScores = new ArrayList<>();

        for (int week = 0; week < weeksInMonth; week++) {
            LocalDate weekStart = startOfMonth.plusWeeks(week);
            LocalDate weekEnd = weekStart.plusWeeks(1);

            // Calculate LeetCode average for the week
            double weekLeetcodeAvg = allStudents.stream()
                .filter(s -> {
                    LocalDateTime updated = s.getLastUpdated();
                    return updated != null &&
                           updated.toLocalDate().isAfter(weekStart.minusDays(1)) &&
                           updated.toLocalDate().isBefore(weekEnd);
                })
                .mapToDouble(Students::getLeetcodeScore)
                .average()
                .orElse(0.0);
            leetcodeScores.add(weekLeetcodeAvg);

            // Calculate Aptitude average for the week
            double weekAptitudeAvg = allStudents.stream()
                .filter(s -> {
                    LocalDateTime updated = s.getLastUpdated();
                    return updated != null &&
                           updated.toLocalDate().isAfter(weekStart.minusDays(1)) &&
                           updated.toLocalDate().isBefore(weekEnd);
                })
                .mapToDouble(Students::getAverageAptitudeScore)
                .average()
                .orElse(0.0);
            aptitudeScores.add(weekAptitudeAvg);

            // Calculate Overall average
            double weekOverallAvg = (weekLeetcodeAvg + weekAptitudeAvg) / 2;
            overallScores.add(weekOverallAvg);
        }

        weeklyTrends.put("leetcode", leetcodeScores);
        weeklyTrends.put("aptitude", aptitudeScores);
        weeklyTrends.put("overall", overallScores);
        
        // Add week labels
        List<String> weekLabels = new ArrayList<>();
        for (int week = 0; week < weeksInMonth; week++) {
            LocalDate weekStart = startOfMonth.plusWeeks(week);
            weekLabels.add("Week " + (week + 1) + " (" + weekStart.format(DateTimeFormatter.ofPattern("MMM dd")) + ")");
        }
        
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
            
            // Get Aptitude topper
            Students aptitudeTopper = allStudents.stream()
                .max(Comparator.comparing(Students::getAverageAptitudeScore))
                .orElse(null);
                
            if (aptitudeTopper != null) {
                Map<String, Object> aptitudeTopperMap = new HashMap<>();
                aptitudeTopperMap.put("name", aptitudeTopper.getName());
                aptitudeTopperMap.put("title", "Aptitude Topper");
                aptitudeTopperMap.put("univNo", aptitudeTopper.getUniversityNo());
                aptitudeTopperMap.put("greets", "Congratulations " + aptitudeTopper.getName());
                toppers.add(aptitudeTopperMap);
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

