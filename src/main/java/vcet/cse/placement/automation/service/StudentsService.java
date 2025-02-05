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

@CrossOrigin
@Service
public class StudentsService {
@Autowired
private StudentsDatabaseCollector studentsDB;
 
    /* Get all the students data from the data-base */

    public List<Students> getStudents() {
        return studentsDB.findAll();
    }

    /* Get the students by the student id */

    public Students getStudentById(Long studentId) {
        return studentsDB.findById(studentId).orElse(null);
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

    @Transactional
    public Map<String, Object> updateAllStudentData(List<Students> studentsToUpdate) {
        List<String> errors = new ArrayList<>();
        List<Students> successfulUpdates = new ArrayList<>();
        List<Long> failedUniversityNos = new ArrayList<>();
        int rowNum = 0;
        
        // Process each student update
        for (Students studentUpdate : studentsToUpdate) {
            rowNum++;
            try {
                // Validate required fields
                if (studentUpdate.getName() == null || studentUpdate.getName().trim().isEmpty()) {
                    errors.add(String.format("Row %d: Name cannot be empty for University No %d", 
                        rowNum, studentUpdate.getUniversityNo()));
                    failedUniversityNos.add(studentUpdate.getUniversityNo());
                    continue;
                }
                if (studentUpdate.getRollNo() == null || studentUpdate.getRollNo().trim().isEmpty()) {
                    errors.add(String.format("Row %d: Roll No cannot be empty for University No %d", 
                        rowNum, studentUpdate.getUniversityNo()));
                    failedUniversityNos.add(studentUpdate.getUniversityNo());
                    continue;
                }

                // Try to update the student
                if (studentsDB.existsById(studentUpdate.getUniversityNo())) {
                    studentsDB.updateStudent(
                        studentUpdate.getUniversityNo(),
                        studentUpdate.getName(),
                        studentUpdate.getClassName(),
                        studentUpdate.getRollNo(),
                        studentUpdate.getGender(),
                        studentUpdate.getLeetcodeUsername(),
                        studentUpdate.getBatch(),
                        studentUpdate.getEasyProblemsSolved(),
                        studentUpdate.getMediumProblemsSolved(),
                        studentUpdate.getHardProblemsSolved()
                    );
                    successfulUpdates.add(studentUpdate);
                } else {
                    errors.add(String.format("Row %d: Student with University No %d not found", 
                        rowNum, studentUpdate.getUniversityNo()));
                    failedUniversityNos.add(studentUpdate.getUniversityNo());
                }
            } catch (Exception e) {
                errors.add(String.format("Row %d: Error updating University No %d - %s", 
                    rowNum, studentUpdate.getUniversityNo(), e.getMessage()));
                failedUniversityNos.add(studentUpdate.getUniversityNo());
            }
        }


        
        // Return results
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successfulUpdates.size());
        result.put("failureCount", failedUniversityNos.size());
        result.put("successfulUpdates", successfulUpdates);
        result.put("failedUniversityNos", failedUniversityNos);
        result.put("errors", errors);
        
        return result;
    }
 

    /* method to update the student data for a particular student */

    @Transactional
    public void updateStudent(Long universityNo, Students student) {
        if (!studentsDB.existsById(universityNo)) {
            throw new StudentNotFoundException(universityNo);
        }
        studentsDB.updateStudent(
            universityNo,
            student.getName(),
            student.getClassName(),
            student.getRollNo(),
            student.getGender(),
            student.getLeetcodeUsername(),
            student.getBatch(),
            student.getEasyProblemsSolved(),
            student.getMediumProblemsSolved(),
            student.getHardProblemsSolved()
        );
    }

 
    /* method that gets the student information by using the university no */


    public Students getStudentByUniversityNo(Long universityNo) {
        return studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
    }


      /* function to get the student by the querying using the params */


        public List<Students> searchStudents(String query) {
            if (query == null || query.trim().isEmpty()) {
                return Collections.emptyList();
            }
            return studentsDB.searchStudents(query.trim());

        }

 
    /* function to delete the students record */

    @Transactional
    public void deleteStudent(Long universityNo) {
        if (!studentsDB.existsById(universityNo)) {
            throw new StudentNotFoundException(universityNo);
        }
        studentsDB.deleteById(universityNo);
    }

    /* Method to update the student score */
    
    @Transactional
    public Map<String, Object> updateStudentScore(Long universityNo, String testName, Double score) {
        // Validate inputs
        if (testName == null || testName.trim().isEmpty()) {
            return Map.of("error", "Test name cannot be empty");
        }
        if (score == null) {
            return Map.of("error", "Score cannot be null");
        }

        // Check if student exists
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));

        try {
            // Try to update existing score
            int updatedRows = studentsDB.updateStudentTestScore(universityNo, testName, score);
            
            if (updatedRows == 0) {
                // If no existing score found, create new one
                student.addTestScore(testName, score);
                studentsDB.save(student);
            }

            return Map.of(
                "message", "Score updated successfully",
                "universityNo", universityNo,
                "testName", testName,
                "score", score
            );

        } catch (Exception e) {
            return Map.of("error", "Failed to update score: " + e.getMessage());
        }
    }

    /* Method to get the student scores */

    public Map<String, Object> getStudentScores(Long universityNo) {
        Students student = studentsDB.findById(universityNo)
            .orElseThrow(() -> new StudentNotFoundException(universityNo));
            
        Map<String, Object> result = new HashMap<>();
        result.put("universityNo", student.getUniversityNo());
        result.put("name", student.getName());
        result.put("scores", student.getStudentScores().stream()
            .map(score -> Map.of(
                "testName", score.getTestName(),
                "score", score.getScore()
            ))
            .collect(Collectors.toList()));
            
        return result;
    }
}

