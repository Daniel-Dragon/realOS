package util;

import util.Globals.*;
import util.Globals.ProcessState;

public class PCB {
    //Private members
    private static int pidCounter = 0;

    public ProcessState processState;
    //public int currentInstruction;
    public int pid;
    public int programCounter;
    public int stackLimit;
    public int stackPointer;
    public int segment;

    //cpu registers
    //cpu schedule information
    //memory management information
    public int accountingInformation;
    //I/o status information

    public PCB() {
        this.processState = ProcessState.TERMINATED;
        this.pid = -1;
        this.programCounter = 0;
        this.accountingInformation = 0;
        //this.currentInstruction = 0;
        this.accountingInformation = 0;
        this.stackPointer = Globals.SEGMENT_SIZE;
    }

    public PCB(int[] program, int segment) {
        this();
        this.pid = pidCounter++;
        this.processState = ProcessState.NEW;
        stackLimit = program.length;
        this.segment = segment;

    }

    public void stateSave() {
        processState = ProcessState.READY;
        //Save CPU Registers?
    }

    public void stateRestore() {
        processState = ProcessState.RUNNING;
        //Restore CPU Registers?
    }

    public void processDetails() {
        Globals.console.putText("Process State: " + getprocessState());
        Globals.console.putText(" PID: " + pid);
        Globals.console.putText(" Program Counter: " + programCounter);
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public String getprocessState() {
        ProcessState processIn = processState;
        switch(processIn) {
            case NEW:
                return "NEW";
            case READY:
                return "READY";
            case RUNNING:
                return "RUNNING";
            case WAITING:
                return "WAITING";
            case TERMINATED:
                return "TERMINATED";
            default:
                return "INVALID PROCESS STATE";
        }
    }

    public String getCurrentInstruction() {

        return String.valueOf(Globals.mmu.read(this.segment, this.programCounter));
    }
}
