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
    private int[] top;

    public MemoryManagementUnit() {
        freeSegment = new boolean[Globals.world.NUM_MEM_SEGMENT];
        Arrays.fill(freeSegment, true);

        top = new int[Globals.world.NUM_MEM_SEGMENT];
        Arrays.fill(top, Globals.SEGMENT_SIZE);

        memory = new Memory[Globals.world.NUM_MEM_SEGMENT];
    }

    public PCB load(int[] program) {
        int nextSegment = findNextFreeSegment();
        if (nextSegment >= 0) {
            allocate(nextSegment);
            for (int i = 0; i < program.length; i++) {

                memory[nextSegment].set(i, program[i]);
                Globals.world.interactWithMemory(nextSegment, i, MemoryOperation.WRITE);
            }
            return new PCB(program, nextSegment);
        }

        //Don't think we want this to happen...
        return new PCB();
    }

    public void unload(int segment) {
        //TODO unload all from the segment
    }

    public int read(int segment, int location) {
        Globals.world.interactWithMemory(segment, location, MemoryOperation.READ);
        return memory[segment].get(location);
    }

    public void push(int segment, int value) {
        Globals.world.interactWithMemory(segment, top[segment], MemoryOperation.WRITE);
        memory[segment].set(--top[segment], value);
    }

    public int pop(int segment) {
        Globals.world.interactWithMemory(segment, top[segment], MemoryOperation.READ);
        int value = memory[segment].get(top[segment]);
        top[segment]++;

        return value;
    }

    private void allocate(int segment) {
        //Load the program into memory here
        freeSegment[segment] = false;
    }

    private void free(int segment) {
        //Remove program from memory here
        freeSegment[segment] = false;
    }

    public int findNextFreeSegment() {
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
