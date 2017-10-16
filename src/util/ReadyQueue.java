package util;

import java.util.ArrayList;
import util.PCB;


public class ReadyQueue {
    private PCB[] processList;

    public ReadyQueue() {
        processList = new PCB[Globals.NUM_MEM_SEGMENT];
    }
}
