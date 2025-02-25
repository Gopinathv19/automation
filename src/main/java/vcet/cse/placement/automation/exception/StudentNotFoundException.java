package vcet.cse.placement.automation.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long universityNo) {
        super("Could not find student with university number: " + universityNo);
    }
} 