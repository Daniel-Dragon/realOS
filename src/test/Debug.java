package test;

import host.Control;
import util.Globals;


import util.PCB;
public class Debug {
	public static void main(String[] args) throws InterruptedException {
		Control.hostInit();
		Control.startOS();  //start the os...
		Globals.userProgramInput.setText("8 9 10 14 29 4 1 10 3 1 3 3 14 30 2 29 2 30 2 29 13 144 13 28 1 13 2 0 15 0 0");
	}
}
