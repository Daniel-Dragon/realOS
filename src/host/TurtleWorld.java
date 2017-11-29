package host;

// <pre>
/*  Copy this file in its entirety to a file named Turtle.java.
 *  Compile the Turtlet class and then compile this class, before trying to 
 *  compile any program that uses Turtles.
 *  This class draws to an Image object and lets the frame's paint method 
 *  show the Image whenever the frame repaints itself. It is for 
 *  Turtle commands that are given in or from a main application. */

import util.Globals;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import util.Globals.MemoryOperation;
import util.PCB;

/** A TurtleWorld is a JFrame on which an Image object is drawn each time 
 *  the JFrame is repainted.  Each Turtle draws on that Image object. */

public class TurtleWorld extends javax.swing.JFrame implements MouseListener{
	private static final int EDGE = 13, TOP = 60;  // around the JFrame
	private static final int MEM_WIDTH = 30;
	private static final int MEM_MARGIN = 2;
	private static final int MEM_HEADING = 10;
	public static final int NUM_MEM_SEGMENT = Globals.NUM_MEM_SEGMENT;
	public static final int SEGMENT_SIZE = Globals.SEGMENT_SIZE;
	private static final int MEM_TOTAL_HEIGHT = MEM_MARGIN * 2 + SEGMENT_SIZE + MEM_HEADING;
	private static final int DISPLAY_HEIGHT = 80;
	private static final int DISPLAY_WIDTH = 200;
	private static final int DISPLAY_MARGIN = 2;
	private int itsPictureTop = TOP;
	private Image itsPicture;
	private Image buttonSpace;
	private Image[] memPicture = new Image[NUM_MEM_SEGMENT];
	private Image dispPicture;
	private Graphics itsPage;
	private Graphics buttonPainter;
	private Graphics[] memPage = new Graphics[NUM_MEM_SEGMENT];
	private Graphics dispPage;
	private PCB currentPCB = null;
	private FontMetrics itsMetrics;
	private int width;
	private int widthOffset;
	private int height;
	private boolean startActive = true, haltActive = false;
	private String message = "";
	private static final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	private boolean blanked = false;

	public TurtleWorld (int width, int height)
	{	super ("realOS -- The Operating System");  // set the title for the frame
		this.width = width + MEM_WIDTH;
		this.height = height;
		addMouseListener(this);
		createButtons();
		setDefaultCloseOperation (EXIT_ON_CLOSE); // no WindowListener
		widthOffset = ((MEM_WIDTH * NUM_MEM_SEGMENT) > (DISPLAY_WIDTH)) ? (MEM_WIDTH * NUM_MEM_SEGMENT) : DISPLAY_WIDTH;
		setSize (width + 2 * EDGE + widthOffset, height + TOP + EDGE + DISPLAY_HEIGHT);
		toFront();  // put this frame in front of the BlueJ window
		setVisible (true);  // cause a call to paint
        begin (width, height);
		setFocusTraversalKeysEnabled(false);
	}	//======================

	public void createButtons() {
		buttonSpace = new java.awt.image.BufferedImage(width + MEM_WIDTH * NUM_MEM_SEGMENT, 30, java.awt.image.BufferedImage.TYPE_INT_RGB);
		buttonPainter = buttonSpace.getGraphics();
		drawStartButton(buttonPainter, true);
		drawHaltButton(buttonPainter, false);
		message();
		repaint();
	}


	public void begin (int width, int height)
	{	itsPicture = new java.awt.image.BufferedImage (width, height,
			           java.awt.image.BufferedImage.TYPE_INT_RGB);
	    for (int i = 0; i < NUM_MEM_SEGMENT; i++) {
            memPicture[i] = new java.awt.image.BufferedImage (MEM_WIDTH, MEM_TOTAL_HEIGHT,
                    java.awt.image.BufferedImage.TYPE_INT_RGB);
            memPage[i] = memPicture[i].getGraphics();
            memPage[i].setFont(new Font("monospaced", Font.PLAIN, 12));
            memPage[i].setColor(Color.magenta);
            memPage[i].fillRect(0, MEM_HEADING, MEM_WIDTH, SEGMENT_SIZE + (2 * MEM_MARGIN));
            memPage[i].clearRect(MEM_MARGIN, MEM_MARGIN + MEM_HEADING, MEM_WIDTH - MEM_MARGIN * 2, SEGMENT_SIZE);
        }

		dispPicture = new java.awt.image.BufferedImage (DISPLAY_WIDTH, DISPLAY_HEIGHT,
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		dispPage = dispPicture.getGraphics();
		dispPage.setFont(new Font("monospaced", Font.PLAIN, 10));

		initItsPage();
		itsPage.fillRect (0, 30, width, height);
		itsMetrics = itsPage.getFontMetrics();
		repaint();
	}	//======================

	private void initItsPage() {
		itsPage = itsPicture.getGraphics();
		itsPage.setColor (Color.BLACK);
		itsPage.setColor (Color.WHITE);
		itsPage.setFont(new Font("monospaced", Font.PLAIN, 12));  //monospaced is easy to read and deal with...
	}

	public Graphics getPage()
	{	return itsPage; // itsPicture.getGraphics(); => NO COLORS
	}	//======================


	public void paint (Graphics g)
	{
		if (!blanked) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width + widthOffset, height + TOP);
			blanked = true;
		}
		if (itsPicture != null) {
			g.drawImage (itsPicture, EDGE, itsPictureTop, this);
			g.drawImage(buttonSpace, EDGE, 30, width - MEM_WIDTH , 30, this);
		}

