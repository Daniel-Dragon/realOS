package test;

import host.Control;
import util.Globals;


import util.PCB;
public class Debug {
	public static void main(String[] args) throws InterruptedException {
		Control.hostInit();
		Control.startOS();  //start the os...
		Control.processes.add(new PCB());
		Control.processes.add(new PCB());
		Control.processes.add(new PCB());

	}
}
