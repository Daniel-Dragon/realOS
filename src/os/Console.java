package os;

import util.Globals;
import java.util.LinkedList;
import java.awt.*;

public class Console implements Input, Output{
	private String buffer = "";
	private LinkedList<String> inputBuffer = new LinkedList();
	private int inputBufferIndex = -1;
	private int XPos, YPos, endXPos;
	private String promptString = ">";
	public Console() {

	}

	public void init() {
		clearScreen();
		resetXY();
		endXPos = Globals.world.getCharacterWidth() * Globals.world.measureText(0, "x");
		putText(promptString);
	}

	@Override
	public void putText(String string) {
		LinkedList<String> output = wrapText(string);

		if (output.size() > 1) {
			for (String line : output) {
				writeText(line);
				advanceLine();
			}
		} else if(output.size() == 1){
			writeText(string);
		}
	}

	private void writeText(String string) {
		if (!string.equals("")) {
			if (needLineBreak(string))
				inputLineBreak();
			Globals.world.drawText(XPos, YPos, string);
			int offset = Globals.world.measureText(XPos, string);
			XPos += offset;
		}
	}

	public LinkedList<String> wrapText(String string) {
		LinkedList<String> output = new LinkedList();
		int characterWidth = Globals.world.getCharacterWidth();
		boolean foundSplitPoint = false;

		while (string.length() > 0) {

			if (string.length() <= characterWidth) {
				output.add(string);
				return output;
			} else {
				int checkingPoint = characterWidth - 1;

				while (!foundSplitPoint) {
					char charToCheck = string.charAt(checkingPoint);
					if (charToCheck == ' ' || charToCheck == '-') {
						foundSplitPoint = true;
					} else {
						if (--checkingPoint <= 0) {
							//We go through without finding an appropriate split location we just split at the end.
							checkingPoint = characterWidth;
							foundSplitPoint = true;
						}
					}
				}
				output.add(string.substring(0, checkingPoint));
				string = string.substring(checkingPoint + 1, string.length() - 1);
				foundSplitPoint = false;
			}
		}


		return output;
	}

	@Override
	public void advanceLine() {
		XPos = 0;
		YPos += Globals.world.fontHeightMargin() + Globals.world.fontDescent() + Globals.world.fontSize();
	}

	public void retreatLine() {
		XPos = Globals.world.measureText(0, promptString + buffer);

		YPos -= Globals.world.getLineHeight();
	}

	@Override
	public void clearScreen() {
		Globals.world.clearRect(0, 0, Globals.world.width(), Globals.world.height());
	}

	@Override
	public void resetXY() {
		XPos = 0;
		YPos = Globals.world.startYPos();
	}

	@Override
	public void handleInput() {
		while(! Globals.kernelInputQueue.isEmpty()) {
			String next = Globals.kernelInputQueue.removeFirst();
			if(next.length() > 1) {
			    //TODO Abstract these codes to Globals.
                if (next.equals("38:0")) {
                    //Up arrow
                    nextInputBuffer();
                } else if (next.equals("40:0")) {
                    //Down arrow
                    prevInputBuffer();
                } else if (next.equals("9:0")) {
                    //Tab key
                    tabComplete();
                } else {
                    continue;
                }
            } else if(next.equals("\n") || next.equals("\r") || next.equals("" + ((char)10))){
			    inputBuffer.offerFirst(buffer);
				Globals.osShell.handleInput(buffer);
				putText(promptString);
				buffer = "";
				inputBufferIndex = -1;
			} else if (next.equals("\b")) {
				if (buffer.length() > 0) {
					removeText(1);
				}
			} else {
            	writeText("" + next);
				buffer += next;
			}
		}
	}

	private void removeText(int numChar) {

		for (int i = 0; i < numChar; i++) {
			String currText = buffer.substring(buffer.length() - 1, buffer.length());
			int xOffset = Globals.world.measureText(XPos, currText);
			int yOffset = Globals.world.fontSize();
			XPos -= xOffset;
			Globals.world.clearRect(XPos, YPos - yOffset, xOffset, yOffset + Globals.world.fontDescent());
			buffer = buffer.substring(0, buffer.length()-1);

			if (XPos == 0) {
				retreatLine();
				removeText(1);
			}
		}
	}

	private boolean needLineBreak(String string) {
	    return ((Globals.world.measureText(0, string) + XPos) > endXPos);
    }

    private boolean needToShiftUp() {
		return YPos == Globals.world.height();
	}

    private void nextInputBuffer() {
		if ((inputBuffer.size() > (inputBufferIndex + 1))) {
		    setPromptString(inputBuffer.get(++inputBufferIndex));
		}
	}

	private void prevInputBuffer() {
		if (inputBufferIndex > 0) {
		    setPromptString(inputBuffer.get(--inputBufferIndex));
		}
	}

	private void tabComplete() {
		if (!buffer.isEmpty()) {
            for (String input: inputBuffer) {
                if (input.startsWith(buffer)) {
                    setPromptString(input);
                    break;
                }
            }
		}
	}

	private void setPromptString (String string) {
	    removeText(buffer.length());
	    buffer = string;
	    XPos = 0;
	    putText(promptString + buffer);
    }

	@Override
	public int getXPos() {
		return XPos;
	}

	private void inputLineBreak() {
		int checkPlace = buffer.length() - 1;
		String nextLine = "";
		boolean foundBreakPoint = false;

		while (!foundBreakPoint) {
			char charToCheck = buffer.charAt(checkPlace);
			if (charToCheck == ' ' || charToCheck == '-') {
				foundBreakPoint = true;
			} else {
				if(--checkPlace <= 0) {
					foundBreakPoint = true;
					checkPlace = buffer.length() - 1;
				}
			}
		}
		nextLine = buffer.substring(checkPlace, buffer.length());
		removeText(buffer.length() - checkPlace - 1);
		advanceLine();
		buffer += nextLine;
		putText(nextLine);
	}

}
