package os;

import java.util.ArrayList;

public interface ShellCommandFunction {
	Object execute(ArrayList<String> input);
}
