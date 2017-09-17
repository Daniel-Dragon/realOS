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
		// TODO Auto-generated method stub
		clearScreen();
		resetXY();
		endXPos = Globals.world.getCharacterWidth() * Globals.world.measureText(0, "x");
		putText(promptString);
	}

	@Override
	public void putText(String string) {
		if(!string.equals("")) {
//                _DrawingContext.drawText(this.currentFont, this.currentFontSize, this.currentXPosition, this.currentYPosition, text);
			if (needLineBreak(string))
				advanceLine();
            Globals.world.drawText(XPos, YPos, string);
			int offset = Globals.world.measureText(XPos, string);
			XPos += offset;
		}
	}

	@Override
	public void advanceLine() {
		XPos = 0;
		YPos += Globals.world.fontHeightMargin() + Globals.world.fontDescent() + Globals.world.fontSize();
	}

	public void retreatLine() {
		XPos = endXPos;
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
				putText("" + next);
				buffer += next;
			}
		}
	}

	private void removeText(int numChar) {

		for (int i = 0; i < numChar; i++) {
			if (XPos == 0)
				retreatLine();
			String currText = buffer.substring(buffer.length() - 1, buffer.length());
			int xOffset = Globals.world.measureText(XPos, currText);
			int yOffset = Globals.world.fontSize();
			XPos -= xOffset;
			Globals.world.clearRect(XPos, YPos - yOffset, xOffset, yOffset + Globals.world.fontDescent());

			buffer = buffer.substring(0, buffer.length()-1);
		}
	}

	private boolean needLineBreak(String string) {
	    return ((Globals.world.measureText(0, string) + XPos) > endXPos);
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

}
