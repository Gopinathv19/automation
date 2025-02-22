package vcet.cse.placement.automation.controller;
import  vcet.cse.placement.automation.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
@RequestMapping("/cse/v1/students")
public class HomeController {

@Autowired
private  HomeService homeService;

/*   This end points is used to get the eligibility of the students  */


@GetMapping("/search")
public ResponseEntity<?> searchStudents(@RequestParam ("q") String query){
    try{
        return ResponseEntity.ok().body(homeService.searchStudents(query));
    }
    catch (Exception e){
        return  ResponseEntity.badRequest().body("Unable to get the student:"+query);
    }
}


@RequestMapping("/charts/{batch}")
Map<String,Object> getChartData(@PathVariable int batch){
   return homeService.getChartData(batch);
}


/* this end point is used to get the apptitude topper and the leetcode topper  of the batch */

@RequestMapping("/getToppers/{batch}")

ResponseEntity<?> getToppersOfTheBatch(@PathVariable int batch){
    try {
        return ResponseEntity.ok().body(homeService.getToppers(batch));
    }
    catch (Exception e){
        System.out.println(e);
        return ResponseEntity.badRequest().body("Unnable to fetch the topppers");
    }
}

/* this end point is get the students list of apptitude scores */



/* this end point is used to get the students leetcode scores */
@RequestMapping("/getStudent/LeetCodeScores/{batch}")

ResponseEntity<?> getStudentLeetCodeScores(@PathVariable int batch){
    try{
        return ResponseEntity.ok().body(homeService.getAllLeetcodeScores(batch));
    }
    catch(Exception e){
        return ResponseEntity.badRequest().body("Error getting the student leetcode scores");
    }
}

}
