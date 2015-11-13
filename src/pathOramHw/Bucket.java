package pathOramHw;

import java.util.ArrayList;

import javax.management.RuntimeErrorException;

/*
 * Name: Charles Qian
 * NetID: cq38
 */

public class Bucket{
	private static boolean is_init = false;
	private static int max_size_Z = -1;
	
	//TODO Add necessary variables
	private ArrayList<Block> blocks;
	
	Bucket(){
		if(is_init == false)
		{
			throw new RuntimeException(
					"Please set bucket size before creating a bucket");
		}
		//TODO Must complete this method for submission
		blocks = new ArrayList<Block>(max_size_Z);
		for (int i = 0; i < max_size_Z; i++) {
			// initialize to dummy block
			blocks.add(new Block());
		}
	}
	
	// Copy constructor
	Bucket(Bucket other)
	{
		if(other == null)
		{
			throw new RuntimeException("the other bucket is not malloced.");
		}
		//TODO Must complete this method for submission
		ArrayList<Block> other_blocks = other.getBlocks();
		blocks = new ArrayList<Block>(max_size_Z);
		for (Block new_block : other_blocks) { //int i = 0; i < max_size_Z; i++) {
			blocks.add(new_block);
		}
	}
	
	//Implement and add your own methods.
	Block getBlockByKey(int block_id){
		// TODO Must complete this method for submission
		for (Block b : blocks) {
			if (b.index == block_id) {
				System.out.printf("Found the requested block: %d\n", block_id);
				return b;
			}
		}
		return null;
	}
	
	void addBlock(Block new_blk){
		// TODO Must complete this method for submission
		int i = 0;
		for (i = 0; i < max_size_Z; i++) { 
			if (blocks.get(i).leaf_id == -1) {
				blocks.set(i, new_blk);
				return;
			}
		}
		if (i == max_size_Z) {
			throw new RuntimeException("Logic is wrong, or bucket is already full.");
		}
	}
	
	boolean removeBlock(Block rm_blk)
	{
		// TODO Must complete this method for submission
		for (Block b : blocks) {
			// Reference equality. Might need to be value equality, not sure.
			if (b == rm_blk) {
				// Set this to a dummy block.
				b.leaf_id = -1;
				b.index = -1;
				b.data = new byte[24];
				return true;
			}
		}
		return false;
	}
	
	
	ArrayList<Block> getBlocks(){
		// TODO Must complete this method for submission
		// This should return an ArrayList of all blocks that have been added 
		// to the Bucket, both dummy and non dummy.
		return blocks;
	}
	
	

	static void setMaxSize(int maximumSize)
	{
		if(is_init == true)
		{
			throw new RuntimeException("Max Bucket Size was already set");
		}
		max_size_Z = maximumSize;
		is_init = true;
	}
	
	//Your implementation SHOULD NOT call this method
	static void resetState()
	{
		is_init = false;
	}

}
