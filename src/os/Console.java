package os;

import util.Globals;

import java.awt.*;

public class Console implements Input, Output{
	private String buffer = "";
	private int XPos, YPos;
	public Console() {

	}

	public void init() {
		// TODO Auto-generated method stub
		clearScreen();
		resetXY();
	}

	@Override
	public void putText(String string) {
		if(!string.equals("")) {
//                _DrawingContext.drawText(this.currentFont, this.currentFontSize, this.currentXPosition, this.currentYPosition, text);
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
			if(next.length() > 1) continue; //TODO: handle special key strokes...
			if(next.equals("\n") || next.equals("\r") || next.equals("" + ((char)10))){
				Globals.osShell.handleInput(buffer);
				buffer = "";
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
			String currText = buffer.substring(buffer.length() - 1, buffer.length());
			int xOffset = Globals.world.measureText(XPos, currText);
			int yOffset = Globals.world.fontSize() + Globals.world.fontDescent();
			XPos -= xOffset;
			//Globals.world.set(Color.blue);
			Globals.world.clearRect(XPos, YPos - yOffset, xOffset, yOffset);

			buffer = buffer.substring(0, buffer.length()-1);
		}
	}

	@Override
	public int getXPos() {
		return XPos;
	}

}
