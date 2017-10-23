package host;

import java.util.HashMap;

import os.Interrupt;
import util.PCB;
import util.Globals;

public class CPU {
	private int pc = 0; // the program counter.
	private int acc = 0; //the accumulator.
	private int xreg = 0; //on register.
	private int yreg = 0;  //the y register.
	private int zflag = 0; //the zflag.
	public PCB currentProcess = null; //Tracks what process is currently executing.
	
	public void cycle() {
		Control.kernel.kernelTrace("CPU Cycle");
		try {
			executeProgram();
		} catch (Exception e) {
			System.out.println("Uncaught exception: " + e.getMessage());
			halt(1);
		}
		//executeProgram();
	}

	public boolean isExecuting() {
		return !(currentProcess == null || currentProcess.processState == Globals.ProcessState.TERMINATED);
	}

	public void loadProgram(PCB processIn) {
		currentProcess = processIn;
	}

	public void haltProgram() { currentProcess = null; }

	public void executeProgram() {
		int instruction = Globals.mmu.read(currentProcess.segment, currentProcess.programCounter);
		currentProcess.processState = Globals.ProcessState.RUNNING;
		switch(instruction) {
			case 0:
				//Jump
				jmp();
				break;
			case 1:
				//Branch if Equal
				beq();
				break;
			case 2:
				//Load location
				ldloc();
				break;
			case 3:
				//System call
				sys();
				break;
			case 4:
				//Integer Arithmetic
				iarith();
				break;
			case 5:
				//Floating Artithmetic, skip this.
				break;
			case 6:
				//Undefined
				break;
			case 7:
				//Undefined
				break;
			case 8:
				//Push 0 to top of the stack
				pushZero();
				break;
			case 9:
				//Push 1 to top of the stack
				pushOne();
				break;
			case 10:
				//Duplicate top value on stack.
				dup();
				break;
			case 11:
				//Down val, move top value down by value given on stack.
				downVal();
				break;
			case 12:
				//Undefined
				break;
			case 13:
				//Push next value onto the stack
				pushNext();
				break;
			case 14:
				//Pops value off the stack and stores it in the specified location in memory (relative and absolute)
				stLoc();
				break;
			case 15:
				//Halt commands, does cleanup then frees up CPU.
				halt(0);
				break;
			default:
				halt(1);
				break;
		}
		if (currentProcess != null)
			currentProcess.accountingInformation++;
	}

	public void jmp() {
		currentProcess.programCounter = Globals.mmu.pop(currentProcess);
	}

	public void beq() {
		int jumpTo = Globals.mmu.pop(currentProcess);

		if (Globals.mmu.pop(currentProcess) == Globals.mmu.pop(currentProcess))
			currentProcess.programCounter = jumpTo;
		else
			currentProcess.programCounter++;
	}

	public void ldloc() {
		int location = Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter);

		if (location < 0)
			location = currentProcess.stackLimit + location;

		Globals.mmu.push(currentProcess, Globals.mmu.read(currentProcess.segment, location));
		currentProcess.programCounter++;
	}

	public void sys() {
		int instruction = Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter);

		switch(instruction) {
			case 1:
				//print top value of stack as int
				Globals.console.putText(String.valueOf(Globals.mmu.pop(currentProcess)));

				break;
			case 2:
				//print top value of stack as char
				char currChar = (char)Globals.mmu.pop(currentProcess);
				if (currChar == '\n')
					Globals.console.advanceLine();
				else
					Globals.console.putText(String.valueOf(currChar));
				Globals.console.putText(String.valueOf((char)Globals.mmu.pop(currentProcess)));
				break;
			case 3:
				//prints new line character
				Globals.console.advanceLine();
				break;
			case 4:
				//readline, don't implement
				break;
			default:
				halt(1);
				break;
		}

		currentProcess.programCounter++;
	}

	public void iarith() {
		int instruction = Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter);

		switch(instruction) {
			case 1:
				//First + second
				Globals.mmu.push(currentProcess, (Globals.mmu.pop(currentProcess) + Globals.mmu.pop(currentProcess)));
				break;
			case 2:
				//First - second
				Globals.mmu.push(currentProcess, (Globals.mmu.pop(currentProcess) - Globals.mmu.pop(currentProcess)));
				break;
			case 3:
				//First * second
				Globals.mmu.push(currentProcess, (Globals.mmu.pop(currentProcess) * Globals.mmu.pop(currentProcess)));
				break;
			case 4:
				//First / second
				Globals.mmu.push(currentProcess, (Globals.mmu.pop(currentProcess) / Globals.mmu.pop(currentProcess)));
				break;
			case 5:
				//if (first == second) push 0; if (first > second) push 1; if (first < second) push -1;
				int first = Globals.mmu.pop(currentProcess);
				int second = Globals.mmu.pop(currentProcess);
				int answer;

				if (first == second)
					answer = 0;
				else if (first > second)
					answer = 1;
				else
					answer = -1;

				Globals.mmu.push(currentProcess, answer);
				break;
			default:
				halt(1);
				break;
		}
		currentProcess.programCounter++;
	}

	private void pushZero() {
		Globals.mmu.push(currentProcess, 0);
		currentProcess.programCounter++;
	}

	private void pushOne() {
		Globals.mmu.push(currentProcess, 1);
		currentProcess.programCounter++;
	}

	private void dup() {
		int val = Globals.mmu.pop(currentProcess);
		Globals.mmu.push(currentProcess, val);
		Globals.mmu.push(currentProcess, val);
		currentProcess.programCounter++;
	}

	private void downVal() {
		//int numDown = Globals.mmu.pop(currentProcess);
		int numDown = Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter);
		int val = Globals.mmu.pop(currentProcess);

		if (numDown > getStackSize()) {
			//Globals.osShell.shellBsod.execute()
		}
		int[] tempStorage = new int[numDown];

		for (int i = 0; i < numDown; i++)
			tempStorage[i] = Globals.mmu.pop(currentProcess);

		Globals.mmu.push(currentProcess, val);

		for (int i = numDown - 1; i >= 0; i--)
			Globals.mmu.push(currentProcess, tempStorage[i]);

		currentProcess.programCounter++;
	}

	private void pushNext() {
		Globals.mmu.push(currentProcess, Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter));

		currentProcess.programCounter++;
	}

	private void stLoc() {
		int value = Globals.mmu.pop(currentProcess);
		int location = Globals.mmu.read(currentProcess.segment, ++currentProcess.programCounter);
		if (location < 0)
			location += currentProcess.stackLimit;

		Globals.mmu.write(currentProcess.segment, location, value);

		currentProcess.programCounter++;
	}

	private void halt(int statusCode) {
		HashMap<String, String> event = new HashMap<String, String>();
		event.put("statusCode", String.valueOf(statusCode));
		event.put("pid", String.valueOf(currentProcess.pid));
		Globals.kernelInterruptQueue.add(new Interrupt(Globals.HALT_IRQ, event));

		System.out.println("Program PID: " + currentProcess.pid + " has exited with code " + statusCode);
		if (statusCode != 0) {
			Globals.standardOut.putText("An error has occurred and process " + currentProcess.pid + " has exited(" + statusCode + ")");
			Globals.standardOut.advanceLine();
		}

		currentProcess.processState = Globals.ProcessState.TERMINATED;
		//currentProcess = null;
	}

	private int getStackSize() {
		return (Globals.SEGMENT_SIZE - currentProcess.stackLimit);
	}
}
