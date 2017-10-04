package host;

import util.Globals;
import java.util.Arrays;

public class MemoryManagementUnit {

    private boolean[] freeSegment = new boolean[Globals.world.NUM_MEM_SEGMENT];

    public MemoryManagementUnit() {
        Arrays.fill(freeSegment, true);
    }

    private void allocate(int segment) {
        //Load the program into memory here

        freeSegment[segment] = false;
    }

    private void free(int segment) {
        //Remove program from memory here
        freeSegment[segment] = false;
    }

    private int findNextFreeSegment() {
        for (int i = 0; i < Globals.world.NUM_MEM_SEGMENT; i++) {
            if (isSegmentFree(i))
                return i;
        }

        return -1;
    }

    private boolean isSegmentFree(int segment) {
        return freeSegment[segment];
    }
}
