package util;

import exceptions.NoAvailableMemorySegment;
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
        for (int i = 0; i < Globals.world.NUM_MEM_SEGMENT; i++) {
            memory[i] = new Memory();
        }
    }

    public PCB load(int[] program) {
        int nextSegment = findNextFreeSegment();
        if (nextSegment >= 0) {
            allocate(nextSegment);
            for (int i = 0; i < program.length; i++) {

                memory[nextSegment].set(i, program[i]);
                Globals.world.interactWithMemory(nextSegment, i, program[i], MemoryOperation.WRITE);
            }
            return new PCB(program, nextSegment);
        }

        //This should never happen... we will error check before calling.
        return new PCB();
    }

    public void unload(int segment) {
        free(segment);
        Globals.world.interactWithMemory(segment, 0, 0, Globals.MemoryOperation.CLEAR);
    }

    public int read(int segment, int location) {
        int value = memory[segment].get(location);
        Globals.world.interactWithMemory(segment, location, value, MemoryOperation.READ);
        return value;
    }

    public void write(int segment, int location, int value) {
        Globals.world.interactWithMemory(segment, location, value, MemoryOperation.WRITE);
        memory[segment].set(location, value);
    }

    public void push(int segment, int value) {
        Globals.world.interactWithMemory(segment, top[segment], value, MemoryOperation.WRITE);
        memory[segment].set(--top[segment], value);
    }

    public int pop(int segment) {
        int value = memory[segment].get(top[segment]);
        Globals.world.interactWithMemory(segment, top[segment], value, MemoryOperation.READ);
        top[segment]++;

        return value;
    }

    private void allocate(int segment) {
        //Load the program into memory here
        freeSegment[segment] = false;
    }

    private void free(int segment) {
        //Remove program from memory here
        freeSegment[segment] = true;
        Globals.world.interactWithMemory(segment, 0, 0, MemoryOperation.CLEAR);
    }

    public int findNextFreeSegment() {
        for (int i = 0; i < Globals.world.NUM_MEM_SEGMENT; i++) {
            if (isSegmentFree(i))
                return i;
        }

        return -1;
    }

    public boolean hasFreeSegment() {
        return (findNextFreeSegment() >= 0);
    }

    private boolean isSegmentFree(int segment) {
        return freeSegment[segment];
    }
}
