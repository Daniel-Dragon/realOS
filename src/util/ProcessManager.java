package util;

import exceptions.ProgramNotFound;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessManager {

    private ResidentList residentList;
    private ReadyQueue readyQueue;

    public ProcessManager() {
        readyQueue = new ReadyQueue();
        residentList = new ResidentList();
    }

    public void beginExecuting(int pid) {
        if (isProgramLoaded(pid))
            host.Control.cpu.loadProgram(getProgram(pid));
    }

    public PCB loadProgram(int[] program) {

        if (Globals.mmu.findNextFreeSegment() >= 0) {
            PCB currProcess = Globals.mmu.load(program);
            residentList.loadProgram(currProcess);
            return currProcess;
        } else {
            Globals.console.putText("No memory segment available.");
            return new PCB();
        }
    }

    public void haltProgram(int pid, int statusCode) {
        if (statusCode != 0)
            System.out.println("System exited with status code: " + statusCode);
        if (residentList.isProgramLoaded(pid)) {
            PCB process = residentList.getProgram(pid);
            //residentList.unloadProgram(pid);
            host.Control.cpu.haltProgram();
            Globals.mmu.unload(process.segment);


        }
    }

    public PCB getProgram(int pid) {
        //Should have already checked to see if it exists... but there's a better way to do this.
        try {
            return getProgramHelper(pid);
        } catch(ProgramNotFound e) {
            return new PCB();
        }
    }
    private PCB getProgramHelper(int pid) throws ProgramNotFound {
        return residentList.getProgram(pid);
    }

    public boolean isProgramLoaded(int pid) {
        try {
            getProgramHelper(pid);
            return true;
        } catch (ProgramNotFound e) {
            return false;
        }
    }

    public String[] top() {
        return residentList.top();
    }

    public void jobScheduler() {
        //TODO Something with this?
//        if (!host.Control.cpu.isExecuting()) {
//            if (!readyQueue.isEmpty()) {
//                host.Control.cpu.loadProgram(readyQueue.remove());
//            }
//            else {
//                //Do nothing, there is nothing to execute.
//            }
//        } else {
//            //Something is already running.
//            host.Control.cpu.executeProgram();
//        }
    }
}
