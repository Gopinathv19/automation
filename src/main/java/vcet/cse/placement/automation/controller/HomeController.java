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

    @PostMapping("/upload/Student")
    public ResponseEntity<?> uploadStudents(@RequestBody Students student){
        try{
            studentsService.addStudents(student);
            return ResponseEntity.ok().body("Students where added successfully");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Error uploading the student details");
        }
    } 
    @PostMapping("uploadAll/student")
    public ResponseEntity<?> uploadAllStudents(@RequestBody List<Students> students){
        try{
            studentsService.addAllStudents(students);
            return ResponseEntity.ok().body("All the students where added");
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body("Error uploading all the students data");
        }
    }



    
    @GetMapping("getAll/Students")
    public List<Students> getAllStudents() {
        return studentsService.getStudents();
    }

    // this method is used to post all the students data in a single short

 



    // // the put method is used to update the students data in the Admin Page 

    // @PutMapping("/{universityNo}/leetcode")
    // public void updateLeetcodeStats(
    //         @PathVariable Long universityNo,
    //         @RequestBody Students leetcodeUpdate) {
    //     studentsService.updateLeetcode(universityNo, leetcodeUpdate);
    // }

    @DeleteMapping("/{universityNo}")
    public void deleteStudent(@PathVariable Long universityNo) {
        studentsService.deleteStudent(universityNo);
    }



 

    // ... existing code ...

    @PutMapping("/{universityNo}/updateAll")
    public ResponseEntity<?> updateStudentData(
            @PathVariable Long universityNo,
            @RequestBody Students studentUpdate) {
        try {
            studentsService.updateStudentData(universityNo, studentUpdate);
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

 
 

 
 
 
 
    @GetMapping("/analytics/charts/{batch}")
    public Map<String, Object> getChartData(@PathVariable int batch) {
        return studentsService.getChartData(batch);
    }
    @GetMapping("/analytics/toppers/{batch}")
    public List<Map<String, Object>> getToppers(@PathVariable int batch) {
        return studentsService.getToppers(batch);
    }

   // the search endpoint is also used in the admin page to search the students data

    @GetMapping("/search")
    public List<Students> searchStudents(@RequestParam("q") String query) {
        return studentsService.searchStudents(query);
    }
 

    @GetMapping("/students/scores/apptitude/{batch}")
    public List<Map<String, Object>> getAllStudentScores(@PathVariable int batch) {
        return studentsService.getAllAptitudeScores(batch);
    }
    @GetMapping("/students/scores/leetcode/{batch}")
    public List<Map<String, Object>> getAllLeetcodeScores(@PathVariable int batch   ) {
        return studentsService.getAllLeetcodeScores(batch);
    }

 
// ************* End of User side routes **********    
}