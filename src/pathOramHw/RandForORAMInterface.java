package pathOramHw;

import java.util.ArrayList;

public interface RandForORAMInterface {
	
	/**
	 * @return a number from the uniform distribution in [0, num_leaves)
	 * You need to call setBound before using this method.
	 */
	abstract int getRandomLeaf();

	abstract void setBound(int num_leaves);
	
}
