package host;

import util.PCB;

public class CPU {
	private int pc = 0; // the program counter.
	private int acc = 0; //the accumulator.
	private int xreg = 0; //on register.
	private int yreg = 0;  //the y register.
	private int zflag = 0; //the zflag.

	private boolean isExecuting = false;
	
	public void cycle() {
		Control.kernel.kernelTrace("CPU Cycle");
	}

	public boolean isExecuting() {
		return isExecuting;
	}

	public void executeProgram(PCB processIn) {
		//instruction = _memoryManager.read(PCB, this.PC);
		int instruction = 00;
		switch(instruction) {
			case 00:
				break;
			default:
				break;
		}
	}
/*
	Cpu.prototype.executeProgram = function (pcb) {
		this.instruction = _MemoryManager.read(pcb, this.PC);
		switch (this.instruction) {
			case 'A9':
				this.loadAccFromConstant();
				break;
			case 'AD':
				this.loadAccFromMemory();
				break;
			case '8D':
				this.storeAccInMemory();
				break;
			case '6D':
				this.addWithCarry();
				break;
			case 'A2':
				this.loadXWithConstant();
				break;
			case 'AE':
				this.loadXFromMemory();
				break;
			case 'A0':
				this.loadYWithConstant();
				break;
			case 'AC':
				this.loadYFromMemory();
				break;
			case 'EC':
				this.compareByteToX();
				break;
			case 'D0':
				this.branch();
				break;
			case 'EE':
				this.incrementByte();
				break;
			case 'FF':
				this.sysCall();
				break;
			case 'EA':
				this.PC++;
				break;
			case '00':
				this.breakProgram();
				break;
			default:
				_StdOut.putText("ERROR: Invalid op code.");
				console.log("INVALID OP CODE: " + this.instruction + "at PC " + this.PC);
				_ProcessManager.terminateProcess(pcb);
		}
		this.updatePCB(pcb);
	};
	*/
}
