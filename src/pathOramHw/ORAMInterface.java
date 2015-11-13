package pathOramHw;

import java.util.ArrayList;


public interface ORAMInterface {
	
	public enum Operation {READ,WRITE};
	
	public byte[] access(Operation op, int blockIndex,byte[] newdata);

	public abstract int P(int leaf, int level);

	public abstract Integer[] getPositionMap();
	
	public abstract ArrayList<Block> getStash();

	public abstract int getStashSize();

	public int getNumLeaves();
	
	public int getNumLevels();
	
	public int getNumBlocks();
	
	public int getNumBuckets();
	
}