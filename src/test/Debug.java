package test;

import host.Control;
import util.Globals;


import util.PCB;
public class Debug {
	public static void main(String[] args) throws InterruptedException {
		Control.hostInit();
		Control.startOS();  //start the os...
		Globals.userProgramInput.setText("13 3 0 13 31 0 11 2 10 14 -2 11 1 10 14 -1 11 1 4 4 2 -1 4 3 2 -2 4 2 11 1 0 13 23 13 73 13 40 13 6 0 3 1 3 3 15 0 0");
	}
}
