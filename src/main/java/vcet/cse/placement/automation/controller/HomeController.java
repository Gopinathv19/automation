package vcet.cse.placement.automation.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.service.StudentsService;
import java.util.List;
import java.util.Map;
import vcet.cse.placement.automation.exception.StudentNotFoundException;
 


@RestController
@CrossOrigin(
    origins = "*",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/cse/v1/students")
public class HomeController {
    
    @Autowired
    private StudentsService studentsService;


// ************* Admin side  routes **********    
    

    // this method is used to get all the students data in the Admin Page


    @GetMapping
    public List<Students> getAllStudents() {
        return studentsService.getStudents();
    }

    // this method is used to post all the students data in a single short

    @PostMapping
    public void createStudent(@RequestBody Students student) {
        studentsService.addStudents(student);
    }


    // the put method is used to update the students data in the Admin Page 

    @PutMapping("/{universityNo}/leetcode")
    public void updateLeetcodeStats(
            @PathVariable Long universityNo,
            @RequestBody Students leetcodeUpdate) {
        studentsService.updateLeetcode(universityNo, leetcodeUpdate);
    }

    @DeleteMapping("/{universityNo}")
    public void deleteStudent(@PathVariable Long universityNo) {
        studentsService.deleteStudent(universityNo);
    }



    @PutMapping("/{universityNo}/aptitude")
    public void updateAptitudeScores(
            @PathVariable Long universityNo,
            @RequestBody Students aptiUpdate) {
        studentsService.updateAptitudeScores(universityNo, aptiUpdate);
    }

    @PutMapping("/{universityNo}/technical")
    public void updateTechnicalScores(
            @PathVariable Long universityNo,
            @RequestBody Students techUpdate) {
        studentsService.updateTechAptitudeScores(universityNo, techUpdate);
    }

    @PutMapping("/{universityNo}/programming")
    public void updateProgrammingScores(
            @PathVariable Long universityNo,
            @RequestBody Students progUpdate) {
        studentsService.updateProgrammingScores(universityNo, progUpdate);
    }

    // ... existing code ...

    @PutMapping("/{universityNo}/updateAll")
    public ResponseEntity<?> updateAllStudentData(
            @PathVariable Long universityNo,
            @RequestBody Students studentUpdate) {
        try {
            studentsService.updateAllStudentData(universityNo, studentUpdate);
            return ResponseEntity.ok().build();
        } catch (StudentNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

// ... existing code ...


//  ************* End of Admin side routes **********    







// ************* User side routes **********    


    @GetMapping("/{universityNo}")
    public Students getStudent(@PathVariable Long universityNo) {
        return studentsService.getStudentByUniversityNo(universityNo);
    }

    @GetMapping("/{universityNo}/leetcode")
    public Map<String, Object> getLeetcodeStats(@PathVariable Long universityNo) {
        return studentsService.getStudentLeetcodeData(universityNo);
    }

    @GetMapping("/{universityNo}/aptitude")
    public Map<String, Object> getAptitudeScores(@PathVariable Long universityNo) {
        return studentsService.getStudentAptitudeData(universityNo);
    }

    @GetMapping("/{universityNo}/technical")
    public Map<String, Object> getTechnicalScores(@PathVariable Long universityNo) {
        return studentsService.getStudentTechnicalData(universityNo);
    }

    @GetMapping("/{universityNo}/programming")
    public Map<String, Object> getProgrammingScores(@PathVariable Long universityNo) {
        return studentsService.getStudentProgrammingData(universityNo);
    }
    @GetMapping("/analytics/eligibility")
    public Map<String, Object> getEligibilityStats() {
        return studentsService.getEligibility();
    }
    @GetMapping("/analytics/gender")
    public Map<String, Object> getGenderDistribution() {
        return studentsService.getGenderPerformanceScore();
    }
    @GetMapping("/analytics/charts")
    public Map<String, Object> getChartData() {
        return studentsService.getChartData();
    }
    @GetMapping("/analytics/toppers")
    public List<Map<String, Object>> getToppers() {
        return studentsService.getToppers();
    }

   // the search endpoint is also used in the admin page to search the students data

    @GetMapping("/search")
    public List<Students> searchStudents(@RequestParam("q") String query) {
        return studentsService.searchStudents(query);
    }
 

    @GetMapping("/students/scores/apptitude")
    public List<Map<String, Object>> getAllStudentScores() {
        return studentsService.getAllAptitudeScores();
    }
    @GetMapping("/students/scores/leetcode")
    public List<Map<String, Object>> getAllLeetcodeScores() {
        return studentsService.getAllLeetcodeScores();
    }

 
// ************* End of User side routes **********    
}