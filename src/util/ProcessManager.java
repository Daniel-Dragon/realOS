package util;

import java.util.Queue;

public class ProcessManager {

//    private Queue<PCB> residentList;
    private Queue<PCB> readyQueue;

    public ProcessManager() {

    }

    public void runProcess(PCB process) {

    }

    public void loadProgram(int[] program) {

        if (Globals.mmu.findNextFreeSegment() >= 0) {
            readyQueue.add(Globals.mmu.load(program));
        }
    }

    public void jobScheduler() {
        if (!host.Control.cpu.isExecuting()) {
            if (!readyQueue.isEmpty()) {
                host.Control.cpu.loadProgram(readyQueue.remove());
            }
            else {
                //Do nothing, there is nothing to execute.
            }
        } else {
            //Something is already running.
            host.Control.cpu.executeProgram();
        }
    }
}
