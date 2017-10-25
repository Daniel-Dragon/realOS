package util;

import exceptions.ProgramNotFound;
import host.Control;

import java.util.LinkedList;
import java.util.Queue;

public class ProcessManager {
    private ResidentList residentList;
    private ReadyQueue readyQueue;
    private int quantum;

    public ProcessManager() {
        readyQueue = new ReadyQueue();
        residentList = new ResidentList();
        quantum = Globals.DEFAULT_QUANTUM;
    }

    public void beginExecuting(int pid) {
        if (isProgramInResidentList(pid))
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
            host.Control.cpu.haltProgram();
            Globals.mmu.unload(process.segment);
            Globals.console.putText("Process exited successfully. Cycles used: "
                    + process.accountingInformation
                    + " segment " + process.segment + " is now free.");
            Globals.console.advanceLine();

        }
    }

    public PCB getProgram(int pid) throws ProgramNotFound{
        //Should have already checked to see if it exists... but there's a better way to do this.
        return residentList.getProgram(pid);
    }

    public boolean isProgramInResidentList(int pid) {
        return residentList.isLoaded(pid);
    }

    public String[] top() {
        return residentList.top();
    }

    public void unloadAll() {
        if (host.Control.cpu.isExecuting()) {
            haltProgram(Control.cpu.currentProcess.pid, 1);
        }
        residentList.unloadAll();
        Globals.mmu.clearMemory();
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
}
