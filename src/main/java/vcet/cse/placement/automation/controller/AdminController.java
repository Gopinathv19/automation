package vcet.cse.placement.automation.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.service.StudentsService;
import java.util.List;
import vcet.cse.placement.automation.exception.StudentNotFoundException;
import java.util.Map;


@RestController
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/cse/v1/admin")
public class AdminController {

    @Autowired
    private StudentsService studentsService;


    // ************* Admin side  routes **********

    /* The Method used to upload the single student if guy is not in the database */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadStudents(@RequestBody Students student){
        try{
            studentsService.addStudents(student);
            return ResponseEntity.ok().body("Students where added successfully");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Error uploading the student details");
        }
    }
    @PostMapping("uploadAll")
    public ResponseEntity<?> uploadAllStudents(@RequestBody List<Students> students){
        try{
            studentsService.addAllStudents(students);
            return ResponseEntity.ok().body("All the students where added");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Error uploading all the students data");
        }
    }
    /* This method is used to get the list of all students in the database */

    @GetMapping("getAll")
    public List<Students> getAllStudents() {
        return studentsService.getStudents();
    }

    /* This method is used to get the student by the student id */
    @GetMapping("get/{studentId}")
    public Students getStudentById(@PathVariable Long studentId) {
        return studentsService.getStudentById(studentId);
    }

    /* This method is used to delete the student by the student id */

    @DeleteMapping("delete/{universityNo}")
    public void deleteStudent(@PathVariable Long universityNo) {
        studentsService.deleteStudent(universityNo);
    }

    /* This method is used to update the student data by the student id */

    @PutMapping("/updateAll")
    public ResponseEntity<?> updateStudentData(@RequestBody List<Students> studentUpdate) {
        try {
            Map<String, Object> result = studentsService.updateAllStudentData(studentUpdate);

            // If there were any failures but some updates succeeded
            @SuppressWarnings("unchecked")
            List<String> errors = (List<String>) result.get("errors");
            if (!errors.isEmpty() && (int)result.get("successCount") > 0) {
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .body(result);
            }

            // If all updates failed
            if ((int)result.get("successCount") == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(result);
            }

            // If all updates succeeded
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /* This method is used to update the student details for the particular student */
    @PutMapping("/update/{universityNo}")
    public ResponseEntity<?> updateStudent(@PathVariable Long universityNo, @RequestBody Students student) {
        try {
            studentsService.updateStudent(universityNo, student);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /* This method is used to update the student score */
    @PutMapping("/updateTestScore/{universityNo}/{testName}/{score}")
    public ResponseEntity<?> updateStudentScore(
            @PathVariable Long universityNo,
            @PathVariable String testName,
            @PathVariable double score) {
        try {
            Map<String, Object> result = studentsService.updateStudentScore(universityNo, testName, score);

            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/scores/{universityNo}")
    public ResponseEntity<?> getStudentScores(@PathVariable Long universityNo) {
        try {
            Map<String, Object> scores = studentsService.getStudentScores(universityNo);
            return ResponseEntity.ok(scores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
