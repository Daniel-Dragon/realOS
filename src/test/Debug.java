package test;

import host.Control;
import util.Globals;


import util.PCB;
public class Debug {
	public static void main(String[] args) throws InterruptedException {
		Control.hostInit();
		Control.startOS();  //start the os...
//Test filling RAM
//		for (int i = 20; i < 100; i++) {
//			Globals.world.fillMemory(i);
//		}
	}
}
