package util;

import host.TurtleWorld;
import os.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Globals {
	
	/**
	 * Constants
	 */
	public static final double version = 0.01d;
	public static final String name = "realOS";
	public static final int CPU_CLOCK_INTERVAL = 100;
	public enum IRQ {TIMER, KEYBOARD, PROCESS, HALT, CONTEXT_SWITCH};
	public static final String UP_ARROW = "38:0";
	public static final String DOWN_ARROW = "40:0";
	public static final String TAB_KEY = "9:0";



	public static Thread host;
    public static final String defaultDateFormat = "yyyy/MM/dd HH:mm:ss";
	public static final String currentLocation = "You are here.";
	public static final short asciiValM = 109;
	public static final int DEFAULT_QUANTUM = 6;

	public static final int SEGMENT_SIZE = 256;
	public static final int NUM_MEM_SEGMENT = 3;

	/**
	 * Variables.
	 */
	public static int OSclock = 0;
	public static int mode = 0;

	/*
	 * Enum
	 */
	public enum ProcessState {NEW, READY, RUNNING, WAITING, TERMINATED}

	public enum MemoryOperation {READ, WRITE, CLEAR}

	//we need variables that have to do with graphics.
	
	public static boolean trace = true;
	
	public static LinkedList<Interrupt> kernelInterruptQueue = null;  //don't really know what the type of this should be....
	public static LinkedList<String> kernelInputQueue = null; //same.  What type?
	public static ArrayList<?> kernelBuffers = null;
	
	public static Input standardIn;
	public static Output standardOut;
	
	public static Console console;
	public static Shell osShell;

	public static MemoryManagementUnit mmu = new MemoryManagementUnit();
	public static ProcessManager processManager = new ProcessManager();
	
	public static int hardwareClockID;
	
	public static DeviceKeyboardDriver kernelKeyboardDriver;
	
	public static TurtleWorld world;
	public static JTextArea userProgramInput;

	public static test.Robot Klapaucius;
    static {
		try {
			Klapaucius = new test.Robot();
		} catch (AWTException e) {
			Klapaucius = null;
		}
	}
	
}
