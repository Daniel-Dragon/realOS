package exceptions;

public class ProgramNotFound extends RuntimeException{
    public ProgramNotFound(String message) {
        super(message);
    }
}
