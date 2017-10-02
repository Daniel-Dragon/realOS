package util;

import util.Globals.*;
import util.Globals.ProcessState;

public class PCB {

    //Private members
    private static int pidCounter = 0;

    private ProcessState processState;
    private int pid;
    private int programCounter;
    //cpu registers
    //cpu schedule information
    //memory management information
    private int accountingInformation;
    //I/o status information

    public PCB() {
        this.processState = ProcessState.NEW;
        this.pid = pidCounter++;
        programCounter = 0; //Initialize to 0?
    }

    public void stateSave() {
        processState = ProcessState.READY;
        //Save CPU Registers
    }

    public void stateRestore() {
        processState = ProcessState.RUNNING;
        //Restore CPU Registers
    }

    public void processDetails() {
        Globals.console.putText("Process State: " + getprocessState(processState));
        Globals.console.putText(" PID: " + pid);
        Globals.console.putText(" Program Counter: " + programCounter);
    }

    private static String getprocessState(ProcessState processIn) {
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
