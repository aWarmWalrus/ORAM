package pathOramHw;

import java.util.ArrayList;

/*
 * Name: Charles Qian
 * NetID: cq38
 */

public class ORAMWithDeterministicRLEviction implements ORAMInterface{

	/**
	 * TODO add necessary variables 
	 */
		
	public ORAMWithDeterministicRLEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO Complete the constructor
	}
		
	@Override
	public Integer[] getPositionMap(){
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public byte[] access(Operation op, int blockIndex, byte[] newdata) {
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public int P(int leaf, int level) {
		// TODO Must complete this method for submission
		return 0;
	}


	@Override
	public ArrayList<Block> getStash() {
		// TODO Must complete this method for submission
		return null;
	}


	@Override
	public int getStashSize() {
		// TODO Must complete this method for submission
		return 0;
	}

	@Override
	public int getNumLeaves() {
		// TODO Must complete this method for submission
		return 0;
	}

	@Override
	public int getNumLevels() {
		// TODO Must complete this method for submission
		return 0;
	}

	@Override
	public int getNumBlocks() {
		// TODO Must complete this method for submission
		return 0;
	}

	@Override
	public int getNumBuckets() {
		// TODO Must complete this method for submission
		return 0;
	}
	
	public int getGlobalG() {
		// TODO Must complete this method for submission
		return 0;
	}
	
	public int ReverseBits(int G, int bits_length)
	{
		// TODO Must complete this method for submission
		return 0;
	}

	//You may add helper methods.
	
}
