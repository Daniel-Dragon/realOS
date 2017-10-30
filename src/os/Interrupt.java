package os;

import java.util.HashMap;

import util.Globals.IRQ;

public class Interrupt {
	public final IRQ irq;
	public final HashMap<String, String> params;
	
	public Interrupt(IRQ irq, HashMap<String, String> params) {
		this.irq = irq;
		this.params = params;
	}
}
