package exceptions;

public class NoAvailableMemorySegment extends Exception {
    public NoAvailableMemorySegment(String message) {
        super(message);
    }
}
