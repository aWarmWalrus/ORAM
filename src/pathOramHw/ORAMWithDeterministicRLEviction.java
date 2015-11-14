package pathOramHw;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Name: Charles Qian
 * NetID: cq38
 */

public class ORAMWithDeterministicRLEviction implements ORAMInterface{

	/**
	 * TODO add necessary variables 
	 */
	private int N;
	private int L;
	private final int B = 24;
	private int Z;
	private HashMap<Integer,Block> S_hm;
	private Integer pos_map[];

	private Bucket tree[];
	private RandForORAMInterface rand;
	private UntrustedStorageInterface server;
		
	public ORAMWithDeterministicRLEviction(UntrustedStorageInterface storage, RandForORAMInterface rand_gen, int bucket_size, int num_blocks){
		// TODO Complete the constructor
		Z = bucket_size;
		N = num_blocks;
		rand = rand_gen;
		server = storage;
		L = log_2_ceil(N);
		pos_map = new Integer[ (int) Math.pow(2, L) ];

		// Initialize the position map
		rand_gen.setBound(pos_map.length);
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
