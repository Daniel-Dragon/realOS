package os;

public interface Output {

	void putText(String string);
	void advanceLine();
	void clearScreen();
	void resetXY();
	int getXPos();

}
