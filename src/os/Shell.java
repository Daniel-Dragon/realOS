package os;

import host.Control;
import host.TurtleWorld;
import jdk.nashorn.internal.objects.Global;
import util.Globals;
import util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

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
		commandList.add(new ShellCommand(shellTop, "top", "- Shows status of processes on this machine."));
		commandList.add(new ShellCommand(shellRamCheck, "ramcheck", "- Checks all ram."));
		
	}
	
//	public static ShellCommandFunction shellPrompt = new ShellCommandFunction() {
//		public Object execute(ArrayList<String> in) {
//			if (in.size() > 0) {
//				Globals.osShell.setPrompt(in.get(0));
//        } else {
//            Globals.standardOut.putText("Usage: prompt <string>.  Please supply a string.");
//        }
//			return null;
//		}
//	};
	
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
					char charOut = ((short)ch > Globals.asciiValM) ? (char)((short)ch - 13) : (char)((short)ch + 13);
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
		public Object execute(ArrayList<String> input) {
			String program = Globals.userProgramInput.getText();

			if (program.isEmpty()) {
				Globals.console.putText("No program found, please try again.");
			} else if (java.util.regex.Pattern.compile("([0-9]*\\s*)*").matcher(program).matches()) {
				int[] intArr = new int[input.size()];
				for (int i = 0; i < input.size(); i++) {
					intArr[i] = Integer.parseInt(input.get(i));
				}
				Globals.mmu.load(intArr);
			}
			else
				Globals.console.putText("Invalid program!");
			return null;
		}
	};

	public static ShellCommandFunction shellTop = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> input) {
			for (util.PCB process: Control.processes) {
				process.processDetails();
				Globals.console.advanceLine();
			}

			return null;
		}
	};

	public static ShellCommandFunction shellRamCheck = new ShellCommandFunction() {
		@Override
		public Object execute(ArrayList<String> in) {
			if (in.size() == 2) {

				try {
					Globals.world.fillMemory(Integer.parseInt(in.get(0)), Integer.parseInt(in.get(1)));
				} catch(Exception e) {
					Globals.console.putText("Need a valid segment and location to check.");
				}
				return null;
			} else {
				Globals.console.putText("Need a valid segment and location to check.");
				return null;
			}
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
		//buffer = buffer.toLowerCase();
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
}
