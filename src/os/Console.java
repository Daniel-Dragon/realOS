package os;

import util.Globals;
import java.util.LinkedList;
import java.awt.*;

public class Console implements Input, Output{
	private String buffer = "";
	private LinkedList<String> inputBuffer = new LinkedList();
	private int inputBufferIndex = 0;
	private StringBuilder outputBuffer = new StringBuilder(100);
	private int XPos, YPos;
	private String promptString = ">";
	public Console() {

	}

	public void init() {
		// TODO Auto-generated method stub
		clearScreen();
		resetXY();
		putText(promptString);
	}

	@Override
	public void putText(String string) {
		if(!string.equals("")) {
//                _DrawingContext.drawText(this.currentFont, this.currentFontSize, this.currentXPosition, this.currentYPosition, text);
			outputBuffer.append(string);
            Globals.world.drawText(XPos, YPos, string);
			int offset = Globals.world.measureText(XPos, string);
			boolean test = needLineBreak(buffer);
			XPos += offset;
		}
	}

	@Override
	public void advanceLine() {
		XPos = 0;
		YPos += Globals.world.fontHeightMargin() + Globals.world.fontDescent() + Globals.world.fontSize();
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
			//if(next.length() > 1) continue; //TODO: handle special key strokes...
			if(next.equals("\n") || next.equals("\r") || next.equals("" + ((char)10))){
			    outputBuffer.append("\n");
			    inputBuffer.offerFirst(buffer);
				Globals.osShell.handleInput(buffer);
				outputBuffer.append("\n");
				putText(promptString);
				buffer = "";
			} else if (next.equals("\b")) {
				if (buffer.length() > 0) {
					removeText(1);
				}
			} else if (next.equals("38:0")) {
				//Up arrow
				nextInputBuffer();
			} else if (next.equals("40:0")) {
				//Down arrow
				prevInputBuffer();
			} else if (next.equals("9:0")) {
				//Tab key
				tabComplete();
			} else {
				putText("" + next);
				buffer += next;
			}
		}
	}

	private void removeText(int numChar) {

		for (int i = 0; i < numChar; i++) {
		    outputBuffer.deleteCharAt(outputBuffer.length() - 1);
			String currText = buffer.substring(buffer.length() - 1, buffer.length());
			int xOffset = Globals.world.measureText(XPos, currText);
			int yOffset = Globals.world.fontSize();
			XPos -= xOffset;
			Globals.world.clearRect(XPos, YPos - yOffset, xOffset, yOffset + Globals.world.fontDescent());

			buffer = buffer.substring(0, buffer.length()-1);
		}
	}

	private boolean needLineBreak(String string) {
	    int textWidth = Globals.world.measureText(0, " ");
	    int windowWidth = Globals.world.width();
	    int stringWidth = (string.length() + promptString.length()) * textWidth;
	    return (windowWidth - stringWidth) < textWidth;
    }

    private void nextInputBuffer() {
		if ((inputBuffer.size() > inputBufferIndex)) {
		    setPromptString(inputBuffer.get(inputBufferIndex++));
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
