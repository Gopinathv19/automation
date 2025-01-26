package vcet.cse.placement.automation.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long universityNo) {
        super("Student not found with university number: " + universityNo);
    }
} 