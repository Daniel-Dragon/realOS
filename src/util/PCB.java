package util;

import util.Globals.*;
import util.Globals.ProcessState;

public class PCB {

    //Private members
    private static int pidCounter = 0;

    public ProcessState processState;
    public int currentInstruction;
    public int pid;
    public int programCounter;
    public int stackLimit;
    public int stackPointer;

    //cpu registers
    //cpu schedule information
    //memory management information
    private int accountingInformation;
    //I/o status information

    public PCB() {
        this.processState = ProcessState.NEW;
        this.pid = pidCounter++;
        programCounter = 0;
        accountingInformation = 0;
    }

    public PCB(int[] program) {
        this();
        currentInstruction = program[0];
        stackLimit = program.length;
        stackPointer = Globals.SEGMENT_SIZE - 1;

    }

    public void stateSave() {
        processState = ProcessState.READY;
        //Save CPU Registers?
    }

    public void stateRestore() {
        processState = ProcessState.RUNNING;
        //Restore CPU Registers?
    }

    public void process() {
        accountingInformation++;
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
}
