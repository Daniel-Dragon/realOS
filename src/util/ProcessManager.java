package util;

import java.util.Queue;

public class ProcessManager {

    private Queue<PCB> residentList;
    private Queue<PCB> readyQueue;

    public ProcessManager() {

    }

    public void runProcess(PCB process) {

    }

    public void loadProgram(int[] program) {
        residentList.add(new PCB(program));
        Globals.mmu.load(program);
    }
}
