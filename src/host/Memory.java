package host;

import java.util.Arrays;
import util.Globals;

public class Memory {
    private int[] memory;

    public Memory() {
        memory = new int[Globals.SEGMENT_SIZE];
        Arrays.fill(memory, 0);
    }

    public void set(int location, int value) {
        memory[location] = value;
    }

    public int get(int location) {
        return memory[location];
    }
}
