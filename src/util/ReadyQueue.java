package util;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import exceptions.ProgramNotFound;

public class ReadyQueue {
    private LinkedBlockingQueue<PCB> processQueue;

    public ReadyQueue() {
        processQueue = new LinkedBlockingQueue<PCB>();
    }

    public void loadProgram(PCB program) {
        if (!processQueue.contains(program))
            processQueue.add(program);
    }

    public void unloadProgram(int pid) {
        //Don't need to do anything? Keeping stub just in case this becomes useful.
    }

    public PCB getProgram(int pid) throws ProgramNotFound{
        for (PCB program : processQueue) {
            if (program != null && program.pid == pid)
                return program;
        }

        throw new ProgramNotFound("Program not found in Ready Queue");
    }

    public boolean isLoaded(int pid) {
        for (PCB process : processQueue) {
            if (process.pid == pid)
                return true;
        }

        return false;
    }

    public void unloadAll() {
        for (PCB process : processQueue) {
            process.processState = Globals.ProcessState.TERMINATED;
        }
    }

    public boolean isEmpty() {
        return processQueue.isEmpty();
    }

    public int queueSize() {
        return processQueue.size();
    }

    public PCB contextSwitch() {
        try {
            processQueue.put(processQueue.take());
            return processQueue.peek();
        } catch (InterruptedException e) {
            //TODO killAll???
            return new PCB();
        }
    }

    public PCB peek() {
        return processQueue.peek();
    }

    public PCB remove(int pid) {
        for (PCB process: processQueue) {
            if (process.pid == pid) {
                processQueue.remove(process);
                return process;
            }
        }
        //Shouldn't not find it because we should always check before removing.
        return new PCB();
    }

    public PCB[] getProcessesAsArray() {
        return processQueue.toArray(new PCB[processQueue.size()]);
    }

}
