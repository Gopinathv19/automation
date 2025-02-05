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

@RequestMapping("/eligibility")
public ResponseEntity<?> getEligibility(){
    try{
        return ResponseEntity.ok().body(homeService.getEligibility());
    }
    catch(Exception e){
        return ResponseEntity.badRequest().body("Error getting the eligibility");
    }
}

/* This end points is used to get the gender score of the batch */

@RequestMapping("/genderScore")
public ResponseEntity<?> getGenderScore(){
    try{
        return ResponseEntity.ok().body(homeService.getGenderScore());
    }
    catch(Exception e){
        return ResponseEntity.badRequest().body("Error getting the gender score");
    }
}

                
/* This end point is used to get the chart data of the batch*/

@RequestMapping("/getChartData/{batch}")
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

@RequestMapping("/getStudent/ApptitudeScores/{batch}")

ResponseEntity<?> getStudentApptitudeScores(@PathVariable int batch){
    try{
        return ResponseEntity.ok().body(homeService.getAllAptitudeScores(batch));
    }
    catch(Exception e){
        return ResponseEntity.badRequest().body("Error getting the student apptitude scores");
    }
}

/* this end point is used to get the students leetcode scores */
@RequestMapping("/getStudent/LeetCodeScores/{batch}")

ResponseEntity<?> getStudentLeetCodeScores(@PathVariable int batch){
    try{
        return ResponseEntity.ok().body(homeService.getAllAptitudeScores(batch));
    }
    catch(Exception e){
        return ResponseEntity.badRequest().body("Error getting the student leetcode scores");
    }
}

}
