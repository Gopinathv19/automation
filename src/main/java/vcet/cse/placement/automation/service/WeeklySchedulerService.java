package vcet.cse.placement.automation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vcet.cse.placement.automation.Model.Students;
import vcet.cse.placement.automation.repo.StudentsDatabaseCollector;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class WeeklySchedulerService {

    @Autowired
    private StudentsDatabaseCollector studentsDB;
    
    @Scheduled(cron = "0 59 23 * * SUN")
    @Transactional
    public void createWeeklyHistory() {
        List<Students> allStudents = studentsDB.findAll();
        LocalDate currentDate = LocalDate.now();
        
        for (Students student : allStudents) {
            try {
                boolean existingEntry = studentsDB.existsWeeklyHistoryByStudentAndDate(
                    student, 
                    currentDate
                );
                
                if (!existingEntry) {
                    int totalSolved = student.getEasyProblemsSolved() + 
                                    student.getMediumProblemsSolved() + 
                                    student.getHardProblemsSolved();
                    
                    studentsDB.saveWeeklyHistory(
                        student,
                        student.getEasyProblemsSolved(),
                        student.getMediumProblemsSolved(),
                        student.getHardProblemsSolved(),
                        totalSolved,
                        student.getLeetcodeScore(),
                        currentDate
                    );
                }
            } catch (Exception e) {
                System.err.println("Error creating weekly history for student: " + 
                    student.getUniversityNo() + " - " + e.getMessage());
            }
        }
    }
} 