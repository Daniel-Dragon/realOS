package util;

import exceptions.ProgramNotFound;
import host.Control;
import os.Interrupt;

import java.util.HashMap;
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
            readyQueue.loadProgram(residentList.getProgram(pid));
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
        if (readyQueue.isLoaded(pid)) {
            PCB process = readyQueue.remove(pid);
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
        killAll();
        residentList.unloadAll();
        Globals.mmu.clearMemory();
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public void contextSwitch() {
        Control.cpu.currentProcess = readyQueue.contextSwitch();
    }

    public void handleCycle() {
        if (readyQueue.queueSize() != 1 && Globals.OSclock % quantum == 0) {
            String message = "Context switch from " + String.valueOf(readyQueue.peek().pid) + " to ";
            contextSwitch();
            message = message + String.valueOf(readyQueue.peek().pid);
            System.out.println(message);
        }
        else if (Control.cpu.isExecuting())
            Control.cpu.cycle();
        else {
            Control.cpu.currentProcess = readyQueue.peek();
            Control.cpu.cycle();
        }
    }

    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }

    public void runAll() {
        PCB[] processesLoaded = residentList.getProcessesAsArray();
        for (PCB process: processesLoaded) {
            readyQueue.loadProgram(process);
        }
    }

    public void killAll() {
        PCB[] processesRunning = readyQueue.getProcessesAsArray();
        for (int i = 0; i < processesRunning.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("pid", String.valueOf(processesRunning[i].pid));
            map.put("statusCode", "0");
            Globals.kernelInterruptQueue.add(new Interrupt(Globals.IRQ.HALT, map));
        }
    }

    public PCB[] getReadyQueueAsArray() {
        return readyQueue.getProcessesAsArray();
    }
}
