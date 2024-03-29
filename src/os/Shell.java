package os;

import exceptions.NoAvailableMemorySegment;
import host.Control;
import host.TurtleWorld;
import jdk.nashorn.internal.objects.Global;
import util.Globals;
import util.Utils;
import util.PCB;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class Shell {
	private ArrayList<ShellCommand> commandList = new ArrayList<ShellCommand>();

	public Shell() {
		
	}
	
	public void init() {
		commandList.add(new ShellCommand(shellInvalidCommand, "", ""));
		commandList.add(new ShellCommand(shellVer, "ver", "- Displays the current version data."));
		commandList.add(new ShellCommand(shellHelp, "help", "- Displays summary descriptions of common commands."));
		commandList.add(new ShellCommand(shellShutdown, "shutdown", "- Shuts down the virtual OS but leaves the underlying host / hardware simulation running."));
		commandList.add(new ShellCommand(shellCls, "cls", "- Clears the screen and resets the cursor position."));
		commandList.add(new ShellCommand(shellMan, "man", "<topic> - Displays the MANual page for <topic>."));
		commandList.add(new ShellCommand(shellTrace, "trace", "<on | off> - Turns the OS trace on or off."));
		commandList.add(new ShellCommand(shellDate, "date", "- Displays the current date and time. Optional argument for format you wish to display the date in."));
		commandList.add(new ShellCommand(shellWhereAmI, "whereami", "- Displays your current location within the system."));
		commandList.add(new ShellCommand(shellRot13, "rot13", "<string | message> - Does rot13 obfuscation on <string>."));
		commandList.add(new ShellCommand(shellStatus, "status", "<string | status> - Changes the system status message."));
		commandList.add(new ShellCommand(shellBsod, "bsod", "<string | reason> - Brings up the BSOD with the reason given."));
		commandList.add(new ShellCommand(shellLoad, "load", "- Loads program into main memory."));
		commandList.add(new ShellCommand(shellRun, "run", "- <int | pid> runs process with given pid if available."));
		commandList.add(new ShellCommand(shellTop, "top", "- Shows status of processes on this machine."));
		commandList.add(new ShellCommand(shellClearMem, "clearmem", "- removes all programs from memory"));
		commandList.add(new ShellCommand(shellKill, "kill", "- <pid> Attempts to gracefully shut down the process with PID provided"));
		commandList.add(new ShellCommand(shellQuantum, "quantum", "- <quantum number> Sets the round robin quantum of the process manager to specified value"));
		commandList.add(new ShellCommand(shellRunAll, "runall", "- Runs all loaded programs at once."));
		commandList.add(new ShellCommand(shellKillAll, "killall", "- Removes all currently running processes."));
		commandList.add(new ShellCommand(shellPs, "ps", "List currently running processes"));
		
	}
	
	public static ShellCommandFunction shellHexDump = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			if (in.size() > 0) {
	            Globals.standardOut.putText(Utils.hexDump(String.join(" ", in.toArray(new String[]{}))));
        } else {
            Globals.standardOut.putText("Usage: hexdump <string>.  Please supply a string.");
        }
			return null;
		}
	};
	
	public static ShellCommandFunction shellMan = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			if(in.size() > 0) {
				String topic = in.get(0);
				if(topic.equals("help")) {
					Globals.standardOut.putText("Help displays a list of (hopefully) valid commands.");
				} else {
					Globals.standardOut.putText("No manual entry for " + topic + ".");
				}
			} else {
				Globals.standardOut.putText("Usage: man <topic>.  Please supply a topic.");
			}
			return null;
		}
	};
	
	public static ShellCommandFunction shellTrace = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			if(in.size() > 0) {
				String setting = in.get(0);
				if(setting.equals("on")) {
					if(!Globals.trace) {
						Globals.trace = true;
						Globals.standardOut.putText("Trace ON");
					}
				} else if(setting.equals("off")) {
					if(Globals.trace) {
						Globals.trace = false;
						Globals.standardOut.putText("Trace OFF");
					}
				} else {
					Globals.standardOut.putText("Usage: trace <on | off>.  Please supply an argument.");
				}
			}
			return null;
		}
	};

	public static ShellCommandFunction shellDate = new ShellCommandFunction() {
        public Object execute(ArrayList<String> in) {
            Date currDate = new Date();

            if (in.isEmpty()) {
                SimpleDateFormat defaultFormat = new SimpleDateFormat(Globals.defaultDateFormat);
                Globals.standardOut.putText( defaultFormat.format(currDate) );
            } else {
                try {
                    SimpleDateFormat customFormat = new SimpleDateFormat(String.join(" ", in));
                    Globals.standardOut.putText( customFormat.format(currDate));
                }
                catch(IllegalArgumentException e) {
                    Globals.standardOut.putText("Error with your date format");
                }
            }
            return null;
        }
    };
	public static ShellCommandFunction shellWhereAmI = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.putText(Globals.currentLocation);
			return null;
		}
	};

	public static ShellCommandFunction shellRot13 = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			String stringIn = String.join(" ", in);
			StringBuilder stringOut = new StringBuilder();

			for (char ch: stringIn.toCharArray()) {
				if (Character.isLetter(ch)) {
					char charOut = ((short) Character.toLowerCase(ch) > Globals.asciiValM) ? (char)((short)ch - 13) : (char)((short)ch + 13);
					stringOut.append(charOut);
				} else {
					stringOut.append(ch);
				}
			}

			Globals.standardOut.putText(stringOut.toString());
			return null;
		}
	};

	public static ShellCommandFunction shellStatus = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			Globals.world.setMessage(String.join(" ", in));
			return null;
		}
	};

	public static ShellCommandFunction shellVer = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.putText(Globals.name + " version " + Globals.version);
			return null;
		}
	};
	
	public static ShellCommandFunction shellHelp = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.putText("Commands:");
			for(ShellCommand s : Globals.osShell.commandList) {
				Globals.standardOut.advanceLine();
				Globals.standardOut.putText("  " + s.getCommand() + " " + s.getDescription());
			}
			return null;
		}
	};
	
	public static ShellCommandFunction shellShutdown = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.putText("Shutting down...");
			Control.kernel.kernelShutdown();
			return null;
		}
	};
	
	public static ShellCommandFunction shellCls = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.clearScreen();
			Globals.standardOut.resetXY();
			return null;
		}
	};

	public static ShellCommandFunction shellInvalidCommand = new ShellCommandFunction() {
		public Object execute(ArrayList<String> in) {
			Globals.standardOut.putText("Invalid Command. ");
			return null;
		}
	};

	public static ShellCommandFunction shellBsod = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> input) {
			Globals.world.blueScreen(String.join(" ", input));
			shellStatus.execute(input);
			return null;
		}
	};

	public static ShellCommandFunction shellLoad = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			String program = Globals.userProgramInput.getText().trim();

			if (program.isEmpty()) {
				Globals.console.putText("No program found, please try again.");
			} else if (java.util.regex.Pattern.compile("(-*[0-9]*\\s*)*").matcher(program).matches()) {
				String[] programList = program.split(" ");
				int[] intArr = new int[programList.length];
				for (int i = 0; i < programList.length; i++) {
					intArr[i] = Integer.parseInt(programList[i]);
				}
				if (Globals.mmu.hasFreeSegment()) {
					PCB currProcess = Globals.processManager.loadProgram(intArr);

					Globals.console.putText("PID: " + currProcess.pid + " loaded into segment: " + currProcess.segment);
				}
				else
					Globals.console.putText("No segment available for program.");

			}
			else
				Globals.console.putText("Invalid program!");
			return null;
		}
	};

	public static ShellCommandFunction shellTop = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> input) {

			String[] processes = Globals.processManager.top();

			for (String process : processes) {
				Globals.console.putText(process);
				Globals.console.advanceLine();
			}

			return null;
		}
	};

	public static ShellCommandFunction shellClearMem = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			Globals.processManager.unloadAll();
			return null;
		}
	};

	public static ShellCommandFunction shellRun = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			int pid;
			HashMap<String, String> event = new HashMap<String, String>();
			if (in.isEmpty())
				Globals.console.putText("Please provide PID to load");
			else {
				pid = Integer.parseInt(in.get(0));
				if (Globals.processManager.isProgramInResidentList(pid)) {
					PCB currProcess = Globals.processManager.getProgram(pid);
					event.put("pid", String.valueOf(pid));
					Globals.kernelInterruptQueue.add(new Interrupt(Globals.IRQ.PROCESS, event));
					Globals.world.displayPCB(currProcess);
				} else {
					Globals.console.putText("Program with pid " + pid + " wasn't found, did you load it first?");
				}
			}
			return null;
		}
	};

	public static ShellCommandFunction shellKill = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			if (in.size() == 0) {
				Globals.console.putText("Please provide PID to kill.");
				return null;
			}

			int pid;

			try {
				pid = Integer.parseInt(in.get(0));
			} catch (NumberFormatException e) {
				Globals.console.putText(in.get(0) + " is not a valid PID.");
				return null;
			}

			if (Globals.processManager.isProgramInResidentList(pid)) {
				Globals.processManager.haltProgram(pid, 0);
			} else {
				Globals.console.putText("Program with PID " + pid + " wasn't found! Did it finish?");
			}
			return null;
		}
	};

	public static ShellCommandFunction shellQuantum = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			if (in.size() == 0)
			{
				Globals.console.putText("Please provide a valid integer for the quantum.");
				return null;
			}

			int quantum;
			try {
				quantum = Integer.parseInt(in.get(0));
			} catch(NumberFormatException e) {
				Globals.console.putText(in.get(0) + " isn't a valid quantum.");
				return null;
			}

			Globals.processManager.setQuantum(quantum);
			Globals.console.putText("Quantum has been set to " + String.valueOf(quantum));
			return null;
		}
	};

	public static ShellCommandFunction shellRunAll = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			Globals.processManager.runAll();
			return null;
		}
	};

	public static ShellCommandFunction shellKillAll = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			Globals.processManager.killAll();
			return null;
		}
	};

	public static ShellCommandFunction shellPs = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			PCB[] processes = Globals.processManager.getReadyQueueAsArray();
			if (processes.length == 0)
				Globals.console.putText("No processes running!");
			else
			{
				String[] pids = new String[processes.length];
				for (int i = 0; i < processes.length; i++) {
					pids[i] = String.valueOf(processes[i].pid);
				}
				Globals.console.putText("PID(s): " + String.join(",", pids));

			}
			return null;
		}
	};

	public void handleInput(String buffer) {
		Control.kernel.kernelTrace("Shell Command~" + buffer);
		UserCommand userCommand = parseInput(buffer);
		String command = userCommand.getCommand();
		
		ShellCommand function = commandList.get(0);
		for(ShellCommand sc : commandList) {
			if(sc.getCommand().equals(command)) {
				function = sc;
			}
		}
		
		execute(function, userCommand);
	}

	private void execute(ShellCommand function, UserCommand userCommand) {
		Globals.standardOut.advanceLine();
		function.function().execute(userCommand);
		if(Globals.standardOut.getXPos() > 0) {
			Globals.standardOut.advanceLine();
		}
	}
	
	public UserCommand parseInput(String buffer) {
		UserCommand retVal;
		buffer = buffer.trim();
		String[] parts = buffer.split(" ");
		String name = parts[0].toLowerCase();
		retVal = new UserCommand(name);
		for(int i = 1; i < parts.length; i++) {
			String arg = parts[i].trim();
			if(!arg.equals("")) {
				retVal.add(arg);
			}
		}
		return retVal;
	}

	public String[] getShellCommands() {
		String[] commands = new String[commandList.size()];

		for (int i = 0; i < commandList.size(); i++) {
			commands[i] = commandList.get(i).getCommand();
		}

		return commands;
	}
}
