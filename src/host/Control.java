package host;

import os.Kernel;
import util.Globals;

import java.text.SimpleDateFormat;
import java.util.Date;
import util.PCB;
import java.util.LinkedList;

public class Control {
	public static Kernel kernel;
	public static CPU cpu;
	public static TurtleWorld frame;
	
	public static void hostInit() {
		frame = new TurtleWorld(900, 500);
		//frame.addKeyListener(Devices.devices);
		Globals.world = frame;
		Globals.userProgramInput = TextArea.createAndShowGUI();
	}
	
	public static void hostLog(String message, String source) {
		if(source == null) source = "?";
		int clock = util.Globals.OSclock;
		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
		System.out.println("{clock: " + clock + ", source: " + source + ", message: " + message + ", now: " + timeStamp);
	}
	
	public static void startOS() {  //TODO: this should be connected to a button somehow...
		//disable the start button.
		//enable shutdown and reset buttons.
		//refocus to console.
		Globals.world.setMessage("System is running...");
		cpu = new CPU();
		//            _hardwareClockID = setInterval(Devices.hostClockPulse, CPU_CLOCK_INTERVAL);
		//      as seen above, must do something with clocks....

		Globals.host = new Thread() {
			public void run() {
				try {
					while(true) {
						sleep(Globals.CPU_CLOCK_INTERVAL);
						Devices.devices.hostClockPulse();
					}
				} catch (InterruptedException e) {
					System.err.println("Weird.  That's not supposed to happen...");
				}
				Devices.devices.hostClockPulse();
			}
		};
		Globals.host.start();
		
		kernel = new Kernel();
		kernel.kernelBootstrap();
		Globals.world.focus();
	}
	
	public static void haltOS() {
		Globals.processManager.shutdown();
		Globals.world.setMessage("System is halted...");
		Control.hostLog("Emergency Halt", "host");
		Control.hostLog("Attempting Kernel Shutdown", "host");
		kernel.kernelShutdown();
		Globals.host.stop();
        //			clearInterval(_hardwareClockID);
		//some clock stuff here as well.

	}
	
	public static void resetOS() {
		//I guess this must be a hard reset.  This may not be easy to do in Java.  
	}
}
