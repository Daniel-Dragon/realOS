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
        //Don't need to do anything?
    }

    public PCB getProgram(int pid) {
        //Again, not the best but... we should be calling isProgramLoaded first...
        try {
            return getProgramHelper(pid);
        } catch(ProgramNotFound e) {
            return new PCB();
        }
    }
    public PCB getProgramHelper(int pid) throws ProgramNotFound{
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

    private int numProgramsLoaded() {
        int ans = 0;
        for (PCB process : processList) {
            if (process != null)
                ans++;
        }

        return ans;
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
            if (process.pid == pid)
                return true;
        }

        return false;
    }

}
