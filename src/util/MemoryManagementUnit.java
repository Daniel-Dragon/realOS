package util;

import host.Memory;
import util.Globals;
import util.Globals.MemoryOperation;
import util.PCB;
import java.util.Arrays;
import java.util.List;

public class MemoryManagementUnit {

    private boolean[] freeSegment;
    private Memory[] memory;

    public MemoryManagementUnit() {
        freeSegment = new boolean[Globals.world.NUM_MEM_SEGMENT];
        Arrays.fill(freeSegment, true);
        memory = new Memory[Globals.world.NUM_MEM_SEGMENT];
    }

    public void load(int[] program) {
        int nextSegment = findNextFreeSegment();

        if (nextSegment >= 0) {
            allocate(nextSegment);
            for (int i = 0; i < program.length; i++) {

                memory[nextSegment].set(i, program[i]);
                Globals.world.drawMemory(MemoryOperation.WRITE, nextSegment, i);
            }
        }
    }

    public void unload(int segment) {
        //TODO unload all from the segment
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
