package util;

import java.util.Arrays;
import java.util.LinkedList;

import exceptions.ProgramNotFound;

public class ResidentList {
    private PCB[] processList;

    public ResidentList() {
        processList = new PCB[Globals.NUM_MEM_SEGMENT];
        Arrays.fill(processList, new PCB());
    }

    public void loadProgram(PCB program) {
        processList[program.segment] = program;
    }

    public void unloadProgram(int pid) {
        //Don't need to do anything? Keeping stub just in case this becomes useful.
    }

    public PCB getProgram(int pid) throws ProgramNotFound{
        for (PCB program : processList) {
            if (program != null && program.pid == pid)
                return program;
        }

        throw new ProgramNotFound("Program not found in Resident List");
    }

    public boolean isProgramLoaded(int pid) {
        if (pid < 0)
            return false;
        for (PCB program : processList) {
            if (program.pid == pid) {
                return true;
            }
        }

        return false;
    }

    public String[] top() {
        LinkedList<String> val = new LinkedList<String>();


        for(PCB process : processList) {
            if (process.processState != Globals.ProcessState.TERMINATED) {
                val.add("pid: " + process.pid + " segment: " + process.segment);
            }
        }

        if (val.size() == 0)
            val.add("No processes loaded!");

        return val.toArray(new String[val.size()]);
    }

    public boolean isLoaded(int pid) {
        for (PCB process : processList) {
            if (process.pid == pid && process.processState != Globals.ProcessState.TERMINATED)
                return true;
        }

        return false;
    }

    public void unloadAll() {
        for (PCB process : processList) {
            process.processState = Globals.ProcessState.TERMINATED;
        }
    }

    public PCB[] getProcessesAsArray() {
        LinkedList<PCB> processes = new LinkedList<PCB>();

        for (PCB process : processList) {
            if (process.processState != Globals.ProcessState.TERMINATED)
                processes.add(process);
        }

        return processes.toArray(new PCB[processes.size()]);
    }

}