        if (memPicture != null && memPage[0] != null) {
            for (int i = 0; i < NUM_MEM_SEGMENT; i++) {
				g.drawImage(memPicture[i], width + (i * MEM_WIDTH) - 17, TOP - 30 + DISPLAY_HEIGHT, MEM_WIDTH, height - DISPLAY_HEIGHT + 30, this);
				memPage[i].setColor(new Color(0, 0, 0, 15));
				memPage[i].fillRect(MEM_MARGIN, MEM_MARGIN + MEM_HEADING, MEM_WIDTH - MEM_MARGIN * 2, SEGMENT_SIZE);
			}
        }

        if (dispPicture != null) {
        	g.drawImage(dispPicture, width - 17, TOP-30, DISPLAY_WIDTH, DISPLAY_HEIGHT, this);
		}
	}	//======================


	public void drawText(int xPos, int yPos, String string) {
//		if(true) throw new RuntimeException();
		if (needToShiftUp(yPos))
			shiftUp();
		itsPage.drawString(string, xPos, yPos);
//		System.out.println("painting \"" + string + "\"" + " at (" + xPos + ", " + yPos + ")");
		repaint();
	}

	public int measureText(int xPos, String string) {
		return itsMetrics.stringWidth(string);
	}


	public int fontHeightMargin() {
		return itsMetrics.getLeading();
	}


	public int fontDescent() {
		return itsMetrics.getDescent();
	}


	public int fontSize() {
		return itsMetrics.getAscent();
	}


	public int width() {
		return width;
	}


	public int height() {
		return height;
	}


	public void clearRect(int x, int y, int width, int height) {
		itsPage.clearRect(x, y, width, height);
		repaint();
	}


	public int startYPos() {
		return 100;
	}


	public void focus() {
		toFront();  // put this frame in front of the BlueJ window
	}

	public void setColor(int r, int g, int b) {
		itsPage.setColor(new Color(r, g, b));
	}
	
	public void drawStartButton(Graphics g, boolean activated) {
		if(activated) g.setColor(new Color(0, 128, 0));
		else g.setColor(Color.gray);
		g.fillRect(0, 0, 80, 30);
		g.setColor(Color.black);
		g.drawString("START", 20, 22);
	}
	
	public void drawHaltButton(Graphics g, boolean activated) {
		if(activated) g.setColor(new Color(0, 128, 0));
		else g.setColor(Color.gray);
		g.fillRect(80, 0, 80, 30);
		g.setColor(Color.black);
		g.drawString("HALT", 105, 22);
	}
	
	public void message()
	{
		buttonPainter.setColor(Color.black);
		buttonPainter.fillRect(165, 0, width - 165 + MEM_WIDTH * NUM_MEM_SEGMENT, 30);  //just clear the whole thing!
		buttonPainter.setColor(Color.white);
		Date currDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(Globals.defaultDateFormat);
		String drawString = dateFormat.format(currDate) + " " + getCurrMessage();
		buttonPainter.drawString(drawString, 165, 22);

		if (dispPage != null && Control.cpu.currentProcess != null) {
			dispPage.setColor(Color.WHITE);
			dispPage.fillRect(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
			dispPage.clearRect(DISPLAY_MARGIN, DISPLAY_MARGIN, DISPLAY_WIDTH - 2 * DISPLAY_MARGIN, DISPLAY_HEIGHT - 2 * DISPLAY_MARGIN);

			dispPage.drawString("Process Information", DISPLAY_MARGIN, 10 + DISPLAY_MARGIN);
			dispPage.drawString("Instruction: " + String.valueOf(Control.cpu.currentProcess.getCurrentInstruction()),  DISPLAY_MARGIN, 20 + DISPLAY_MARGIN);
			dispPage.drawString("Stack Limit: " + Control.cpu.currentProcess.stackLimit,  DISPLAY_MARGIN, 30 + DISPLAY_MARGIN);
			dispPage.drawString("Program Counter: " + Control.cpu.currentProcess.programCounter,  DISPLAY_MARGIN, 40 + DISPLAY_MARGIN);
			dispPage.drawString("State: " + Control.cpu.currentProcess.getprocessState(),  DISPLAY_MARGIN, 50 + DISPLAY_MARGIN);
			dispPage.drawString("Stack Pointer: " + Control.cpu.currentProcess.stackPointer,  DISPLAY_MARGIN, 60 + DISPLAY_MARGIN);
			PCB[] tempPCBs = Globals.processManager.getReadyQueueAsArray();
			String[] pids = new String[tempPCBs.length];
			for (int i = 0; i < tempPCBs.length; i++) {
				pids[i] = String.valueOf(tempPCBs[i].pid);
			}
			dispPage.drawString("PID(s): " + String.join(",", pids), DISPLAY_MARGIN, 70 + DISPLAY_MARGIN);
		} else if (dispPage != null){
			dispPage.setColor(Color.BLACK);
			dispPage.fillRect(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		}

	}

	public String getCurrMessage() {
		return message;
	}

	public void setMessage(String newMessage) {
		message = newMessage;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getX() < 4 + 81 && e.getX() > 3 && e.getY() > 30 && e.getY() <= 60) {
			if(haltActive) {
				Control.startOS();
				startActive = true;
				haltActive = false;
				drawStartButton(buttonPainter, startActive);
				drawHaltButton(buttonPainter, haltActive);
				message();
				repaint();
			}
		} else if(e.getX() < 4 + 81 + 80 && e.getX() > 3 + 80 && e.getY() > 30 && e.getY() <= 60) {
			if(startActive) {
				Control.haltOS();
				startActive = false;
				haltActive = true;
				drawStartButton(buttonPainter, startActive);
				drawHaltButton(buttonPainter, haltActive);
				message();
				repaint();
			}
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODOno Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODOno Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODOno Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODOno Auto-generated method stub

	}

	public int getLineHeight() {
		return fontSize() + fontDescent() + fontHeightMargin();
	}

	public int getConsoleHeight() {
		//height by default, plus the itsPictureTop since this becomes negative it will actually add to the height.
		return height - itsPictureTop - TOP;
	}

	public void shiftUp() {
		int lineHeight = getLineHeight();
		itsPictureTop -= lineHeight;
		int height = itsPicture.getHeight(this);
		int width = itsPicture.getWidth(this);
		Image altered = new java.awt.image.BufferedImage (width, height + lineHeight,
				java.awt.image.BufferedImage.TYPE_INT_RGB);

		altered.getGraphics().drawImage(itsPicture, 0, 0, this);


		itsPicture = altered;
		initItsPage();

	}

	private boolean needToShiftUp(int YPos) {
		return (YPos > Globals.world.getConsoleHeight());
	}

	public void blueScreen(String reason) {
	    int topOfScreen = (itsPictureTop < 0) ? -itsPictureTop : 0;
	    itsPage.setColor(Color.blue);
        itsPage.fillRect(0, topOfScreen, width, height);
        itsPage.setColor(Color.black);
	    itsPage.drawString(reason, 0, topOfScreen + getLineHeight());
	    Control.kernel.kernelTrapError(reason);
    }

    public int getCharacterWidth() {
		return ((width - (EDGE * 2)) /measureText(0, "x"));
	}

	public void interactWithMemory(int segment, int location, int value, MemoryOperation operation) {
	    memPage[segment].setColor(Color.BLACK);
	    memPage[segment].fillRect(0, 0, MEM_WIDTH, MEM_HEADING);
        int headingX = 0;
        int headingY = MEM_HEADING;
        int height = 1;
		try {
			switch (operation) {
				case READ:
					memPage[segment].setColor(Color.BLUE);
					memPage[segment].drawString(String.valueOf(value) + "", headingX, headingY);
					break;
				case WRITE:
					memPage[segment].setColor(Color.RED);
					memPage[segment].drawString(String.valueOf(value) + "", headingX, headingY);
					break;
				case CLEAR:
					memPage[segment].drawString(" ", headingX, headingY);
					height = Globals.SEGMENT_SIZE;
				default:
					//Something went wrong.
			}
		} catch (NullPointerException e) {
			//This is an edge case that we can't do a write for SOME reason. It doesn't hurt anything,
			//but it seems to be thrown when clearmem writes a LOT to the screen at once.
			System.out.println("Null pointer exception thrown {Segment: " + segment + ", Location: " + location + ", Value: " + value + "}");
		}

		memPage[segment].fillRect(MEM_MARGIN, location + MEM_HEADING + MEM_MARGIN, MEM_WIDTH - (2 * MEM_MARGIN), height);

        repaint();
    }

    public void displayPCB(util.PCB process) {
		currentPCB = process;
	}

	public void clearPCB() {
		currentPCB = null;
	}

}
// </pre>

