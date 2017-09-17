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

/** A TurtleWorld is a JFrame on which an Image object is drawn each time 
 *  the JFrame is repainted.  Each Turtle draws on that Image object. */

public class TurtleWorld extends javax.swing.JFrame implements MouseListener{
	private static final int EDGE = 13, TOP = 60;  // around the JFrame
	private int itsPictureTop = TOP;
	private Image itsPicture;
	private Image buttonSpace;
	private Graphics itsPage;
	private Graphics buttonPainter;
	private FontMetrics itsMetrics;
	private int width;
	private int height;
	private boolean startActive = true, haltActive = false;
	private String message = "";
	private static final GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

	public TurtleWorld (int width, int height)
	{	super ("Trurl -- The Operating System");  // set the title for the frame
		this.width = width;
		this.height = height;
		addMouseListener(this);
		createButtons();
		setDefaultCloseOperation (EXIT_ON_CLOSE); // no WindowListener
		setSize (width + 2 * EDGE, height + TOP + EDGE);
		toFront();  // put this frame in front of the BlueJ window
		setVisible (true);  // cause a call to paint
        begin (width, height);
		setFocusTraversalKeysEnabled(false);
	}	//======================

	public void createButtons() {
		buttonSpace = new java.awt.image.BufferedImage(width, 30, java.awt.image.BufferedImage.TYPE_INT_RGB);
		buttonPainter = buttonSpace.getGraphics();
		drawStartButton(buttonPainter, true);
		drawHaltButton(buttonPainter, false);
		message();
		repaint();
	}


	public void begin (int width, int height)
	{	itsPicture = new java.awt.image.BufferedImage (width, height, 
			           java.awt.image.BufferedImage.TYPE_INT_RGB);
		initItsPage();
		itsPage.fillRect (0, 30, width, height);
		itsMetrics = itsPage.getFontMetrics();
		repaint();
	}	//======================

	private void initItsPage() {
		itsPage = itsPicture.getGraphics();
		itsPage.setColor (Color.black);
		itsPage.setColor (Color.white);
		itsPage.setFont(new Font("monospaced", Font.PLAIN, 12));  //monospaced is easy to read and deal with...
	}

	public Graphics getPage()
	{	return itsPage; // itsPicture.getGraphics(); => NO COLORS
	}	//======================


	public void paint (Graphics g)
	{	if (itsPicture != null)
			g.drawImage (itsPicture, EDGE, itsPictureTop, this);
			g.drawImage(buttonSpace, EDGE, TOP-30, width, 30, this);
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
		buttonPainter.fillRect(165, 0, width - 165, 30);  //just clear the whole thing!
		buttonPainter.setColor(Color.white);
		Date currDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(Globals.defaultDateFormat);
		String drawString = dateFormat.format(currDate) + " " + getCurrMessage();
		buttonPainter.drawString(drawString, 165, 22);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

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
		Image altered = new java.awt.image.BufferedImage (width, height + lineHeight,
				java.awt.image.BufferedImage.TYPE_INT_RGB);

//		BufferedImage altered = config.createCompatibleImage(itsPicture.getWidth(this), itsPicture.getHeight(this) + lineHeight);

		altered.getGraphics().drawImage(itsPicture, 0, 0, this);


		itsPicture = altered;
		initItsPage();

	}

	private boolean needToShiftUp(int YPos) {
		return (YPos >= Globals.world.getConsoleHeight());
	}

	public void blueScreen(String reason) {
	    int topOfScreen = (itsPictureTop < 0) ? -itsPictureTop : 0;
	    itsPage.setColor(Color.blue);
        itsPage.fillRect(0, topOfScreen, width, height);
        itsPage.setColor(Color.black);
	    itsPage.drawString(reason, 0, topOfScreen + getLineHeight());
	    Control.kernel.kernelTrapError(reason);
    }

}
// </pre>

